package org.eclipselabs.recommenders.bookmark.action;

import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.commands.BookmarkDeletionCommand;
import org.eclipselabs.recommenders.bookmark.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.view.BookmarkCommandInvoker;
import org.eclipselabs.recommenders.bookmark.view.tree.RepresentationSwitchableTreeViewer;

public class BookmarkDeletionAction extends Action implements SelfEnabling {

    private final BookmarkCommandInvoker commandInvoker;
    private final RepresentationSwitchableTreeViewer treeViewer;

    public BookmarkDeletionAction(RepresentationSwitchableTreeViewer treeViewer, BookmarkCommandInvoker commandInvoker) {
        this.treeViewer = treeViewer;
        this.commandInvoker = commandInvoker;
        this.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.ICON_DELETE));
        this.setToolTipText("Delete selected bookmarks");
        this.setText("Delete bookmarks");
        this.setEnabled(false);
    }

    @Override
    public void run() {

        IStructuredSelection selections = treeViewer.getSelections();
        @SuppressWarnings("rawtypes")
        Iterator iterator = selections.iterator();
        while (iterator.hasNext()) {
            Object selectedObject = iterator.next();
            if (selectedObject instanceof IBookmarkModelComponent) {
                commandInvoker.invoke(new BookmarkDeletionCommand((IBookmarkModelComponent) selectedObject));
            }
        }

    }

    @Override
    public void updateEnableStatus() {
        if (treeViewer.getSelections().isEmpty()) {
            setEnabled(false);
            return;
        }
        setEnabled(true);
    }

}