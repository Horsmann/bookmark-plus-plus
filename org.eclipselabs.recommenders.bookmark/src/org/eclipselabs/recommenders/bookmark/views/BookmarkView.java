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
		
		final Tree tree = viewer.getTree();
		
		final Color black = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
		final TreeEditor editor = new TreeEditor(tree);
		
//		final TreeItem[] lastItem = new TreeItem[1];
		final TreeItem[] lastItem = new TreeItem[1];
		tree.addListener(SWT.MouseDoubleClick, new Listener() {
			public void handleEvent(Event event) {
				if (tree.getSelectionCount() != 1)
					return;
//				final TreeItem item = (TreeItem) event.item;
				final TreeItem item = tree.getSelection()[0];
//				tree.getSelection
				if (item != null && item == lastItem[0]) {
					boolean showBorder = true;
					final Composite composite = new Composite(tree, SWT.NONE);
					if (showBorder)
						composite.setBackground(black);
					final Text text = new Text(composite, SWT.NONE);
					final int inset = showBorder ? 1 : 0;
					composite.addListener(SWT.Resize, new Listener() {
						public void handleEvent(Event e) {
							Rectangle rect = composite.getClientArea();
							text.setBounds(rect.x + inset, rect.y + inset,
									rect.width - inset * 2, rect.height - inset
											* 2);
						}
					});
					Listener textListener = new Listener() {
						public void handleEvent(final Event e) {
							switch (e.type) {
							case SWT.FocusOut:
								item.setText(text.getText());
								composite.dispose();
								break;
							case SWT.Verify:
								String newText = text.getText();
								String leftText = newText.substring(0, e.start);
								String rightText = newText.substring(e.end,
										newText.length());
								GC gc = new GC(text);
								Point size = gc.textExtent(leftText + e.text
										+ rightText);
								gc.dispose();
								size = text.computeSize(size.x, SWT.DEFAULT);
								editor.horizontalAlignment = SWT.LEFT;
								Rectangle itemRect = item.getBounds(),
								rect = tree.getClientArea();
								editor.minimumWidth = Math.max(size.x,
										itemRect.width) + inset * 2;
								int left = itemRect.x,
								right = rect.x + rect.width;
								editor.minimumWidth = Math.min(
										editor.minimumWidth, right - left);
								editor.minimumHeight = size.y + inset * 2;
								editor.layout();
								break;
							case SWT.Traverse:
								switch (e.detail) {
								case SWT.TRAVERSE_RETURN:
									item.setText(text.getText());
									// FALL THROUGH
								case SWT.TRAVERSE_ESCAPE:
									composite.dispose();
									e.doit = false;
								}
								break;
							}
						}
					};
					text.addListener(SWT.FocusOut, textListener);
					text.addListener(SWT.Traverse, textListener);
					text.addListener(SWT.Verify, textListener);
					editor.setEditor(composite, item);
					text.setText(item.getText());
					text.selectAll();
					text.setFocus();
				}
				lastItem[0] = item;
			}
		});
		
		
		
		
	}

	@Override
	public void setFocus() {
	}

	public IViewSite getViewSite() {
		return (IViewSite) getSite();
	}

}