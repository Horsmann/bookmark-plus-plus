package org.eclipselabs.recommenders.bookmark.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.junit.Test;

public class TreeNodeTest {
	
	@Test
	public void testHasParent() {
		BMNode root = getTestTree();
		assertFalse(root.hasParent());
		
		BMNode child = root.getChildren()[0];
		assertTrue(child.hasParent());
	}
	
	@Test
	public void testRemoveAllChildren() {
		BMNode root = getTestTree();
		
		root.removeAllChildren();
		
		BMNode[] children = root.getChildren();
		
		assertFalse(root.hasChildren());
		assertEquals(0, children.length);
	}
	
	private BMNode getTestTree()
	{
		TreeNode node = new TreeNode("Node", true, true);
		node.addChild(new TreeNode("child 1", false, false));
		node.addChild(new TreeNode("child 2", false, false));
		return node;
	}

	@Test
	public void testSelfReference() {
		TreeNode node = new TreeNode("node", false, true);
		assertFalse(node.hasReference());
		assertEquals(node, node.getReference());
	}

	@Test
	public void testChildren() {
		TreeNode node = new TreeNode("Node", true, true);
		node.addChild(new TreeNode("child 1", false, false));
		node.addChild(new TreeNode("child 2", false, false));

		assertEquals(2, node.getChildren().length);

		doChildrenHaveParentAsParent(node);
		testChildRemoval(node);
	}

	private void doChildrenHaveParentAsParent(TreeNode node) {
		for (BMNode child : node.getChildren()) {
			assertEquals(node, child.getParent());
			assertTrue(child.hasParent());
		}
	}

	private void testChildRemoval(TreeNode node) {
		node.removeChild(node.getChildren()[0]);
		assertEquals(1, node.getChildren().length);

		node.removeChild(node.getChildren()[0]);
		assertEquals(0, node.getChildren().length);
	}

	@Test
	public void testGetSetValue() {
		TreeNode node = new TreeNode(null, false, true);

		assertNull(node.getValue());

		node.setValue("123");
		assertEquals("123", node.getValue());

		node.setValue(new Integer(23));
		assertEquals(new Integer(23), node.getValue());
	}

	@Test
	public void testIsBookmark() {
		TreeNode node = new TreeNode("Bookmark", true, true);
		assertTrue(node.isBookmarkNode());
	}

}
