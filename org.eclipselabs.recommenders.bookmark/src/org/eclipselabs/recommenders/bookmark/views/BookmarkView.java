package org.eclipselabs.recommenders.bookmark.views;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.part.ViewPart;

public class BookmarkView extends ViewPart {

	private TreeViewer viewer;

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		// int operations = DND.DROP_COPY | DND.DROP_MOVE;
		// Transfer[] transferTypes = new Transfer[] {
		// TextTransfer.getInstance() };
		// viewer.addDropSupport(operations, transferTypes, new MyDropListener(
		// viewer));
		viewer.setContentProvider(new TreeContentProvider());
		viewer.setLabelProvider(new BookmarkLabelProvider());
		viewer.setInput(new TreeModel().getModel());
	}

	@Override
	public void setFocus() {
	}

	public IViewSite getViewSite() {
		return (IViewSite) getSite();
	}

}