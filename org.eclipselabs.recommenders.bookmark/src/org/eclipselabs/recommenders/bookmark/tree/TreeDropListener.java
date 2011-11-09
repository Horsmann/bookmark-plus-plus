package org.eclipselabs.recommenders.bookmark.tree;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipselabs.recommenders.bookmark.tree.node.BookmarkNode;
import org.eclipselabs.recommenders.bookmark.tree.node.ReferenceNode;
import org.eclipselabs.recommenders.bookmark.tree.node.TreeNode;

public class TreeDropListener implements DropTargetListener {

	private final TreeViewer viewer;
	private TreeModel model;
	private TreeNode targetNode = null;

	public TreeDropListener(TreeViewer viewer, TreeModel model) {
		// super(viewer);
		this.viewer = viewer;
		this.model = model;
	}

	@Override
	public void dragEnter(DropTargetEvent event) {
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

		if (!isValidDrop(event))
			event.detail = DND.DROP_NONE;
	}

	private boolean isValidDrop(DropTargetEvent event) {

		// Iterate the selected items that are being droped
		List<IStructuredSelection> selectedList = getTreeSelections();
		for (int i = 0; i < selectedList.size(); i++) {
			TreeNode node = (TreeNode) selectedList.get(i);
			if (!(node instanceof ReferenceNode))
				return false;

			ReferenceNode refNode = (ReferenceNode) node;

			TreeNode target = (TreeNode) getTarget(event);
			if (causesRecursion(refNode, target)) {
				return false;
			}

			// Is multi-selection one of the selected nodes
			if (refNode == target)
				return false;

		}
		return true;
	}

	@SuppressWarnings("unchecked")
	private List<IStructuredSelection> getTreeSelections() {
		ISelection selection = viewer.getSelection();
		if (selection == null)
			return Collections.emptyList();
		if (selection instanceof IStructuredSelection && !selection.isEmpty())
			return ((IStructuredSelection) selection).toList();

		return Collections.emptyList();
	}

	private boolean causesRecursion(TreeNode source, TreeNode target) {
		
		if (target == null)
			return false;

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

		if (getTarget(event) != null && getTarget(event) instanceof TreeNode)
			targetNode = (TreeNode) getTarget(event);

		if (isInsideViewDrop(event))
			processDropEventWithDragFromWithinTheView();
		else
			processDropEventWithDragInitiatedFromOutsideTheView(event);

		targetNode = null;

	}

	private boolean isInsideViewDrop(DropTargetEvent event) {
		return !getTreeSelections().isEmpty()
				&& !(event.data instanceof IResource[]);
	}

	private void processDropEventWithDragInitiatedFromOutsideTheView(
			DropTargetEvent event) {
		if (event.data instanceof IResource[]) {
			IResource[] resources = (IResource[]) event.data;

			if (targetNode != null) {
				for (IResource res : resources) {
					createNewNodeAndAddAsChildToTargetNode(res.getFullPath()
							.toString());
				}
			} else {
				createNewTopLevelNode(resources);
			}
		}

	}

	private void createNewTopLevelNode(IResource[] resources) {
		TreePath[] treeExpansion = viewer.getExpandedTreePaths();

		BookmarkNode bookmarkNode = new BookmarkNode("New Bookmark");

		for (IResource resource : resources) {

			ReferenceNode refNode = new ReferenceNode(resource.getFullPath()
					.toString());
			bookmarkNode.addChild(refNode);
			refNode.setParent(bookmarkNode);
		}
		model.getModelRoot().addChild(bookmarkNode);
		viewer.refresh();
		viewer.setExpandedTreePaths(treeExpansion);
	}

	private void processDropEventWithDragFromWithinTheView() {
		List<IStructuredSelection> selections = getTreeSelections();
		for (int i = 0; i < selections.size(); i++) {
			TreeNode node = (TreeNode) selections.get(i);

			if (isValidSourceAndTarget(node)) {
				ReferenceNode sourceNode = (ReferenceNode) node;

				sourceNode.getParent().removeChild(sourceNode);

				sourceNode.setParent(targetNode);
				targetNode.addChild(sourceNode);
			}
		}
	}

	private boolean isValidSourceAndTarget(TreeNode node) {
		return (node instanceof ReferenceNode && targetNode != null);
	}

	private void createNewNodeAndAddAsChildToTargetNode(String data) {
		ReferenceNode newNode = new ReferenceNode(data);
		newNode.setParent(targetNode);
		targetNode.addChild(newNode);

		TreePath[] treeExpansion = viewer.getExpandedTreePaths();

		viewer.refresh();

		viewer.setExpandedTreePaths(treeExpansion);

	}

	private Object getTarget(DropTargetEvent event) {
		return ((event.item == null) ? null : event.item.getData());
	}

}