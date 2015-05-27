import re, logging
from plugins.outputs.PerformanceStats import *
from plugins.outputs.JMeterPerformanceStats import JMeterMetric

logger = logging.getLogger('parsible')

#pattern to tell if this is an XML element
xmlPattern = '<.*?sample.*?>$'
reXmlPattern = re.compile(xmlPattern, re.IGNORECASE)

#the current element we're processing
currElePattern = '<(\w+)\s+'
reCurrElePattern = re.compile(currElePattern)

#was this element closed, does it have a trailing />
eleClosedPattern = '/.*?>$'
reEleClosedPattern = re.compile(eleClosedPattern)

#get the element's key value attributes
xmlKeyValPattern = '(\w+)=\"(.*?)\"'
reXmlKeyValPattern = re.compile(xmlKeyValPattern)


#a CSV line
csvPattern = '\s*\w+,'
reCsvPattern = re.compile(csvPattern)

#our CSV Header
csvHeader = []

#the currently open XML elements
currentElement = ""


def parseCsvLine(line):
    global csvHeader

    print "Apply CSV Pattern to Line: %s" % (line)

    fields = line.split(',')

    if len(csvHeader) == 0:
        csvHeader=fields
    else:
        col=0
        row=""
        for f in fields:
            row = '%s=%s | ' % (csvHeader[col], fields[col])               
            col += 1
        print row

def parseXmlLine(line, statPrefix):
    global xmlElementsOpen, currentElement

    #protect against empty string stat prefixes
    if (statPrefix is not None and len(statPrefix) <= 0):
        statPrefix = None

    #logger.debug("Parsing XML Line %s" % line)

    #for now only support processing outer sample elements, and exclude things like redirects
    #and forwards. Also make sure they we have attributes on the line, avoiding lines that
    #don't contain valid jmeter stats
    if ((currentElement is None or len(currentElement) == 0) and reXmlKeyValPattern.findall(line)):
        #logger.debug("Processing XML Element %s" % line)
    
        #get all of the key=val attribugtes from the current line
        fields=reXmlKeyValPattern.findall(line)
        
        #map the fields to a dictionary
        fieldsDict = {}
        for f in fields:
            key = f[0]
            val = f[1]
            
            fieldsDict[key] = val

        #create a new Metric Instance for our jmeter element
        metric = JMeterMetric(fieldsDict['lb'])
        metric.metricClass = statPrefix
        
        #set our timestampe
        metric.setTimeMs(fieldsDict['ts'])
            
        for f in fields:
            sName = f[0]
            sVal = f[1]
    
            metric.addValue(sName, sVal)
        
        metric.dumpMetricStats()

        #if the element is closed then we won't consider this to be the current open element
        if (reEleClosedPattern.search(line)):
            currentElement = None
        else:
            #else if the element was not closed then identify the element name
            m = reCurrElePattern.match(line)
            currentElement = m.groups(1)
        
        return metric

    else:
        #does this line terminate the currently open element
        regex="</%s>"%(currentElement)

        if re.match(regex, line):
            currentElement = ""

        return None

#this parses a line from the jmeter results
def parse_jmeter(line, prefix):
    global logger

    line=line.rstrip('\r\n')

    #logger.debug("[parse_jmeter] Processing %s" % line)

    if reXmlPattern.search(line):
        #return a MetricInstance or None depending 
        return parseXmlLine(line, prefix)
        
