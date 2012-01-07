package org.eclipselabs.recommenders.bookmark.tests;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.tree.persistent.BookmarkFileIO;
import org.eclipselabs.recommenders.bookmark.tree.persistent.GsonConverter;
import org.eclipselabs.recommenders.bookmark.tree.persistent.serialization.TreeSerializer;
import org.eclipselabs.recommenders.bookmark.tree.persistent.serialization.TreeSerializerFacade;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeValueConverter;
import org.eclipselabs.recommenders.bookmark.view.tree.TreeContentProvider;
import org.junit.Test;

public class TreeSerializerTest
{

	@Test
	public void testSerializationFacade()
	{
		Display display = Display.getCurrent();
		Shell shell = new Shell(display, SWT.NONE);
		TreeViewer viewer = new TreeViewer(shell, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);

		TreeNode root = createTestTree();
		TreeModel model = new TreeModel();
		model.setModelRoot(root);
		viewer.setContentProvider(new TreeContentProvider());
		viewer.setInput(model.getModelRoot());

		File file = new File("file");
		TreeSerializerFacade.serialize(model, viewer.getExpandedElements(),
				file, null);

		String[] read = BookmarkFileIO.readFromFile(file);

		assertEquals(1, read.length);

		file.delete();
	}

	@Test
	public void testSerialization()
	{
		TreeNode node = new TreeNode("", true, true);
		node.addChild(new TreeNode("A", false, false));
		node.addChild(new TreeNode("B", false, false));

		Object[] expanded = new Object[] { node };
		GsonConverter converter = new GsonConverter();

		String serialized = TreeSerializer.serializeTree(node, expanded,
				converter);

		String expected = "{\"isExpanded\":true,\"children\":[{\"isExpanded\":false,\"children\":[],\"value\":\"A\",\"isBookmarkNode\":false,\"showInFlatModus\":false},{\"isExpanded\":false,\"children\":[],\"value\":\"B\",\"isBookmarkNode\":false,\"showInFlatModus\":false}],\"value\":\"\",\"isBookmarkNode\":true,\"showInFlatModus\":true}";

		assertEquals(expected, serialized);

	}

	private TreeNode createTestTree()
	{
		TreeNode root = new TreeNode("", false, true);

		TreeNode bm1 = new TreeNode("BM#1", true, false);
		TreeNode bm2 = new TreeNode("BM#2", true, false);

		IFile ifile = TreeValueConverter
				.attemptTransformationToIFile("../../TestProj/resource/project.properties");
		TreeNode bm1c1 = new TreeNode(ifile, false, false);
		TreeNode bm1c2 = new TreeNode("bm1c2", false, true);

		TreeNode bm1c1c1 = new TreeNode("bm1c1c1", false, false);
		TreeNode bm1c1c2 = new TreeNode("bm1c1c2", false, false);

		TreeNode bm1c1c2c1 = new TreeNode("bm1c1c2c1", false, false);

		TreeNode bm2c1 = new TreeNode("bm2c1", false, false);
		IJavaElement element = TreeValueConverter
				.attemptTransformationToIJavaElement("=LKJLD/src<test.project{MyTest.java");
		TreeNode bm2c2 = new TreeNode(element, false, false);

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
