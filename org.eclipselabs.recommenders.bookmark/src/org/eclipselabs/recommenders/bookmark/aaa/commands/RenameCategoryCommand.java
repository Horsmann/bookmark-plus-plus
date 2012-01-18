package org.eclipselabs.recommenders.bookmark.aaa.commands;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipselabs.recommenders.bookmark.aaa.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.aaa.model.Category;

public class RenameCategoryCommand implements IBookmarkModelCommand {

    private TreeItem item = null;
    private final Tree tree;
    private final TreeViewer viewer;

    public RenameCategoryCommand(TreeViewer viewer, Tree tree, TreeItem item) {
        this.viewer = viewer;
        this.tree = tree;
        this.item = item;
    }

    @Override
    public void execute(BookmarkModel model) {

        final TreeEditor editor = new TreeEditor(tree);
        editor.horizontalAlignment = SWT.LEFT;
        editor.minimumWidth = 100;

        if (item.getData() instanceof Category) {

            final Category category = (Category) item.getData();

            final Text text = new Text(tree, SWT.NONE);
            text.setText(category.getLabel());
            text.selectAll();
            text.setFocus();

            // If the text field loses focus, set its text into the tree
            // and end the editing session
            text.addFocusListener(new FocusAdapter() {
                public void focusLost(FocusEvent event) {
                    category.setLabel(text.getText());
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
                        if (isNotBlank(newValue)) {
                            category.setLabel(newValue);
                        }
                    case SWT.ESC:
                        // End editing session
                        text.dispose();
                        setFocusAndSelection(item);
                        viewer.refresh();
                        break;
                    }
                }

                private boolean isNotBlank(String newValue) {
                    return newValue.trim().compareTo("") != 0;
                }
            });

            // Set the text field into the editor
            editor.setEditor(text, item);
        }
        
    }

    private void setFocusAndSelection(TreeItem item) {
        tree.setSelection(item);
        tree.setFocus();
    }

}
