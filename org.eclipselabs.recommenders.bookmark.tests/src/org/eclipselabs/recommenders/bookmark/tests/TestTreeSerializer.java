package org.eclipselabs.recommenders.bookmark.tests;

import static org.junit.Assert.assertEquals;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.tree.persistent.GsonConverter;
import org.eclipselabs.recommenders.bookmark.tree.persistent.serialization.TreeSerializer;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeValueConverter;
import org.junit.Test;

public class TestTreeSerializer {
	
//	@Test
//	public void testSerializationFacade() {
//		Display display = Display.getCurrent();
//		Shell shell = new Shell(display, SWT.NONE);
//		// org.eclipse.swt.widgets.Composite composite = new
//		// org.eclipse.swt.widgets.Composite(
//		// shell, SWT.NONE);
//		TreeViewer viewer = new TreeViewer(shell, SWT.MULTI | SWT.H_SCROLL
//				| SWT.V_SCROLL);
//		
//		TreeNode root = createTestTree();
//		TreeModel model = new TreeModel();
//		model.setModelRoot(root);
//		viewer.setContentProvider(new TreeContentProvider());
//		viewer.setInput(model.getModelRoot());
//		
//		File file = new File("file");
//		TreeSerializerFacade.serialize(viewer, model, file);
//		
//		String [] read = BookmarkFileIO.readFromFile(file);
//		
//		assertEquals(1, read.length);
//		
//		file.delete();
//	}
	
	@Test
	public void testSerialization() {
		TreeNode node = new TreeNode("");
		node.addChild(new TreeNode("A"));
		node.addChild(new TreeNode("B"));
	
		Object [] expanded = new Object [] {node};
		GsonConverter converter = new GsonConverter();
		
		String serialized = TreeSerializer.serializeTree(node, expanded, converter);
		assertEquals("{\"isExpanded\":true,\"children\":[{\"isExpanded\":false,\"children\":[],\"value\":\"A\",\"isBookmarkNode\":false},{\"isExpanded\":false,\"children\":[],\"value\":\"B\",\"isBookmarkNode\":false}],\"value\":\"\",\"isBookmarkNode\":false}", serialized);
		
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
