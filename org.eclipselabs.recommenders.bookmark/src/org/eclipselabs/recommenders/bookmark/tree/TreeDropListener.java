package org.eclipselabs.recommenders.bookmark.tree;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
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

		TreeSelection treeSelection = null;
		TreePath[] treePath = null;
		if (event.data instanceof TreeSelection) {
			treeSelection = (TreeSelection) event.data;
			treePath = treeSelection.getPaths();
		}

		// IResource [] resources=null;
		// if (event.data instanceof IResource[]){
		// resources = (IResource[])event.data;
		// }

		try {
			if (dragListener.isDragInProgress())
				processDropEventWithDragFromWithinTheView(event);
			else
				processDropEventWithDragInitiatedFromOutsideTheView(treePath);

		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		bookmarkNode = null;

	}

	private void processDropEventWithDragInitiatedFromOutsideTheView(
			TreePath[] treePath) throws JavaModelException {

		if (bookmarkNode != null)
			for (int i = 0; i < treePath.length; i++)
				createNewNodeAndAddAsChild(treePath[i]);
		else
			createNewTopLevelNode(treePath);

	}

	private void createNewTopLevelNode(TreePath[] treePath)
			throws JavaModelException {

		TreeNode bookmarkNode = new TreeNode("New Bookmark", true);

		for (int i = 0; i < treePath.length; i++) {
			TreeNode node = buildTreeStructure(treePath[i]);

			if (node == null)
				return;
			//
			// TreeNode mergeSource = findAndFollowEqualPathsUntilInequalty(
			// bookmarkNode, node);
			// TreeNode mergeTarget =
			// findAndFollowEqualPathsUntilInequalty(node,
			// bookmarkNode);
			//
			// if (mergeSource != null && mergeTarget != null) {
			// for (TreeNode c : mergeSource.getChildren())
			// linkNodes(mergeTarget, c);
			// } else
			bookmarkNode.addChild(node);

		}
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
//				target.setParent(node);
				target.addChild(node);

			}
		}
		refreshTree();
	}

	private void createNewNodeAndAddAsChild(TreePath path)
			throws JavaModelException {

		TreeNode node = buildTreeStructure(path);

		if (node == null)
			return;

		// TreeNode mergeSource = findAndFollowEqualPathsUntilInequalty(
		// bookmarkNode, node);
		// TreeNode mergeTarget = findAndFollowEqualPathsUntilInequalty(node,
		// bookmarkNode);
		//
		// if (mergeSource != null && mergeTarget != null)
		// for (TreeNode c : mergeSource.getChildren()) {
		// /*
		// * Implication: If an equal path already exist it won't have
		// * children and thus creation of duplicates is prevented
		// */
		// linkNodes(mergeTarget, c);
		// }
		// else
		bookmarkNode.addChild(node);

		refreshTree();

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
		TreeNode leaf = new TreeNode(value);

		if (value instanceof IMethod) {
			IJavaElement javaEle = (IJavaElement) value;

			// Get the type that declares the method
			IJavaElement type = javaEle.getParent();
			TreeNode typeNode = new TreeNode(type);
			typeNode.addChild(leaf);

			// Get the source file that declares the type and the method
			IJavaElement file = javaEle.getParent().getParent();
			TreeNode fileNode = new TreeNode(file);
			fileNode.addChild(typeNode);
			return fileNode;
		}

		if (value instanceof IType) {
			IJavaElement javaEle = (IJavaElement) value;

			IJavaElement file = javaEle.getParent();
			TreeNode fileNode = new TreeNode(file);
			fileNode.addChild(leaf);

			return fileNode;
		}

		if (path.getSegment(segNr) instanceof IFile
				|| path.getSegment(segNr) instanceof IJavaElement) {
			return new TreeNode(path.getSegment(segNr));
		}

		return null;
	}


	private Object getTarget(DropTargetEvent event) {
		return ((event.item == null) ? null : event.item.getData());
	}
}