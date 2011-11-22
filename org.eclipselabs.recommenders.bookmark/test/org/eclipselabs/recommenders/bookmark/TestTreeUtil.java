package org.eclipselabs.recommenders.bookmark;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.eclipselabs.recommenders.bookmark.tree.TreeUtil;
import org.eclipselabs.recommenders.bookmark.tree.node.TreeNode;
import org.junit.Test;

public class TestTreeUtil {

	@Test
	public void testGetLeafsForSingleNode() {
		TreeNode root = new TreeNode("");
		LinkedList<TreeNode> leafList = TreeUtil.getLeafs(root);

		assertEquals(1, leafList.size());
		assertEquals("", leafList.remove().getValue());
	}

	@Test
	public void testGetLeafsForNodeWithTwoChildren() {
		TreeNode root = buildRootWithTwoChildren();

		LinkedList<TreeNode> leafList = TreeUtil.getLeafs(root);

		assertEquals(2, leafList.size());
		assertEquals("child",
				((String) leafList.remove().getValue()).substring(0, 5));
		assertEquals("child",
				((String) leafList.remove().getValue()).substring(0, 5));
	}

	@Test
	public void testParentRelationshipForNodeWithTwoChildren() {
		TreeNode root = buildRootWithTwoChildren();

		LinkedList<TreeNode> leafList = TreeUtil.getLeafs(root);

		assertEquals(2, leafList.size());

		TreeNode node = leafList.remove();
		assertEquals(root, node.getParent());

		node = leafList.remove();
		assertEquals(root, node.getParent());
	}

	private TreeNode buildRootWithTwoChildren() {
		TreeNode root = new TreeNode("");
		TreeNode child1 = new TreeNode("child1");
		TreeNode child2 = new TreeNode("child2");

		root.addChild(child1);
		root.addChild(child2);

		return root;
	}

}
