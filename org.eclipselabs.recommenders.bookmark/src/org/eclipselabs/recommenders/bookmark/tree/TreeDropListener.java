package org.eclipselabs.recommenders.bookmark.tree;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ILocalVariable;
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
		// viewer.getTree().get
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

		try {
			if (treePath != null)

				processDropEventWithDragInitiatedFromOutsideTheView(treePath);

			else
				processDropEventWithDragFromWithinTheView(event);
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
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
			linkNodes(bookmarkNode, node);

		}
		linkNodes(model.getModelRoot(), bookmarkNode);
		refreshTree();

	}

	private void processDropEventWithDragFromWithinTheView(DropTargetEvent event) {
		List<IStructuredSelection> selections = getTreeSelections();
		for (int i = 0; i < selections.size(); i++) {
			TreeNode node = (TreeNode) selections.get(i);

			if ((TreeNode) getTarget(event) != null) {
				TreeNode target = (TreeNode) getTarget(event);
				node.getParent().removeChild(node);
				linkNodes(target, node);

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
		bookmarkNode = linkNodes(bookmarkNode, node);

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

//	private boolean isNodesValueEqual(TreeNode nodeA, TreeNode nodeB) {
//		return nodeA.getValue().equals(nodeB.getValue());
//	}

	private TreeNode buildTreeStructure(TreePath path)
			throws JavaModelException {
		int segNr = path.getSegmentCount() - 1;
		TreeNode node = null;

		// Fall through, several instanceof may succeed, take the most specific
		// one

		// if (path.getSegment(segNr) instanceof IJavaElement)
		// node = new TreeNode(path.getSegment(segNr));

		if (path.getSegment(segNr) instanceof IJavaElement) {
			IJavaElement element = (IJavaElement) path.getSegment(segNr);
			node = new TreeNode(element.getPath().toString());
			
			node = new TreeNode(path.getSegment(segNr));
		}
		//
//		if (path.getSegment(segNr) instanceof IMethod) {
//			IMethod method = (IMethod) path.getSegment(segNr);
//			String methodString = method.getPath().toString() + "#"
//					+ method.getElementName().toString();
//
//			methodString += ">" + method.getSignature();
//			node = new TreeNode(methodString);

			
			
//		}
		//
		// if (path.getSegment(segNr) instanceof IType)
		// node = buildTreeForType(path);
		//
		// if (path.getSegment(segNr) instanceof IField)
		// node = null;

		return node;
	}

	// private TreeNode buildTreeForType(TreePath path) {
	// int segNr = path.getSegmentCount() - 1;
	//
	// if (segNr < 0)
	// return null;
	// TreeNode typeNode = new TreeNode(path.getSegment(segNr));
	//
	// segNr = segNr - 1;
	// if (segNr < 0)
	// return typeNode;
	// TreeNode fileNode = new TreeNode(path.getSegment(segNr));
	//
	// fileNode = linkNodes(fileNode, typeNode);
	// return fileNode;
	// }
	//
	// private TreeNode buildTreeForMethod(TreePath path) {
	// int segNr = path.getSegmentCount() - 1;
	//
	// if (segNr < 0)
	// return null;
	// TreeNode methodNode = new TreeNode(path.getSegment(segNr));
	//
	// segNr = segNr - 1;
	// if (segNr < 0)
	// return methodNode;
	// TreeNode typeNode = new TreeNode(path.getSegment(segNr));
	// typeNode = linkNodes(typeNode, methodNode);
	//
	// segNr = segNr - 1;
	// if (segNr < 0)
	// return typeNode;
	// TreeNode fileNode = new TreeNode(path.getSegment(segNr));
	// fileNode = linkNodes(fileNode, typeNode);
	//
	// return fileNode;
	// }

	private TreeNode linkNodes(TreeNode parent, TreeNode child) {
		child.setParent(parent);
		parent.addChild(child);

		return parent;
	}

	private Object getTarget(DropTargetEvent event) {
		return ((event.item == null) ? null : event.item.getData());
	}

}