package org.eclipselabs.recommenders.bookmark.tree.listener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipselabs.recommenders.bookmark.Util;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.node.TreeNode;

public class TreeDropListener implements DropTargetListener {

	private final TreeViewer viewer;
	private TreeModel model;
	private TreeNode bookmarkNode = null;

	/**
	 * The drag listener of the <b>same</b> view the drop listener is listening
	 * for. By having access to the corresponding drag listener a drag action
	 * triggered within the view can be distinguished from a drag action started
	 * in a foreign view
	 */
	private TreeDragListener dragListener = null;

	public TreeDropListener(TreeViewer viewer, TreeModel model,
			TreeDragListener localViewsDragListener) {
		this.viewer = viewer;
		this.model = model;
		dragListener = localViewsDragListener;
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

		if (dragListener.isDragInProgress()) {
			List<IStructuredSelection> selectedList = getTreeSelections();
			for (int i = 0; i < selectedList.size(); i++) {
				TreeNode node = (TreeNode) selectedList.get(i);

				TreeNode target = (TreeNode) getTarget(event);
				if (causesRecursion(node, target))
					return false;

				if (node == target)
					return false;
			}
		}
		return true;
	}

	// private boolean checkForDropWithDragInitiatedOutsideOurView(
	// DropTargetEvent event) {
	//
	// if (!dragListener.isDragInProgress()){
	// boolean x = (event.data instanceof IFile
	// || event.data instanceof ICompilationUnit
	// || event.data instanceof IType || event.data instanceof IMethod);
	//
	// boolean y = (event.data instanceof IFile[]
	// || event.data instanceof ICompilationUnit[]
	// || event.data instanceof IType[] || event.data instanceof IMethod);
	// }
	//
	// return true;
	// }
	//
	// private boolean checkForDropWithDragInitiatedInOurView(DropTargetEvent
	// event) {
	// if (dragListener.isDragInProgress()) {
	// List<IStructuredSelection> selectedList = getTreeSelections();
	// for (int i = 0; i < selectedList.size(); i++) {
	// TreeNode node = (TreeNode) selectedList.get(i);
	//
	// TreeNode target = (TreeNode) getTarget(event);
	// if (causesRecursion(node, target))
	// return false;
	//
	// if (node == target)
	// return false;
	// }
	// }
	// return true;
	// }

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
			bookmarkNode = (TreeNode) getTarget(event);

