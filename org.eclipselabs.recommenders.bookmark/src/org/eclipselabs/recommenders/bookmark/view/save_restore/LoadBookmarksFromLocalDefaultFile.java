package org.eclipselabs.recommenders.bookmark.view.save_restore;

import java.io.IOException;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.tree.util.GsonConverter;
import org.eclipselabs.recommenders.bookmark.tree.util.ObjectConverter;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeDeSerializer;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;

public class LoadBookmarksFromLocalDefaultFile {
	private TreeModel model;
	private TreeViewer viewer;

	public LoadBookmarksFromLocalDefaultFile(TreeViewer viewer, TreeModel model) {
		this.viewer = viewer;
		this.model = model;
	}

	public void load() throws IOException {
		String[] lines = BookmarkLoader.loadDefaultFile();

		if (lines.length != 2)
			return;

		String serializedTree = lines[0];
		String expandedNodes = lines[1];

		ObjectConverter converter = new GsonConverter();
		TreeNode newRoot = TreeDeSerializer
				.deSerializeTree(serializedTree, converter);

		model.setModelRoot(newRoot);
		viewer.setInput(model.getModelRoot());

		String[] idsExpandedNodes = expandedNodes.split(";");
		for (String id : idsExpandedNodes) {
			TreeNode node = TreeUtil.locateNodeWithEqualID(id, newRoot);
			if (node != null)
				viewer.setExpandedState(node, true);
		}

		viewer.refresh();
	}
}
