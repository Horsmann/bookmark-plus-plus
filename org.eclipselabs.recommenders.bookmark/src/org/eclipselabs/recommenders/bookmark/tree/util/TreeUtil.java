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
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.util.ResourceAvailabilityValidator;

public class TreeUtil
{

	private static BMNode getReference(BMNode node)
	{
		if (node == null) {
			return null;
		}

		if (node.hasReference()) {
			return node.getReference();
		}
		return node;
	}

	public static String getNameOfNodesBookmark(BMNode node)
	{

		if (isRootNode(node)) {
			return "";
		}

		if (node.isBookmarkNode()) {
			return (String) node.getValue();
		}

		BMNode bookmark = TreeUtil.getBookmarkNode(node);
		String bookmarkName = (String) bookmark.getValue();
		return bookmarkName;
	}

	public static BMNode[] getTreeBelowNode(BMNode node)
	{

		LinkedList<BMNode> children = new LinkedList<BMNode>();

		for (BMNode child : node.getChildren()) {
			children.add(child);
			children.addAll(addChildrenOfNode(child));
		}

		return children.toArray(new BMNode[0]);
	}

	private static LinkedList<BMNode> addChildrenOfNode(BMNode node)
	{
		LinkedList<BMNode> children = new LinkedList<BMNode>();

		for (BMNode child : node.getChildren()) {
			children.add(child);
			children.addAll(addChildrenOfNode(child));
		}

		return children;
	}

	/*
	 * Determines whether a node is in the descendant-hierarchy of a node. The
	 * provided starting node is excluded, a descendant cannot be equal to the
	 * start node
	 */
	public static boolean isDescendant(BMNode startNode,
			BMNode potentialDescendant)
	{
		for (BMNode child : startNode.getChildren()) {

			if (child == potentialDescendant) {
				return true;
			}

			boolean isDesc = isDescendant(child, potentialDescendant);

			if (isDesc) {
				return true;
			}

		}

		return false;
	}

	public static BMNode buildTreeStructure(TreePath path)
		throws JavaModelException
	{

		int segNr = path.getSegmentCount() - 1;
		if (segNr < 0)
			return null;

		Object value = path.getSegment(segNr);

		if (isValueInTypeHierarchyBelowICompilationUnit(value)) {
			return createHierarchyUpToCompilationUnitLevel(value);
		}

		if (value instanceof IFile || value instanceof ICompilationUnit) {
			return new TreeNode(path.getSegment(segNr), false, false);
		}

		return null;
	}

	public static BMNode addNodesToExistingBookmark(BMNode bookmark,
			TreePath treePath)
		throws JavaModelException
	{
		BMNode node = TreeUtil.buildTreeStructure(treePath);
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
			BMNode node, final TreeModel model)
	{

		for (BMNode child : node.getChildren()) {
			deleteNodesReferencingToDeadResourcesUnderNode(child, model);
		}

		if (node.isBookmarkNode() || isNodeModelRoot(node, model))
			return;

		Object value = node.getValue();
		boolean isProjectOpen = ResourceAvailabilityValidator
				.isAssociatedProjectOpen(value);

		boolean doesProjectExist = ResourceAvailabilityValidator
				.doesAssociatedProjectExists(value);

		boolean delete = ResourceAvailabilityValidator
				.doesReferecedObjectExists(value);

		if ((!delete && isProjectOpen) || !doesProjectExist) {

			node.getParent().removeChild(node);
			node.setParent(null);
		}

	}

	public static boolean isDuplicate(BMNode bookmark, BMNode node)
	{
		bookmark = getReference(bookmark);
		node = getReference(node);

		LinkedList<BMNode> leafs = TreeUtil.getLeafs(node);

		for (BMNode leaf : leafs) {
			String id = TreeValueConverter.getStringIdentification(leaf
					.getValue());

			boolean duplicateFound = TreeUtil.isDuplicate(bookmark, id);
			if (duplicateFound)
				return true;
		}

		return false;
	}

	public static BMNode getBookmarkNode(BMNode node)
	{

		if (node == null)
			return null;

		node = getReference(node);

		if (node.isBookmarkNode())
			return node;

		return getBookmarkNode(node.getParent());
	}

	public static BMNode locateNodeWithEqualID(String id, BMNode node)
	{
		node = getReference(node);

		if (doesNodeMatchId(id, node))
			return node;
		for (BMNode child : node.getChildren()) {
			BMNode located = locateNodeWithEqualID(id, child);
			if (located != null)
				return located;
		}

		return null;
	}

