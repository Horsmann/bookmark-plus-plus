package org.eclipselabs.recommenders.bookmark.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.LinkedList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeValueConverter;
import org.eclipselabs.recommenders.bookmark.view.tree.TreeContentProvider;
import org.junit.Test;

public class TestTreeUtil {

	@Test
	public void testGetLeafs() {
		TreeNode root = createTestTree();

		LinkedList<TreeNode> leafList = TreeUtil.getLeafs(root);

		assertEquals(5, leafList.size());

		Object value = leafList.remove().getValue();
		assertEquals("bm1c1c1", value);

		value = leafList.remove().getValue();
		assertEquals("bm1c1c2c1", value);

		value = leafList.remove().getValue();
		assertEquals("bm1c2", value);

		value = leafList.remove().getValue();
		assertEquals("bm2c1", value);

		value = leafList.remove().getValue();
		assertTrue(value instanceof IJavaElement);

	}

	@Test
	public void testGetFirstLeafOfTreePath() {

		TreeNode leaf = TreeUtil.getLeafOfTreePath(null);
		assertNull(leaf);

		TreeNode root = createTestTree();

		leaf = TreeUtil.getLeafOfTreePath(root);

		Object value = leaf.getValue();
		assertEquals("bm1c1c1", value);

	}

	@Test
	public void testGetNodeBelowBookmarkForCurrentNode() {
		TreeNode root = createTestTree();

		TreeNode leaf = TreeUtil.getLeafOfTreePath(root);

		TreeNode belowBookmark = TreeUtil.climbUpUntilLevelBelowBookmark(leaf);

		Object value = belowBookmark.getValue();
		assertTrue(value instanceof IFile);

	}

	@Test
	public void testGetBookmarkNode() {
		TreeNode root = createTestTree();

		TreeNode leaf = TreeUtil.getLeafOfTreePath(root);
		TreeNode bookmark = TreeUtil.getBookmarkNode(leaf);

		assertEquals(true, bookmark.isBookmarkNode());
		assertNotNull(bookmark.getParent());
	}

	@Test
	public void testGetBookmarkNodeForNull() {
		TreeNode bookmark = TreeUtil.getBookmarkNode(null);

		assertNull(bookmark);
	}

	@Test
	public void testLocateNodeWithItsStringValueAsID() {
		TreeNode root = createTestTree();
		TreeNode node = TreeUtil.locateNodeWithEqualID(
				"../../TestProj/resource/project.properties", root);

		assertNotNull(node);
		assertNotNull(node.getParent());
		assertTrue(node.getParent().isBookmarkNode());
		assertEquals(2, node.getChildren().length);

		node = TreeUtil.locateNodeWithEqualID("bm2c1", root);
		assertNotNull(node);
		assertNotNull(node.getParent());
		assertEquals(0, node.getChildren().length);

		node = TreeUtil.locateNodeWithEqualID("X", root);
		assertNull(node);
	}

	@Test
	public void testCopyTreeBelowBookmark() {

		TreeNode copy = TreeUtil.copyTreeBelowBookmark(null);
		assertNull(copy);

		TreeNode root = createTestTree();
		copy = TreeUtil.copyTreeBelowBookmark(root);
		assertNull(copy);

		TreeNode bm1c1 = root.getChildren()[0].getChildren()[0];
		copy = TreeUtil.copyTreeBelowBookmark(bm1c1);

		Object value1 = bm1c1.getValue();
		Object value2 = copy.getValue();
		assertEquals(value1, value2);

		TreeNode node1 = bm1c1.getChildren()[0];
		TreeNode node2 = copy.getChildren()[0];
		value1 = node1.getValue();
		value2 = node2.getValue();
		assertEquals(value1, value2);
		assertFalse(node1 == node2);

		node1 = bm1c1.getChildren()[1];
		node2 = copy.getChildren()[1];
		value1 = node1.getValue();
		value2 = node2.getValue();
		assertEquals(value1, value2);
		assertFalse(node1 == node2);
	}

