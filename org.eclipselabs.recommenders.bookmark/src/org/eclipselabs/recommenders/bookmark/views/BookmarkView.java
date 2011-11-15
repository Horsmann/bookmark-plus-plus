package org.eclipselabs.recommenders.bookmark.views;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.part.ResourceTransfer;
import org.eclipse.ui.part.ViewPart;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.SWTNodeEditListener;
import org.eclipselabs.recommenders.bookmark.tree.TreeContentProvider;
import org.eclipselabs.recommenders.bookmark.tree.TreeDragListener;
import org.eclipselabs.recommenders.bookmark.tree.TreeDropListener;
import org.eclipselabs.recommenders.bookmark.tree.TreeLabelProvider;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.node.TreeNode;

public class BookmarkView extends ViewPart {

	private TreeViewer viewer;
	private Action openBookmarks;

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		TreeModel model = new TreeModel();

		addDragDropSupportToView(viewer, model);

		viewer.setContentProvider(new TreeContentProvider());
		viewer.setLabelProvider(new TreeLabelProvider());
		viewer.setInput(model.getModelRoot());

		// viewer.setContentProvider(new
		// StandardJavaElementContentProvider(true));
		// viewer.setLabelProvider(new JavaElementLabelProvider());
		// viewer.setInput(JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()));

		viewer.getTree().addListener(SWT.MouseDoubleClick,
				new SWTNodeEditListener(viewer));

		createActions();
		setUpToolbar();

	}

	private void createActions() {
		openBookmarks = new Action("Open Bookmarks") {
			public void run() {
				openBookmarks();
			}
		};

		openBookmarks.setImageDescriptor(Activator.getDefault()
				.getImageRegistry().getDescriptor(Activator.ICON_OPEN_BOOKMARK));
	}

	private void setUpToolbar() {

		IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
		mgr.add(openBookmarks);
	}

	private void openBookmarks() {
		
		List<IStructuredSelection> selectedList = getTreeSelections();
		for (int i = 0; i < selectedList.size(); i++) {
			TreeNode node = (TreeNode) selectedList.get(i);
			System.err.println("Open: " + node.getValue());
		}
		
		System.err.println("****END*****");
	}
	
	@SuppressWarnings("unchecked")
	private List<IStructuredSelection> getTreeSelections() {
		ISelection selection = viewer.getSelection();
		if (selection == null)
			return Collections.emptyList();
		if (selection instanceof IStructuredSelection && !selection.isEmpty())
			return ((IStructuredSelection) selection).toList();

		return Collections.emptyList();
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

		viewer.addDropSupport(operations, transferTypes, new TreeDropListener(
				viewer, model));
		viewer.addDragSupport(operations, transferTypes, new TreeDragListener(
				viewer));
	}

}