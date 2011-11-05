package org.eclipselabs.recommenders.bookmark.tree;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;

public class TreeDropListener extends ViewerDropAdapter {

	private final TreeViewer viewer;
	private TreeModel model;
	private TreeNode targetNode = null;

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
			targetNode = (TreeNode) getCurrentTarget();

		if (getSelectedObject() instanceof TreeNode && targetNode != null) {
			TreeNode sourceNode = (TreeNode) getSelectedObject();

			sourceNode.getParent().removeChild(sourceNode);

			sourceNode.setParent(targetNode);
			targetNode.addChild(sourceNode);

			return true;
		}

		if (data instanceof String) {
			String dataString = (String) data;
			if (targetNode != null) {
				createNewNodeAndAddAsChildToTargetNode(dataString);
			} else {
				createNewTopLevelNode(dataString);
			}
		}

		if (data instanceof IResource[]) {
			IResource[] resources = (IResource[]) data;

			for (IResource res : resources) {
				if (targetNode != null) {
					createNewNodeAndAddAsChildToTargetNode(res.getFullPath()
							.toString());
				} else {
					createNewTopLevelNode(res.getFullPath().toString());
				}
			}
			targetNode = null;
		}

		return false;
	}

	private void createNewTopLevelNode(String data) {
		TreePath[] treeExpansion = viewer.getExpandedTreePaths();
		model.getModelRoot().addChild(new TreeNode((String) data));

		viewer.refresh();

		viewer.setExpandedTreePaths(treeExpansion);
	}

	private void createNewNodeAndAddAsChildToTargetNode(String data) {
		TreeNode newNode = new TreeNode(data);
		newNode.setParent(targetNode);
		targetNode.addChild(newNode);

		TreePath[] treeExpansion = viewer.getExpandedTreePaths();

		viewer.refresh();

		viewer.setExpandedTreePaths(treeExpansion);

	}

	@Override
	public boolean validateDrop(Object target, int operation,
			TransferData transferType) {

		IStructuredSelection selection = (IStructuredSelection) viewer
				.getSelection();

		// The drag is not performed on the tree
		if (selection.getFirstElement() == null)
			return true;
		// The bookmarks head node can't become a child node
		TreeNode node = ((TreeNode) getSelectedObject()).getParent();
		if (node != null && node == model.getModelRoot())
			return false;

		return true;

	}

}