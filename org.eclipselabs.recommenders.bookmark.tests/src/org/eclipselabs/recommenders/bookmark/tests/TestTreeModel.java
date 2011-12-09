package org.eclipselabs.recommenders.bookmark.tests;

import static org.junit.Assert.*;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeValueConverter;
import org.junit.Test;

public class TestTreeModel {
	
	@Test
	public void testTreeModel() {
		TreeModel model =  new TreeModel();
		
		assertNotNull(model.getModelRoot());
		assertEquals(model.getModelRoot(), model.getModelHead());
		assertTrue(model.isHeadEqualRoot());
		
		TreeNode root = createTestTree();
		model.setModelRoot(root);
		model.setHeadNode(root.getChildren()[0]);
		assertFalse(model.isHeadEqualRoot());
		
		model.resetHeadToRoot();
		assertTrue(model.isHeadEqualRoot());
	}
	
	private TreeNode createTestTree() {
		TreeNode root = new TreeNode("");

		TreeNode bm1 = new TreeNode("BM#1", true);
		TreeNode bm2 = new TreeNode("BM#2", true);

		IFile ifile = TreeValueConverter
				.attemptTransformationToIFile("../../TestProj/resource/project.properties");
		TreeNode bm1c1 = new TreeNode(ifile);
		TreeNode bm1c2 = new TreeNode("bm1c2");

		TreeNode bm1c1c1 = new TreeNode("bm1c1c1");
		TreeNode bm1c1c2 = new TreeNode("bm1c1c2");

		TreeNode bm1c1c2c1 = new TreeNode("bm1c1c2c1");

		TreeNode bm2c1 = new TreeNode("bm2c1");
		IJavaElement element = TreeValueConverter
				.attemptTransformationToIJavaElement("=LKJLD/src<test.project{MyTest.java");
		TreeNode bm2c2 = new TreeNode(element);

		// Link
		bm1c1c2.addChild(bm1c1c2c1);

		bm1c1.addChild(bm1c1c1);
		bm1c1.addChild(bm1c1c2);

		bm1.addChild(bm1c1);
		bm1.addChild(bm1c2);

		bm2.addChild(bm2c1);
		bm2.addChild(bm2c2);

		root.addChild(bm1);
		root.addChild(bm2);

		return root;
	}

}
