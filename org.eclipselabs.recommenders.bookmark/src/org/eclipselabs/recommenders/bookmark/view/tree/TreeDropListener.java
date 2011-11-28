package org.eclipselabs.recommenders.bookmark.view.tree;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.ImportContainer;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.tree.persistent.serialization.TreeSerializerFacade;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeValueConverter;

@SuppressWarnings("restriction")
public class TreeDropListener implements DropTargetListener {

	private final TreeViewer viewer;
	private TreeModel model;

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
		this.dragListener = localViewsDragListener;
	}

	@Override
	public void drop(DropTargetEvent event) {

		try {
			if (dragListener.isDragInProgress())
				processDropEventWithDragFromWithinTheView(event);
			else
				processDropEventWithDragInitiatedFromOutsideTheView(event);

			TreeSerializerFacade.serializeToDefaultLocation(viewer, model);

			saveNewTreeModelState();

		} catch (JavaModelException e) {
			e.printStackTrace();
		}

		viewer.refresh();
	}

	private void saveNewTreeModelState() {
		TreeSerializerFacade.serializeToDefaultLocation(viewer, model);
	}

	private void processDropEventWithDragFromWithinTheView(DropTargetEvent event)
			throws JavaModelException {
		List<IStructuredSelection> selections = getTreeSelections();
		for (int i = 0; i < selections.size(); i++) {

			TreeNode node = (TreeNode) selections.get(i);
			TreeNode nodeCopy = TreeUtil.copyTreePath(node);

			TreeNode dropTarget = (TreeNode) getTarget(event);
			TreeNode targetBookmark = TreeUtil.getBookmarkNode(dropTarget);

			if (didDropOccurInEmptyArea(targetBookmark)) {
				createNewBookmarkAndAdd(node, nodeCopy);
				continue;
			}

			if (TreeUtil.isDuplicate(targetBookmark, node))
				continue;

			TreeNode merged = null;
			if ((merged = attemptMerge(targetBookmark, nodeCopy)) != null) {
				unlink(node);
				showNodeExpanded(merged);
				continue;
			}

			node.getParent().removeChild(node);
			TreeNode head = TreeUtil.climbUpUntilLevelBelowBookmark(nodeCopy);
			targetBookmark.addChild(head);
			showNodeExpanded(head);

		}
	}

	private void processDropEventWithDragInitiatedFromOutsideTheView(
			DropTargetEvent event) throws JavaModelException {

		TreePath[] treePath = getTreePath(event);
		TreeNode target = (TreeNode) getTarget(event);
		TreeNode bookmark = TreeUtil.getBookmarkNode(target);

		if (bookmark != null) {
			addToExistingBookmark(bookmark, treePath);
		} else {
			createNewBookmarkAddAsNode(treePath);
		}

	}

	private void addToExistingBookmark(TreeNode bookmark, TreePath[] treePath)
			throws JavaModelException {
		for (int i = 0; i < treePath.length; i++) {
			TreeNode added = addNodesSeparatlyToExistingBookmark(bookmark,
					treePath[i]);

			if (added != null) {
				showNodeExpanded(added);
			}
		}
	}

	private TreeNode addNodesSeparatlyToExistingBookmark(TreeNode bookmark,
			TreePath treePath) throws JavaModelException {
		TreeNode node = buildTreeStructure(treePath);
		if (node == null)
			return null;

		boolean isDuplicate = TreeUtil.isDuplicate(bookmark, node);
		if (!isDuplicate) {
			mergeAddNodeToBookmark(bookmark, node);
			return node;
		}

		return null;
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
			TreeNode target = (TreeNode) getTarget(event);

			for (int i = 0; i < selectedList.size(); i++) {
				TreeNode node = (TreeNode) selectedList.get(i);

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

	private void showNodeExpanded(TreeNode node) {
		viewer.refresh();
		TreeNode leaf = TreeUtil.getLeafOfTreePath((node));

		if (leaf == null)
			return;

		viewer.expandToLevel(leaf, AbstractTreeViewer.ALL_LEVELS);
		viewer.update(leaf, null);

		viewer.refresh();
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

	private void createNewBookmarkAddAsNode(TreePath[] treePath)
			throws JavaModelException {

		TreeNode bookmark = makeBookmarkNode();

		model.getModelRoot().addChild(bookmark);

		for (int i = 0; i < treePath.length; i++) {
			TreeNode node = addNodesSeparatlyToExistingBookmark(bookmark,
					treePath[i]);
			showNodeExpanded(node);
		}

	}

	private TreeNode makeBookmarkNode() {
		return new TreeNode("New Bookmark", true);
	}

	private void unlink(TreeNode node) {
		node.getParent().removeChild(node);
		node.setParent(null);
	}

	private boolean didDropOccurInEmptyArea(TreeNode targetBookmark) {
		return targetBookmark == null;
	}

	private void createNewBookmarkAndAdd(TreeNode node, TreeNode nodeCopy) {
		TreeNode bookmark = makeBookmarkNode();

		while (nodeCopy.getParent() != null)
			nodeCopy = nodeCopy.getParent();

		bookmark.addChild(nodeCopy);
		model.getModelRoot().addChild(bookmark);
		showNodeExpanded(bookmark);
		unlink(node);
	}

	private void mergeAddNodeToBookmark(TreeNode bookmark, TreeNode node)
			throws JavaModelException {

		if (attemptMerge(bookmark, node) == null)
			bookmark.addChild(node);
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
	private TreeNode attemptMerge(TreeNode bookmark, TreeNode node) {
		LinkedList<TreeNode> leafs = TreeUtil.getLeafs(node);

		for (TreeNode leaf : leafs) {
			TreeNode parent = leaf.getParent();
			if (climbUpTreeHierarchyMergeIfIDMatches(bookmark, parent))
				return parent;
		}

		return null;
	}

	private boolean climbUpTreeHierarchyMergeIfIDMatches(TreeNode bookmark,
			TreeNode parent) {
		while (parent != null) {
			TreeNode mergeTargetExistingTree = getNodeThatMatchesID(bookmark,
					parent);

			if (isMergeTargetFound(mergeTargetExistingTree)) {
				merge(mergeTargetExistingTree, parent);
				return true;
			}
			parent = parent.getParent();
		}
		return false;
	}

	private TreeNode getNodeThatMatchesID(TreeNode bookmark, TreeNode parent) {
		String id = TreeValueConverter.getStringIdentification(parent
				.getValue());
		return TreeUtil.locateNodeWithEqualID(id, bookmark);
	}

	private void merge(TreeNode mergeTargetExistingTree, TreeNode parent) {
		for (TreeNode child : parent.getChildren()) {
			mergeTargetExistingTree.addChild(child);
		}
	}

	private boolean isMergeTargetFound(TreeNode mergeTargetExistingTree) {
		return mergeTargetExistingTree != null;
	}

	private TreeNode buildTreeStructure(TreePath path)
			throws JavaModelException {

		int segNr = path.getSegmentCount() - 1;
		if (segNr < 0)
			return null;

		Object value = path.getSegment(segNr);

		if (isValueInTypeHierarchyBelowICompilationUnit(value)) {
			return createHierarchyUpToCompilationUnitLevel(value);
		}

		if (value instanceof IFile || value instanceof ICompilationUnit) {
			return new TreeNode(path.getSegment(segNr));
		}

		return null;
	}

	private TreeNode createHierarchyUpToCompilationUnitLevel(Object value) {
		TreeNode tmpChild = new TreeNode(value);
		TreeNode tmpParent = null;

		while (true) {
			IJavaElement javaEle = (IJavaElement) value;

			IJavaElement element = javaEle.getParent();
			tmpParent = new TreeNode(element);
			tmpParent.addChild(tmpChild);

			if (implementsRequiredInterfaces(element.getParent())) {
				tmpChild = tmpParent;
				value = element;
			} else {
				break;
			}
		}

		return tmpParent;
	}

	private boolean implementsRequiredInterfaces(Object value) {
		return (value instanceof ICompilationUnit);
	}

	private boolean isValueInTypeHierarchyBelowICompilationUnit(Object value) {
		return value instanceof IMethod || value instanceof IType
				|| value instanceof IField
				|| value instanceof IImportDeclaration
				|| value instanceof ImportContainer
				|| value instanceof IPackageDeclaration;
	}

	private Object getTarget(DropTargetEvent event) {
		return ((event.item == null) ? null : event.item.getData());
	}
}