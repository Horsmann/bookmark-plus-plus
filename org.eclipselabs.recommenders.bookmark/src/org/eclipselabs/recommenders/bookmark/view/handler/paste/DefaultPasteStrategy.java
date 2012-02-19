package org.eclipselabs.recommenders.bookmark.view.handler.paste;

import java.util.LinkedList;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.widgets.Display;
import org.eclipselabs.recommenders.bookmark.commands.AddElementToModelCommand;
import org.eclipselabs.recommenders.bookmark.model.IBookmark;
import org.eclipselabs.recommenders.bookmark.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.view.BookmarkCommandInvoker;
import org.eclipselabs.recommenders.bookmark.view.handler.BookmarkTransfer;
import org.eclipselabs.recommenders.bookmark.view.handler.BookmarkTransferObject;
import org.eclipselabs.recommenders.bookmark.view.tree.RepresentationSwitchableTreeViewer;
import org.eclipselabs.recommenders.bookmark.visitor.GetValueVisitor;

import com.google.common.base.Optional;

public class DefaultPasteStrategy implements IPasteStrategy {

    private final BookmarkCommandInvoker invoker;
    protected final RepresentationSwitchableTreeViewer treeViewer;

    public DefaultPasteStrategy(BookmarkCommandInvoker inovker, RepresentationSwitchableTreeViewer treeViewer) {
        this.invoker = inovker;
        this.treeViewer = treeViewer;
    }

    @Override
    public void execute(ExecutionEvent event) {
        Clipboard cb = new Clipboard(Display.getCurrent());
        Object contents = cb.getContents(BookmarkTransfer.getInstance());
        if (isBookmarkTransfer(contents)) {
            Optional<IBookmarkModelComponent> dropTarget = getDropTarget();

            BookmarkTransferObject transferData = (BookmarkTransferObject) contents;

            Object[] values = getValues(transferData.bookmarks);

            Optional<String> name = Optional.absent();
            invoker.invoke(new AddElementToModelCommand(values, invoker, false, dropTarget, name));
        }

    }

    protected Optional<IBookmarkModelComponent> getDropTarget() {
        IBookmarkModelComponent target = null;
        IStructuredSelection selections = treeViewer.getSelections();
        if (selections.size() > 0) {
            target = (IBookmarkModelComponent) treeViewer.getSelections().getFirstElement();
        }
        Optional<IBookmarkModelComponent> dropTarget = Optional.absent();
        if (target != null) {
            dropTarget = Optional.of(target);
        }
        return dropTarget;
    }

    private boolean isBookmarkTransfer(Object contents) {
        return (contents != null && contents instanceof BookmarkTransferObject);
    }

    private Object[] getValues(IBookmark[] bookmarks) {
        LinkedList<Object> values = new LinkedList<Object>();
        for (IBookmark bookmark : bookmarks) {
            GetValueVisitor visitor = new GetValueVisitor();
            bookmark.accept(visitor);
            values.add(visitor.getValue());
        }
        return values.toArray();
    }

}
