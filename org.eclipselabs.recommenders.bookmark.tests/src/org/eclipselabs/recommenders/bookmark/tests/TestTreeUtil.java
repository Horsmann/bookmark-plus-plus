package org.eclipselabs.recommenders.bookmark.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeValueConverter;
import org.junit.Test;

public class TestTreeUtil {

	@Test
	public void testGetLeafsForSingleNode() {
		TreeNode root = new TreeNode("");
		LinkedList<TreeNode> leafList = TreeUtil.getLeafs(root);

		assertEquals(1, leafList.size());
		TreeNode node = leafList.remove();
		assertNull(node.getParent());
		assertEquals("", node.getValue());
	}

	@Test
	public void testGetLeafsForNodeWithTwoChildren() {
		TreeNode root = buildNodeWithTwoChildren();

		LinkedList<TreeNode> leafList = TreeUtil.getLeafs(root);

		assertEquals(2, leafList.size());
		assertEquals("child",
				((String) leafList.remove().getValue()).substring(0, 5));
		assertEquals("child",
				((String) leafList.remove().getValue()).substring(0, 5));
	}

	@Test
	public void testGetLeafOfTreePath() {
		TreeNode root = buildNodeWithTwoChildren();

		TreeNode leaf = TreeUtil.getLeafOfTreePath(root);
		assertEquals("child1", leaf.getValue());

		root = TreeUtil
				.climbUpUntilLevelBelowBookmark(buildTreeStructureAndReturnLeafNode());
		leaf = TreeUtil.getLeafOfTreePath(root);
		assertEquals("childChildChild", leaf.getValue());

	}

	private TreeNode buildNodeWithTwoChildren() {
		TreeNode root = new TreeNode("");
		TreeNode child1 = new TreeNode("child1");
		TreeNode child2 = new TreeNode("child2");

		root.addChild(child1);
		root.addChild(child2);

		return root;
	}

	@Test
	public void testGetBookmarkNode() {
		TreeNode node = buildTreeStructureAndReturnLeafNode();
		TreeNode bookmark = TreeUtil.getBookmarkNode(node);

		assertEquals(true, bookmark.isBookmarkNode());
		assertNotSame(bookmark, node);
		assertNull(bookmark.getParent());
	}

	private TreeNode buildTreeStructureAndReturnLeafNode() {
		TreeNode root = new TreeNode("Bookmark", true);
		TreeNode child = new TreeNode("child");
		root.addChild(child);

		TreeNode childOfchild = new TreeNode("childChild");
		child.addChild(childOfchild);

		TreeNode childOfchildOfchild = new TreeNode("childChildChild");
		childOfchild.addChild(childOfchildOfchild);

		return childOfchildOfchild;
	}

	@Test
	public void testGetBookmarkNodeForNull() {
		TreeNode bookmark = TreeUtil.getBookmarkNode(null);

		assertNull(bookmark);
	}

	@Test
	public void testLocateNodeWithItsStringValueAsID() {
		String SEEK = "XXXX";
		TreeNode bookmark = buildThreeLevelTree(SEEK);
		TreeNode node = TreeUtil.locateNodeWithEqualID(SEEK, bookmark);

		assertNotNull(node);
		assertNotNull(node.getParent());
		assertEquals("Bookmark", node.getParent().getValue());
		assertEquals(SEEK, node.getValue());
		assertTrue(node.hasChildren());

		node = TreeUtil.locateNodeWithEqualID("", bookmark);
		assertNull(node);

		node = TreeUtil.locateNodeWithEqualID("", new TreeNode(null));
		assertNull(node);

		node = TreeUtil.locateNodeWithEqualID(null, new TreeNode(null));
		assertNull(node);
	}

	private TreeNode buildThreeLevelTree(String value) {
		TreeNode bookmark = new TreeNode("Bookmark", true);

		TreeNode levelOneLeft = new TreeNode("L");
		bookmark.addChild(levelOneLeft);

		TreeNode levelOneRight = new TreeNode(value);
		bookmark.addChild(levelOneRight);

		TreeNode levelOneLeftTwoLeft = new TreeNode("LL");
		levelOneLeft.addChild(levelOneLeftTwoLeft);

		TreeNode levelOneLeftTwoRight = new TreeNode("LR");
		levelOneLeft.addChild(levelOneLeftTwoRight);

		TreeNode levelOneRightTwoLeft = new TreeNode("RL");
		levelOneRight.addChild(levelOneRightTwoLeft);

		TreeNode levelOneRightTwoRight = new TreeNode("RR");
		levelOneRight.addChild(levelOneRightTwoRight);

		return bookmark;
	}

