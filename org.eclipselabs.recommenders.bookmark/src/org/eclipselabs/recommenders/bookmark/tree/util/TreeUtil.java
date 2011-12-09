package org.eclipselabs.recommenders.bookmark.tree.util;

import java.util.Collections;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IImportContainer;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.util.ResourceAvailabilityValidator;

public class TreeUtil {

	public static TreeNode[] getTreeBelowNode(TreeNode node) {

		LinkedList<TreeNode> children = new LinkedList<TreeNode>();

		for (TreeNode child : node.getChildren()) {
			children.add(child);
			children.addAll(addChildrenOfNode(child));
		}

		return children.toArray(new TreeNode[0]);
	}

	private static LinkedList<TreeNode> addChildrenOfNode(TreeNode node) {
		LinkedList<TreeNode> children = new LinkedList<TreeNode>();

		for (TreeNode child : node.getChildren()) {
			children.add(child);
			children.addAll(addChildrenOfNode(child));
		}

		return children;
	}

	public static TreeNode buildTreeStructure(TreePath path)
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

	public static TreeNode addNodesToExistingBookmark(TreeNode bookmark,
			TreePath treePath) throws JavaModelException {
		TreeNode node = TreeUtil.buildTreeStructure(treePath);
		if (node == null)
			return null;

		boolean isDuplicate = TreeUtil.isDuplicate(bookmark, node);
		if (!isDuplicate) {
			mergeAddNodeToBookmark(bookmark, node);
			return node;
		}

		return null;
	}

	public static void deleteNodesReferencingToDeadResourcesUnderNode(
			TreeNode node, final TreeModel model) {

		for (TreeNode child : node.getChildren()) {
			deleteNodesReferencingToDeadResourcesUnderNode(child, model);
		}

		if (node.isBookmarkNode() || isNodeModelRoot(node, model))
			return;

		Object value = node.getValue();
		boolean isProjectOpen = ResourceAvailabilityValidator
				.isAssociatedProjectOpen(value);

		boolean delete = ResourceAvailabilityValidator
				.doesReferecedObjectExists(value);

		if (!delete && isProjectOpen) {

			node.getParent().removeChild(node);
			node.setParent(null);
		}

	}

	public static boolean isDuplicate(TreeNode bookmark, TreeNode node) {

		LinkedList<TreeNode> leafs = TreeUtil.getLeafs(node);

		for (TreeNode leaf : leafs) {
			String id = TreeValueConverter.getStringIdentification(leaf
					.getValue());

			boolean duplicateFound = TreeUtil.isDuplicate(bookmark, id);
			if (duplicateFound)
				return true;
		}

		return false;
	}

	public static TreeNode getBookmarkNode(TreeNode node) {

		if (node == null)
			return null;

		if (node.isBookmarkNode())
			return node;

		return getBookmarkNode(node.getParent());
	}

	public static TreeNode locateNodeWithEqualID(String id, TreeNode node) {

		if (doesNodeMatchId(id, node))
			return node;
		for (TreeNode child : node.getChildren()) {
			TreeNode located = locateNodeWithEqualID(id, child);
			if (located != null)
				return located;
		}

		return null;
	}

	public static void showNodeExpanded(TreeViewer viewer, TreeNode node) {
		viewer.refresh();
		TreeNode leaf = TreeUtil.getLeafOfTreePath((node));

		if (leaf == null)
			return;

		viewer.expandToLevel(leaf, AbstractTreeViewer.ALL_LEVELS);
		viewer.update(leaf, null);

		viewer.refresh();
	}

