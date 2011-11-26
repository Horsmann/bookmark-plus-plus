package org.eclipselabs.recommenders.bookmark.tree.util;

import java.lang.reflect.Type;

import org.eclipselabs.recommenders.bookmark.tree.TreeNode;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GsonConverter implements ObjectConverter {

	@Override
	public String convertToString(Object object, Object [] expanded) {
		
		TreeNode preSerialized = (TreeNode) object;
		
		Gson gson = new Gson();
		Type typeOfSrc = new TypeToken<TreeNode>() {
		}.getType();
		String gsonTreeString = gson.toJson(preSerialized, typeOfSrc);
		
		if (expanded != null && expanded.length > 0) {
			gsonTreeString += "\n";

			for (int i = 0; i < expanded.length; i++) {
				TreeNode node = (TreeNode) expanded[i];
				String id = TreeValueConverter.getStringIdentification(node
						.getValue());
				gsonTreeString += id;
				if (i + 1 < expanded.length)
					gsonTreeString += ";";
			}
		}
		
		return gsonTreeString;
	}

	@Override
	public Object [] convertToObject(String string) {
		Gson gson = new Gson();
		Type typeOfSrc = new TypeToken<TreeNode>() {
		}.getType();
		TreeNode rootNode = gson.fromJson(string, typeOfSrc);

		TreeNode deSerializedTreesRoot = TreeDeSerializer.deSerializeTreeValues(rootNode);
		return new Object[]{deSerializedTreesRoot};
	}

}
