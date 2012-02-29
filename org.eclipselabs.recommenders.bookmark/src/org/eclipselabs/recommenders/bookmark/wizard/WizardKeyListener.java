package org.eclipselabs.recommenders.bookmark.wizard;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipselabs.recommenders.bookmark.commands.DeleteInferredBookmarksCommand;
import org.eclipselabs.recommenders.bookmark.commands.DeleteSingleBookmarkCommand;
import org.eclipselabs.recommenders.bookmark.commands.RenameCategoryCommand;
import org.eclipselabs.recommenders.bookmark.model.IBookmark;
import org.eclipselabs.recommenders.bookmark.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.view.BookmarkCommandInvoker;
import org.eclipselabs.recommenders.bookmark.view.tree.RepresentationSwitchableTreeViewer;
import org.eclipselabs.recommenders.bookmark.visitor.IsIBookmarkVisitor;

public class WizardKeyListener implements KeyListener {

    private final RepresentationSwitchableTreeViewer treeViewer;
    private final BookmarkCommandInvoker invoker;

    public WizardKeyListener(RepresentationSwitchableTreeViewer treeViewer, BookmarkCommandInvoker invoker) {
        this.treeViewer = treeViewer;
        this.invoker = invoker;
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (isDeletion(e)) {
            processDeletion(e);
        } else if (isRename(e)) {
            processRename(e);
        }
        treeViewer.refresh();
    }

    private void processDeletion(KeyEvent e) {
        TreeItem[] selections = getTreeSelections(e);
        for (TreeItem item : selections) {
            searchBookmarkDeleteSelection(item);
        }
    }

    private boolean isRename(KeyEvent e) {
        return e.keyCode == SWT.F2;
    }

    private void processRename(KeyEvent e) {
        invoker.invoke(new RenameCategoryCommand(treeViewer));
    }

    private TreeItem[] getTreeSelections(KeyEvent e) {
        Tree tree = (Tree) e.getSource();
        TreeItem[] selections = tree.getSelection();
        return selections;
    }

    protected void searchBookmarkDeleteSelection(TreeItem item) {
        if (!item.isDisposed()) {
            IBookmarkModelComponent component = (IBookmarkModelComponent) item.getData();
            IBookmarkModelComponent parent = component.getParent();
            delete(component);
            deleteInferredBookmarksRecursively(parent);
        }
    }

    private void deleteInferredBookmarksRecursively(IBookmarkModelComponent parent) {
        if (parent == null) {
            return;
        }
        IsIBookmarkVisitor isIBookmark = new IsIBookmarkVisitor();
        parent.accept(isIBookmark);
        if (isIBookmark.isIBookmark()) {
            invoker.invoke(new DeleteInferredBookmarksCommand((IBookmark) parent));
        }
    }

    private void delete(IBookmarkModelComponent component) {
        invoker.invoke(new DeleteSingleBookmarkCommand(component));
    }

    private boolean isDeletion(KeyEvent e) {
        int BACKSPACE = 8;
        return (e.keyCode == SWT.DEL || e.keyCode == BACKSPACE);
    }
}