	@Test
	public void testDetectionOfPartiallyOrFullDuplicateTreePaths() {
		TreeNode root = createTestTree();
		TreeNode fullyContained = createFullyContainedPath();
		TreeNode partially = createPartiallyContaintedInTree();

		boolean isDuplicate = TreeUtil.isDuplicate(root, fullyContained);
		assertTrue(isDuplicate);

		isDuplicate = TreeUtil.isDuplicate(root, partially);
		assertFalse(isDuplicate);

	}

	private TreeNode createFullyContainedPath() {
		TreeNode root = new TreeNode("", false, false);

		TreeNode bm1 = new TreeNode("BM#1", true, true);

		IFile ifile = TreeValueConverter
				.attemptTransformationToIFile("../../TestProj/resource/project.properties");
		TreeNode bm1c1 = new TreeNode(ifile, false, false);
		TreeNode bm1c1c1 = new TreeNode("bm1c1c1", false, false);

		bm1c1.addChild(bm1c1c1);
		bm1.addChild(bm1c1);
		root.addChild(bm1);

		return root;
	}

	private TreeNode createPartiallyContaintedInTree() {

		TreeNode root = new TreeNode("", false, true);

		TreeNode bm1 = new TreeNode("BM#1", true, true);

		IFile ifile = TreeValueConverter
				.attemptTransformationToIFile("../../TestProj/resource/project.properties");
		TreeNode bm1c1 = new TreeNode(ifile, false, false);
		TreeNode other = new TreeNode("other", false, false);

		bm1c1.addChild(other);
		bm1.addChild(bm1c1);
		root.addChild(bm1);

		return root;
	}

	@Test
	public void testLocateNodeWithItsIFileValueAsID() {
		String SEEK = "../../TestProj/resource/project.properties";
		TreeNode root = createTestTree();
		TreeNode node = TreeUtil.locateNodeWithEqualID(SEEK, root);

		assertNotNull(node);
		assertNotNull(node.getParent());
		assertTrue(node.getParent().isBookmarkNode());
		assertTrue(node.getValue() instanceof IFile);
		assertTrue(node.hasChildren());

	}

	@Test
	public void testGetTreeBelowNode() {
		TreeNode root = createTestTree();
		TreeNode bm1 = root.getChildren()[0];

		TreeNode[] below = TreeUtil.getTreeBelowNode(bm1);

		assertEquals(5, below.length);
		assertTrue(below[0].getValue() instanceof IFile);
		assertEquals("bm1c1c1", below[1].getValue());
		assertEquals("bm1c1c2", below[2].getValue());
		assertEquals("bm1c1c2c1", below[3].getValue());
		assertEquals("bm1c2", below[4].getValue());

	}

	//
	// // private void removeDummyProject() throws CoreException {
	// // IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
	// // IProject project = root.getProject("TestProj");
	// // project.delete(true, true, null);
	// // }
	// //
	// // private void setUpDummyProject() throws CoreException {
	// // IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
	// // IProject project = root.getProject("TestProj");
	// // project.create(null);
	// // project.open(null);
	// // project.getFolder("resource");
	// // project.getFile("project.properties");
	// //
	// // }
	//
	@Test
	public void testLocateNodeWithItsIJavaElementValueAsID() {
		String SEEK = "=LKJLD/src<test.project{MyTest.java";
		TreeNode root = createTestTree();
		TreeNode node = TreeUtil.locateNodeWithEqualID(SEEK, root);

		assertNotNull(node);
		assertNotNull(node.getParent());
		assertEquals("BM#2", node.getParent().getValue());
		assertTrue(node.getValue() instanceof IJavaElement);
		assertFalse(node.hasChildren());

	}

