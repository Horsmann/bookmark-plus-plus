package org.eclipselabs.recommenders.bookmark.view;

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
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.part.ResourceTransfer;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.view.actions.CloseAllOpenEditorsAction;
import org.eclipselabs.recommenders.bookmark.view.actions.CreateNewBookmarkAction;
import org.eclipselabs.recommenders.bookmark.view.actions.DeleteAction;
import org.eclipselabs.recommenders.bookmark.view.actions.OpenFileInSystemExplorerAction;
import org.eclipselabs.recommenders.bookmark.view.actions.RefreshViewAction;
import org.eclipselabs.recommenders.bookmark.view.actions.SelfEnabling;
import org.eclipselabs.recommenders.bookmark.view.actions.ShowBookmarksInEditorAction;
import org.eclipselabs.recommenders.bookmark.view.actions.ToggleViewAction;
import org.eclipselabs.recommenders.bookmark.view.tree.DefaultTreeDropListener;
import org.eclipselabs.recommenders.bookmark.view.tree.TreeContentProvider;
import org.eclipselabs.recommenders.bookmark.view.tree.TreeDoubleclickListener;
import org.eclipselabs.recommenders.bookmark.view.tree.TreeDragListener;
import org.eclipselabs.recommenders.bookmark.view.tree.TreeKeyListener;
import org.eclipselabs.recommenders.bookmark.view.tree.TreeLabelProvider;
import org.eclipselabs.recommenders.bookmark.view.tree.TreeSelectionListener;

public class DefaultView implements BookmarkView {

	TreeViewer viewer = null;
	Composite composite = null;
	private TreeModel model = null;
	private Action showInEditor = null;
//	private Action exportBookmarks = null;
//	private Action importBookmarks = null;
	private Action closeAllOpenEditors = null;
	private Action refreshView = null;
	private Action openInSystemFileExplorer = null;
	private Action toggleLevel = null;
	private Action newBookmark = null;
	private Action deleteSelection = null;

	private ViewManager manager = null;

	private ControlNotifier notifier = null;

	private TreeKeyListener keyListener = null;
	private TreeSelectionListener selectionListener = null;
	private TreeDoubleclickListener doubleClickListener = null;
	private TreeDragListener dragListener = null;
	private DefaultTreeDropListener dropListener = null;

	public DefaultView(ViewManager manager, Composite parent, TreeModel model) {
		this.model = model;
		this.manager = manager;

		composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout());
		viewer = new TreeViewer(composite, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);
		
		viewer.setContentProvider(new TreeContentProvider());
		viewer.setLabelProvider(new TreeLabelProvider());
		viewer.setInput(model.getModelRoot());

		initializerActionsListenerAndMenus();

		addListenerToView();
		addListenerToTreeInView();
	}

	private void initializerActionsListenerAndMenus() {
		createActions();
		setUpContextMenu();
		initializeControlNotifier();

		keyListener = new TreeKeyListener(this, showInEditor);
		selectionListener = new TreeSelectionListener(notifier);
		doubleClickListener = new TreeDoubleclickListener(showInEditor);

		dragListener = new TreeDragListener(viewer);
		dropListener = new DefaultTreeDropListener(this, dragListener,
				keyListener);

		
	}

	private void initializeControlNotifier() {
		notifier = new ControlNotifier();

		notifier.add((SelfEnabling) openInSystemFileExplorer);
		notifier.add((SelfEnabling) showInEditor);
		notifier.add((SelfEnabling) deleteSelection);
		notifier.add((SelfEnabling) toggleLevel);

	}

	private void addListenerToTreeInView() {
		viewer.getTree().addKeyListener(keyListener);

		viewer.getTree().addSelectionListener(selectionListener);
	}

	private void addListenerToView() {
		addDragDropSupportToView();
		viewer.addDoubleClickListener(doubleClickListener);

	}

	private void updateEnableStatusOfControls() {
		notifier.fire();
	}

	public void addDragDropSupportToView() {
		int operations = DND.DROP_LINK;
		Transfer[] transferTypes = new Transfer[] {
				ResourceTransfer.getInstance(),
				LocalSelectionTransfer.getTransfer() };

		viewer.addDropSupport(operations, transferTypes, dropListener);
		viewer.addDragSupport(operations, transferTypes, dragListener);
	}

	private void createActions() {
		showInEditor = new ShowBookmarksInEditorAction(manager.getViewPart(),
				viewer);
//		exportBookmarks = new ExportBookmarksAction(this);
//		importBookmarks = new ImportBookmarksAction(this);
		closeAllOpenEditors = new CloseAllOpenEditorsAction();
		refreshView = new RefreshViewAction(this);
		openInSystemFileExplorer = new OpenFileInSystemExplorerAction(viewer);
		toggleLevel = new ToggleViewAction(manager, this);
		newBookmark = new CreateNewBookmarkAction(this);
		deleteSelection = new DeleteAction(this);
	}

	void setUpToolbarForViewPart() {

		IToolBarManager mgr = manager.getViewPart().getViewSite()
				.getActionBars().getToolBarManager();
		mgr.removeAll();
		mgr.add(showInEditor);
		mgr.add(refreshView);
		mgr.add(closeAllOpenEditors);
//		mgr.add(exportBookmarks);
//		mgr.add(importBookmarks);
		mgr.add(new Separator());
		mgr.add(toggleLevel);
		mgr.add(newBookmark);
		mgr.add(deleteSelection);

		mgr.update(true);
	}

	private void setUpContextMenu() {
		final MenuManager menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);

		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager mgr) {
				menuMgr.add(showInEditor);
				menuMgr.add(refreshView);
//				menuMgr.add(exportBookmarks);
//				menuMgr.add(importBookmarks);
				menuMgr.add(new Separator());
				menuMgr.add(toggleLevel);
				menuMgr.add(newBookmark);
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

	@Override
	public boolean requiresSelectionForToggle() {
		return true;
	}

	@Override
	public void updateControls() {
		viewer.refresh(true);
		updateEnableStatusOfControls();
	}

	@Override
	public TreeModel getModel() {
		return model;
	}

}