	@Test
	public void testCopyTreePath() {

		TreeNode copy = TreeUtil.copyTreePath(null);
		assertNull(copy);

		TreeNode node = getTestTree(0);
		copy = TreeUtil.copyTreePath(node);
		assertNull(copy);

		node = getTestTree(1);
		copy = TreeUtil.copyTreePath(node);
		assertNotNull(copy);
		assertNull(copy.getParent());
		assertTrue(copy.hasChildren());
		assertEquals(node.getValue(), copy.getValue());
		assertNotSame(node, copy);
		assertEquals(1, node.getParent().getChildren().length);

		node = getTestTree(2);
		copy = TreeUtil.copyTreePath(node);
		assertNotNull(copy);
		assertNotNull(copy.getParent());
		assertTrue(copy.hasChildren());
		assertEquals("top", copy.getParent().getValue());
		assertEquals(node.getValue(), copy.getValue());
		assertNotSame(node, copy);
		assertEquals(1, node.getParent().getChildren().length);

		node = getTestTree(3);
		copy = TreeUtil.copyTreePath(node);
		assertNotNull(copy);
		assertNotNull(copy.getParent());
		assertFalse(copy.hasChildren());
		assertEquals("middle", copy.getParent().getValue());
		assertEquals(node.getValue(), copy.getValue());
		assertNotSame(node, copy);
		assertEquals(1, copy.getParent().getChildren().length);

		node = getTestTree(4);
		copy = TreeUtil.copyTreePath(node);
		assertNotNull(copy);
		assertNotNull(copy.getParent());
		assertFalse(copy.hasChildren());
		assertEquals("middle", copy.getParent().getValue());
		assertEquals(node.getValue(), copy.getValue());
		assertNotSame(node, copy);
		assertEquals(1, copy.getParent().getChildren().length);
	}

	private TreeNode getTestTree(int i) {

		TreeNode bookmark = new TreeNode("Bookmark", true);

		TreeNode top = new TreeNode("top");
		bookmark.addChild(top);

		TreeNode middle = new TreeNode("middle");
		top.addChild(middle);

		TreeNode leaf1 = new TreeNode("leaf #1");
		middle.addChild(leaf1);

		TreeNode leaf2 = new TreeNode("leaf #2");
		middle.addChild(leaf2);

		switch (i) {
		case 0:
			return bookmark;
		case 1:
			return top;
		case 2:
			return middle;
		case 3:
			return leaf1;
		case 4:
			return leaf2;
		}

		return null;
	}

	@Test
	public void testDetectionOfPartiallyOrFullDuplicateValues() {
		TreeNode tree = createTree();
		TreeNode contained = createFullyContainedInTree();
		TreeNode partially = createPartiallyContaintedInTree();
		TreeNode notContained = createNotContainedInTree();

		boolean isDuplicate = TreeUtil.isDuplicate(tree, contained);
		assertTrue(isDuplicate);

		isDuplicate = TreeUtil.isDuplicate(tree, partially);
		assertTrue(isDuplicate);

		isDuplicate = TreeUtil.isDuplicate(tree, notContained);
		assertFalse(isDuplicate);
	}

	private TreeNode createNotContainedInTree() {

		TreeNode a = new TreeNode("X");
		TreeNode aa = new TreeNode("XY");
		TreeNode bb = new TreeNode("XZ");
		a.addChild(aa);
		a.addChild(bb);

		return a;
	}

	private TreeNode createPartiallyContaintedInTree() {
		TreeNode a = new TreeNode("A");
		TreeNode aa = new TreeNode("AA");
		TreeNode bb = new TreeNode("BB");
		a.addChild(aa);
		a.addChild(bb);

		TreeNode aaa = new TreeNode("AAA");
		TreeNode aab = new TreeNode("AAB");
		aa.addChild(aaa);
		aa.addChild(aab);

		TreeNode aaaa = new TreeNode("AAAA");
		aaa.addChild(aaaa);

		return a;
	}

	private TreeNode createFullyContainedInTree() {
		TreeNode a = new TreeNode("A");
		TreeNode aa = new TreeNode("AA");
		TreeNode bb = new TreeNode("BB");
		a.addChild(aa);
		a.addChild(bb);

		TreeNode aaa = new TreeNode("AAA");
		TreeNode aab = new TreeNode("AAB");
		aa.addChild(aaa);
		aa.addChild(aab);

		return a;
	}