			while (!bookmarkNode.isBookmarkNode())
				bookmarkNode = bookmarkNode.getParent();
		}

		try {
			if (dragListener.isDragInProgress())
				processDropEventWithDragFromWithinTheView(event);
			else
				processDropEventWithDragInitiatedFromOutsideTheView(event);

		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		bookmarkNode = null;

	}

	private void processDropEventWithDragInitiatedFromOutsideTheView(
			DropTargetEvent event) throws JavaModelException {

		TreePath[] treePath = getTreePath(event);

		if (bookmarkNode != null)
			for (int i = 0; i < treePath.length; i++)
				addToExistingBookmark(treePath[i]);
		else
			createNewBookmarkAddAsNode(treePath);

	}

	private TreePath[] getTreePath(DropTargetEvent event) {
		TreeSelection treeSelection = null;
		TreePath[] treePath = null;
		if (event.data instanceof TreeSelection) {
			treeSelection = (TreeSelection) event.data;
			treePath = treeSelection.getPaths();
		}
		return treePath;
	}

	private void addToExistingBookmark(TreePath treePath)
			throws JavaModelException {
		TreeNode node = buildTreeStructure(treePath);
		if (node == null)
			return;
		boolean isDuplicate = isNodeADupblicate(node);
		if (!isDuplicate)
			mergeAddNodeToBookmark(node);
	}

	private void createNewBookmarkAddAsNode(TreePath[] treePath)
			throws JavaModelException {

		bookmarkNode = new TreeNode("New Bookmark", true);

		for (int i = 0; i < treePath.length; i++)
			addToExistingBookmark(treePath[i]);

		model.getModelRoot().addChild(bookmarkNode);
		refreshTree();

	}

	private void processDropEventWithDragFromWithinTheView(DropTargetEvent event) {
		List<IStructuredSelection> selections = getTreeSelections();
		for (int i = 0; i < selections.size(); i++) {
			TreeNode node = (TreeNode) selections.get(i);

			if ((TreeNode) getTarget(event) != null) {
				TreeNode target = (TreeNode) getTarget(event);
				node.getParent().removeChild(node);
				target.addChild(node);

			}
		}
		refreshTree();
	}

	private void mergeAddNodeToBookmark(TreeNode node)
			throws JavaModelException {

		if (!attemptMerge(node))
			bookmarkNode.addChild(node);

		refreshTree();

	}

	/**
	 * Takes a node (tree path) that shall be added, determines the leaf of it
	 * and seeks equal nodes in the existing tree that are equal to the leafs
	 * <b>parent</b>. If such an equal parent node is found, the leaf is
	 * attached to it as child and <code>true</code> is returned
	 * 
	 * @param node
	 * @return
	 */
	private boolean attemptMerge(TreeNode node) {
		LinkedList<TreeNode> leafs = Util.getLeafs(node);

		for (TreeNode leaf : leafs) {
			TreeNode parent = leaf.getParent();

			while (parent != null) {
				String id = Util.getStringIdentification(parent.getValue());
				TreeNode mergeTargetExistingTree = locateNodeWithEqualID(id);

				if (mergeTargetExistingTree != null) {

					// TreeNode x2 = locateNodeWithEqualID(id, node);
					// x2 = x2.getChildren()[0];
					for (TreeNode child : parent.getChildren())
						mergeTargetExistingTree.addChild(child);
					return true;
				}

				parent = parent.getParent();
			}
		}

		return false;
	}

	// Starting from the bookmark node compare all childs to the provided id
	private TreeNode locateNodeWithEqualID(String id) {
		for (TreeNode child : bookmarkNode.getChildren()) {
			TreeNode node = locateNodeWithEqualID(id, child);
			if (node != null)
				return node;
		}
		return null;
	}

	private TreeNode locateNodeWithEqualID(String id, TreeNode node) {

		if (doesNodeMatchesId(id, node))
			return node;
		else
			for (TreeNode child : node.getChildren()) {
				TreeNode located = locateNodeWithEqualID(id, child);
				if (located != null)
					return located;
			}

		return null;
	}

	private boolean doesNodeMatchesId(String id, TreeNode node) {
		String compareID = Util.getStringIdentification(node.getValue());
		return (id.compareTo(compareID) == 0);
	}

	private boolean isNodeADupblicate(TreeNode node) {

		LinkedList<TreeNode> leafs = Util.getLeafs(node);

		for (TreeNode leaf : leafs) {
			String id = Util.getStringIdentification(leaf.getValue());

			for (TreeNode child : bookmarkNode.getChildren()) {
				boolean duplicateFound = isNodeADupblicate(child, id);
				if (duplicateFound)
					return true;
			}
		}

		return false;
	}

	private boolean isNodeADupblicate(TreeNode node, String representation) {

		// if (node.getChildren().length == 0) {
		String leafStringRepresentation = Util.getStringIdentification(node
				.getValue());
		if (leafStringRepresentation.compareTo(representation) == 0)
			return true;
		// }

		for (TreeNode child : node.getChildren()) {
			boolean duplicateFound = isNodeADupblicate(child, representation);
			if (duplicateFound)
				return true;
		}

		return false;
	}

	private void refreshTree() {
		TreePath[] treeExpansion = viewer.getExpandedTreePaths();
		viewer.refresh();
		viewer.setExpandedTreePaths(treeExpansion);
	}

	// /**
	// * Seeks an equal path in the tree structure of the first parameters
	// * TreeNode The return value is the last point of equality for the 2nd
	// * parameters TreeNode or null if the path are unequal
	// *
	// * @param existingStructure
	// * @param newStructure
	// * @return
	// */
	// private TreeNode findAndFollowEqualPathsUntilInequalty(
	// TreeNode existingStructure, TreeNode newStructure) {
	//
	// TreeNode link = null;
	//
	// if (isNodesValueEqual(existingStructure, newStructure))
	// link = newStructure;
	//
	// for (TreeNode ns : newStructure.getChildren()) {
	// TreeNode ret = findAndFollowEqualPathsUntilInequalty(
	// existingStructure, ns);
	// if (ret != null)
	// link = ret;
	// }
	//
	// for (TreeNode ex : existingStructure.getChildren()) {
	// TreeNode ret = findAndFollowEqualPathsUntilInequalty(ex,
	// newStructure);
	// if (ret != null)
	// link = ret;
	// }
	//
	// return link;
	// }

	// private boolean isNodesValueEqual(TreeNode nodeA, TreeNode nodeB) {
	// return nodeA.getValue().equals(nodeB.getValue());
	// }

	private TreeNode buildTreeStructure(TreePath path)
			throws JavaModelException {

		int segNr = path.getSegmentCount() - 1;
		if (segNr < 0)
			return null;

		Object value = path.getSegment(segNr);
		// TreeNode leaf = new TreeNode(value);

		if (value instanceof IMethod || value instanceof IType) {

			TreeNode tmpChild = new TreeNode(value);
			TreeNode tmpParent = null;

			while (true) {
				IJavaElement javaEle = (IJavaElement) value;

				// Get the type that declares the method
				IJavaElement element = javaEle.getParent();
				tmpParent = new TreeNode(element);
				tmpParent.addChild(tmpChild);

				if (implementsNeededInterfaces(element.getParent())) {

					tmpChild = tmpParent;
					value = element;
				} else
					break;
			}

			return tmpParent;
		}

		if (value instanceof IFile || value instanceof ICompilationUnit) {
			return new TreeNode(path.getSegment(segNr));
		}

		return null;
	}

	private boolean implementsNeededInterfaces(Object value) {
		return (value instanceof IType || value instanceof IMethod || value instanceof ICompilationUnit);
	}

	private Object getTarget(DropTargetEvent event) {
		return ((event.item == null) ? null : event.item.getData());
	}
}