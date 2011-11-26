package org.eclipselabs.recommenders.bookmark.views;

import java.io.IOException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.part.ResourceTransfer;
import org.eclipse.ui.part.ViewPart;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.view.actions.CloseAllOpenEditors;
import org.eclipselabs.recommenders.bookmark.view.actions.ExportBookmarksAction;
import org.eclipselabs.recommenders.bookmark.view.actions.ImportBookmarksAction;
import org.eclipselabs.recommenders.bookmark.view.actions.ShowBookmarksInEditorAction;
import org.eclipselabs.recommenders.bookmark.view.save_restore.LoadBookmarksFromLocalDefaultFile;
import org.eclipselabs.recommenders.bookmark.view.tree.TreeContentProvider;
import org.eclipselabs.recommenders.bookmark.view.tree.TreeDoubleclickListener;
import org.eclipselabs.recommenders.bookmark.view.tree.TreeDragListener;
import org.eclipselabs.recommenders.bookmark.view.tree.TreeDropListener;
import org.eclipselabs.recommenders.bookmark.view.tree.TreeExpandCollapseListener;
import org.eclipselabs.recommenders.bookmark.view.tree.TreeKeyListener;
import org.eclipselabs.recommenders.bookmark.view.tree.TreeLabelProvider;

public class BookmarkView extends ViewPart {

	private TreeViewer viewer = null;
	private TreeModel model;
	private Action showInEditor = null;
	private Action exportBookmarks = null;
	private Action importBookmarks = null;
	private Action closeAllOpenEditors = null;

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		model = new TreeModel();

		createActions();
		setUpToolbar();

		viewer.setContentProvider(new TreeContentProvider());
		viewer.setLabelProvider(new TreeLabelProvider());
		viewer.setInput(model.getModelRoot());

		addListenerToView();
		addListenerToTreeInView();
		
//		IPartService service = (IPartService) getSite().getService(IPartService.class);
//		service.addPartListener(new IPartListener2() {
//			
//			@Override
//			public void partVisible(IWorkbenchPartReference partRef) {
//				System.err.println("partVisible");
//			}
//			
//			@Override
//			public void partOpened(IWorkbenchPartReference partRef) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void partInputChanged(IWorkbenchPartReference partRef) {
//				System.err.println("InputChanged");
//			}
//			
//			@Override
//			public void partHidden(IWorkbenchPartReference partRef) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void partDeactivated(IWorkbenchPartReference partRef) {
//				System.err.println("deactivated");
//			}
//			
//			@Override
//			public void partClosed(IWorkbenchPartReference partRef) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void partBroughtToTop(IWorkbenchPartReference partRef) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void partActivated(IWorkbenchPartReference partRef) {
//				// TODO Auto-generated method stub
//				
//			}
//		});


		restoreBookmarks();

	}

	private void addListenerToView() {
		addDragDropSupportToView(viewer, model);
		viewer.addDoubleClickListener(new TreeDoubleclickListener(showInEditor));

	}

	private void addListenerToTreeInView() {
		viewer.getTree().addKeyListener(
				new TreeKeyListener(viewer, model, showInEditor));
		viewer.getTree().addTreeListener(
				new TreeExpandCollapseListener(viewer, model));
	}

	private void restoreBookmarks() {
		try {
			new LoadBookmarksFromLocalDefaultFile(viewer, model).load();
		} catch (IOException e) {
			// Ignore silently
		}
	}

	private void createActions() {

		showInEditor = new ShowBookmarksInEditorAction(this, viewer);
		exportBookmarks = new ExportBookmarksAction(viewer, model);
		importBookmarks = new ImportBookmarksAction(viewer, model);
		closeAllOpenEditors = new CloseAllOpenEditors();

	}

	private void setUpToolbar() {

		IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
		mgr.add(showInEditor);
		mgr.add(closeAllOpenEditors);
		mgr.add(exportBookmarks);
		mgr.add(importBookmarks);
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