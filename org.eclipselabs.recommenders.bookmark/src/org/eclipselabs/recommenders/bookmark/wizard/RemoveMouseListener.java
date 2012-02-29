package org.eclipselabs.recommenders.bookmark.wizard;

import java.util.Iterator;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipselabs.recommenders.bookmark.commands.DeleteInferredBookmarksCommand;
import org.eclipselabs.recommenders.bookmark.commands.DeleteSingleBookmarkCommand;
import org.eclipselabs.recommenders.bookmark.model.IBookmark;
import org.eclipselabs.recommenders.bookmark.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.view.BookmarkCommandInvoker;
import org.eclipselabs.recommenders.bookmark.view.tree.RepresentationSwitchableTreeViewer;
import org.eclipselabs.recommenders.bookmark.visitor.IsIBookmarkVisitor;

public class RemoveMouseListener implements MouseListener {

    private final RepresentationSwitchableTreeViewer treeViewer;
    private final BookmarkCommandInvoker invoker;

    public RemoveMouseListener(RepresentationSwitchableTreeViewer treeViewer, BookmarkCommandInvoker invoker) {
        this.treeViewer = treeViewer;
        this.invoker = invoker;
    }

    @Override
    public void mouseUp(MouseEvent e) {
    }

    @Override
    public void mouseDown(MouseEvent e) {
        IStructuredSelection selections = treeViewer.getSelections();
        @SuppressWarnings("rawtypes")
        Iterator iterator = selections.iterator();
        while (iterator.hasNext()) {
            IBookmarkModelComponent component = (IBookmarkModelComponent) iterator.next();
            IBookmarkModelComponent parent = component.getParent();
            invoker.invoke(new DeleteSingleBookmarkCommand(component));
            deleteInferredNodes(parent);

            treeViewer.refresh();
        }
    }

    private void deleteInferredNodes(IBookmarkModelComponent parent) {
        if (parent == null) {
            return;
        }
        IsIBookmarkVisitor isBookmark = new IsIBookmarkVisitor();
        parent.accept(isBookmark);
        if (isBookmark.isIBookmark()) {
            invoker.invoke(new DeleteInferredBookmarksCommand((IBookmark) parent));
        }
    }

    @Override
    public void mouseDoubleClick(MouseEvent e) {
    }
}
