package org.eclipselabs.recommenders.bookmark.view.actions;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.tree.persistent.BookmarkFileIO;
import org.eclipselabs.recommenders.bookmark.tree.persistent.deserialization.RestoredTree;
import org.eclipselabs.recommenders.bookmark.tree.persistent.deserialization.TreeDeserializerFacade;
import org.eclipselabs.recommenders.bookmark.tree.persistent.serialization.TreeSerializerFacade;
import org.eclipselabs.recommenders.bookmark.view.dialog.ImportDialog;

public class ImportBookmarksAction extends Action  {

	private TreeViewer viewer;
	private TreeModel model;

	public ImportBookmarksAction(TreeViewer viewer, TreeModel model) {
		super();
		this.viewer = viewer;
		this.model = model;

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
		TreeSerializerFacade.serializeToDefaultLocation(viewer, model);		
	}

	private TreeNode deSerializeTreeAndImportBookmarks(String serializedTree) {

		RestoredTree rstTree = TreeDeserializerFacade
				.deserialize(serializedTree);
		TreeNode newRoot = rstTree.getRoot();

		TreeNode existingTreesRoot = model.getModelRoot();

		for (TreeNode bookmarks : newRoot.getChildren()) {
			existingTreesRoot.addChild(bookmarks);
		}

		viewer.refresh();

		TreeDeserializerFacade.setExpandedNodesForView(viewer,
				rstTree.getExpanded());

		return newRoot;
	}

}
