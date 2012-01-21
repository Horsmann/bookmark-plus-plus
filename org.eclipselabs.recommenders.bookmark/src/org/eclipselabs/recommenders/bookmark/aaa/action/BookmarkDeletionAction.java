package org.eclipselabs.recommenders.bookmark.aaa.action;

import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.aaa.BookmarkCommandInvoker;
import org.eclipselabs.recommenders.bookmark.aaa.commands.BookmarkDeletionCommand;
import org.eclipselabs.recommenders.bookmark.aaa.commands.IBookmarkModelCommand;
import org.eclipselabs.recommenders.bookmark.aaa.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.aaa.tree.RepresentationSwitchableTreeViewer;

public class BookmarkDeletionAction extends Action implements SelfEnabling {

    private final BookmarkCommandInvoker commandInvoker;
    private final RepresentationSwitchableTreeViewer treeViewer;

    public BookmarkDeletionAction(RepresentationSwitchableTreeViewer treeViewer, BookmarkCommandInvoker commandInvoker) {
        this.treeViewer = treeViewer;
        this.commandInvoker = commandInvoker;
        this.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.ICON_DELETE));
        this.setToolTipText("Delete selected item");
        this.setText("Delete Selection");
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
