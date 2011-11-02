package org.eclipselabs.recommenders.bookmark.views;

import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;

public class TreeDropListener extends ViewerDropAdapter {

	private final TreeViewer viewer;
	private TreeModel model;
	private TreeNode nodeTarget = null;

	public TreeDropListener(TreeViewer viewer, TreeModel model) {
		super(viewer);
		this.viewer = viewer;
		this.model = model;
	}

	@Override
	public void drop(DropTargetEvent event) {
		int location = this.determineLocation(event);

		if (determineTarget(event) instanceof TreeNode)
			nodeTarget = (TreeNode) determineTarget(event);

		// String target = (String) determineTarget(event);
		String translatedLocation = "";
		switch (location) {
		case 1:
			translatedLocation = "Dropped before the target ";
			break;
		case 2:
			translatedLocation = "Dropped after the target ";
			break;
		case 3:
			translatedLocation = "Dropped on the target ";
			break;
		case 4:
			translatedLocation = "Dropped into nothing ";
			break;
		}
		System.err.println(translatedLocation);
		// System.err.println("The drop was done on the element: " + target);
		super.drop(event);
	}

	// This method performs the actual drop
	// We simply add the String we receive to the model and trigger a refresh of
	// the
	// viewer by calling its setInput method.
	@Override
	public boolean performDrop(Object data) {

		if (nodeTarget != null) {
			TreeNode newNode = new TreeNode((String) data);
			newNode.setParent(nodeTarget);
			nodeTarget.addChild(newNode);
			TreePath[] treeExpansion = viewer.getExpandedTreePaths();
			viewer.setInput(model.getModelRoot());
			viewer.setExpandedTreePaths(treeExpansion);
		} else {

			TreePath[] treeExpansion = viewer.getExpandedTreePaths();
			model.getModelRoot().addChild(new TreeNode((String) data));
			viewer.setInput(model.getModelRoot());

			if (treeExpansion != null)
				viewer.setExpandedTreePaths(treeExpansion);
		}
		return false;
	}

	@Override
	public boolean validateDrop(Object target, int operation,
			TransferData transferType) {
		return true;

	}

}