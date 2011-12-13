package org.eclipselabs.recommenders.bookmark.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.junit.Test;

public class TestTreeNode {

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
		for (TreeNode child : node.getChildren()) {
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
