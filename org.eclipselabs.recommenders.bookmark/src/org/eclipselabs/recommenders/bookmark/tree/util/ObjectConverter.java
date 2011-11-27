package org.eclipselabs.recommenders.bookmark.tree.util;

public interface ObjectConverter {
	
	
	public String convertToString(Object[] values);
	
	public Object [] convertToObject(String string);

}
