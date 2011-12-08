package org.eclipselabs.recommenders.bookmark.view;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;

public class ComboSelectionListener implements SelectionListener {

	private Combo combo;
	private TreeModel model;
	private ViewManager manager;

	public ComboSelectionListener(Combo combo, TreeModel model,
			ViewManager manager) {
		this.combo = combo;
		this.model = model;
		this.manager = manager;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		
		Object[] currentlyVisibleNodes = TreeUtil.getAllChildsOfNode(model
				.getModelHead());
		for (Object o : currentlyVisibleNodes) {
			manager.deleteExpandedNodeFromStorage(o);
		}
		manager.addCurrentlyExpandedNodesToStorage();
		
		
		int index = combo.getSelectionIndex();
		TreeNode[] bookmarks = model.getModelRoot().getChildren();

		model.setHeadNode(bookmarks[index]);
		TreeViewer viewer = manager.getActiveViewer();
		viewer.setInput(null);
		viewer.setInput(model.getModelHead());
		manager.setStoredExpandedNodesForActiveView();
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {

	}

}
