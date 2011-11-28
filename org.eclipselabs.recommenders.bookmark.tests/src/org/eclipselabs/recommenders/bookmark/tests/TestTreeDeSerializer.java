package org.eclipselabs.recommenders.bookmark.tests;

import static org.junit.Assert.assertEquals;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.tree.util.GsonConverter;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeSerializer;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeValueConverter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestTreeDeSerializer {
	private TreeNode root;

	@Test
	public void testSerializationOfTree() {

//		final String correctSerializedTree = "{\"isExpanded\":false,\"serChildren\":[{\"isExpanded\":false,\"serChildren\":[{\"isExpanded\":false,\"serChildren\":[],\"children\":[],\"value\":\"../../TestProj/resource/project.properties\",\"isBookmarkNode\":false}],\"children\":[],\"value\":\"Bookmark #1\",\"isBookmarkNode\":true},{\"isExpanded\":false,\"serChildren\":[{\"isExpanded\":false,\"serChildren\":[],\"children\":[],\"value\":\"\u003dLKJLD/src\u003ctest.project{MyTest.java\",\"isBookmarkNode\":false}],\"children\":[],\"value\":\"Bookmark #2\",\"isBookmarkNode\":true}],\"children\":[],\"value\":\"\",\"isBookmarkNode\":false}";
//
//		GsonConverter converter = new GsonConverter();
//		String serTree = TreeSerializer.serializeTree(root, null, converter);
//		System.err.println(serTree);
//		assertEquals(correctSerializedTree, serTree);
	}

	@Before
	public void setUp() throws CoreException {

		createTestProject();

		root = new TreeNode("");
		TreeNode bookmark1 = new TreeNode("Bookmark #1", true);
		TreeNode bookmark2 = new TreeNode("Bookmark #2", true);
		root.addChild(bookmark1);
		root.addChild(bookmark2);

		String fileID = "../../TestProj/resource/project.properties";
		IFile file = TreeValueConverter.attemptTransformationToIFile(fileID);
		TreeNode child1 = new TreeNode(file);
		bookmark1.addChild(child1);

		IJavaElement ele = TreeValueConverter
				.attemptTransformationToIJavaElement("=LKJLD/src<test.project{MyTest.java");
		TreeNode child2 = new TreeNode(ele);
		bookmark2.addChild(child2);

	}

	private void createTestProject() throws CoreException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject("TestProj");
		project.create(null);
		project.open(null);
		project.getFolder("resource");
		project.getFile("project.properties");
	}

	@After
	public void tearDown() throws CoreException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject("TestProj");
		project.delete(true, true, null);
	}
}
