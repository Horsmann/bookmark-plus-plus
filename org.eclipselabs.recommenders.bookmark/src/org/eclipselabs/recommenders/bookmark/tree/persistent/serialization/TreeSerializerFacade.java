package org.eclipselabs.recommenders.bookmark.tree.persistent.serialization;

import java.io.File;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.persistent.BookmarkFileIO;
import org.eclipselabs.recommenders.bookmark.tree.persistent.GsonConverter;
import org.eclipselabs.recommenders.bookmark.tree.persistent.ObjectConverter;

public class TreeSerializerFacade
{

	public static void serialize(TreeModel model, Object[] expandedNodes,
			File file)
	{

		ObjectConverter converter = new GsonConverter();
		String serializedTree = TreeSerializer.serializeTree(
				model.getModelRoot(), expandedNodes, converter);
		if (serializedTree != null) {
			BookmarkFileIO.writeToFile(file, serializedTree);
		}
	}

	public static void serializeToDefaultLocation(TreeViewer viewer,
			TreeModel model)
	{
		ObjectConverter converter = new GsonConverter();
		String serializedTree = TreeSerializer.serializeTree(
				model.getModelRoot(), viewer.getExpandedElements(), converter);
		if (serializedTree != null) {
			File file = Activator.getDefaultLocationForStoringBookmark();
			BookmarkFileIO.writeToFile(file, serializedTree);
		}
	}

}
