package org.eclipselabs.recommenders.bookmark.view.tree;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.tree.commands.DeleteSelectionCommand;
import org.eclipselabs.recommenders.bookmark.tree.persistent.serialization.TreeSerializerFacade;
import org.eclipselabs.recommenders.bookmark.view.BookmarkView;

public class TreeKeyListener implements KeyListener {
	private BookmarkView viewer = null;
	private Action showInEditor = null;

	private boolean isAltPressed = false;

	public TreeKeyListener(BookmarkView viewer, Action showInEditor) {
		this.viewer = viewer;

		this.showInEditor = showInEditor;

	}

	@Override
	public void keyPressed(KeyEvent e) {

		if (e.keyCode == SWT.ALT) {
			isAltPressed = true;
			System.err.println("keylistener: ALT->true");
		}

		checkForNodeDeletion(e);
		checkForOpenNodeInEditor(e);
		checkForRename(e);
	}

	private void checkForRename(KeyEvent e) {
		if (e.keyCode == SWT.F2 && e.stateMask == 0) {
			checkForBookmarkAndChangeName(e);
		}

	}

	private void checkForBookmarkAndChangeName(KeyEvent e) {
		TreeItem[] items = getSelections(e);
		performRenameForBookmarks(items);
	}

	private void performRenameForBookmarks(TreeItem[] items) {
		final TreeEditor editor = new TreeEditor(viewer.getView().getTree());
		editor.horizontalAlignment = SWT.LEFT;
		editor.minimumWidth = 100;

		final TreeItem item = items[0];

		final TreeNode node = (TreeNode) item.getData();
		if (!node.isBookmarkNode())
			return;

		final Text text = new Text(viewer.getView().getTree(), SWT.NONE);
		text.setText((String) node.getValue());
		text.selectAll();
		text.setFocus();

		// If the text field loses focus, set its text into the tree
		// and end the editing session
		text.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent event) {
				node.setValue(text.getText());
				text.dispose();
				setFocusAndSelection(item);
			}
		});

		text.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent event) {
				switch (event.keyCode) {
				case SWT.CR:
					// drop through
					String newValue = text.getText();
					node.setValue(newValue);
				case SWT.ESC:
					// End editing session
					text.dispose();
					setFocusAndSelection(item);

					saveNewTreeModelState();
					break;
				}
			}
		});

		// Set the text field into the editor
		editor.setEditor(text, item);
	}

	private void saveNewTreeModelState() {
		TreeSerializerFacade.serializeToDefaultLocation(viewer.getView(),
				viewer.getModel());
	}

	private void checkForOpenNodeInEditor(KeyEvent e) {
		if (e.keyCode == SWT.CR && e.stateMask == 0) {
			showInEditor.run();
		}
	}

	private void checkForNodeDeletion(KeyEvent e) {
		if (isDeleteNodeEvent(e)) {
			new DeleteSelectionCommand(viewer).execute();
			saveNewTreeModelState();
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

	private boolean isDeleteNodeEvent(KeyEvent e) {
		return (isBackSpace(e.keyCode) || isDelete(e.keyCode))
				&& e.stateMask == 0;
	}

	private boolean isDelete(int key) {
		return (key == SWT.DEL);
	}

	private boolean isBackSpace(int key) {
		return (key == 8);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		System.err.println("key released");
		isAltPressed = false;
	};

	public boolean isAltPressed() {
		return isAltPressed;
	}

	private void setFocusAndSelection(TreeItem item) {
		viewer.getView().refresh();
		viewer.getView().getTree().setSelection(item);
		viewer.getView().getTree().setFocus();
	}
}
