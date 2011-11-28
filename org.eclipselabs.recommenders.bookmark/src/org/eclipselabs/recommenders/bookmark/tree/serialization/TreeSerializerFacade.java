package org.eclipselabs.recommenders.bookmark.tree.serialization;

import java.io.File;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;

public class TreeSerializerFacade {

	public static void serialize(TreeViewer viewer, TreeModel model, File file) {

		ObjectConverter converter = new GsonConverter();
		String serializedTree = TreeSerializer.serializeTree(
				model.getModelRoot(), viewer.getExpandedElements(), converter);
		if (serializedTree != null) {
			BookmarkFileIO.writeToFile(file, serializedTree);
		}
	}
	
	public static void serializeToDefaultLocation(TreeViewer viewer, TreeModel model) {

		ObjectConverter converter = new GsonConverter();
		String serializedTree = TreeSerializer.serializeTree(
				model.getModelRoot(), viewer.getExpandedElements(), converter);
		if (serializedTree != null) {
			File file = Activator.getDefaultLocationForStoringBookmark();
			BookmarkFileIO.writeToFile(file, serializedTree);
		}
	}

}