	public static void showNodeExpanded(TreeViewer viewer, BMNode node)
	{
		viewer.refresh();
		BMNode leaf = TreeUtil.getLeafOfTreePath((node));

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
	public static boolean causesRecursion(BMNode source, BMNode target)
	{

		if (target == null)
			return false;

		BMNode targetParent = target.getParent();

		if (targetParent == null)
			return false;
		else if (targetParent == source)
			return true;
		else
			return causesRecursion(source, targetParent);

	}

	public static BMNode makeBookmarkNode()
	{
		return new TreeNode("New Bookmark", true, true);
	}

	public static void unlink(BMNode node)
	{

		if (node == null)
			return;

		node.getParent().removeChild(node);
		node.setParent(null);
	}

	private static BMNode createHierarchyUpToCompilationUnitLevel(Object value)
	{
		BMNode tmpChild = new TreeNode(value, false, false);
		BMNode tmpParent = null;

		while (true) {
			IJavaElement javaEle = (IJavaElement) value;

			IJavaElement element = javaEle.getParent();
			tmpParent = new TreeNode(element, false, true);
			tmpParent.addChild(tmpChild);

			IJavaElement nextParent = element.getParent();
			if (nextParent != null && implementsRequiredInterfaces(nextParent)) {
				tmpChild = tmpParent;
				value = element;
			}
			else {
				break;
			}
		}

		return tmpParent;
	}

	private static void mergeAddNodeToBookmark(BMNode bookmark, BMNode node)
		throws JavaModelException
	{

		if (TreeUtil.attemptMerge(bookmark, node) == null)
			bookmark.addChild(node);
	}

	private static boolean implementsRequiredInterfaces(Object value)
	{
		return (value instanceof ICompilationUnit)
				|| isValueInTypeHierarchyBelowICompilationUnit(value);
	}

	private static boolean isValueInTypeHierarchyBelowICompilationUnit(
			Object value)
	{
		return value instanceof IMethod || value instanceof IType
				|| value instanceof IField
				|| value instanceof IImportDeclaration
				|| value instanceof IImportContainer
				|| value instanceof IPackageDeclaration;
	}

	private static boolean isNodeModelRoot(BMNode node, TreeModel model)
	{
		return (node == model.getModelRoot());
	}

	/**
	 * Returns the leaf of a tree path. If a node as two or more children the
	 * path of the first encountered children is followed.
	 * 
	 * @param node
	 * @return
	 */
	public static BMNode getLeafOfTreePath(BMNode node)
	{
		if (node == null)
			return null;

		if (node.hasChildren()) {
			BMNode child = node.getChildren()[0];
			return getLeafOfTreePath(child);
		}
		else
			return node;
	}

	public static LinkedList<BMNode> getLeafs(BMNode node)
	{
		node = getReference(node);

		LinkedList<BMNode> leafs = new LinkedList<BMNode>();

		if (node.hasChildren())
			for (BMNode child : node.getChildren())
				leafs.addAll(getLeafs(child));
		else
			leafs.add(node);
		return leafs;
	}
	
	public static BMNode copyTreePathFromNodeToBookmark(BMNode node)
	{
		if (node == null)
			return null;

		node = getReference(node);

		BMNode upPath = copyTreePathNodeToBookmark(node, true);

		return upPath;

	}

	/**
	 * Copies the <b>tree structure</b> below the provided node. Starting from
	 * the provided node upwards a <b>path</b> to the bookmark the node belongs
	 * to will be copied too. The upward-path can either include or
	 * exclude the bookmark node
	 * 
	 * @param node
	 * @return
	 */
	public static BMNode copyTreeBelowNode(BMNode node,
			boolean inclusiveBookmark)
	{
		if (node == null)
			return null;

		node = getReference(node);

		LinkedList<BMNode> newChilds = new LinkedList<BMNode>();
		for (BMNode child : node.getChildren())
			newChilds.add(copyTreePathNodeToLeafs(child));

		BMNode upPath = copyTreePathNodeToBookmark(node, inclusiveBookmark);

		if (upPath == null)
			return null;

		BMNode leafOfUpPath = TreeUtil.getLeafOfTreePath(upPath);

		for (BMNode child : newChilds)
			leafOfUpPath.addChild(child);

		return upPath;

	}

	private static boolean isRootNode(BMNode node)
	{
		return node.getParent() == null;
	}

	private static BMNode copyTreePathNodeToBookmark(BMNode node,
			boolean inclusiveBookmark)
	{
		if (node == null || node.getParent() == null
				|| (node.isBookmarkNode() && !inclusiveBookmark))
			return null;

		Object value = node.getValue();
		boolean isBookmark = node.isBookmarkNode();
		boolean isAutoGenerated = node.isAutoGenerated();

		BMNode newNode = new TreeNode(value, isBookmark, isAutoGenerated);
		BMNode parent = node.getParent();
		BMNode newParent = copyTreePathNodeToBookmark(parent, inclusiveBookmark);
		if (newParent != null) {
			BMNode leaf = TreeUtil.getLeafOfTreePath(newParent);
			leaf.addChild(newNode);
			return newParent;
		}
		return newNode;
	}

	private static BMNode copyTreePathNodeToLeafs(BMNode node)
	{

		Object value = node.getValue();
		boolean isBookmark = node.isBookmarkNode();
		boolean isAutoGenerated = node.isAutoGenerated();

		BMNode newNode = new TreeNode(value, isBookmark, isAutoGenerated);

		for (BMNode child : node.getChildren()) {
			BMNode newChild = copyTreePathNodeToLeafs(child);
			newNode.addChild(newChild);
		}

		return newNode;
	}

	private static boolean doesNodeMatchId(String id, BMNode node)
	{
		Object value = node.getValue();
		// if (value == null || id == null)
		// return false;
		String compareID = TreeValueConverter.getStringIdentification(value);
		return (id.compareTo(compareID) == 0);
	}

	public static boolean isDuplicate(BMNode node, String masterID)
	{
		node = getReference(node);

		String compareID = TreeValueConverter.getStringIdentification(node
				.getValue());
		if (compareID.compareTo(masterID) == 0)
			return true;

		for (BMNode child : node.getChildren()) {
			boolean duplicateFound = TreeUtil.isDuplicate(child, masterID);
			if (duplicateFound)
				return true;
		}

		return false;
	}

	public static BMNode climbUpUntilLevelBelowBookmark(BMNode node)
	{
		node = getReference(node);

		BMNode climber = node;
		while (climber.getParent() != null
				&& !climber.getParent().isBookmarkNode()) {
			climber = climber.getParent();
		}

		return climber;
	}

	@SuppressWarnings("unchecked")
	public static List<IStructuredSelection> getTreeSelections(TreeViewer viewer)
	{
		ISelection selection = viewer.getSelection();
		if (selection instanceof IStructuredSelection && !selection.isEmpty())
			return ((IStructuredSelection) selection).toList();

		return Collections.emptyList();
	}

	/**
	 * Takes a node (tree path) that shall be added, determines the leaf of it
	 * and seeks equal nodes in the existing tree that are equal to the leafs
	 * <b>parent</b>. If such an equal parent node is found, the leaf is
	 * attached to it as child
	 * 
	 * @param node
	 * @return TreeNode - found merge target or null
	 */
	public static BMNode attemptMerge(BMNode bookmark, BMNode node)
	{
		bookmark = getReference(bookmark);
		node = getReference(node);

		LinkedList<BMNode> leafs = TreeUtil.getLeafs(node);

		for (BMNode leaf : leafs) {
			BMNode parent = leaf.getParent();
			if (climbUpTreeHierarchyMergeIfIDMatches(bookmark, parent))
				return parent;
		}

		return null;
	}

	private static boolean climbUpTreeHierarchyMergeIfIDMatches(
			BMNode bookmark, BMNode parent)
	{
		while (parent != null) {
			BMNode mergeTargetExistingTree = getNodeThatMatchesID(bookmark,
					parent);

			if (isMergeTargetFound(mergeTargetExistingTree)) {
				merge(mergeTargetExistingTree, parent);
				return true;
			}
			parent = parent.getParent();
		}
		return false;
	}

	private static BMNode getNodeThatMatchesID(BMNode bookmark, BMNode parent)
	{
		Object value = parent.getValue();

		if (value == null)
			return null;

		String id = TreeValueConverter.getStringIdentification(value);
		return TreeUtil.locateNodeWithEqualID(id, bookmark);
	}

	private static void merge(BMNode mergeTargetExistingTree, BMNode parent)
	{
		for (BMNode child : parent.getChildren()) {
			mergeTargetExistingTree.addChild(child);
		}
	}

	private static boolean isMergeTargetFound(BMNode mergeTargetExistingTree)
	{
		return mergeTargetExistingTree != null;
	}

	public static BMNode[] getNonAutoGeneratedNodesUnderNode(BMNode node)
	{
		LinkedList<BMNode> nonAutoGenerated = new LinkedList<BMNode>();

		for (BMNode child : node.getChildren()) {
			if (!child.isAutoGenerated()) {
				nonAutoGenerated.add(child);
			}
			nonAutoGenerated
					.addAll(getNonAutoGeneratedNodesUnderNodeRecursive(child));
		}

		return nonAutoGenerated.toArray(new BMNode[0]);
	}

	private static LinkedList<BMNode> getNonAutoGeneratedNodesUnderNodeRecursive(
			BMNode node)
	{
		LinkedList<BMNode> children = new LinkedList<BMNode>();

		for (BMNode child : node.getChildren()) {
			if (!child.isAutoGenerated()) {
				children.add(child);
			}
			children.addAll(getNonAutoGeneratedNodesUnderNodeRecursive(child));
		}

		return children;
	}

}
