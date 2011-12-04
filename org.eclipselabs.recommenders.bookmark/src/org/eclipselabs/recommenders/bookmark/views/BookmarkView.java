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
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
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
import org.eclipselabs.recommenders.bookmark.view.actions.ExportBookmarksAction;
import org.eclipselabs.recommenders.bookmark.view.actions.ImportBookmarksAction;
import org.eclipselabs.recommenders.bookmark.view.actions.OpenFileInSystemExplorerAction;
import org.eclipselabs.recommenders.bookmark.view.actions.RefreshViewAction;
import org.eclipselabs.recommenders.bookmark.view.actions.ShowBookmarksInEditorAction;
import org.eclipselabs.recommenders.bookmark.view.tree.TreeContentProvider;
import org.eclipselabs.recommenders.bookmark.view.tree.TreeDoubleclickListener;
import org.eclipselabs.recommenders.bookmark.view.tree.TreeDragListener;
import org.eclipselabs.recommenders.bookmark.view.tree.TreeDropListener;
import org.eclipselabs.recommenders.bookmark.view.tree.TreeKeyListener;
import org.eclipselabs.recommenders.bookmark.view.tree.TreeLabelProvider;

public class BookmarkView extends ViewPart {

	private TreeViewer viewer = null;
	private TreeModel model;
	private Action showInEditor = null;
	private Action exportBookmarks = null;
	private Action importBookmarks = null;
	private Action closeAllOpenEditors = null;
	private Action refreshView = null;
	private Action openInSystemFileExplorer = null;

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		model = new TreeModel();

		createActions();
		setUpToolbar();
		addContextMenu();

		viewer.setContentProvider(new TreeContentProvider());
		viewer.setLabelProvider(new TreeLabelProvider());
		viewer.setInput(model.getModelRoot());

		addListenerToView();
		addListenerToTreeInView();

		restoreBookmarks();

	}

	private void addContextMenu() {
		final MenuManager menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager mgr) {
				menuMgr.add(showInEditor);
				menuMgr.add(refreshView);
				menuMgr.add(closeAllOpenEditors);
				menuMgr.add(exportBookmarks);
				menuMgr.add(importBookmarks);
				menuMgr.add(new Separator());
				menuMgr.add(openInSystemFileExplorer);
			}
		});

		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);

		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void addListenerToView() {
		addDragDropSupportToView(viewer, model);
		viewer.addDoubleClickListener(new TreeDoubleclickListener(showInEditor));

		IPartService service = (IPartService) getSite().getService(
				IPartService.class);
		service.addPartListener(new BookmarkViewPartListener(viewer, model));

	}

	private void addListenerToTreeInView() {
		viewer.getTree().addKeyListener(
				new TreeKeyListener(viewer, model, showInEditor));
	}

	private void restoreBookmarks() {
		String[] lines = BookmarkFileIO.loadFromDefaultFile();
		if (lines != null && lines.length > 0) {
			RestoredTree restoredTree = TreeDeserializerFacade
					.deserialize(lines[0]);
			model.setModelRoot(restoredTree.getRoot());
			viewer.setInput(model.getModelRoot());
			TreeDeserializerFacade.setExpandedNodesForView(viewer,
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
			viewer.refresh();
		}

	}

	private void createActions() {

		showInEditor = new ShowBookmarksInEditorAction(this, viewer);
		exportBookmarks = new ExportBookmarksAction(viewer, model);
		importBookmarks = new ImportBookmarksAction(viewer, model);
		closeAllOpenEditors = new CloseAllOpenEditorsAction();
		refreshView = new RefreshViewAction(viewer);
		openInSystemFileExplorer = new OpenFileInSystemExplorerAction(viewer);

	}

	private void setUpToolbar() {

		IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
		mgr.add(showInEditor);
		mgr.add(refreshView);
		mgr.add(closeAllOpenEditors);
		mgr.add(exportBookmarks);
		mgr.add(importBookmarks);
		mgr.add(new Separator());
		mgr.add(openInSystemFileExplorer);
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