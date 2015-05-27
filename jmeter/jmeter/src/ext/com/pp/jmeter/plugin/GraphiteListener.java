/*
 * MonitorEverywhere plugin to publish selective metrics to a end point in a JSON format
 * This is derived from the JMeter Summary Report Listener 
 *
 */

package com.pp.jmeter.plugin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;

import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.gui.util.FileDialoger;
import org.apache.jmeter.gui.util.HeaderAsPropertyRenderer;
import org.apache.jmeter.samplers.Clearable;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.save.CSVSaveService;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.visualizers.gui.AbstractVisualizer;
import org.apache.jorphan.gui.ObjectTableModel;
import org.apache.jorphan.gui.NumberRenderer;
import org.apache.jorphan.gui.RateRenderer;
import org.apache.jorphan.gui.RendererUtils;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.jorphan.reflect.Functor;
import org.apache.jorphan.util.JOrphanUtils;
import org.apache.log.Logger;

/**
 * Simpler (lower memory) version of Aggregate Report (StatVisualizer).
 * Excludes the Median and 90% columns, which are expensive in memory terms
 */
public class GraphiteListener extends AbstractVisualizer 
	implements Clearable, ActionListener, TableModelListener, TestStateListener {

    private static final long serialVersionUID = 241L;

    private static final Logger log 			= LoggingManager.getLoggerForClass();

    private static final String USE_GROUP_NAME  = "useGroupName"; //$NON-NLS-1$

    private static final String SAVE_HEADERS    = "saveHeaders"; //$NON-NLS-1$
    
    private static final String SAVE_ENDPOINT   = "saveendpoint"; //$NON-NLS-1$
    
    private static final String SAVE_FOLDERNAME  = "savefoldername"; //$NON-NLS-1$
    
    private static final long POLLING_RATE       = 2L;
    private static final long PUBLISH_RATE       = 5L;
    
    
    private static final AgentConfigurer configurer = AgentConfigurer.getInstance();

    private static final String[] COLUMNS = {
            "sampler_label",               //$NON-NLS-1$
            "aggregate_report_count",      //$NON-NLS-1$
            "average",                     //$NON-NLS-1$
            "last average",				   //$NON-NLS-1$
            "aggregate_report_min",        //$NON-NLS-1$
            "aggregate_report_max",        //$NON-NLS-1$
            "aggregate_report_stddev",     //$NON-NLS-1$
            "95%",
            "aggregate_report_error%",     //$NON-NLS-1$
            "aggregate_report_rate",       //$NON-NLS-1$
            "last_10_txns",			   //$NON-NLS-1$
            "aggregate_report_bandwidth",  //$NON-NLS-1$
            "average_bytes",               //$NON-NLS-1$
            };
    

    private final Map<Integer,Integer[]> rowColMatrix = new HashMap<Integer,Integer[]>();
    
    private final transient List<String> collectedData = Collections.synchronizedList(new ArrayList<String>());
    
    private final String TOTAL_ROW_LABEL
        = JMeterUtils.getResString("aggregate_report_total_label");  //$NON-NLS-1$

    private JTable myJTable;

    private JScrollPane myScrollPane;

    private final JButton saveTable =
        new JButton(JMeterUtils.getResString("aggregate_graph_save_table"));            //$NON-NLS-1$

    private final JCheckBox saveHeaders = // should header be saved with the data?
        new JCheckBox(JMeterUtils.getResString("aggregate_graph_save_table_header"),true);    //$NON-NLS-1$

    private final JCheckBox useGroupName =
        new JCheckBox(JMeterUtils.getResString("aggregate_graph_use_group_name"));            //$NON-NLS-1$
    
    /**
     * 
     * 
     */
    
    private final JLabel graphiteDataLocation		= new JLabel("Graphite Folder hierarchy ");
    private final JLabel graphiteDataHelpLabel     = new JLabel(" (Use object notation, parentfolder.folder.subfolder)");
    private final JLabel endPointFormatLabel		= new JLabel("Graphite End Point             ");
    private final JLabel graphiteEndPointHelpLabel = new JLabel(" (hostname:port)                                                   "
    		+"          ");
    
    private final JTextField folderTextField		= new JTextField();
    private final JTextField endPointTextField		= new JTextField();
    
    private final String configurationFieldset		= "Graphite Configuration";
   
	
	private boolean isCollectorRunning = false;
	
	private ScheduledExecutorService scheduler;
	
    private transient ObjectTableModel model;
    

    /**
     * Lock used to protect tableRows update + model update
     */
    private final transient Object lock = new Object();
    
    /**
     * Lock used to protect collector
     */
    private final transient Object collectorLock = new Object();

    private final Map<String, SamplingStatCalculatorExt> tableRows = new ConcurrentHashMap<String, SamplingStatCalculatorExt>();

    // Column renderers
    private static final TableCellRenderer[] RENDERERS =
        new TableCellRenderer[]{
            null, // Label
            null, // count
            null, // Mean
            null, // Mean(last)
            null, // Min
            null, // Max
            new NumberRenderer("#0.00"), // Std Dev.
            new NumberRenderer("#0.00"), // 95% Percentile.
            new NumberRenderer("#0.00%"), // Error %age
            new RateRenderer("#.0"),      // Throughput
            new RateRenderer("#.0"),	   //Throughput (last)
            new NumberRenderer("#0.00"),  // kB/sec
            new NumberRenderer("#.0"),    // avg. pageSize
        };

    public GraphiteListener() {
        super();
        model = new ObjectTableModel(COLUMNS,
                SamplingStatCalculatorExt.class,// All rows have this class
                new Functor[] {
                    new Functor("getLabel"),              //$NON-NLS-1$
                    new Functor("getCount"),              //$NON-NLS-1$
                    new Functor("getMeanAsNumber"),       //$NON-NLS-1$
                    new Functor("getLastMeanAsNumber"),   //$NON-NLS-1$
                    new Functor("getMin"),                //$NON-NLS-1$
                    new Functor("getMax"),                //$NON-NLS-1$
                    new Functor("getStandardDeviation"),  //$NON-NLS-1$
                    new Functor("getPercentPoint",new Object[] {0.95}),  //$NON-NLS-1$
                    new Functor("getErrorPercentage"),    //$NON-NLS-1$
                    new Functor("getRate"),               //$NON-NLS-1$
                    new Functor("getLastTenRate"),		  //$NON-NLS-1$
                    new Functor("getKBPerSecond"),        //$NON-NLS-1$
                    new Functor("getAvgPageBytes"),       //$NON-NLS-1$
                },
                new Functor[] { null, null, null, null, null, null, null, null , null, null,null,null,null },
                new Class[] { String.class, Long.class, Long.class, Long.class,Long.class, Long.class,
                              String.class,String.class, String.class, String.class, String.class,
                              String.class, String.class });
        clearData();
        init();
		 //register teststatelistener
		log.info(">>>> Register for test state listener");
        StandardJMeterEngine.register(GraphiteListener.this);
    }

    /** @deprecated - only for use in testing */
    @Deprecated
    public static boolean testFunctors(){
        GraphiteListener instance = new GraphiteListener();
        return instance.model.checkFunctors(null,instance.getClass());
    }

    @Override
    public String getLabelResource() {
        return "Graphite Listener";  //$NON-NLS-1$
    }
    
    @Override
    public String getStaticLabel() {
    	return "Graphite Listener";
    }

    @Override
    public void add(final SampleResult res) {
        final String sampleLabel = res.getSampleLabel(useGroupName.isSelected());
         
        JMeterUtils.runSafe(new Runnable() {
            @Override
            public void run() {
                SamplingStatCalculatorExt row = null;
                synchronized (lock) {
                    row = tableRows.get(sampleLabel);
                    if (row == null) {
                        row = new SamplingStatCalculatorExt(sampleLabel);
                        tableRows.put(row.getLabel(), row);
                        model.insertRow(row, model.getRowCount() - 1);
                    }
                }
                /*
                 * Synch is needed because multiple threads can update the counts.
                 */
                synchronized(row) {
                    row.addSample(res);
                }
                SamplingStatCalculatorExt tot = tableRows.get(TOTAL_ROW_LABEL);
                synchronized(tot) {
                    tot.addSample(res);
                }
                model.fireTableDataChanged();
                
            	synchronized(collectorLock) {
            		int activeThreadCount = JMeterContextService.getNumberOfThreads();
            		if(!isCollectorRunning && activeThreadCount > 0) {
            			log.info("Scheduling executor threads");
            			scheduler = Executors.newSingleThreadScheduledExecutor();
            			scheduler.scheduleWithFixedDelay(new JMeterDataCollectorService(collectedData,myJTable),
            						1,POLLING_RATE,TimeUnit.SECONDS);
            			scheduler.scheduleWithFixedDelay(new JMeterDataPublishingService(collectedData),10,PUBLISH_RATE,TimeUnit.SECONDS);
            			isCollectorRunning = true;
            		}
            		else if(isCollectorRunning && activeThreadCount == 0) {
        				log.info("active thread count == 0, shutting down collector and publisher");
            			testEnded();
            		}
            		else {}
            	}            
            }
        });
    }
    
    @Override
	public void testEnded() {
		isCollectorRunning = false;
		log.info("test complete notification" );
		try {
			if(null != scheduler && !scheduler.isShutdown()) {
				log.info("Started thread termination");
				// Wait a while for existing tasks to terminate
				if (!scheduler.awaitTermination(3,TimeUnit.SECONDS)) {
					log.info("Trying to shutdown ..");
					scheduler.shutdownNow(); // Cancel currently
																// executing tasks
					// Wait a while for tasks to respond to being cancelled
					if (!scheduler.awaitTermination(3,TimeUnit.SECONDS))
						log.error("Pool did not terminate");
					else
						log.info("Pool shutdown successfully");
				}
				log.info("setRunning(false,*local*)");
			}
		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			scheduler.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}
	}
    
    @Override
	public void testStarted() {
    	
    	String endPoint   = endPointTextField.getText();
    	String folderName = folderTextField.getText();
    	
    	 if(null != endPoint && endPoint.contains(":")) {
    		 log.info("endpoint provided : " + endPoint);
         	 String[] hostTokens = endPoint.split(":");
         	 log.info("Tokens in hosttokens : "+ hostTokens);
         	 if(hostTokens.length != 2 || !StringUtils.isNumeric(hostTokens[1])) {
         		StandardJMeterEngine.stopThreadNow(Thread.currentThread().getName());
         		log.error("Invalid input, usage host:port");
         	 } else { 
         		configurer.setHost(hostTokens[0], Integer.parseInt(hostTokens[1]));
         		log.info("Setting the endpoint to host : " +hostTokens[0] + " port : " + hostTokens[1]);
         	 }
         } else {
      		StandardJMeterEngine.stopThreadNow(Thread.currentThread().getName());
      		log.error("Invalid input, usage host:port");         }
         
         if(folderName == null || folderName.isEmpty()) {
      		StandardJMeterEngine.stopThreadNow(Thread.currentThread().getName());
      		log.error("Invalid input, usage host:port");
      	 }
         configurer.setFolderName(folderName);
         log.info("Setting Graphite folder name : " + folderName);

         System.out.println("REgister listener");
         StandardJMeterEngine.register(GraphiteListener.this);

		
	}

	@Override
	public void testStarted(String host) {
		// TODO Auto-generated method stub
	}

	@Override
	public void testEnded(String host) {
		testEnded();
	}

    /**
     * Clears this visualizer and its model, and forces a repaint of the table.
     */
    @Override
    public void clearData() {
        //Synch is needed because a clear can occur while add occurs
        synchronized (lock) {
            model.clearData();
            tableRows.clear();
            tableRows.put(TOTAL_ROW_LABEL, new SamplingStatCalculatorExt(TOTAL_ROW_LABEL));
            model.addRow(tableRows.get(TOTAL_ROW_LABEL));
        }
    }

    /**
     * Main visualizer setup.
     */
    private void init() {
        this.setLayout(new BorderLayout());

        // MAIN PANEL
        JPanel mainPanel = new JPanel();
        Border margin = new EmptyBorder(10, 10, 5, 10);

        mainPanel.setBorder(margin);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(makeTitlePanel());
        
        /**
         * Add support for Graphite configuration
         * 
         */
       
        final JPanel credentialPanel = new JPanel();
        credentialPanel.setBorder(margin);
        credentialPanel.setBorder(BorderFactory.createTitledBorder(configurationFieldset));
        credentialPanel.setLayout(new BoxLayout(credentialPanel,BoxLayout.Y_AXIS));
        createCredentialsForm(credentialPanel);
        mainPanel.add(credentialPanel);
        
        
        final JPanel collectedDataPanel = new JPanel();
        collectedDataPanel.setLayout(new BorderLayout());
        
        myJTable = new JTable(model);
        myJTable.getTableHeader().setDefaultRenderer(new HeaderAsPropertyRenderer());
        myJTable.setPreferredScrollableViewportSize(new Dimension(500, 70));
        RendererUtils.applyRenderers(myJTable, RENDERERS);
        myScrollPane = new JScrollPane(myJTable);
        
        
        this.add(mainPanel, BorderLayout.NORTH);
        collectedDataPanel.add(myScrollPane,BorderLayout.CENTER);
        this.add(collectedDataPanel, BorderLayout.CENTER);
        saveTable.addActionListener(this);
        JPanel opts = new JPanel();
        opts.add(useGroupName, BorderLayout.WEST);
        opts.add(saveTable, BorderLayout.CENTER);
        opts.add(saveHeaders, BorderLayout.EAST);
        this.add(opts,BorderLayout.SOUTH);
        
       
    }
    

    private void createCredentialsForm(JPanel credentialPanel) {
    	
    	graphiteDataHelpLabel.setForeground(Color.RED);
    	folderTextField.setToolTipText("Usage : 'foo.bar.baz' , this would create the same folder hirearchy in graphite");
    	
    	
    	JPanel folderPanel = new JPanel();
    	folderPanel.setLayout(new BorderLayout());
    	graphiteDataLocation.setBorder(new EmptyBorder(0,0,10,0));
    	folderPanel.add(graphiteDataLocation,BorderLayout.WEST);
    	folderPanel.add(folderTextField,BorderLayout.CENTER);
    	folderPanel.add(graphiteDataHelpLabel,BorderLayout.EAST);
    	folderTextField.setEnabled(true);
    	credentialPanel.add(folderPanel);
    	
    	
    	graphiteEndPointHelpLabel.setForeground(Color.RED);
    	endPointTextField.setToolTipText("Valid end point, hostname:port");
    	
    	JPanel endPointPanel = new JPanel();
    	endPointPanel.setLayout(new BorderLayout());
    	endPointFormatLabel.setBorder(new EmptyBorder(0,0,10,0));
    	endPointPanel.add(endPointFormatLabel,BorderLayout.WEST);
    	endPointPanel.add(endPointTextField,BorderLayout.CENTER);
    	endPointPanel.add(graphiteEndPointHelpLabel,BorderLayout.EAST);
    	endPointTextField.setEnabled(true);
    	credentialPanel.add(endPointPanel);
    	
	}

	@Override
    public void modifyTestElement(TestElement c) {
        super.modifyTestElement(c);
        c.setProperty(USE_GROUP_NAME, useGroupName.isSelected(), false);
        c.setProperty(SAVE_HEADERS, saveHeaders.isSelected(), true);
        c.setProperty(SAVE_ENDPOINT,endPointTextField.getText());
        c.setProperty(SAVE_FOLDERNAME,folderTextField.getText());
        
    }

    @Override
    public void configure(TestElement el) {
        super.configure(el);
        useGroupName.setSelected(el.getPropertyAsBoolean(USE_GROUP_NAME, false));
        saveHeaders.setSelected(el.getPropertyAsBoolean(SAVE_HEADERS, true));
        endPointTextField.setText(el.getPropertyAsString(SAVE_ENDPOINT));
        folderTextField.setText(el.getPropertyAsString(SAVE_FOLDERNAME));	

    }
    
    

    @Override
    public void actionPerformed(ActionEvent ev) {
        if (ev.getSource() == saveTable) {
            JFileChooser chooser = FileDialoger.promptToSaveFile("summary.csv");//$NON-NLS-1$
            if (chooser == null) {
                return;
            }
            FileWriter writer = null;
            try {
                writer = new FileWriter(chooser.getSelectedFile());
                CSVSaveService.saveCSVStats(model,writer, saveHeaders.isSelected());
            } catch (FileNotFoundException e) {
                log.warn(e.getMessage());
            } catch (IOException e) {
                log.warn(e.getMessage());
            } finally {
                JOrphanUtils.closeQuietly(writer);
            }
        }
            
    }
    
    private static void validate(JTextField ... varargs) {
        for(JTextField textField : varargs) {
            if(isNullOrEmpty(textField.getText())) {
                JOptionPane.showMessageDialog(null, "highlighted field cannot be empty","Alert!"
                        , JOptionPane.ERROR_MESSAGE);
                textField.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                return;
            }
        }
    }
    
    private static boolean isNullOrEmpty(String s) {
        return (s == null || s.isEmpty());
    }

	@Override
	public void tableChanged(TableModelEvent e) {
		int row = e.getFirstRow();
		int col = e.getColumn();
		
		if(row < 0 || col < 0)
			return;
		log.info("Is cell change : [" + row + " , " + col + "]");
		applyTableCellRenderer();
		
	}
	
	private void applyTableCellRenderer() {
		for(Integer rowIndx : rowColMatrix.keySet()) {
			Integer[] cols = rowColMatrix.get(rowIndx);
			for(int i = 0 ; i < cols.length; i++) {
				Integer col = cols[i];
				log.info("rowIndx : " + rowIndx + " I : " +i + " col : " +col);
				log.info("Value : " +myJTable.getModel().getValueAt(rowIndx, i));
				if(col != null) {
					final Component c				   =  myJTable
							.getCellRenderer(rowIndx, i)
							.getTableCellRendererComponent(myJTable, null, false, false, rowIndx, i);
					c.setBackground(Color.GREEN);
					
				}
				else {
					final Component c				   =  myJTable
							.getCellRenderer(rowIndx, i)
							.getTableCellRendererComponent(myJTable,null, false, false, rowIndx, i);
					c.setBackground(Color.WHITE);
				}
				myJTable.updateUI();
			}
		}
	}
}
