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
		if ((TreeNode) determineTarget(event) instanceof TreeNode)
			nodeTarget = (TreeNode) determineTarget(event);

		super.drop(event);
	}

	@Override
	public boolean performDrop(Object data) {

		if (nodeTarget != null)
			createNewNodeAndAddAsChildToTargetNode((String) data);
		else
			createNewTopLevelNode((String) data);

		return false;
	}

	private void createNewTopLevelNode(String data) {
		TreePath[] treeExpansion = viewer.getExpandedTreePaths();
		model.getModelRoot().addChild(new TreeNode((String) data));

		viewer.setInput(model.getModelRoot());

		viewer.setExpandedTreePaths(treeExpansion);
	}

	private void createNewNodeAndAddAsChildToTargetNode(String data) {
		TreeNode newNode = new TreeNode(data);
		newNode.setParent(nodeTarget);
		nodeTarget.addChild(newNode);

		TreePath[] treeExpansion = viewer.getExpandedTreePaths();

		viewer.setInput(model.getModelRoot());

		viewer.setExpandedTreePaths(treeExpansion);
		
		nodeTarget = null;
	}

	@Override
	public boolean validateDrop(Object target, int operation,
			TransferData transferType) {
		return true;

	}

}