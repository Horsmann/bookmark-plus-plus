package org.eclipselabs.recommenders.bookmark.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.part.ResourceTransfer;
import org.eclipse.ui.part.ViewPart;
import org.eclipselabs.recommenders.bookmark.actions.CloseAllOpenEditors;
import org.eclipselabs.recommenders.bookmark.actions.LoadBookmarksAction;
import org.eclipselabs.recommenders.bookmark.actions.ExportBookmarksAction;
import org.eclipselabs.recommenders.bookmark.actions.ShowBookmarksInEditorAction;
import org.eclipselabs.recommenders.bookmark.tree.TreeContentProvider;
import org.eclipselabs.recommenders.bookmark.tree.TreeLabelProvider;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.listener.TreeDoubleclickListener;
import org.eclipselabs.recommenders.bookmark.tree.listener.TreeDragListener;
import org.eclipselabs.recommenders.bookmark.tree.listener.TreeDropListener;
import org.eclipselabs.recommenders.bookmark.tree.listener.TreeKeyListener;

public class BookmarkView extends ViewPart {

	private TreeViewer viewer = null;
	private TreeModel model;
	private Action showInEditor = null;
	private Action saveBookmarks = null;
	private Action loadBookmarks = null;
	private Action closeAllOpenEditors = null;

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		model = new TreeModel();

		addDragDropSupportToView(viewer, model);

		viewer.setContentProvider(new TreeContentProvider());
		viewer.setLabelProvider(new TreeLabelProvider());
		viewer.setInput(model.getModelRoot());

		createActions();
		setUpToolbar();

		viewer.addDoubleClickListener(new TreeDoubleclickListener(showInEditor));
		viewer.getTree().addKeyListener(
				new TreeKeyListener(viewer, showInEditor));
	}

	private void createActions() {

		showInEditor = new ShowBookmarksInEditorAction(this, viewer);
		saveBookmarks = new ExportBookmarksAction(model);
		loadBookmarks = new LoadBookmarksAction(viewer, model);
		closeAllOpenEditors = new CloseAllOpenEditors();

	}

	private void setUpToolbar() {

		IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
		mgr.add(showInEditor);
		mgr.add(closeAllOpenEditors);
		mgr.add(saveBookmarks);
		mgr.add(loadBookmarks);
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public IViewSite getViewSite() {
		return (IViewSite) getSite();
	}

	public void addDragDropSupportToView(TreeViewer viewer, TreeModel model) {
		int operations = DND.DROP_LINK;
		Transfer[] transferTypes = new Transfer[] {
				ResourceTransfer.getInstance(),
				LocalSelectionTransfer.getTransfer() };

		TreeDragListener dragListener = new TreeDragListener(viewer);
		TreeDropListener dropListener = new TreeDropListener(viewer, model,
				dragListener);

		viewer.addDropSupport(operations, transferTypes, dropListener);
		viewer.addDragSupport(operations, transferTypes, dragListener);
	}

}