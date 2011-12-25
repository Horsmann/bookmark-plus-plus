package org.eclipselabs.recommenders.bookmark.view.defaultView;

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
import org.eclipselabs.recommenders.bookmark.view.BookmarkView;
import org.eclipselabs.recommenders.bookmark.view.ControlNotifier;
import org.eclipselabs.recommenders.bookmark.view.ViewManager;
import org.eclipselabs.recommenders.bookmark.view.actions.CloseAllOpenEditorsAction;
import org.eclipselabs.recommenders.bookmark.view.actions.CreateNewBookmarkAction;
import org.eclipselabs.recommenders.bookmark.view.actions.DeleteAction;
import org.eclipselabs.recommenders.bookmark.view.actions.OpenFileInSystemExplorerAction;
import org.eclipselabs.recommenders.bookmark.view.actions.RefreshViewAction;
import org.eclipselabs.recommenders.bookmark.view.actions.RenameBookmarkAction;
import org.eclipselabs.recommenders.bookmark.view.actions.SelfEnabling;
import org.eclipselabs.recommenders.bookmark.view.actions.ShowBookmarksInEditorAction;
import org.eclipselabs.recommenders.bookmark.view.actions.ToggleFlatAndTreeAction;
import org.eclipselabs.recommenders.bookmark.view.actions.ToggleViewAction;
import org.eclipselabs.recommenders.bookmark.view.tree.DefaultTreeDropListener;
import org.eclipselabs.recommenders.bookmark.view.tree.TreeContentProvider;
import org.eclipselabs.recommenders.bookmark.view.tree.TreeDoubleclickListener;
import org.eclipselabs.recommenders.bookmark.view.tree.TreeDragListener;
import org.eclipselabs.recommenders.bookmark.view.tree.TreeKeyListener;
import org.eclipselabs.recommenders.bookmark.view.tree.TreeLabelProvider;
import org.eclipselabs.recommenders.bookmark.view.tree.TreeSelectionListener;

public class DefaultView
	implements BookmarkView
{

	private TreeViewer viewer;
	private Composite composite;
	private TreeModel model;
	private TreeModel flatModel;
	private Action showInEditor;
	private Action closeAllOpenEditors;
	private Action refreshView;
	private Action openInSystemFileExplorer;
	private Action toggleLevel;
	private Action newBookmark;
	private Action deleteSelection;
	private Action renameBookmark;
	private Action toggleFlatTree;

	private ViewManager manager;

	private ControlNotifier notifier;

	private TreeKeyListener keyListener;
	private TreeSelectionListener selectionListener;
	private TreeDoubleclickListener doubleClickListener;
	private TreeDragListener dragListener;
	private DefaultTreeDropListener dropListener;

	public DefaultView(ViewManager manager, Composite parent, TreeModel model,
			TreeModel flatModel)
	{
		this.model = model;
		this.flatModel = flatModel;
		this.manager = manager;

		composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout());
		viewer = new TreeViewer(composite, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);

		viewer.setContentProvider(new TreeContentProvider());
		viewer.setLabelProvider(new TreeLabelProvider(manager));
		viewer.setInput(model.getModelRoot());

		initializerActionsListenerAndMenus();

		addListenerToView();
		addListenerToTreeInView();
		//
	}

	private void initializerActionsListenerAndMenus()
	{
		createActions();
		setUpContextMenu();
		initializeControlNotifier();

		keyListener = new TreeKeyListener(this, showInEditor);
		selectionListener = new TreeSelectionListener(notifier);
		doubleClickListener = new TreeDoubleclickListener(showInEditor);

		dragListener = new TreeDragListener(viewer);
		dropListener = new DefaultTreeDropListener(this, dragListener);

	}

	private void initializeControlNotifier()
	{
		notifier = new ControlNotifier();

		notifier.add((SelfEnabling) openInSystemFileExplorer);
		notifier.add((SelfEnabling) showInEditor);
		notifier.add((SelfEnabling) deleteSelection);
		notifier.add((SelfEnabling) renameBookmark);

	}

	private void addListenerToTreeInView()
	{
		viewer.getTree().addKeyListener(keyListener);

		viewer.getTree().addSelectionListener(selectionListener);
	}

	private void addListenerToView()
	{
		addDragDropSupportToView();
		viewer.addDoubleClickListener(doubleClickListener);

	}

	private void updateEnableStatusOfControls()
	{
		notifier.fire();
	}

	public void addDragDropSupportToView()
	{
		int operations = DND.DROP_LINK | DND.DROP_COPY;
		Transfer[] transferTypes = new Transfer[] {
				ResourceTransfer.getInstance(),
				LocalSelectionTransfer.getTransfer() };

		viewer.addDropSupport(operations, transferTypes, dropListener);
		viewer.addDragSupport(operations, transferTypes, dragListener);
	}

	private void createActions()
	{
		showInEditor = new ShowBookmarksInEditorAction(manager.getViewPart(),
				viewer);
		closeAllOpenEditors = new CloseAllOpenEditorsAction();
		refreshView = new RefreshViewAction(this);
		openInSystemFileExplorer = new OpenFileInSystemExplorerAction(viewer);
		toggleLevel = new ToggleViewAction(manager, this);
		newBookmark = new CreateNewBookmarkAction(manager);
		deleteSelection = new DeleteAction(this);
		renameBookmark = new RenameBookmarkAction(this);
		toggleFlatTree = new ToggleFlatAndTreeAction(manager);
	}

	public void setUpToolbarForViewPart()
	{

		IToolBarManager mgr = manager.getViewPart().getViewSite()
				.getActionBars().getToolBarManager();
		mgr.removeAll();
		mgr.add(showInEditor);
		mgr.add(refreshView);
		mgr.add(closeAllOpenEditors);
		mgr.add(new Separator());
		mgr.add(toggleLevel);
		mgr.add(toggleFlatTree);
		mgr.add(newBookmark);

		mgr.update(true);
	}

	private void setUpContextMenu()
	{
		final MenuManager menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);

		menuMgr.addMenuListener(new IMenuListener()
		{
			public void menuAboutToShow(IMenuManager mgr)
			{
				menuMgr.add(showInEditor);
				menuMgr.add(refreshView);
				menuMgr.add(renameBookmark);
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
	public TreeViewer getView()
	{
		return viewer;
	}

	@Override
	public void updateControls()
	{
		viewer.refresh(true);
		updateEnableStatusOfControls();
	}

	@Override
	public TreeModel getModel()
	{
		return model;
	}

	public Composite getComposite()
	{
		return composite;
	}

	@Override
	public ViewManager getManager()
	{
		return manager;
	}

	@Override
	public TreeModel getFlatModel()
	{
		return flatModel;

	}
}
