package org.eclipselabs.recommenders.bookmark.tree.util;

public interface ObjectConverter {
	
	
	public String convertToString(Object object, Object [] misc);
	
	public Object [] convertToObject(String string);

}
