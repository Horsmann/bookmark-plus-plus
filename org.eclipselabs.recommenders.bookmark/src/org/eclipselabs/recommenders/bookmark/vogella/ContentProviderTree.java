package org.eclipselabs.recommenders.bookmark.vogella;

import java.util.ArrayList;
import java.util.List;

public enum ContentProviderTree {
	INSTANCE;
	List<String> list = new ArrayList<String>();

	private ContentProviderTree() {
		list.add("Branch1");
		list.add("Branch2");
	}

	public List<String> getModel() {
		return list;
	}
}
