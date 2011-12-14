package org.eclipselabs.recommenders.bookmark.view.categoryview;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ToolTip;
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
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.view.BookmarkView;
import org.eclipselabs.recommenders.bookmark.view.ControlNotifier;
import org.eclipselabs.recommenders.bookmark.view.ViewManager;
import org.eclipselabs.recommenders.bookmark.view.actions.CloseAllOpenEditorsAction;
import org.eclipselabs.recommenders.bookmark.view.actions.CreateNewBookmarkAction;
import org.eclipselabs.recommenders.bookmark.view.actions.DeleteAction;
import org.eclipselabs.recommenders.bookmark.view.actions.OpenFileInSystemExplorerAction;
import org.eclipselabs.recommenders.bookmark.view.actions.RefreshViewAction;
import org.eclipselabs.recommenders.bookmark.view.actions.SelfEnabling;
import org.eclipselabs.recommenders.bookmark.view.actions.ShowBookmarksInEditorAction;
import org.eclipselabs.recommenders.bookmark.view.actions.ToggleViewAction;
import org.eclipselabs.recommenders.bookmark.view.tree.CategoryTreeDropListener;
import org.eclipselabs.recommenders.bookmark.view.tree.TreeContentProvider;
import org.eclipselabs.recommenders.bookmark.view.tree.TreeDoubleclickListener;
import org.eclipselabs.recommenders.bookmark.view.tree.TreeKeyListener;
import org.eclipselabs.recommenders.bookmark.view.tree.TreeLabelProvider;
import org.eclipselabs.recommenders.bookmark.view.tree.TreeSelectionListener;
import org.eclipselabs.recommenders.dialog.ComboFakeTooltip;

public class CategoryView implements BookmarkView {

	private TreeViewer viewer = null;
	private Composite composite = null;
	private Combo combo = null;
	private ComboFakeTooltip fakeToolTip=null;

	private TreeModel model = null;

	private Action showInEditor = null;
	private Action closeAllOpenEditors = null;
	private Action refreshView = null;
	private Action openInSystemFileExplorer = null;
	private Action toggleLevel = null;
	private Action deleteSelection = null;
	private Action newBookmark = null;

	private ViewManager manager = null;

	private GridLayout gridLayout = null;

	private ControlNotifier notifier = null;
	private TreeKeyListener keyListener = null;
	private TreeSelectionListener selectionListener = null;
	private TreeDoubleclickListener doubleClickListener = null;
	private TreeFocusListener focusListener = null;
	private CategoryTreeDropListener dropListener = null;

	public CategoryView(ViewManager manager, Composite parent, TreeModel model) {

		this.manager = manager;
		this.model = model;

		composite = new Composite(parent, SWT.NONE);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		composite.setLayout(gridLayout);

		viewer = new TreeViewer(composite, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);

		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		viewer.getControl().setLayoutData(gridData);

		assembleComboBox();

		viewer.setContentProvider(new TreeContentProvider());
		viewer.setLabelProvider(new TreeLabelProvider());
		viewer.setInput(model.getModelRoot());

		initializerActionsListenerAndMenus();

		addListenerToView();
		addListenerToTreeInView();
	}

	private void assembleComboBox() {
		combo = new Combo(composite, SWT.SIMPLE);

		//Change bookmarks name on "Enter"
		ComboKeyListener comboKeyListener = new ComboKeyListener(this);
		combo.addKeyListener(comboKeyListener);

		//Switch the head node in the model if selection changes
		ComboSelectionListener comboSelectionListener = new ComboSelectionListener(
				combo, model, manager, comboKeyListener);
		combo.addSelectionListener(comboSelectionListener);

		GridData gridData = new GridData(SWT.FILL, SWT.VERTICAL, true, false);
		combo.setLayoutData(gridData);
		
//		fakeToolTip = new ComboFakeTooltip(combo);
	}

	private void initializerActionsListenerAndMenus() {
		createActions();
		setUpContextMenu();
		initializeControlNotifier();

		keyListener = new TreeKeyListener(this, showInEditor);
		selectionListener = new TreeSelectionListener(notifier);
		doubleClickListener = new TreeDoubleclickListener(showInEditor);
		focusListener = new TreeFocusListener(this,notifier);

		dropListener = new CategoryTreeDropListener(this, keyListener);
	}

	private void initializeControlNotifier() {
		notifier = new ControlNotifier();

		notifier.add((SelfEnabling) openInSystemFileExplorer);
		notifier.add((SelfEnabling) showInEditor);
		notifier.add((SelfEnabling) deleteSelection);
		notifier.add((SelfEnabling) toggleLevel);

	}

	public void refreshCategories() {

		combo.removeAll();

		String currentHead = (String) model.getModelHead().getValue();
		int selectIndex = 0;
		TreeNode[] bookmarks = model.getModelRoot().getChildren();
		for (int i = 0; i < bookmarks.length; i++) {
			String name = (String) bookmarks[i].getValue();
			combo.add(name);

			if (currentHead.equals(name)) {
				selectIndex = i;
			}
		}
		combo.select(selectIndex);
	}

	private void createActions() {

		showInEditor = new ShowBookmarksInEditorAction(manager.getViewPart(),
				viewer);
		closeAllOpenEditors = new CloseAllOpenEditorsAction();
		refreshView = new RefreshViewAction(this);
		openInSystemFileExplorer = new OpenFileInSystemExplorerAction(viewer);
		toggleLevel = new ToggleViewAction(manager, this);
		deleteSelection = new DeleteAction(this);
		newBookmark = new CreateNewBookmarkAction(this);

	}

	private void addListenerToTreeInView() {
		viewer.getTree().addKeyListener(keyListener);

		viewer.getTree().addSelectionListener(selectionListener);
		
		viewer.getTree().addFocusListener(focusListener);
	}

	private void updateEnableStatusOfControls() {
		notifier.fire();
	}

	public void setUpToolbarForViewPart() {

		IToolBarManager mgr = manager.getViewPart().getViewSite()
				.getActionBars().getToolBarManager();
		mgr.removeAll();
		mgr.add(showInEditor);
		mgr.add(refreshView);
		mgr.add(closeAllOpenEditors);
		mgr.add(new Separator());
		mgr.add(toggleLevel);
		mgr.add(newBookmark);
//		mgr.add(deleteSelection);

		mgr.update(true);
	}

	private void addListenerToView() {
		addDragDropSupportToView(viewer, model);
		viewer.addDoubleClickListener(doubleClickListener);
	}

	public void addDragDropSupportToView(TreeViewer viewer, TreeModel model) {
		int operations = DND.DROP_LINK;
		Transfer[] transferTypes = new Transfer[] {
				ResourceTransfer.getInstance(),
				LocalSelectionTransfer.getTransfer() };

		viewer.addDropSupport(operations, transferTypes, dropListener);
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
		return false;
	}

	@Override
	public void updateControls() {
		refreshCategories();
		viewer.refresh(true);

		updateEnableStatusOfControls();
	}

	@Override
	public TreeModel getModel() {
		return model;
	}
	
	public Composite getComposite() {
		return composite;
	}

	@Override
	public ViewManager getManager()
	{
		return manager;
	}

}
