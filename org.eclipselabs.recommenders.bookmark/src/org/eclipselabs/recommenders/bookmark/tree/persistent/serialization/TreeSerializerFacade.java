package org.eclipselabs.recommenders.bookmark.tree.persistent.serialization;

import java.io.File;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.persistent.BookmarkFileIO;
import org.eclipselabs.recommenders.bookmark.tree.persistent.GsonConverter;
import org.eclipselabs.recommenders.bookmark.tree.persistent.ObjectConverter;
import org.eclipselabs.recommenders.bookmark.view.ViewManager;

public class TreeSerializerFacade
{

	public static void serialize(TreeModel model, Object[] expandedNodes,
			File file, ViewManager manager)
	{

		ObjectConverter converter = new GsonConverter();
		String serializedTree = TreeSerializer.serializeTree(
				model.getModelRoot(), expandedNodes, converter);
		if (serializedTree != null) {
			BookmarkFileIO.writeSerializedTreeToFile(file, serializedTree);

			// when called as export the view state is not exported and the
			// manager might be null
			if (manager != null) {
				BookmarkFileIO.appendFlatAndToggledStateToFile(file, manager);
			}
		}
	}

	public static void serializeToDefaultLocation(ViewManager manager)
	{
		TreeModel model = manager.getModel();
		TreeViewer viewer = manager.getActiveBookmarkView().getView();

		ObjectConverter converter = new GsonConverter();
		String serializedTree = TreeSerializer.serializeTree(
				model.getModelRoot(), viewer.getExpandedElements(), converter);
		if (serializedTree != null) {
			File file = Activator.getDefaultLocationForStoringBookmark();
			BookmarkFileIO.writeSerializedTreeToFile(file, serializedTree);
			BookmarkFileIO.appendFlatAndToggledStateToFile(file, manager);
		}
	}

}
