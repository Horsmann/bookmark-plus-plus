package org.eclipselabs.recommenders.bookmark.tree;

import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.core.IField;
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
		// TODO: Nicht länger korrekt
		// Iterate the selected items that are being droped
		List<IStructuredSelection> selectedList = getTreeSelections();
		for (int i = 0; i < selectedList.size(); i++) {
			TreeNode node = (TreeNode) selectedList.get(i);

			// TODO: Kein drop ausführen, wenn knoten im selben baum landen
			// würde
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

			while (!targetNode.isBookmarkNode())
				targetNode = targetNode.getParent();
		}

		TreeSelection treeSelection = null;
		TreePath[] treePath = null;
		if (event.data instanceof TreeSelection) {
			treeSelection = (TreeSelection) event.data;
			treePath = treeSelection.getPaths();
		}

		if (isInsideViewDrop(event))
			processDropEventWithDragFromWithinTheView();
		else {
			if (treePath != null)
				for (int i = 0; i < treePath.length; i++)
					processDropEventWithDragInitiatedFromOutsideTheView(treePath[i]);
		}

		targetNode = null;

	}

	private boolean isInsideViewDrop(DropTargetEvent event) {
		return event.data == null;
	}

	private void processDropEventWithDragInitiatedFromOutsideTheView(
			TreePath path) {

		if (targetNode != null)
			createNewNodeAndAddAsChildToTargetNode(path);
		else
			createNewTopLevelNode(path);

	}

	private void createNewTopLevelNode(TreePath path) {

		TreeNode bookmarkNode = new TreeNode("New Bookmark", true);

		TreeNode node = buildTreeStructure(path);

		if (node == null)
			return;

		bookmarkNode = linkNodes(bookmarkNode, node);
		model.getModelRoot().addChild(bookmarkNode);
		refreshTree();
	}

	private void processDropEventWithDragFromWithinTheView() {
		List<IStructuredSelection> selections = getTreeSelections();
		for (int i = 0; i < selections.size(); i++) {
			TreeNode node = (TreeNode) selections.get(i);

			if (isValidSourceAndTarget(node)) {
				TreeNode sourceNode = (TreeNode) node;

				TreeNode mergeSource = checkForAndMergeTreeStructure(
						targetNode, sourceNode);
				TreeNode mergeTarget = checkForAndMergeTreeStructure(
						sourceNode, targetNode);

				if (mergeSource != null) {
					for (TreeNode c : mergeSource.getChildren())
						linkNodes(mergeTarget, c);
				}
				else
					linkNodes(targetNode, sourceNode);
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

		TreeNode mergeSource = checkForAndMergeTreeStructure(targetNode, node);
		TreeNode mergeTarget = checkForAndMergeTreeStructure(node, targetNode);

		if (mergeSource != null)
			for (TreeNode c : mergeSource.getChildren())
				linkNodes(mergeTarget, c);
		else
			targetNode = linkNodes(targetNode, node);

		refreshTree();

	}

	private void refreshTree() {
		TreePath[] treeExpansion = viewer.getExpandedTreePaths();
		viewer.refresh();
		viewer.setExpandedTreePaths(treeExpansion);
	}

	/**
	 * Prevents the creation of duplicate entries for a bookmark and performs a
	 * merge of nodes if possible
	 * 
	 * @param node
	 * @return boolean, true if no duplicates were found and no merge could be
	 *         performed; false otherwise
	 */
	private boolean checkForDuplicatesAndPerformNodeMergeIfPossible(
			TreeNode node) {

		if (isDuplicateEntry(node))
			return true;
		return false;
		// return checkForAndMergeTreeStructure(targetNode, node);
	}

	private TreeNode checkForAndMergeTreeStructure(TreeNode existingStructure,
			TreeNode newStructure) {

		TreeNode link = null;

		if (isNodesValueEqual(existingStructure, newStructure))
			link = newStructure;

		for (TreeNode ns : newStructure.getChildren()) {
			TreeNode ret = checkForAndMergeTreeStructure(existingStructure, ns);
			if (ret != null)
				link = ret;
		}

		for (TreeNode ex : existingStructure.getChildren()) {
			TreeNode ret = checkForAndMergeTreeStructure(ex, newStructure);
			if (ret != null)
				link = ret;
		}

		return link;
	}

	private boolean isNodesValueEqual(TreeNode nodeA, TreeNode nodeB) {
		return nodeA.getValue().equals(nodeB.getValue());
	}

	private boolean isDuplicateEntry(TreeNode node) {
		for (TreeNode bmChild : targetNode.getChildren()) {
			for (TreeNode nChild : node.getChildren()) {
				if (bmChild.getValue().equals(nChild.getValue())) {
					return true;
				}
			}
		}
		return false;
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

		if (path.getSegment(segNr) instanceof IField)
			node = null;

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