	@Test
	public void testCauseRecursion() {
		TreeNode root = createTestTree();

		boolean recursion = TreeUtil.causesRecursion(root,
				root.getChildren()[0]);
		assertTrue(recursion);

		TreeNode source = root.getChildren()[0].getChildren()[0];
		TreeNode target = root.getChildren()[1].getChildren()[0];
		recursion = TreeUtil.causesRecursion(source, target);
		assertFalse(recursion);

		recursion = TreeUtil.causesRecursion(source, null);
		assertFalse(recursion);
	}

	@Test
	public void testClimbUpInHierarchyUntilLevelBelowBookmark() {
		TreeNode root = createTestTree();
		TreeNode leaf = TreeUtil.getLeafOfTreePath(root);

		TreeNode node = TreeUtil.climbUpUntilLevelBelowBookmark(leaf);

		assertTrue(node.getValue() instanceof IFile);
		assertEquals(2, node.getChildren().length);
	}

	@Test
	public void testCreateHierarchy() throws JavaModelException {

		// Id is a method
		String idOfMethod = "=LKJLD/src<test.project{IMy.java[IMy~add~I";
		IJavaElement element = TreeValueConverter
				.attemptTransformationToIJavaElement(idOfMethod);
		TreePath path = new TreePath(new Object[] { element });
		TreeNode compilationUnit = TreeUtil.buildTreeStructure(path);

		assertNotNull(compilationUnit);
		assertTrue(compilationUnit.getValue() instanceof ICompilationUnit);

		// ID is a compilation unit
		String idOfCompilationUnit = "=LKJLD/src<test.project{IMy.java";
		element = TreeValueConverter
				.attemptTransformationToIJavaElement(idOfCompilationUnit);
		path = new TreePath(new Object[] { element });
		compilationUnit = TreeUtil.buildTreeStructure(path);

		assertNotNull(compilationUnit);
		assertTrue(compilationUnit.getValue() instanceof ICompilationUnit);

		// path is empty
		path = new TreePath(new Object[] {});
		compilationUnit = TreeUtil.buildTreeStructure(path);
		assertNull(compilationUnit);

	}

	@Test
	public void testDeletionOfDeadReferences() throws CoreException,
			IOException {
		TreeModel model = new TreeModel();
		model.setModelRoot(createTestTree());
		TreeUtil.deleteNodesReferencingToDeadResourcesUnderNode(
				model.getModelHead(), model);

		// Everything except for the boomarks should be deleted, because
		// everything is referencing to non-existing projects or is just a
		// string dummy
		TreeNode root = model.getModelRoot();
		assertEquals(2, root.getChildren().length);

		TreeNode bm1 = root.getChildren()[0];
		assertEquals(0, bm1.getChildren().length);

		TreeNode bm2 = root.getChildren()[1];
		assertEquals(0, bm2.getChildren().length);

	}

	@Test
	public void testCreationOfBookmark() {
		TreeNode node = TreeUtil.makeBookmarkNode();

		assertTrue(node.isBookmarkNode());
	}

	@Test
	public void testUnlink() {
		TreeNode root = createTestTree();

		TreeNode bm1c1 = root.getChildren()[0].getChildren()[0];
		TreeUtil.unlink(bm1c1);

		assertEquals(1, root.getChildren()[0].getChildren().length);

		// Test coverage...
		TreeUtil.unlink(null);
	}

	@Test
	public void testMerge() {
		TreeNode root = createTestTree();
		TreeNode partlyContaintedPath = createPartiallyContaintedInTree();

		TreeNode merge = TreeUtil.attemptMerge(root.getChildren()[0],
				partlyContaintedPath);

		assertNotNull(merge);
		TreeNode bm1c1 = root.getChildren()[0].getChildren()[0];
		assertEquals(merge.getValue(), bm1c1.getValue());
		assertEquals(3, bm1c1.getChildren().length);

		merge = TreeUtil.attemptMerge(root.getChildren()[0], new TreeNode(
				"NotInTree", false, false));
		assertNull(merge);
	}

