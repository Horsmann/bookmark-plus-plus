package org.eclipselabs.recommenders.bookmark.views;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.part.ViewPart;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.tree.persistent.BookmarkFileIO;
import org.eclipselabs.recommenders.bookmark.tree.persistent.deserialization.RestoredTree;
import org.eclipselabs.recommenders.bookmark.tree.persistent.deserialization.TreeDeserializerFacade;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;

public class BookmarkViewManager extends ViewPart implements ViewManager {

	// private TreeViewer activeTreeViewer = null;
	private TreeModel model;
	// private Action showInEditor = null;
	// private Action exportBookmarks = null;
	// private Action importBookmarks = null;
	// private Action closeAllOpenEditors = null;
	// private Action refreshView = null;
	// private Action openInSystemFileExplorer = null;
	// private Action toggleLevel = null;
	// private Action newBookmark = null;
	// private Action deleteSelection = null;

	private StackLayout stackLayout = null;
	private Composite container = null;

	// private TreeViewer defaultView = null;
	// private Composite defaultViewComp = null;

	// private TreeViewer viewWithDropDownTreeView = null;
	// private Composite viewWithdropDownComp = null;

	private BookmarkView activeView = null;
	private DefaultView defaultView = null;
	private ToggledView toggledView = null;

	@Override
	public void createPartControl(Composite parent) {
		model = new TreeModel();
		// //
		container = new Composite(parent, SWT.NONE);
		stackLayout = new StackLayout();
		container.setLayout(stackLayout);
		// //
		defaultView = new DefaultView(this, container, model);
		toggledView = new ToggledView(this, container, model);

		stackLayout.topControl = defaultView.composite;
		activeView = defaultView;
		defaultView.setUpToolbarForViewPart();

		addPartViewFeatures();
		restoreBookmarks();
		activeView.getView().refresh();
	}

	private void addPartViewFeatures() {
		IPartService service = (IPartService) getSite().getService(
				IPartService.class);
		service.addPartListener(new BookmarkViewPartListener(getActiveViewer(), model));
	}

	// private void setUpDefaultView(TreeViewer view) {
	// activeTreeViewer = view;

	// createActions();
	// setUpToolbarForDefaultView();
	// setUpContextMenuForDefaultView();

	// activeTreeViewer.setContentProvider(new TreeContentProvider());
	// activeTreeViewer.setLabelProvider(new TreeLabelProvider());
	// activeTreeViewer.setInput(model.getModelRoot());

	// addListenerToView();
	// addListenerToTreeInView();
	// }

	// private void setUpToggledView(TreeViewer view) {
	// activeTreeViewer = view;
	//
	// createActions();
	// // setUpToolbarForDefaultView();
	// setUpContextMenuForToggledView();
	//
	// activeTreeViewer.setContentProvider(new TreeContentProvider());
	// activeTreeViewer.setLabelProvider(new TreeLabelProvider());
	// activeTreeViewer.setInput(model.getModelRoot());
	//
	// addListenerToView();
	// addListenerToTreeInView();
	// }

	@Override
	public TreeViewer getActiveViewer() {
		return activeView.getView();
	}

	public void activateToggledView() {
		stackLayout.topControl = toggledView.composite;
		activeView = toggledView;
		toggledView.setUpToolbarForViewPart();
		container.layout(true, true);
	}

	public void activateDefaultView() {
		stackLayout.topControl = defaultView.composite;
		activeView = defaultView;
		defaultView.setUpToolbarForViewPart();
		container.layout(true, true);
	}

	// private void setUpContextMenuForDefaultView() {
	// final MenuManager menuMgr = new MenuManager();
	// menuMgr.setRemoveAllWhenShown(true);
	//
	// menuMgr.addMenuListener(new IMenuListener() {
	// public void menuAboutToShow(IMenuManager mgr) {
	// menuMgr.add(showInEditor);
	// menuMgr.add(refreshView);
	// menuMgr.add(exportBookmarks);
	// menuMgr.add(importBookmarks);
	// menuMgr.add(new Separator());
	// menuMgr.add(toggleLevel);
	// menuMgr.add(newBookmark);
	// menuMgr.add(deleteSelection);
	// menuMgr.add(new Separator());
	// menuMgr.add(openInSystemFileExplorer);
	// }
	// });
	//
	// menuMgr.update(true);
	//
	// Menu menu = menuMgr.createContextMenu(activeTreeViewer.getControl());
	// activeTreeViewer.getControl().setMenu(menu);
	//
	//
	// getSite().registerContextMenu(menuMgr, activeTreeViewer);
	// }

