package org.eclipselabs.recommenders.bookmark.views;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.part.ResourceTransfer;
import org.eclipse.ui.part.ViewPart;
import org.eclipselabs.recommenders.bookmark.tree.SWTNodeEditListener;
import org.eclipselabs.recommenders.bookmark.tree.TreeContentProvider;
import org.eclipselabs.recommenders.bookmark.tree.TreeDragListener;
import org.eclipselabs.recommenders.bookmark.tree.TreeDropListener;
import org.eclipselabs.recommenders.bookmark.tree.TreeLabelProvider;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.node.BookmarkNode;

public class BookmarkView extends ViewPart {

	private TreeViewer viewer;

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);

		int operations = DND.DROP_LINK;
		Transfer[] transferTypes = new Transfer[] { TextTransfer.getInstance(),
				ResourceTransfer.getInstance() };

		TreeModel model = new TreeModel();
		viewer.addDropSupport(operations, transferTypes, new TreeDropListener(
				viewer, model));
		viewer.addDragSupport(operations, transferTypes, new TreeDragListener(
				viewer));
		viewer.setContentProvider(new TreeContentProvider());
		viewer.setLabelProvider(new TreeLabelProvider());
		viewer.setInput(model.getModelRoot());

		viewer.getTree().addListener(SWT.MouseDoubleClick, new SWTNodeEditListener(viewer));

	}

	@Override
	public void setFocus() {
	}

	public IViewSite getViewSite() {
		return (IViewSite) getSite();
	}

}