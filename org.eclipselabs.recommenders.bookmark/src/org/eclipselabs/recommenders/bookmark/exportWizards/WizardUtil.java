package org.eclipselabs.recommenders.bookmark.exportWizards;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;

public class WizardUtil
{
	public static BMNode buildTreeConsistingOfSelection(List<IStructuredSelection> treeSelections) {
		
		BMNode root = new TreeNode("", false, true);

		// selected category nodes
		LinkedList<BMNode> listOfSelectedCategories = getEntireSelectedCategories(treeSelections);

		// single nodes which are not below one of the selected categories and
		// need to be pressed separatly
		LinkedList<BMNode> singleSelectedNodes = getSingleSelectedNodesWhichAreNotBewlowAnAlreadySelectedCategory(
				treeSelections, listOfSelectedCategories);

		root = copyCategoriesAndAddToNode(listOfSelectedCategories, root);

		root = copySingleSelectedNodes(root, singleSelectedNodes);
		
		return root;
	}
	
	private static BMNode copySingleSelectedNodes(BMNode root,
			LinkedList<BMNode> singleSelectedNodes)
	{
		LinkedList<BMNode> shareSameBM;
		while (!singleSelectedNodes.isEmpty()) {

			shareSameBM = consolidateSingleNodesAmongCommonCategory(singleSelectedNodes);

			singleSelectedNodes = removeConsolidatedNodesFromSingleSelectionList(
					shareSameBM, singleSelectedNodes);

			if (!shareSameBM.isEmpty()) {
				root = mergeTreeStructureOfNodesUnderCommonCategory(root,
						shareSameBM);
			}

		}
		return root;
	}

	private static BMNode mergeTreeStructureOfNodesUnderCommonCategory(BMNode root,
			LinkedList<BMNode> shareSameBM)
	{
		BMNode shared = shareSameBM.pollFirst();
		BMNode newBookmark = TreeUtil.copyTreePathFromNodeToBookmark(shared);
		while (!shareSameBM.isEmpty()) {
			BMNode next = shareSameBM.pollFirst();
			BMNode copy = TreeUtil.copyTreeBelowNode(next, false);

			if (TreeUtil.attemptMerge(newBookmark, copy) == null) {
				newBookmark.addChild(copy);
			}

		}
		root.addChild(newBookmark);

		return root;
	}

	private static LinkedList<BMNode> removeConsolidatedNodesFromSingleSelectionList(
			LinkedList<BMNode> shareSameBM,
			LinkedList<BMNode> singleSelectedNodes)
	{
		// delete
		for (BMNode node : shareSameBM) {
			singleSelectedNodes.remove(node);
		}

		return singleSelectedNodes;
	}

	private static LinkedList<BMNode> consolidateSingleNodesAmongCommonCategory(
			LinkedList<BMNode> singleSelectedNodes)
	{

		BMNode select = singleSelectedNodes.pollFirst();
		BMNode bm = TreeUtil.getBookmarkNode(select);
		LinkedList<BMNode> shareSameBM = new LinkedList<BMNode>();
		shareSameBM.add(select);

		for (BMNode remain : singleSelectedNodes) {
			BMNode remainBM = TreeUtil.getBookmarkNode(remain);
			if (remainBM == bm) {
				shareSameBM.add(remain);
			}
		}
		return shareSameBM;
	}

	private static BMNode copyCategoriesAndAddToNode(
			LinkedList<BMNode> listOfSelectedCategories, BMNode root)
	{
		// copy entire bookmarks add to root
		for (BMNode cat : listOfSelectedCategories) {
			BMNode copy = TreeUtil.copyTreeBelowNode(cat, true);
			root.addChild(copy);
		}

		return root;
	}

	private static LinkedList<BMNode> getSingleSelectedNodesWhichAreNotBewlowAnAlreadySelectedCategory(
			List<IStructuredSelection> treeSelections,
			LinkedList<BMNode> listOfSelectedCategories)
	{
		LinkedList<BMNode> singleSelectedNodes = new LinkedList<BMNode>();

		BMNode[] selectedCategories = listOfSelectedCategories
				.toArray(new BMNode[0]);
		for (int i = 0; i < treeSelections.size(); i++) {
			BMNode node = (BMNode) treeSelections.get(i);

			if (node.isBookmarkNode()) {
				continue;
			}

			// compare remaining nodes whether they are below a selected
			// category or not,
			// if they are, do nothing since the entire category will be
			// exported anyway
			// if they are not, store them separately
			boolean isContainedInAlreadySelectedCat = false;
			for (BMNode category : selectedCategories) {
				isContainedInAlreadySelectedCat = TreeUtil.isDescendant(
						category, node);
				if (isContainedInAlreadySelectedCat) {
					break;
				}
			}

			if (!isContainedInAlreadySelectedCat) {
				singleSelectedNodes.add(node);
			}
		}
		return singleSelectedNodes;
	}

	private static LinkedList<BMNode> getEntireSelectedCategories(
			List<IStructuredSelection> treeSelections)
	{
		LinkedList<BMNode> listOfSelectedCategories = new LinkedList<BMNode>();

		for (int i = 0; i < treeSelections.size(); i++) {
			BMNode node = (BMNode) treeSelections.get(i);

			if (node.isBookmarkNode()) {
				listOfSelectedCategories.add(node);
			}
		}

		return listOfSelectedCategories;
	}

}
