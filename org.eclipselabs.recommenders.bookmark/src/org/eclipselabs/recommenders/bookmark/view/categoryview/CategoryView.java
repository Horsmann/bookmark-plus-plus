package org.eclipselabs.recommenders.bookmark.view.categoryview;

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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.part.ResourceTransfer;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
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
import org.eclipselabs.recommenders.bookmark.view.actions.ToggleFlatAndTreeAction;
import org.eclipselabs.recommenders.bookmark.view.actions.ToggleViewAction;
import org.eclipselabs.recommenders.bookmark.view.tree.CategoryTreeDropListener;
import org.eclipselabs.recommenders.bookmark.view.tree.TreeContentProvider;
import org.eclipselabs.recommenders.bookmark.view.tree.TreeDoubleclickListener;
import org.eclipselabs.recommenders.bookmark.view.tree.TreeKeyListener;
import org.eclipselabs.recommenders.bookmark.view.tree.TreeLabelProvider;
import org.eclipselabs.recommenders.bookmark.view.tree.TreeSelectionListener;

public class CategoryView
	implements BookmarkView
{

	private TreeViewer viewer;
	private Composite mainComposite;

	private Composite comboComposite;
	private Combo combo;
	private Button saveBookmarkNameChanges;

	private TreeModel model;
	private TreeModel flatModel;

	private Action showInEditor;
	private Action closeAllOpenEditors;
	private Action refreshView;
	private Action openInSystemFileExplorer;
	private Action toggleLevel;
	private Action deleteSelection;
	private Action newBookmark;
	private Action toggleFlatTree;

	private ViewManager manager;

	private GridLayout gridLayout;

	private ControlNotifier notifier;
	private TreeKeyListener keyListener;
	private TreeSelectionListener selectionListener;
	private TreeDoubleclickListener doubleClickListener;
	private TreeFocusListener focusListener;
	private CategoryTreeDropListener dropListener;

	public CategoryView(ViewManager manager, Composite parent, TreeModel model,
			TreeModel flatModel)
	{

		this.manager = manager;
		this.model = model;
		this.flatModel = flatModel;

		mainComposite = new Composite(parent, SWT.NONE);

		setUpMainCompositesContent();

	}

	private void setUpMainCompositesContent()
	{
		addTreeViewerToMainComposite();
		addComboCompositeToMainComposite();

		configureTreeViewer();

		initializerActionsListenerAndMenus();

		addListenerToView();
		addListenerToTreeInView();
	}

	private void configureTreeViewer()
	{
		viewer.setContentProvider(new TreeContentProvider());
		viewer.setLabelProvider(new TreeLabelProvider(manager));
		viewer.setInput(model.getModelRoot());
	}

	private void addTreeViewerToMainComposite()
	{
		gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		mainComposite.setLayout(gridLayout);

		viewer = new TreeViewer(mainComposite, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);

		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		viewer.getControl().setLayoutData(gridData);
	}

	private void addComboCompositeToMainComposite()
	{
		comboComposite = new Composite(mainComposite, SWT.NONE);
		setComboCompositeLayout();
		setUpAndAddLabelToComboComposite();
		setUpAndAddComboBoxToComboComposite();
		setUpAndAddSaveButtonToComboComposite();

		addListenerToComboCompositeControls();

		saveBookmarkNameChanges.setEnabled(false);
	}

	private void addListenerToComboCompositeControls()
	{
		addListenerToComboBox();

		addListenerToSaveButton();
	}

	private void addListenerToComboBox()
	{
		ComboModifyListener comboModifyListener = new ComboModifyListener(
				saveBookmarkNameChanges, this);
		combo.addModifyListener(comboModifyListener);

		// Switch the head node in the model if selection changes
		ComboSelectionListener comboSelectionListener = new ComboSelectionListener(
				combo, manager);
		combo.addSelectionListener(comboSelectionListener);
	}

	private void setUpAndAddSaveButtonToComboComposite()
	{
		saveBookmarkNameChanges = new Button(comboComposite, SWT.NONE);

		Image image = Activator.getDefault().getImageRegistry()
				.get(Activator.ICON_SAVE);
		saveBookmarkNameChanges.setImage(image);

		GridData gridData = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		saveBookmarkNameChanges.setLayoutData(gridData);
	}

	private void addListenerToSaveButton()
	{
		saveBookmarkNameChanges.addListener(SWT.MouseDown,
				new ComboSaveButtonListener(this, saveBookmarkNameChanges,
						combo));

	}

	private void setUpAndAddComboBoxToComboComposite()
	{
		combo = new Combo(comboComposite, SWT.SIMPLE);

		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		combo.setLayoutData(gridData);
	}

	private void setUpAndAddLabelToComboComposite()
	{
		Label label = new Label(comboComposite, SWT.NONE);

		Image image = Activator.getDefault().getImageRegistry()
				.get(Activator.ICON_BOOKMARK);
		label.setImage(image);
		GridData gridData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		label.setLayoutData(gridData);

	}

	private void setComboCompositeLayout()
	{
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		comboComposite.setLayout(gridLayout);
		GridData gridData = new GridData(SWT.FILL, SWT.VERTICAL, true, false);
		comboComposite.setLayoutData(gridData);
	}

	private void initializerActionsListenerAndMenus()
	{
		createActions();
		setUpContextMenu();
		initializeControlNotifier();

		keyListener = new TreeKeyListener(this, showInEditor);
		selectionListener = new TreeSelectionListener(notifier);
		doubleClickListener = new TreeDoubleclickListener(showInEditor);
		focusListener = new TreeFocusListener(this, notifier);

		dropListener = new CategoryTreeDropListener(this);
	}

	private void initializeControlNotifier()
	{
		notifier = new ControlNotifier();

		notifier.add((SelfEnabling) openInSystemFileExplorer);
		notifier.add((SelfEnabling) showInEditor);
		notifier.add((SelfEnabling) deleteSelection);

	}

	public void refreshCategories()
	{

		combo.removeAll();

		BMNode head = model.getModelHead();

		if (head.getValue() == null)
			return;

		BMNode currentHead = model.getModelHead();
		int selectIndex = rebuildCategoryNamesInCombobox(currentHead);
		combo.select(selectIndex);
	}

	private int rebuildCategoryNamesInCombobox(BMNode currentHead)
	{
		int selectIndex = 0;
		BMNode[] bookmarks = model.getModelRoot().getChildren();
		for (int i = 0; i < bookmarks.length; i++) {

			String name = (String) bookmarks[i].getValue();
			combo.add(name);

			if (currentHead == bookmarks[i]) {
				selectIndex = i;
			}
		}
		return selectIndex;
	}

	private void createActions()
	{

		showInEditor = new ShowBookmarksInEditorAction(manager.getViewPart(),
				viewer);
		closeAllOpenEditors = new CloseAllOpenEditorsAction();
		refreshView = new RefreshViewAction(this);
		openInSystemFileExplorer = new OpenFileInSystemExplorerAction(viewer);
		toggleLevel = new ToggleViewAction(manager, this);
		deleteSelection = new DeleteAction(this);
		newBookmark = new CreateNewBookmarkAction(this);
		toggleFlatTree = new ToggleFlatAndTreeAction(manager);

	}

	private void addListenerToTreeInView()
	{
		viewer.getTree().addKeyListener(keyListener);

		viewer.getTree().addSelectionListener(selectionListener);

		viewer.getTree().addFocusListener(focusListener);
	}

	private void updateEnableStatusOfControls()
	{
		notifier.fire();
		saveBookmarkNameChanges.setEnabled(false);
	}

	private void addListenerToView()
	{
		addDragDropSupportToView(viewer, model);
		viewer.addDoubleClickListener(doubleClickListener);
	}

	public void addDragDropSupportToView(TreeViewer viewer, TreeModel model)
	{
		int operations = DND.DROP_LINK;
		Transfer[] transferTypes = new Transfer[] {
				ResourceTransfer.getInstance(),
				LocalSelectionTransfer.getTransfer() };

		viewer.addDropSupport(operations, transferTypes, dropListener);
	}

	@Override
	public TreeViewer getView()
	{
		return viewer;
	}

	@Override
	public void updateControls()
	{
		refreshCategories();
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
		return mainComposite;
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

}
