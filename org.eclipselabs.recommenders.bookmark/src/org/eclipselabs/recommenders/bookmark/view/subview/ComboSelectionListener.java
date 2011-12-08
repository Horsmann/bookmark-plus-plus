package org.eclipselabs.recommenders.bookmark.view.subview;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;

public class ComboSelectionListener implements SelectionListener {

	private Combo combo;
	private TreeModel model;
	private TreeViewer viewer;

	public ComboSelectionListener(Combo combo, TreeModel model,
			TreeViewer viewer) {
		this.combo = combo;
		this.model = model;
		this.viewer = viewer;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		int index = combo.getSelectionIndex();
		TreeNode[] bookmarks = model.getModelRoot().getChildren();

		model.setHeadNode(bookmarks[index]);
		viewer.setInput(null);
		viewer.setInput(model.getModelHead());
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {

	}

}
