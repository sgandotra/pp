package com.ptaas.enums;

import java.util.List;

public interface EnumBase<T> {
	public String getText();	
	public String toString();
	public List<T> getAll(); 
	public List<String> getAllValues();		
}
