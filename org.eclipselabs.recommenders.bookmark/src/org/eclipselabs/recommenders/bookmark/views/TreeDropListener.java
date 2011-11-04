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

		super.drop(event);
	}

	@Override
	public boolean performDrop(Object data) {

		if (getCurrentTarget() instanceof TreeNode)
			nodeTarget = (TreeNode) getCurrentTarget();

		// String [] hm = (String [])data;

		if (nodeTarget != null) {
			if (data instanceof String)
				createNewNodeAndAddAsChildToTargetNode((String) data);
			else if (data instanceof String[])
				createNewNodeAndAddAsChildToTargetNode((String[]) data);
			nodeTarget = null;
		} else {
			if (data instanceof String)
				createNewTopLevelNode((String) data);
			else if (data instanceof String[])
				createNewTopLevelNode((String[]) data);
		}

		return false;
	}

	private void createNewTopLevelNode(String[] data) {

		for (String string : data) {
			createNewTopLevelNode(string);
		}

	}

	private void createNewNodeAndAddAsChildToTargetNode(String[] data) {
		for (String string : data) {
			createNewNodeAndAddAsChildToTargetNode(string);
		}

	}

	private void createNewTopLevelNode(String data) {
		TreePath[] treeExpansion = viewer.getExpandedTreePaths();
		model.getModelRoot().addChild(new TreeNode((String) data));

		viewer.refresh();

		viewer.setExpandedTreePaths(treeExpansion);
	}

	private void createNewNodeAndAddAsChildToTargetNode(String data) {
		TreeNode newNode = new TreeNode(data);
		newNode.setParent(nodeTarget);
		nodeTarget.addChild(newNode);

		TreePath[] treeExpansion = viewer.getExpandedTreePaths();

		viewer.refresh();

		viewer.setExpandedTreePaths(treeExpansion);

	}

	@Override
	public boolean validateDrop(Object target, int operation,
			TransferData transferType) {

		return true;

	}

}