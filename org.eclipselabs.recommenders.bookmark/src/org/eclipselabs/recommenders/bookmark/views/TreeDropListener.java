package org.eclipselabs.recommenders.bookmark.views;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
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
		
		if (data instanceof String) {
			String dataString = (String) data;
			if (nodeTarget != null) {
				createNewNodeAndAddAsChildToTargetNode(dataString);
			} else {
				createNewTopLevelNode(dataString);
			}
		}
		
		if (data instanceof String[]) {
			int a = 0;
		}

		if (data instanceof IResource[]) {
			IResource[] resources = (IResource[]) data;

			for (IResource res : resources) {
				if (nodeTarget != null) {
					createNewNodeAndAddAsChildToTargetNode(res.getFullPath()
							.toString());
				} else {
					createNewTopLevelNode(res.getFullPath().toString());
				}
			}
			nodeTarget = null;
		}

		if (data instanceof TreeNode) {
			TreeNode node = (TreeNode) data;
			if (nodeTarget != null) {
				createNewNodeAndAddAsChildToTargetNode(node.getName());
				nodeTarget = null;
			} else {
				createNewTopLevelNode(node.getName());
			}
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