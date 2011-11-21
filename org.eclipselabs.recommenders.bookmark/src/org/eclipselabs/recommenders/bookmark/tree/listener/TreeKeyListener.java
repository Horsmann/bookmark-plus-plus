package org.eclipselabs.recommenders.bookmark.tree.listener;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipselabs.recommenders.bookmark.tree.node.TreeNode;

public class TreeKeyListener implements KeyListener {
	private TreeViewer viewer = null;
	private Action showInEditor = null;

	public TreeKeyListener(TreeViewer viewer, Action showInEditor) {
		this.viewer = viewer;
		this.showInEditor = showInEditor;
	}

	@Override
	public void keyPressed(KeyEvent e) {

		checkForNodeDeletion(e);
		checkForOpenNodeInEditor(e);
		checkForRename(e);

	}

	private void checkForRename(KeyEvent e) {
		if (e.keyCode == SWT.F2 && !isShiftOrAltOrCrtlPressed(e.stateMask))
			checkForBookmarkAndChangeName(e);

	}

	private void checkForBookmarkAndChangeName(KeyEvent e) {
		TreeItem [] items = getSelections(e);
		performRenameForBookmarks(items);
	}

	private void performRenameForBookmarks(TreeItem[] items) {
//		Text text = new Text(viewer, SWT.NONE);
	}

	private void checkForOpenNodeInEditor(KeyEvent e) {
		if (e.keyCode == SWT.CR && !isShiftOrAltOrCrtlPressed(e.stateMask))
			showInEditor.run();
	}

	private void checkForNodeDeletion(KeyEvent e) {
		if (isDeleteNodeEvent(e.keyCode, e.stateMask)) {
			TreeItem[] items = getSelections(e);
			performDeletion(items);
			viewer.refresh();
		}
	}

	private void performDeletion(TreeItem[] items) {
		if (items == null || items.length == 0)
			return;
		for (TreeItem item : items) {
			TreeNode node = (TreeNode) item.getData();
			node.getParent().removeChild(node);
			node.setParent(null);
		}

	}

	private TreeItem[] getSelections(KeyEvent e) {
		TreeItem[] items = null;
		if (e.getSource() instanceof Tree) {
			Tree tree = (Tree) e.getSource();
			items = tree.getSelection();
		}

		return items;
	}

	private boolean isDeleteNodeEvent(int key, int state) {

		return (isBackSpace(key) || isDelete(key))
				&& !isShiftOrAltOrCrtlPressed(state);
	}

	private boolean isDelete(int key) {
		return (key == SWT.DEL);
	}

	private boolean isBackSpace(int key) {
		return (key == 8);
	}

	private boolean isShiftOrAltOrCrtlPressed(int state) {
		return ((state & SWT.ALT) != 0 || (state & SWT.CTRL) != 0 || (state & SWT.SHIFT) != 0);

	}

	@Override
	public void keyReleased(KeyEvent e) {

	};

}