	@Test
	public void testAddToExistingBookmark() throws JavaModelException {
		TreeNode root = createTestTree();

		String idOfCompilationUnit = "=LKJLD/src<test.project{IMy.java[IMy~add~I";
		IJavaElement element = TreeValueConverter
				.attemptTransformationToIJavaElement(idOfCompilationUnit);
		TreePath path = new TreePath(new Object[] { element });

		TreeNode added = TreeUtil.addNodesToExistingBookmark(
				root.getChildren()[0], path);

		assertNotNull(added);
		assertTrue(added.getValue() instanceof ICompilationUnit);
		assertTrue(added.getParent().isBookmarkNode());

		// add the same again
		added = TreeUtil
				.addNodesToExistingBookmark(root.getChildren()[0], path);
		assertNull(added);

		// add empty tree structure
		path = new TreePath(new Object[] {});
		added = TreeUtil
				.addNodesToExistingBookmark(root.getChildren()[0], path);
		assertNull(added);

	}

	@Test
	public void testShowNodeExpanded() {
		Display display = Display.getCurrent();
		Shell shell = new Shell(display, SWT.NONE);
		// org.eclipse.swt.widgets.Composite composite = new
		// org.eclipse.swt.widgets.Composite(
		// shell, SWT.NONE);
		TreeViewer viewer = new TreeViewer(shell, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);

		TreeNode root = createTestTree();
		viewer.setContentProvider(new TreeContentProvider());
		viewer.setInput(root);
		viewer.refresh();
		TreeUtil.showNodeExpanded(viewer, root.getChildren()[0]);

		Object[] expanded = viewer.getExpandedElements();
		assertEquals(2, expanded.length);

	}

	// @Test
	// public void testGetTreeViewerSelection() {
	// Display display = Display.getCurrent();
	//
	// Shell shell = new Shell(display, SWT.NONE);
	// // org.eclipse.swt.widgets.Composite composite = new
	// // org.eclipse.swt.widgets.Composite(
	// // shell, SWT.NONE);
	// TreeViewer viewer = new TreeViewer(shell, SWT.MULTI | SWT.H_SCROLL
	// | SWT.V_SCROLL);
	//
	// // No selection
	// List<IStructuredSelection> sel = TreeUtil.getTreeSelections(viewer);
	// assertEquals(0, sel.size());
	//
	// // // Select one
	// // TreeNode root = createTestTree();
	// // viewer.setContentProvider(new TreeContentProvider());
	// // viewer.setInput(root);
	// // viewer.getTree().update();
	// // viewer.refresh();
	// //
	// // viewer.expandAll();
	// // TreeItem[] items = viewer.getTree().getItems();
	// // viewer.getTree().setSelection(items[3]);
	// // viewer.refresh();
	// // viewer.getTree().update();
	// //
	// // sel = TreeUtil.getTreeSelections(viewer);
	// // assertEquals(1, sel.size());
	//
	// }

	private TreeNode createTestTree() {
		TreeNode root = new TreeNode("", false, false);

		TreeNode bm1 = new TreeNode("BM#1", true, true);
		TreeNode bm2 = new TreeNode("BM#2", true, true);

		IFile ifile = TreeValueConverter
				.attemptTransformationToIFile("../../TestProj/resource/project.properties");
		TreeNode bm1c1 = new TreeNode(ifile, false, false);
		TreeNode bm1c2 = new TreeNode("bm1c2", false, true);

		TreeNode bm1c1c1 = new TreeNode("bm1c1c1", false, false);
		TreeNode bm1c1c2 = new TreeNode("bm1c1c2", false, false);

		TreeNode bm1c1c2c1 = new TreeNode("bm1c1c2c1", false, true);

		TreeNode bm2c1 = new TreeNode("bm2c1", false, true);
		IJavaElement element = TreeValueConverter
				.attemptTransformationToIJavaElement("=LKJLD/src<test.project{MyTest.java");
		TreeNode bm2c2 = new TreeNode(element, false, true);

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