	/**
	 * Checks for recursion or cyclic connection if source is added to target
	 * 
	 * @param source
	 * @param target
	 * @return
	 */
	public static boolean causesRecursion(TreeNode source, TreeNode target) {

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

	public static TreeNode makeBookmarkNode() {
		return new TreeNode("New Bookmark", true);
	}

	public static void unlink(TreeNode node) {

		if (node == null)
			return;

		node.getParent().removeChild(node);
		node.setParent(null);
	}

	private static TreeNode createHierarchyUpToCompilationUnitLevel(Object value) {
		TreeNode tmpChild = new TreeNode(value);
		TreeNode tmpParent = null;

		while (true) {
			IJavaElement javaEle = (IJavaElement) value;

			IJavaElement element = javaEle.getParent();
			tmpParent = new TreeNode(element);
			tmpParent.addChild(tmpChild);

			IJavaElement nextParent = element.getParent();
			if (nextParent != null && implementsRequiredInterfaces(nextParent)) {
				tmpChild = tmpParent;
				value = element;
			} else {
				break;
			}
		}

		return tmpParent;
	}

	private static void mergeAddNodeToBookmark(TreeNode bookmark, TreeNode node)
			throws JavaModelException {

		if (TreeUtil.attemptMerge(bookmark, node) == null)
			bookmark.addChild(node);
	}

	private static boolean implementsRequiredInterfaces(Object value) {
		return (value instanceof ICompilationUnit)
				|| isValueInTypeHierarchyBelowICompilationUnit(value);
	}

	private static boolean isValueInTypeHierarchyBelowICompilationUnit(
			Object value) {
		return value instanceof IMethod || value instanceof IType
				|| value instanceof IField
				|| value instanceof IImportDeclaration
				|| value instanceof IImportContainer
				|| value instanceof IPackageDeclaration;
	}

	private static boolean isNodeModelRoot(TreeNode node, TreeModel model) {
		return (node == model.getModelRoot());
	}

	/**
	 * Returns the leaf of a tree path. If a node as two or more children
	 * the path of the first encountered children is followed.
	 * @param node
	 * @return
	 */
	public static TreeNode getLeafOfTreePath(TreeNode node) {

		if (node == null)
			return null;

		if (node.hasChildren()) {
			TreeNode child = node.getChildren()[0];
			return getLeafOfTreePath(child);
		} else
			return node;
	}

	public static LinkedList<TreeNode> getLeafs(TreeNode node) {

		LinkedList<TreeNode> leafs = new LinkedList<TreeNode>();

		if (node.hasChildren())
			for (TreeNode child : node.getChildren())
				leafs.addAll(getLeafs(child));
		else
			leafs.add(node);
		return leafs;
	}

	/**
	 * Copies the tree structure of a node below the bookmark the node
	 * belongs to. Calling this method with a node which is a bookmark or
	 * root node returns null.
	 * @param node
	 * @return
	 */
	public static TreeNode copyTreeBelowBookmark(TreeNode node) {

		if (node == null || isRootNode(node))
			return null;

		LinkedList<TreeNode> newChilds = new LinkedList<TreeNode>();
		for (TreeNode child : node.getChildren())
			newChilds.add(copyTreePathNodeToLeafs(child));

		TreeNode newNode = copyTreePathNodeToBookmark(node);

//		if (newNode == null)
//			return null;

		for (TreeNode child : newChilds)
			newNode.addChild(child);

		return newNode;

	}
	
	private static boolean isRootNode(TreeNode node){
		return node.getParent() == null;
	}

	private static TreeNode copyTreePathNodeToBookmark(TreeNode node) {
		if (node == null || node.isBookmarkNode())
			return null;

		TreeNode newNode = new TreeNode(node.getValue());
		TreeNode parent = node.getParent();
		TreeNode newParent = copyTreePathNodeToBookmark(parent);
		if (newParent != null)
			newParent.addChild(newNode);
		return newNode;
	}

	private static TreeNode copyTreePathNodeToLeafs(TreeNode node) {

		Object value = node.getValue();
		TreeNode newNode = new TreeNode(value);

		for (TreeNode child : node.getChildren()) {
			TreeNode newChild = copyTreePathNodeToLeafs(child);
			newNode.addChild(newChild);
		}

		return newNode;
	}

	private static boolean doesNodeMatchId(String id, TreeNode node) {
		Object value = node.getValue();
		if (value == null || id == null)
			return false;
		String compareID = TreeValueConverter.getStringIdentification(value);
		return (id.compareTo(compareID) == 0);
	}

	public static boolean isDuplicate(TreeNode node, String masterID) {

		String compareID = TreeValueConverter.getStringIdentification(node
				.getValue());
		if (compareID.compareTo(masterID) == 0)
			return true;

		for (TreeNode child : node.getChildren()) {
			boolean duplicateFound = TreeUtil.isDuplicate(child, masterID);
			if (duplicateFound)
				return true;
		}

		return false;
	}

	public static TreeNode climbUpUntilLevelBelowBookmark(TreeNode node) {
		TreeNode climber = node;
		while (climber.getParent() != null
				&& !climber.getParent().isBookmarkNode()) {
			climber = climber.getParent();
		}

		return climber;
	}

	@SuppressWarnings("unchecked")
	public static List<IStructuredSelection> getTreeSelections(TreeViewer viewer) {
		ISelection selection = viewer.getSelection();
		if (selection instanceof IStructuredSelection && !selection.isEmpty())
			return ((IStructuredSelection) selection).toList();

		return Collections.emptyList();
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
	public static TreeNode attemptMerge(TreeNode bookmark, TreeNode node) {
		LinkedList<TreeNode> leafs = TreeUtil.getLeafs(node);

		for (TreeNode leaf : leafs) {
			TreeNode parent = leaf.getParent();
			if (climbUpTreeHierarchyMergeIfIDMatches(bookmark, parent))
				return parent;
		}

		return null;
	}

	private static boolean climbUpTreeHierarchyMergeIfIDMatches(
			TreeNode bookmark, TreeNode parent) {
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

	private static TreeNode getNodeThatMatchesID(TreeNode bookmark,
			TreeNode parent) {
		String id = TreeValueConverter.getStringIdentification(parent
				.getValue());
		return TreeUtil.locateNodeWithEqualID(id, bookmark);
	}

	private static void merge(TreeNode mergeTargetExistingTree, TreeNode parent) {
		for (TreeNode child : parent.getChildren()) {
			mergeTargetExistingTree.addChild(child);
		}
	}

	private static boolean isMergeTargetFound(TreeNode mergeTargetExistingTree) {
		return mergeTargetExistingTree != null;
	}

}
