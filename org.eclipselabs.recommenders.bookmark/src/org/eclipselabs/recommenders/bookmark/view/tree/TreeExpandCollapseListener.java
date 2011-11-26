package org.eclipselabs.recommenders.bookmark.view.tree;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.view.save_restore.SaveBookmarksToLocalDefaultFile;

public class TreeExpandCollapseListener implements TreeListener {

	private TreeViewer viewer;
	private TreeModel model;

	public TreeExpandCollapseListener(TreeViewer viewer, TreeModel model) {
		this.viewer = viewer;
		this.model = model;
	}

	@Override
	public void treeCollapsed(TreeEvent e) {
//		viewer.getTree().redraw();
//		viewer.getTree().update();
//		new SaveBookmarksToLocalDefaultFile(viewer, model).saveCurrentState();
//		viewer.getTree().redraw();
//		viewer.getTree().update();
	}

	@Override
	public void treeExpanded(TreeEvent e) {
//		viewer.getTree().redraw();
//		viewer.getTree().update();
//		new SaveBookmarksToLocalDefaultFile(viewer, model).saveCurrentState();
//		viewer.getTree().redraw();
//		viewer.getTree().update();
	}

}
