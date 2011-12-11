package org.eclipselabs.recommenders.bookmark.view.categoryview;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;

public class ComboKeyListener implements KeyListener {

	private CategoryView view;

	private int currentEntry = -1;

	public ComboKeyListener(CategoryView view) {
		this.view = view;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		Combo combo = (Combo) e.getSource();
		if (currentEntry == -1) {
			currentEntry = combo.getSelectionIndex();
		}

		if (e.keyCode == SWT.CR) {

			String newBookmarkName = combo.getText();

			TreeNode bookmark = view.getModel().getModelRoot().getChildren()[currentEntry];
			bookmark.setValue(newBookmarkName);
			view.refreshCategories();
		}

	}

	public void selectionChanged() {
		currentEntry = -1;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

}
