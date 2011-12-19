package org.eclipselabs.recommenders.bookmark.tree.commands;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.persistent.serialization.TreeSerializerFacade;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.view.BookmarkView;

public class RenameBookmarkCommand implements TreeCommand {

	private TreeItem item = null;
	private BookmarkView viewer = null;

	public RenameBookmarkCommand(BookmarkView viewer, TreeItem bookmark) {
		this.item = bookmark;
		this.viewer = viewer;
	}

	@Override
	public void execute() {
		final TreeEditor editor = new TreeEditor(viewer.getView().getTree());
		editor.horizontalAlignment = SWT.LEFT;
		editor.minimumWidth = 100;

		final BMNode node = (BMNode) item.getData();
		if (!node.isBookmarkNode())
			return;

		final Text text = new Text(viewer.getView().getTree(), SWT.NONE);
		text.setText((String) node.getValue());
		text.selectAll();
		text.setFocus();

		// If the text field loses focus, set its text into the tree
		// and end the editing session
		text.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent event) {
				node.setValue(text.getText());
				text.dispose();
				setFocusAndSelection(item);
			}
		});

		text.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent event) {
				switch (event.keyCode) {
				case SWT.CR:
					// drop through
					String newValue = text.getText();
					setNewValue(node, newValue);
//					node.setValue(newValue);
				case SWT.ESC:
					// End editing session
					text.dispose();
					setFocusAndSelection(item);

					saveNewTreeModelState();
					break;
				}
			}
		});

		// Set the text field into the editor
		editor.setEditor(text, item);
	}

	private void setNewValue(BMNode node, String newValue) {
		node.setValue(newValue);
		
		if (node.hasReference()) {
			BMNode reference = node.getReference();
			reference.setValue(newValue);
		}
		
	}
	
	private void setFocusAndSelection(TreeItem item) {
		viewer.getView().refresh();
		viewer.getView().getTree().setSelection(item);
		viewer.getView().getTree().setFocus();
	}

	private void saveNewTreeModelState() {
		TreeSerializerFacade.serializeToDefaultLocation(viewer.getView(),
				viewer.getModel());
	}
}
