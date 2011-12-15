package org.eclipselabs.recommenders.bookmark.tests;

import static org.junit.Assert.*;

import org.eclipselabs.recommenders.bookmark.tree.FlatTreeNode;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.junit.Test;

public class TestFlatTreeNode
{
	@Test
	public void testFlatTreeNode() {
		TreeNode refNode = new TreeNode("node", false, true);
		FlatTreeNode flat = new FlatTreeNode(refNode);
		
		assertTrue(flat.hasReference());
		assertEquals(refNode, flat.getReference());
	}

}