	// private void setUpContextMenuForToggledView() {
	// final MenuManager menuMgr = new MenuManager();
	// menuMgr.setRemoveAllWhenShown(true);
	//
	// menuMgr.addMenuListener(new IMenuListener() {
	// public void menuAboutToShow(IMenuManager mgr) {
	// menuMgr.add(showInEditor);
	// menuMgr.add(refreshView);
	// menuMgr.add(new Separator());
	// // menuMgr.add(toggleLevel);
	// menuMgr.add(deleteSelection);
	// menuMgr.add(new Separator());
	// menuMgr.add(openInSystemFileExplorer);
	// }
	// });
	//
	// menuMgr.update(true);
	//
	// Menu menu = menuMgr.createContextMenu(activeTreeViewer.getControl());
	// activeTreeViewer.getControl().setMenu(menu);
	//
	// getSite().registerContextMenu(menuMgr, activeTreeViewer);
	// }
	//
	// private void addListenerToView() {
	// addDragDropSupportToView(activeTreeViewer, model);
	// activeTreeViewer.addDoubleClickListener(new TreeDoubleclickListener(
	// showInEditor));
	//
	// }
	//
	// private void addListenerToTreeInView() {
	// activeTreeViewer.getTree().addKeyListener(
	// new TreeKeyListener(activeTreeViewer, model, showInEditor));
	//
	// TreeSelectionListener selectionListener = new TreeSelectionListener();
	// selectionListener.add((SelfEnabling) openInSystemFileExplorer);
	// selectionListener.add((SelfEnabling) showInEditor);
	// selectionListener.add((SelfEnabling) deleteSelection);
	// activeTreeViewer.getTree().addSelectionListener(selectionListener);
	// }

	private void restoreBookmarks() {
		String[] lines = BookmarkFileIO.loadFromDefaultFile();
		if (lines != null && lines.length > 0) {
			RestoredTree restoredTree = TreeDeserializerFacade
					.deserialize(lines[0]);
			model.setModelRoot(restoredTree.getRoot());
			activeView.getView().setInput(model.getModelRoot());
			TreeDeserializerFacade.setExpandedNodesForView(
					activeView.getView(), restoredTree.getExpanded());
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
			activeView.getView().refresh();
		}

	}

	// private void createActions() {
	//
	// showInEditor = new ShowBookmarksInEditorAction(this, activeTreeViewer);
	// exportBookmarks = new ExportBookmarksAction(activeTreeViewer, model);
	// importBookmarks = new ImportBookmarksAction(activeTreeViewer, model);
	// closeAllOpenEditors = new CloseAllOpenEditorsAction();
	// refreshView = new RefreshViewAction(activeTreeViewer);
	// openInSystemFileExplorer = new OpenFileInSystemExplorerAction(
	// activeTreeViewer);
	// // toggleLevel = new ToggleLevelAction(this, model);
	// newBookmark = new CreateNewBookmarkAction(activeTreeViewer, model);
	// deleteSelection = new DeleteAction(activeTreeViewer);
	//
	// }

	// private void setUpToolbarForToggledView() {
	//
	// IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
	// mgr.removeAll();
	// mgr.add(showInEditor);
	// mgr.add(refreshView);
	// mgr.add(closeAllOpenEditors);
	// mgr.add(new Separator());
	// // mgr.add(toggleLevel);
	// mgr.add(deleteSelection);
	//
	// mgr.update(true);
	//
	// }

	// private void setUpToolbarForDefaultView() {
	//
	// IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
	// mgr.removeAll();
	// mgr.add(showInEditor);
	// mgr.add(refreshView);
	// mgr.add(closeAllOpenEditors);
	// mgr.add(exportBookmarks);
	// mgr.add(importBookmarks);
	// mgr.add(new Separator());
	// mgr.add(toggleLevel);
	// mgr.add(newBookmark);
	// mgr.add(deleteSelection);
	//
	// mgr.update(true);
	// }

	@Override
	public void setFocus() {
		activeView.getView().getControl().setFocus();
	}

	public IViewSite getViewSite() {
		return (IViewSite) getSite();
	}

	// public void addDragDropSupportToView(TreeViewer viewer, TreeModel model)
	// {
	// int operations = DND.DROP_LINK;
	// Transfer[] transferTypes = new Transfer[] {
	// ResourceTransfer.getInstance(),
	// LocalSelectionTransfer.getTransfer() };
	//
	// TreeDragListener dragListener = new TreeDragListener(viewer);
	// TreeDropListener dropListener = new TreeDropListener(viewer, model,
	// dragListener);
	//
	// viewer.addDropSupport(operations, transferTypes, dropListener);
	// viewer.addDragSupport(operations, transferTypes, dragListener);
	// }

	@Override
	public ViewPart getViewPart() {
		return (ViewPart) this;
	}

	@Override
	public void activateNextView() {

		if (activeView == defaultView) {
			activateToggledView();
			return;
		}

		if (activeView == toggledView) {
			activateDefaultView();
			return;
		}

	}

}