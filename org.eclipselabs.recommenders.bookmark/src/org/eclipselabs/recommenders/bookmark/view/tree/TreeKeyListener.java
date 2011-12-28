package org.eclipselabs.recommenders.bookmark.view.tree;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipselabs.recommenders.bookmark.tree.commands.DeleteSelection;
import org.eclipselabs.recommenders.bookmark.tree.commands.RenameBookmark;
import org.eclipselabs.recommenders.bookmark.view.ViewManager;

public class TreeKeyListener
	implements KeyListener
{
	private Action showInEditor = null;

	private boolean isAltPressed = false;

	private final ViewManager manager;

	public TreeKeyListener(ViewManager manager, Action showInEditor)
	{

		this.manager = manager;
		this.showInEditor = showInEditor;

	}

	@Override
	public void keyPressed(KeyEvent e)
	{

		if (e.keyCode == SWT.ALT) {
			isAltPressed = true;
		}

		checkForNodeDeletion(e);
		checkForOpenNodeInEditor(e);
		checkForRename(e);
	}

	private void checkForRename(KeyEvent e)
	{
		if (e.keyCode == SWT.F2 && e.stateMask == 0) {
			checkForBookmarkAndChangeName(e);
		}

	}

	private void checkForBookmarkAndChangeName(KeyEvent e)
	{
		TreeItem[] items = getSelections(e);
		performRenameForBookmarks(items);
	}

	private void performRenameForBookmarks(TreeItem[] items)
	{

		new RenameBookmark(manager, items[0]).execute();
	}

	private void checkForOpenNodeInEditor(KeyEvent e)
	{
		if (e.keyCode == SWT.CR && e.stateMask == 0) {
			showInEditor.run();
		}
	}

	private void checkForNodeDeletion(KeyEvent e)
	{
		if (isDeleteNodeEvent(e)) {
			new DeleteSelection(manager).execute();
		}
	}

	private TreeItem[] getSelections(KeyEvent e)
	{
		TreeItem[] items = null;
		if (e.getSource() instanceof Tree) {
			Tree tree = (Tree) e.getSource();
			items = tree.getSelection();
		}

		return items;
	}

	private boolean isDeleteNodeEvent(KeyEvent e)
	{
		return (isBackSpace(e.keyCode) || isDelete(e.keyCode))
				&& e.stateMask == 0;
	}

	private boolean isDelete(int key)
	{
		return (key == SWT.DEL);
	}

	private boolean isBackSpace(int key)
	{
		return (key == 8);
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		isAltPressed = false;
	};

	public boolean isAltPressed()
	{
		return isAltPressed;
	}

}
