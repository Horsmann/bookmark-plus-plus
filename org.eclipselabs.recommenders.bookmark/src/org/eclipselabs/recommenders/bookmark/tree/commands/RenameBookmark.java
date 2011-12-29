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
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.persistent.serialization.TreeSerializerFacade;
import org.eclipselabs.recommenders.bookmark.view.BookmarkView;
import org.eclipselabs.recommenders.bookmark.view.ViewManager;

public class RenameBookmark
	implements TreeCommand
{

	private TreeItem item = null;
	private final ViewManager manager;

	public RenameBookmark(ViewManager manager, TreeItem bookmark)
	{
		this.manager = manager;
		this.item = bookmark;
	}

	@Override
	public void execute()
	{

		BookmarkView viewer = manager.getActiveBookmarkView();

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
		text.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent event)
			{
				node.setValue(text.getText());
				text.dispose();
				setFocusAndSelection(item);
			}
		});

		text.addKeyListener(new KeyAdapter()
		{
			public void keyPressed(KeyEvent event)
			{
				switch (event.keyCode) {
				case SWT.CR:
					// drop through
					String newValue = text.getText();
					setNewValue(node, newValue);
				case SWT.ESC:
					// End editing session
					text.dispose();
					setFocusAndSelection(item);

					manager.saveModelState();
					break;
				}
			}
		});

		// Set the text field into the editor
		editor.setEditor(text, item);
	}

	private void setNewValue(BMNode node, String newValue)
	{
		node.setValue(newValue);

		if (node.hasReference()) {
			BMNode reference = node.getReference();
			reference.setValue(newValue);
		}

	}

	private void setFocusAndSelection(TreeItem item)
	{

		BookmarkView viewer = manager.getActiveBookmarkView();

		viewer.getView().refresh();
		viewer.getView().getTree().setSelection(item);
		viewer.getView().getTree().setFocus();
	}

}