	private TreeNode createTree() {
		TreeNode a = new TreeNode("A");
		TreeNode aa = new TreeNode("AA");
		TreeNode bb = new TreeNode("BB");
		a.addChild(aa);
		a.addChild(bb);

		TreeNode aaa = new TreeNode("AAA");
		TreeNode aab = new TreeNode("AAB");
		aa.addChild(aaa);
		aa.addChild(aab);

		TreeNode bba = new TreeNode("BBA");
		TreeNode bbb = new TreeNode("BBB");
		bb.addChild(bba);
		bb.addChild(bbb);

		return a;
	}

	@Test
	public void testLocateNodeWithItsIFileValueAsID() throws CoreException {
		String SEEK = "../../TestProj/resource/project.properties";
		setUpDummyProject();
		TreeNode bookmark = buildThreeLevelTreeWithAnIFileAsNodeValue(SEEK);
		TreeNode node = TreeUtil.locateNodeWithEqualID(SEEK, bookmark);

		assertNotNull(node);
		assertNotNull(node.getParent());
		assertEquals("R", node.getParent().getValue());
		assertTrue(node.getValue() instanceof IFile);
		assertFalse(node.hasChildren());

		removeDummyProject();
	}

	private void removeDummyProject() throws CoreException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject("TestProj");
		project.delete(true, true, null);
	}

	private void setUpDummyProject() throws CoreException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject("TestProj");
		project.create(null);
		project.open(null);
		project.getFolder("resource");
		project.getFile("project.properties");

	}

	private TreeNode buildThreeLevelTreeWithAnIFileAsNodeValue(String value) {
		TreeNode bookmark = new TreeNode("Bookmark", true);

		TreeNode levelOneLeft = new TreeNode("L");
		bookmark.addChild(levelOneLeft);

		TreeNode levelOneRight = new TreeNode("R");
		bookmark.addChild(levelOneRight);

		TreeNode levelOneLeftTwoLeft = new TreeNode("LL");
		levelOneLeft.addChild(levelOneLeftTwoLeft);

		TreeNode levelOneLeftTwoRight = new TreeNode("LR");
		levelOneLeft.addChild(levelOneLeftTwoRight);

		TreeNode levelOneRightTwoLeft = new TreeNode(
				TreeValueConverter.attemptTransformationToIFile(value));
		levelOneRight.addChild(levelOneRightTwoLeft);

		TreeNode levelOneRightTwoRight = new TreeNode("RR");
		levelOneRight.addChild(levelOneRightTwoRight);

		return bookmark;
	}

	@Test
	public void testLocateNodeWithItsIJavaElementValueAsID()
			throws CoreException {
		String SEEK = "=LKJLD/src<test.project{MyTest.java";
		TreeNode bookmark = buildThreeLevelTreeWithAnIJavaElementAsNodeValue(SEEK);
		TreeNode node = TreeUtil.locateNodeWithEqualID(SEEK, bookmark);

		assertNotNull(node);
		assertNotNull(node.getParent());
		assertEquals("R", node.getParent().getValue());
		assertTrue(node.getValue() instanceof IJavaElement);
		assertFalse(node.hasChildren());

	}

	private TreeNode buildThreeLevelTreeWithAnIJavaElementAsNodeValue(
			String value) {
		TreeNode bookmark = new TreeNode("Bookmark", true);

		TreeNode levelOneLeft = new TreeNode("L");
		bookmark.addChild(levelOneLeft);

		TreeNode levelOneRight = new TreeNode("R");
		bookmark.addChild(levelOneRight);

		TreeNode levelOneLeftTwoLeft = new TreeNode("LL");
		levelOneLeft.addChild(levelOneLeftTwoLeft);

		TreeNode levelOneLeftTwoRight = new TreeNode("LR");
		levelOneLeft.addChild(levelOneLeftTwoRight);

		TreeNode levelOneRightTwoLeft = new TreeNode(
				TreeValueConverter.attemptTransformationToIJavaElement(value));
		levelOneRight.addChild(levelOneRightTwoLeft);

		TreeNode levelOneRightTwoRight = new TreeNode("RR");
		levelOneRight.addChild(levelOneRightTwoRight);

		return bookmark;
	}

	@Test
	public void testClimbUpInHierarchyUntilLevelBelowBookmark() {
		TreeNode leaf = getTreeUpsideDown();

		TreeNode node = TreeUtil.climbUpUntilLevelBelowBookmark(leaf);

		assertEquals("node", node.getValue());
		assertTrue(node.getParent().isBookmarkNode());
	}

	private TreeNode getTreeUpsideDown() {

		TreeNode leaf = new TreeNode("leaf");
		TreeNode node = new TreeNode("node");
		node.addChild(leaf);
		TreeNode bookmark = new TreeNode("Bookmark", true);
		bookmark.addChild(node);

		return leaf;
	}

}
