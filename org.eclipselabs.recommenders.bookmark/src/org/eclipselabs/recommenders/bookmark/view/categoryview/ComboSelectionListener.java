package org.eclipselabs.recommenders.bookmark.view.categoryview;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.view.ViewManager;

public class ComboSelectionListener
	implements SelectionListener
{

	private Combo combo;
	private ViewManager manager;

	public ComboSelectionListener(Combo combo, ViewManager manager)
	{
		this.combo = combo;
		this.manager = manager;
	}

	@Override
	public void widgetSelected(SelectionEvent e)
	{

		TreeModel model = manager.getActiveBookmarkView().getModel();

		manager.removeCurrentlyVisibleNodesFromStorage();
		manager.addCurrentlyExpandedNodesToStorage();

		int index = combo.getSelectionIndex();
		BMNode[] bookmarks = model.getModelRoot().getChildren();

		model.setHeadNode(bookmarks[index]);
		TreeViewer viewer = manager.getActiveBookmarkView().getView();

		viewer.setInput(null);
		viewer.setInput(model.getModelHead());
		manager.setStoredExpandedNodesForActiveView();

		if (manager.isViewFlattened()) {
			manager.activateFlattenedModus(bookmarks[index]);
		}

	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e)
	{

	}

}
