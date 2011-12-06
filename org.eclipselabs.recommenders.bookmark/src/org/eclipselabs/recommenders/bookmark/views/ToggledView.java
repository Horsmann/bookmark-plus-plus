package org.eclipselabs.recommenders.bookmark.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.part.ResourceTransfer;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.view.actions.CloseAllOpenEditorsAction;
import org.eclipselabs.recommenders.bookmark.view.actions.DeleteAction;
import org.eclipselabs.recommenders.bookmark.view.actions.OpenFileInSystemExplorerAction;
import org.eclipselabs.recommenders.bookmark.view.actions.RefreshViewAction;
import org.eclipselabs.recommenders.bookmark.view.actions.SelfEnabling;
import org.eclipselabs.recommenders.bookmark.view.actions.ShowBookmarksInEditorAction;
import org.eclipselabs.recommenders.bookmark.view.actions.ToggleLevelAction;
import org.eclipselabs.recommenders.bookmark.view.tree.TreeContentProvider;
import org.eclipselabs.recommenders.bookmark.view.tree.TreeDoubleclickListener;
import org.eclipselabs.recommenders.bookmark.view.tree.TreeDragListener;
import org.eclipselabs.recommenders.bookmark.view.tree.TreeDropListener;
import org.eclipselabs.recommenders.bookmark.view.tree.TreeKeyListener;
import org.eclipselabs.recommenders.bookmark.view.tree.TreeLabelProvider;
import org.eclipselabs.recommenders.bookmark.view.tree.TreeSelectionListener;

public class ToggledView implements BookmarkView {

	TreeViewer viewer = null;
	Composite composite = null;
	private Combo combo=null;

	private TreeModel model = null;

	private Action showInEditor = null;
	private Action closeAllOpenEditors = null;
	private Action refreshView = null;
	private Action openInSystemFileExplorer = null;
	private Action toggleLevel = null;
	private Action deleteSelection = null;

	private ViewManager manager = null;

	public ToggledView(ViewManager manager, Composite parent, TreeModel model) {

		this.manager = manager;
		this.model = model;

		composite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		composite.setLayout(gridLayout);

		viewer = new TreeViewer(composite, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);

		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		viewer.getControl().setLayoutData(gridData);

		combo = new Combo(composite, SWT.SINGLE | SWT.V_SCROLL);
		combo.add("test");
		combo.add("test2");
		combo.add("test3");
		gridData = new GridData(SWT.FILL, SWT.VERTICAL, true, false);
		combo.setLayoutData(gridData);

		createActions();
		setUpContextMenu();

		viewer.setContentProvider(new TreeContentProvider());
		viewer.setLabelProvider(new TreeLabelProvider());
		viewer.setInput(model.getModelRoot());

		addListenerToView();
		addListenerToTreeInView();
	}

	private void createActions() {

		showInEditor = new ShowBookmarksInEditorAction(manager.getViewPart(),
				viewer);
		closeAllOpenEditors = new CloseAllOpenEditorsAction();
		refreshView = new RefreshViewAction(viewer);
		openInSystemFileExplorer = new OpenFileInSystemExplorerAction(viewer);
		toggleLevel = new ToggleLevelAction(manager, this, model);
		deleteSelection = new DeleteAction(viewer);

	}

	private void addListenerToTreeInView() {
		viewer.getTree().addKeyListener(
				new TreeKeyListener(viewer, model, showInEditor));

		TreeSelectionListener selectionListener = new TreeSelectionListener();
		selectionListener.add((SelfEnabling) openInSystemFileExplorer);
		selectionListener.add((SelfEnabling) showInEditor);
		selectionListener.add((SelfEnabling) deleteSelection);
		viewer.getTree().addSelectionListener(selectionListener);
	}

	void setUpToolbarForViewPart() {

		IToolBarManager mgr = manager.getViewPart().getViewSite()
				.getActionBars().getToolBarManager();
		mgr.removeAll();
		mgr.add(showInEditor);
		mgr.add(refreshView);
		mgr.add(closeAllOpenEditors);
		mgr.add(new Separator());
		mgr.add(toggleLevel);
		mgr.add(deleteSelection);

		mgr.update(true);
	}

	private void addListenerToView() {
		addDragDropSupportToView(viewer, model);
		viewer.addDoubleClickListener(new TreeDoubleclickListener(showInEditor));

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

	private void setUpContextMenu() {
		final MenuManager menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);

		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager mgr) {
				menuMgr.add(showInEditor);
				menuMgr.add(refreshView);
				menuMgr.add(new Separator());
				menuMgr.add(toggleLevel);
				menuMgr.add(deleteSelection);
				menuMgr.add(new Separator());
				menuMgr.add(openInSystemFileExplorer);
			}
		});

		menuMgr.update(true);

		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);

		manager.getViewPart().getSite().registerContextMenu(menuMgr, viewer);
	}

	@Override
	public TreeViewer getView() {
		return viewer;
	}

}
