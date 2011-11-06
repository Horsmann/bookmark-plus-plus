package org.eclipselabs.recommenders.bookmark.tree;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;

public class TreeDropListener implements DropTargetListener {

	private final TreeViewer viewer;
	private TreeModel model;
	private ReferenceNode targetNode = null;

	public TreeDropListener(TreeViewer viewer, TreeModel model) {
		// super(viewer);
		this.viewer = viewer;
		this.model = model;
	}

	@Override
	public void dragEnter(DropTargetEvent event) {
		if (event.detail == DND.DROP_NONE)
			event.detail = DND.DROP_LINK;
	}

	@Override
	public void dragLeave(DropTargetEvent event) {
	}

	@Override
	public void dragOperationChanged(DropTargetEvent event) {
	}

	@Override
	public void dragOver(DropTargetEvent event) {
	}

	@Override
	public void dropAccept(DropTargetEvent event) {

		// The bookmarks head node can't become a child node
		ReferenceNode node = (ReferenceNode) getSelectedObject();

		// Drag operation is performed somewhere outside our view
		if (node == null)
			return;

		if (node.getParent() == model.getModelRoot()) {
			event.detail = DND.DROP_NONE;
			return;
		}

		if (causesRecursion(node, (ReferenceNode) getTarget(event))) {
			event.detail = DND.DROP_NONE;
			System.err.println("Rekursion");
			return;
		}

	}

	private boolean causesRecursion(TreeNode source, TreeNode target) {

		TreeNode targetParent = target.getParent();

		if (targetParent == null)
			return false;
		else if (targetParent == source)
			return true;
		else
			return causesRecursion(source, targetParent);

	}

	@Override
	public void drop(DropTargetEvent event) {

		if (getTarget(event) instanceof ReferenceNode)
			targetNode = (ReferenceNode) getTarget(event);

		if (getSelectedObject() instanceof ReferenceNode && targetNode != null) {
			ReferenceNode sourceNode = (ReferenceNode) getSelectedObject();

			sourceNode.getParent().removeChild(sourceNode);

			sourceNode.setParent(targetNode);
			targetNode.addChild(sourceNode);

			return;
		}

		if (event.data instanceof IResource[]) {
			IResource[] resources = (IResource[]) event.data;

			for (IResource res : resources) {
				if (targetNode != null) {
					createNewNodeAndAddAsChildToTargetNode(res.getFullPath()
							.toString());
				} else {
					createNewTopLevelNode(res.getFullPath().toString());
				}
			}
			targetNode = null;
			return;
		}

		if (event.data instanceof String) {
			String dataString = (String) event.data;
			if (targetNode != null) {
				createNewNodeAndAddAsChildToTargetNode(dataString);
			} else {
				createNewTopLevelNode(dataString);
			}
		}

		System.err.println("drop");
		// TODO Auto-generated method stub

	}

	private void createNewTopLevelNode(String data) {
		TreePath[] treeExpansion = viewer.getExpandedTreePaths();
		model.getModelRoot().addChild(new ReferenceNode((String) data));

		viewer.refresh();

		viewer.setExpandedTreePaths(treeExpansion);
	}

	private void createNewNodeAndAddAsChildToTargetNode(String data) {
		ReferenceNode newNode = new ReferenceNode(data);
		newNode.setParent(targetNode);
		targetNode.addChild(newNode);

		TreePath[] treeExpansion = viewer.getExpandedTreePaths();

		viewer.refresh();

		viewer.setExpandedTreePaths(treeExpansion);

	}

	private Object getSelectedObject() {
		ISelection selection = viewer.getSelection();
		if (selection instanceof IStructuredSelection && !selection.isEmpty()) {
			IStructuredSelection structured = (IStructuredSelection) selection;
			return structured.getFirstElement();
		}
		return null;
	}

	private Object getTarget(DropTargetEvent event) {
		return event.item.getData();
	}

}