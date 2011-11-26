package org.eclipselabs.recommenders.bookmark.view.actions;

import java.io.File;
import java.io.IOException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeDeSerializer;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.view.save_restore.BookmarkLoader;

public class ImportBookmarksAction extends Action {

	private TreeViewer viewer;
	private TreeModel model;

	public ImportBookmarksAction(TreeViewer viewer, TreeModel model) {
		super();
		this.viewer = viewer;
		this.model = model;

		this.setImageDescriptor(Activator.getDefault().getImageRegistry()
				.getDescriptor(Activator.ICON_LOAD_BOOKMARKS));
	}

	@Override
	public void run() {

		File file = showFileOpenDialog();
		if (file != null) {

			String[] lines = loadFile(file);

			if (lines.length != 2)
				return;

			String serializedTree = lines[0];
			String expandedNodes = lines[1];

			TreeNode newRoot = deSerializeTreeAndImportBookmarks(serializedTree);

			restoreExpandedStateOfAddedBookmarks(expandedNodes, newRoot);

		}

	}

	private void restoreExpandedStateOfAddedBookmarks(String expandedNodes,
			TreeNode newRoot) {
		String[] idsExpandedNodes = expandedNodes.split(";");
		for (String id : idsExpandedNodes) {
			TreeNode node = TreeUtil.locateNodeWithEqualID(id, newRoot);
			if (node != null)
				viewer.setExpandedState(node, true);
		}

		viewer.refresh();
	}

	private TreeNode deSerializeTreeAndImportBookmarks(String serializedTree) {
		TreeNode newRoot = TreeDeSerializer
				.deSerializeTreeFromGSonString(serializedTree);

		TreeNode existingTreesRoot = model.getModelRoot();
		for (TreeNode bookmarks : newRoot.getChildren())
			existingTreesRoot.addChild(bookmarks);

		viewer.refresh();
		return newRoot;
	}

	private String[] loadFile(File file) {
		try {
			return BookmarkLoader.loadFile(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private File showFileOpenDialog() {
		Shell shell = Display.getCurrent().getActiveShell();
		FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
		fileDialog.setFilterExtensions(new String[] { "*.bm" });
		String fileName = fileDialog.open();
		if (fileName != null)
			return new File(fileName);

		return null;
	}

}
