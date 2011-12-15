package org.eclipselabs.recommenders.bookmark.view.categoryview;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.view.ViewManager;

public class ComboSelectionListener implements SelectionListener {

	private Combo combo;
	private ComboKeyListener keyListener;
	private TreeModel model;
	private ViewManager manager;

	public ComboSelectionListener(Combo combo, TreeModel model,
			ViewManager manager, ComboKeyListener keyListener) {
		this.combo = combo;
		this.model = model;
		this.manager = manager;
		this.keyListener = keyListener;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		
		keyListener.selectionChanged();
		
		Object[] currentlyVisibleNodes = TreeUtil.getTreeBelowNode(model
				.getModelHead());
		for (Object o : currentlyVisibleNodes) {
			manager.deleteExpandedNodeFromStorage(o);
		}
		manager.addCurrentlyExpandedNodesToStorage();
		
		
		int index = combo.getSelectionIndex();
		BMNode[] bookmarks = model.getModelRoot().getChildren();

		model.setHeadNode(bookmarks[index]);
		TreeViewer viewer = manager.getActiveBookmarkView().getView();
		viewer.setInput(null);
		viewer.setInput(model.getModelHead());
		manager.setStoredExpandedNodesForActiveView();
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {

	}

}
