package org.eclipselabs.recommenders.bookmark.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.LinkedList;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.persistent.BookmarkFileIO;
import org.eclipselabs.recommenders.bookmark.tree.persistent.deserialization.RestoredTree;
import org.eclipselabs.recommenders.bookmark.tree.persistent.deserialization.TreeDeserializerFacade;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.wizard.WizardUtil;
import org.junit.Before;
import org.junit.Test;

public class WizardUtilTest
{

	BMNode root;
	BMNode[] selected;

	@Test
	public void test()
	{
		BMNode tree = WizardUtil.buildTreeConsistingOfSelection(selected);

		assertEquals(2, tree.getChildren().length);
		assertEquals("Test-Bookmark#2", tree.getChildren()[0].getValue());
		assertEquals("Test-Bookmark#1", tree.getChildren()[1].getValue());

		//selected categories are returned first
		testBookmark2(tree);

		testBookmark1(tree);
	}

	private void testBookmark2(BMNode tree)
	{
		BMNode comp = tree.getChildren()[1].getChildren()[0];
		assertEquals(2, comp.getChildren().length);

		BMNode type = comp.getChildren()[0];
		assertEquals(1, type.getChildren().length);

		BMNode nestedType = type.getChildren()[0];
		assertEquals(1, nestedType.getChildren().length);
		assertTrue(nestedType.getChildren()[0].getValue() instanceof IField);

		BMNode type2 = comp.getChildren()[1];
		assertEquals(1, type2.getChildren().length);
		assertTrue(type2.getChildren()[0].getValue() instanceof IMethod);

	}

	private void testBookmark1(BMNode tree)
	{
		BMNode comp = tree.getChildren()[0].getChildren()[0];
		assertEquals(0, comp.getChildren().length);

		assertTrue(comp.getValue() instanceof ICompilationUnit);
	}

	@Before
	public void setUp()
	{
		File file = new File("resource/testbookmark.bm");
		String[] data = BookmarkFileIO.readFromFile(file);
		RestoredTree deserialize = TreeDeserializerFacade.deserialize(data[0]);
		root = deserialize.getRoot();

		LinkedList<BMNode> leafs = TreeUtil.getLeafs(root);

		// variable in a nested type
		BMNode first = leafs.get(0);

		// variable
		BMNode second = leafs.get(2);

		// bookmark category
		BMNode third = root.getChildren()[1];

		// compilation unit
		BMNode fourth = leafs.get(3);

		selected = new BMNode[] { first, second, third, fourth };

	}
}
