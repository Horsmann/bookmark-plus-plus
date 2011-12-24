package org.eclipselabs.recommenders.bookmark.view.actions;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.importWizards.ImportDialog;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.persistent.BookmarkFileIO;
import org.eclipselabs.recommenders.bookmark.tree.persistent.deserialization.RestoredTree;
import org.eclipselabs.recommenders.bookmark.tree.persistent.deserialization.TreeDeserializerFacade;
import org.eclipselabs.recommenders.bookmark.tree.persistent.serialization.TreeSerializerFacade;
import org.eclipselabs.recommenders.bookmark.view.BookmarkView;

public class ImportBookmarksAction extends Action {

	private BookmarkView viewer;

	public ImportBookmarksAction(BookmarkView viewer) {
		super();
		this.viewer = viewer;

		this.setImageDescriptor(Activator.getDefault().getImageRegistry()
				.getDescriptor(Activator.ICON_LOAD_BOOKMARKS));
		this.setToolTipText("Imports bookmarks from an external source");
		this.setText("Import Bookmarks");
	}

	@Override
	public void run() {

		File file = ImportDialog.showDialog();
		if (file != null) {

			String[] lines = BookmarkFileIO.readFromFile(file);

			String serializedTree = lines[0];
			deSerializeTreeAndImportBookmarks(serializedTree);

			saveNewTreeModelState();
		}

	}

	private void saveNewTreeModelState() {
		TreeSerializerFacade.serializeToDefaultLocation(viewer.getView(),
				viewer.getModel());
	}

	private BMNode deSerializeTreeAndImportBookmarks(String serializedTree) {

		RestoredTree rstTree = TreeDeserializerFacade
				.deserialize(serializedTree);
		BMNode newRoot = rstTree.getRoot();

		BMNode existingTreesRoot = viewer.getModel().getModelRoot();

		for (BMNode bookmarks : newRoot.getChildren()) {
			existingTreesRoot.addChild(bookmarks);
		}

		viewer.getView().refresh();

		TreeDeserializerFacade.setExpandedNodesForView(viewer.getView(),
				rstTree.getExpanded());

		return newRoot;
	}

}
