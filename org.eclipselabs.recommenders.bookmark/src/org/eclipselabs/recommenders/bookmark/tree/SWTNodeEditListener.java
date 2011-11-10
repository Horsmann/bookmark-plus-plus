package org.eclipselabs.recommenders.bookmark.tree;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
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
import org.eclipselabs.recommenders.bookmark.tree.node.BookmarkNode;

/* A lot of inspiration (and code) taken from:
 * http://dev.eclipse.org/viewcvs/viewvc.cgi/org.eclipse.swt.snippets/src/org/eclipse/swt/snippets/Snippet111.java?view=co
 * */

public class SWTNodeEditListener implements Listener {

	private TreeViewer viewer;
	private Tree tree;
	private TreeEditor editor;

	private Color black = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);

	public SWTNodeEditListener(TreeViewer viewer) {
		this.viewer = viewer;
		this.tree = viewer.getTree();
		this.editor = new TreeEditor(tree);
	}

	public void handleEvent(Event event) {

		if (tree.getSelectionCount() != 1)
			return;

		TreeItem item = tree.getSelection()[0];

		if (!(item.getData() instanceof BookmarkNode))
			return;

		if (item != null) {
			boolean showBorder = true;
			Composite composite = new Composite(tree, SWT.NONE);
			if (showBorder)
				composite.setBackground(black);
			Text text = new Text(composite, SWT.NONE);
			int inset = showBorder ? 1 : 0;

			composite.addListener(SWT.Resize, new CompositeResizeListener(
					composite, text, inset));

			Listener textListener = new TextListener(viewer, item, editor,
					composite, text, inset);

			text.addListener(SWT.FocusOut, textListener);
			text.addListener(SWT.Traverse, textListener);
			text.addListener(SWT.Verify, textListener);
			editor.setEditor(composite, item);
			text.setText(item.getText());
			text.selectAll();
			text.setFocus();
		}
	}

}

class CompositeResizeListener implements Listener {

	Composite composite;
	int inset;
	Text text;

	public CompositeResizeListener(Composite composite, Text text, int inset) {
		this.composite = composite;
		this.text = text;
		this.inset = inset;

	}

	public void handleEvent(Event e) {
		Rectangle rect = composite.getClientArea();
		text.setBounds(rect.x + inset, rect.y + inset, rect.width - inset * 2,
				rect.height - inset * 2);
	}
}

class TextListener implements Listener {

	Tree tree;
	TreeItem item;
	TreeEditor editor;
	TreeViewer viewer;
	Composite composite;
	Text text;
	int inset;

	public TextListener(TreeViewer viewer, TreeItem item, TreeEditor editor,
			Composite composite, Text text, int inset) {
		this.item = item;
		this.editor = editor;
		this.composite = composite;
		this.text = text;
		this.tree = viewer.getTree();
		this.viewer = viewer;
		this.inset = inset;
	}

	public void handleEvent(final Event e) {
		switch (e.type) {
		case SWT.FocusOut:
			item.setText(text.getText());
			composite.dispose();
			break;
		case SWT.Verify:
			String newText = text.getText();
			String leftText = newText.substring(0, e.start);
			String rightText = newText.substring(e.end, newText.length());
			GC gc = new GC(text);
			Point size = gc.textExtent(leftText + e.text + rightText);
			gc.dispose();
			size = text.computeSize(size.x, SWT.DEFAULT);
			editor.horizontalAlignment = SWT.LEFT;
			Rectangle itemRect = item.getBounds(),
			rect = tree.getClientArea();
			editor.minimumWidth = Math.max(size.x, itemRect.width) + inset * 2;
			int left = itemRect.x,
			right = rect.x + rect.width;
			editor.minimumWidth = Math.min(editor.minimumWidth, right - left);
			editor.minimumHeight = size.y + inset * 2;
			editor.layout();
			break;
		case SWT.Traverse:
			switch (e.detail) {
			case SWT.TRAVERSE_RETURN:
				item.setText(text.getText());
				if (item.getData() instanceof BookmarkNode) {
					BookmarkNode bn = (BookmarkNode) item.getData();
					bn.setText(item.getText());
				}
				viewer.refresh();
				tree.setSelection(item);
				tree.redraw();

				// FALL THROUGH
			case SWT.TRAVERSE_ESCAPE:
				composite.dispose();
				tree.setFocus();
				e.doit = false;
			}
			break;
		}
	}
}
