package org.eclipselabs.recommenders.bookmark.view.save_restore;

import java.io.IOException;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.tree.util.GsonConverter;
import org.eclipselabs.recommenders.bookmark.tree.util.ObjectConverter;
import org.eclipselabs.recommenders.bookmark.tree.util.RestoredTree;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeDeserializer;

public class LoadBookmarksFromLocalDefaultFile {
	private TreeModel model;
	private TreeViewer viewer;

	public LoadBookmarksFromLocalDefaultFile(TreeViewer viewer, TreeModel model) {
		this.viewer = viewer;
		this.model = model;
	}

	public void load() throws IOException {
		String[] lines = BookmarkLoader.loadDefaultFile();

		String serializedTree = lines[0];

		ObjectConverter converter = new GsonConverter();
		RestoredTree rstTree = TreeDeserializer.deSerializeTree(serializedTree,
				converter);
		TreeNode newRoot = rstTree.getRoot();

		model.setModelRoot(newRoot);
		viewer.setInput(model.getModelRoot());
		
		viewer.setExpandedElements(rstTree.getExpanded());

//		for (TreeNode expanded : rstTree.getExpanded()){
//			viewer.expandToLevel(expanded, AbstractTreeViewer.ALL_LEVELS);
//			viewer.setExpandedState(expanded, true);
//		}
				
		

//		viewer.getTree().redraw();
//		viewer.refresh();
	}
}
