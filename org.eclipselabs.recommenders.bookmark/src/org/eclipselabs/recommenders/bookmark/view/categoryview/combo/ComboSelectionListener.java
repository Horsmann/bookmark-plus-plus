package org.eclipselabs.recommenders.bookmark.view.categoryview.combo;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.view.ExpandedStorage;
import org.eclipselabs.recommenders.bookmark.view.ViewManager;

public class ComboSelectionListener
	implements SelectionListener
{

	private Combo combo;
	private ViewManager manager;
	private final Button saveBookmarkNameChanges;

	public ComboSelectionListener(Button saveBookmarkNameChanges, Combo combo,
			ViewManager manager)
	{
		this.saveBookmarkNameChanges = saveBookmarkNameChanges;
		this.combo = combo;
		this.manager = manager;
	}

	@Override
	public void widgetSelected(SelectionEvent e)
	{
		ExpandedStorage storage = manager.getExpandedStorage();
		TreeModel model = manager.getModel();

		storage.removeCurrentlyVisibleNodes();
		storage.addCurrentlyExpandedNodes();

		int index = combo.getSelectionIndex();
		BMNode[] bookmarks = model.getModelRoot().getChildren();

		model.setHeadNode(bookmarks[index]);
		TreeViewer viewer = manager.getActiveBookmarkView().getView();

		viewer.setInput(null);
		viewer.setInput(model.getModelHead());
		storage.expandStoredNodesForActiveView();

		if (manager.isViewFlattened()) {
			manager.activateFlattenedModus(bookmarks[index]);
		}

		saveBookmarkNameChanges.setEnabled(false);
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e)
	{

	}

}
