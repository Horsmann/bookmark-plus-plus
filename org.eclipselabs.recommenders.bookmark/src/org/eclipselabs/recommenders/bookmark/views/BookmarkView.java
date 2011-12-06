package org.eclipselabs.recommenders.bookmark.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.part.ResourceTransfer;
import org.eclipse.ui.part.ViewPart;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.tree.persistent.BookmarkFileIO;
import org.eclipselabs.recommenders.bookmark.tree.persistent.deserialization.RestoredTree;
import org.eclipselabs.recommenders.bookmark.tree.persistent.deserialization.TreeDeserializerFacade;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.view.actions.CloseAllOpenEditorsAction;
import org.eclipselabs.recommenders.bookmark.view.actions.CreateNewBookmarkAction;
import org.eclipselabs.recommenders.bookmark.view.actions.DeleteAction;
import org.eclipselabs.recommenders.bookmark.view.actions.ExportBookmarksAction;
import org.eclipselabs.recommenders.bookmark.view.actions.ImportBookmarksAction;
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

public class BookmarkView extends ViewPart {

	private TreeViewer activeTreeViewer = null;
	private TreeModel model;
	private Action showInEditor = null;
	private Action exportBookmarks = null;
	private Action importBookmarks = null;
	private Action closeAllOpenEditors = null;
	private Action refreshView = null;
	private Action openInSystemFileExplorer = null;
	private Action toggleLevel = null;
	private Action newBookmark = null;
	private Action deleteSelection = null;

	private StackLayout stackLayout = null;
	private Composite container = null;

	private TreeViewer defaultView = null;
	private Composite defaultViewComp = null;

	private TreeViewer viewWithDropDownTreeView = null;
	private Composite viewWithdropDownComp = null;

	// viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);

