package org.eclipselabs.recommenders.bookmark.tree;

import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
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

			// TODO: Kein drop ausfŸhren, wenn knoten im selben baum landen
			// wŸrde
			TreeNode target = (TreeNode) getTarget(event);
			if (causesRecursion(node, target)) {
				return false;
			}

			if (node == target)
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

		if (getTarget(event) != null && getTarget(event) instanceof TreeNode) {
			targetNode = (TreeNode) getTarget(event);

			// Get the bookmark node, any drop must be added to a bookmark
			// while (!targetNode.isBookmarkNode())
			// targetNode = targetNode.getParent();
		}

		TreeSelection treeSelection = null;
		if (event.data instanceof TreeSelection)
			treeSelection = (TreeSelection) event.data;

		TreePath[] treePath = treeSelection.getPaths();

		if (isInsideViewDrop(event))
			processDropEventWithDragFromWithinTheView();
		else {
			for (int i = 0; i < treePath.length; i++) {
				processDropEventWithDragInitiatedFromOutsideTheView(treePath[i]);
			}
		}

		targetNode = null;

	}

	private boolean isInsideViewDrop(DropTargetEvent event) {
		return event.data instanceof TreeNode;
	}

	private void processDropEventWithDragInitiatedFromOutsideTheView(
			TreePath path) {

		if (targetNode != null)
			createNewNodeAndAddAsChildToTargetNode(path);
		else
			createNewTopLevelNode(path);

	}

	private void createNewTopLevelNode(TreePath path) {
		TreePath[] treeExpansion = viewer.getExpandedTreePaths();

		TreeNode bookmarkNode = new TreeNode("New Bookmark", true);

		TreeNode node = buildTreeStructure(path);

		if (node == null)
			return;

		bookmarkNode = linkNodes(bookmarkNode, node);
		model.getModelRoot().addChild(bookmarkNode);
		viewer.refresh();
		viewer.setExpandedTreePaths(treeExpansion);
	}

	private void processDropEventWithDragFromWithinTheView() {
		List<IStructuredSelection> selections = getTreeSelections();
		for (int i = 0; i < selections.size(); i++) {
			TreeNode node = (TreeNode) selections.get(i);

			if (isValidSourceAndTarget(node)) {
				TreeNode sourceNode = (TreeNode) node;

				sourceNode.getParent().removeChild(sourceNode);

				sourceNode.setParent(targetNode);
				targetNode.addChild(sourceNode);
			}
		}
	}

	private boolean isValidSourceAndTarget(TreeNode node) {
		return (node instanceof TreeNode && targetNode != null);
	}

	private void createNewNodeAndAddAsChildToTargetNode(TreePath path) {

		TreeNode node = buildTreeStructure(path);
		if (node == null)
			return;

		targetNode = linkNodes(targetNode, node);

		TreePath[] treeExpansion = viewer.getExpandedTreePaths();
		viewer.refresh();
		viewer.setExpandedTreePaths(treeExpansion);

	}

	private TreeNode buildTreeStructure(TreePath path) {
		int segNr = path.getSegmentCount() - 1;
		TreeNode node = null;

		// Fall through, several instanceof may succeed, take the most specific
		// one
		if (path.getSegment(segNr) instanceof IJavaElement)
			node = new TreeNode(path.getSegment(segNr));

		if (path.getSegment(segNr) instanceof IMethod)
			node = buildTreeForMethod(path);

		if (path.getSegment(segNr) instanceof IType)
			node = buildTreeForType(path);

		return node;
	}

	private TreeNode buildTreeForType(TreePath path) {
		int segNr = path.getSegmentCount() - 1;

		if (segNr < 0)
			return null;
		TreeNode typeNode = new TreeNode(path.getSegment(segNr));

		segNr = segNr - 1;
		if (segNr < 0)
			return typeNode;
		TreeNode fileNode = new TreeNode(path.getSegment(segNr));

		fileNode = linkNodes(fileNode, typeNode);
		return fileNode;
	}

	private TreeNode buildTreeForMethod(TreePath path) {
		int segNr = path.getSegmentCount() - 1;

		if (segNr < 0)
			return null;
		TreeNode methodNode = new TreeNode(path.getSegment(segNr));

		segNr = segNr - 1;
		if (segNr < 0)
			return methodNode;
		TreeNode typeNode = new TreeNode(path.getSegment(segNr));
		typeNode = linkNodes(typeNode, methodNode);

		segNr = segNr - 1;
		if (segNr < 0)
			return typeNode;
		TreeNode fileNode = new TreeNode(path.getSegment(segNr));
		fileNode = linkNodes(fileNode, typeNode);

		return fileNode;
	}

	private TreeNode linkNodes(TreeNode parent, TreeNode child) {
		child.setParent(parent);
		parent.addChild(child);

		return parent;
	}

	private Object getTarget(DropTargetEvent event) {
		return ((event.item == null) ? null : event.item.getData());
	}

}