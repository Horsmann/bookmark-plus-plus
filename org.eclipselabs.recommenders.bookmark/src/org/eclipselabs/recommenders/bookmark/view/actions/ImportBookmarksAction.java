package org.eclipselabs.recommenders.bookmark.view.actions;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.tree.util.GsonConverter;
import org.eclipselabs.recommenders.bookmark.tree.util.ObjectConverter;
import org.eclipselabs.recommenders.bookmark.tree.util.RestoredTree;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeDeserializer;
import org.eclipselabs.recommenders.bookmark.view.save_restore.BookmarkLoader;
import org.eclipselabs.recommenders.bookmark.view.save_restore.SaveBookmarksToLocalDefaultFile;

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

			String serializedTree = lines[0];

			deSerializeTreeAndImportBookmarks(serializedTree);
			new SaveBookmarksToLocalDefaultFile(viewer, model)
					.saveCurrentState();
		}

	}

	private TreeNode deSerializeTreeAndImportBookmarks(String serializedTree) {
		ObjectConverter converter = new GsonConverter();

		RestoredTree rstTree = TreeDeserializer.deSerializeTree(serializedTree,
				converter);
		TreeNode newRoot = rstTree.getRoot();

		TreeNode existingTreesRoot = model.getModelRoot();

		for (TreeNode bookmarks : newRoot.getChildren()) {
			existingTreesRoot.addChild(bookmarks);
		}
		
		viewer.refresh();

		LinkedList<TreeNode> exNodeList = new LinkedList<TreeNode>();
		for (Object node : viewer.getExpandedElements())
			exNodeList.add((TreeNode) node);

		for (TreeNode node : rstTree.getExpanded())
			exNodeList.add(node);


		viewer.setExpandedElements(exNodeList.toArray());

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
