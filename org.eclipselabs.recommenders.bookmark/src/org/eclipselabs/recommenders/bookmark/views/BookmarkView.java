package org.eclipselabs.recommenders.bookmark.views;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.part.PluginTransfer;
import org.eclipse.ui.part.ResourceTransfer;
import org.eclipse.ui.part.ViewPart;
import org.eclipselabs.recommenders.bookmark.tree.SWTNodeEditListener;
import org.eclipselabs.recommenders.bookmark.tree.TreeContentProvider;
import org.eclipselabs.recommenders.bookmark.tree.TreeDragListener;
import org.eclipselabs.recommenders.bookmark.tree.TreeDropListener;
import org.eclipselabs.recommenders.bookmark.tree.TreeLabelProvider;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;

public class BookmarkView extends ViewPart {

	private TreeViewer viewer;

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		TreeModel model = new TreeModel();

		addDragDropSupportToView(viewer, model);

		viewer.setContentProvider(new TreeContentProvider());
		viewer.setLabelProvider(new TreeLabelProvider());
		viewer.setInput(model.getModelRoot());

		viewer.getTree().addListener(SWT.MouseDoubleClick,
				new SWTNodeEditListener(viewer));

	}

	@Override
	public void setFocus() {
	}

	public IViewSite getViewSite() {
		return (IViewSite) getSite();
	}

	public void addDragDropSupportToView(TreeViewer viewer, TreeModel model) {
		int operations = DND.DROP_LINK;
		Transfer[] transferTypes = new Transfer[] { TextTransfer.getInstance(),
				ResourceTransfer.getInstance(), PluginTransfer.getInstance() };

		viewer.addDropSupport(operations, transferTypes, new TreeDropListener(
				viewer, model));
		viewer.addDragSupport(operations, transferTypes, new TreeDragListener(
				viewer));
	}

}