	@Override
	public void createPartControl(Composite parent) {
		model = new TreeModel();
		////
		container = new Composite(parent, SWT.NONE);
		stackLayout = new StackLayout();
		container.setLayout(stackLayout);
		////
		defaultViewComp = new Composite(container, SWT.NONE);
		defaultViewComp.setLayout(new FillLayout());
		defaultView = new TreeViewer(defaultViewComp, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);
		///
		viewWithdropDownComp = new Composite(container, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		viewWithdropDownComp.setLayout(gridLayout);

		viewWithDropDownTreeView = new TreeViewer(viewWithdropDownComp,
				SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);

		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		viewWithDropDownTreeView.getControl().setLayoutData(gridData);

		Combo combo = new Combo(viewWithdropDownComp, SWT.SINGLE | SWT.V_SCROLL);
		combo.add("test");
		combo.add("test2");
		combo.add("test3");
		gridData = new GridData(SWT.FILL, SWT.VERTICAL, true, false);
		combo.setLayoutData(gridData);
		
		setUpView(defaultView);
		setUpView(viewWithDropDownTreeView);
		
		stackLayout.topControl = defaultViewComp;
		activeTreeViewer = defaultView;

		// stackLayout.topControl = treeView;
		// stackLayout.topControl = dropDown;


		
		
		

//		createActions();
//		setUpToolbar();
//		addContextMenu();
//
//		activeTreeViewer.setContentProvider(new TreeContentProvider());
//		activeTreeViewer.setLabelProvider(new TreeLabelProvider());
//		activeTreeViewer.setInput(model.getModelRoot());
//
//		addListenerToView();
//		addListenerToTreeInView();

		addPartViewFeatures();
		restoreBookmarks();

	}
	
	private void addPartViewFeatures() {
		IPartService service = (IPartService) getSite().getService(
				IPartService.class);
		service.addPartListener(new BookmarkViewPartListener(activeTreeViewer, model));		
	}

	private void setUpView(TreeViewer view) {
		activeTreeViewer = view;
		
		createActions();
		setUpToolbar();
		addContextMenu();

		activeTreeViewer.setContentProvider(new TreeContentProvider());
		activeTreeViewer.setLabelProvider(new TreeLabelProvider());
		activeTreeViewer.setInput(model.getModelRoot());

		addListenerToView();
		addListenerToTreeInView();		
	}

	public TreeViewer getActiveViewer() {
		return activeTreeViewer;
	}
	
	public void activateToggledView() {
		stackLayout.topControl = viewWithdropDownComp;
		activeTreeViewer = viewWithDropDownTreeView;
		container.layout(true, true);
	}
	
	public void activateDefaultView() {
		stackLayout.topControl = defaultViewComp;
		activeTreeViewer = defaultView;
		container.layout(true, true);
	}

	private void addContextMenu() {
		final MenuManager menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager mgr) {
				menuMgr.add(showInEditor);
				menuMgr.add(refreshView);
				menuMgr.add(exportBookmarks);
				menuMgr.add(importBookmarks);
				menuMgr.add(new Separator());
				menuMgr.add(toggleLevel);
				menuMgr.add(newBookmark);
				menuMgr.add(deleteSelection);
				menuMgr.add(new Separator());
				menuMgr.add(openInSystemFileExplorer);
			}
		});

		Menu menu = menuMgr.createContextMenu(activeTreeViewer.getControl());
		activeTreeViewer.getControl().setMenu(menu);

		getSite().registerContextMenu(menuMgr, activeTreeViewer);
	}

	private void addListenerToView() {
		addDragDropSupportToView(activeTreeViewer, model);
		activeTreeViewer.addDoubleClickListener(new TreeDoubleclickListener(showInEditor));

	

	}

	private void addListenerToTreeInView() {
		activeTreeViewer.getTree().addKeyListener(
				new TreeKeyListener(activeTreeViewer, model, showInEditor));

		TreeSelectionListener selectionListener = new TreeSelectionListener();
		selectionListener.add((SelfEnabling) openInSystemFileExplorer);
		selectionListener.add((SelfEnabling) showInEditor);
		selectionListener.add((SelfEnabling) deleteSelection);
		activeTreeViewer.getTree().addSelectionListener(selectionListener);
	}

	private void restoreBookmarks() {
		String[] lines = BookmarkFileIO.loadFromDefaultFile();
		if (lines != null && lines.length > 0) {
			RestoredTree restoredTree = TreeDeserializerFacade
					.deserialize(lines[0]);
			model.setModelRoot(restoredTree.getRoot());
			activeTreeViewer.setInput(model.getModelRoot());
			TreeDeserializerFacade.setExpandedNodesForView(activeTreeViewer,
					restoredTree.getExpanded());
		}

		checkPreferencesForDeletionOfDeadReferences();

	}

	private void checkPreferencesForDeletionOfDeadReferences() {
		IPreferenceStore preferenceStore = Activator.getDefault()
				.getPreferenceStore();
		boolean removeDeadReferences = preferenceStore
				.getBoolean(org.eclipselabs.recommenders.bookmark.preferences.PreferenceConstants.REMOVE_DEAD_BOOKMARK_REFERENCES);

		if (removeDeadReferences) {
			final TreeNode root = model.getModelRoot();

			TreeUtil.deleteNodesReferencingToDeadResourcesUnderNode(root, model);
			activeTreeViewer.refresh();
		}

	}

	private void createActions() {

		showInEditor = new ShowBookmarksInEditorAction(this, activeTreeViewer);
		exportBookmarks = new ExportBookmarksAction(activeTreeViewer, model);
		importBookmarks = new ImportBookmarksAction(activeTreeViewer, model);
		closeAllOpenEditors = new CloseAllOpenEditorsAction();
		refreshView = new RefreshViewAction(activeTreeViewer);
		openInSystemFileExplorer = new OpenFileInSystemExplorerAction(activeTreeViewer);
		toggleLevel = new ToggleLevelAction(this, model);
		newBookmark = new CreateNewBookmarkAction(activeTreeViewer, model);
		deleteSelection = new DeleteAction(activeTreeViewer);

	}

	private void setUpToolbar() {

		IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
		mgr.add(showInEditor);
		mgr.add(refreshView);
		mgr.add(closeAllOpenEditors);
		mgr.add(exportBookmarks);
		mgr.add(importBookmarks);
		mgr.add(new Separator());
		mgr.add(toggleLevel);
		mgr.add(newBookmark);
		mgr.add(deleteSelection);
	}

	@Override
	public void setFocus() {
		activeTreeViewer.getControl().setFocus();
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