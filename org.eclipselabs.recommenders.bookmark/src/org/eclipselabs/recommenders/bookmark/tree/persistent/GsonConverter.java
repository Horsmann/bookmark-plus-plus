package org.eclipselabs.recommenders.bookmark.tree.persistent;

import java.lang.reflect.Type;

import org.eclipselabs.recommenders.bookmark.tree.persistent.serialization.SerializedTreeNode;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GsonConverter implements ObjectConverter {

	@Override
	public String convertToString(Object[] values) {
		
		SerializedTreeNode preSerialized = (SerializedTreeNode) values[0];
		
		Gson gson = new Gson();
		String gsonTreeString = gson.toJson(preSerialized, SerializedTreeNode.class);
		return gsonTreeString;
	}

	@Override
	public Object [] convertToObject(String string) {
		Gson gson = new Gson();
		SerializedTreeNode rootNode = gson.fromJson(string, SerializedTreeNode.class);

		return new Object[]{rootNode};
	}

}
