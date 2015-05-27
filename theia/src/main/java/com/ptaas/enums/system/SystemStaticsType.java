/**
 * 
 */
package com.ptaas.enums.system;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ptaas.enums.EnumBase;
import com.ptaas.util.CommonUtils;


/**
 * @author gthattiyottu
 * 
 */
public enum SystemStaticsType implements EnumBase<SystemStaticsType> {
	CPU("cpu"),DISK("disk"),MEMORY("memory"),FILE_SYSTEM("file_system"),NET("net"),PROCESS("process"),VM("vm"),STAR("*");
	private String	text;
	private static final Map<SystemStaticsType,List<String>> mapOfEnum=new HashMap<SystemStaticsType,List<String>>();
	static
	{
		mapOfEnum.put(CPU, SystemCPUStatisticsType.CPU.getAllValues());
		mapOfEnum.put(MEMORY, SystemMemoryStatisticsType.MEMORY.getAllValues());
		mapOfEnum.put(FILE_SYSTEM, SystemFileSystemStatisticsType.JFSFILE.getAllValues());
		mapOfEnum.put(NET, SystemNetworkStatisticsType.NETWORK.getAllValues());
		mapOfEnum.put(DISK, SystemDiskStatisticsType.DISK_BUSY.getAllValues());
		mapOfEnum.put(PROCESS, SystemProcessStatisticsType.PROCESS.getAllValues());
		mapOfEnum.put(VM, SystemVirtuaMachineStatisticsType.VIRTUAL_MACHINE.getAllValues());
		List<String> starList=new ArrayList<String>();
		starList.add("*");
		mapOfEnum.put(STAR,starList);
	}
	SystemStaticsType(String text) {
		this.text = text!=null?text.toLowerCase():text;
	}
	
	public String getText() {
		return this.text;
	}
	
	public static SystemStaticsType fromString(String text) {
		return CommonUtils.getEnumFromString(SystemStaticsType.class, text);
	}
	
	public String toString() {
		return getText();
	}
	@Override
	public List<SystemStaticsType> getAll() {
		// TODO Auto-generated method stub
		List<SystemStaticsType> result= new ArrayList<SystemStaticsType>(EnumSet.allOf(SystemStaticsType.class));
		result.remove(SystemStaticsType.STAR);
		return result;
	}
	@Override
	public List<String> getAllValues() {
		List<String> result= new ArrayList<String>();
		List<SystemStaticsType> getAllValues=getAll();
		for (SystemStaticsType systemStaticsType : getAllValues) {
			result.add(systemStaticsType.toString());
		}
		// TODO Auto-generated method stub
		return result;
	}
	public List<String> getTargets(List<String> stringList)
	{
		List<String> result=new ArrayList<String>();
		for (String string : stringList) {
			if(mapOfEnum.get(fromString(string))!=null)
			{
				List<String> intermediateList=mapOfEnum.get(fromString(string));
				for (String string1 : intermediateList) {
					result.add(string1+".*");
					result.add(string1+".*%");
				}
			}
			
		}		
		return result;
	}
}
