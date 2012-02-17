package org.eclipselabs.recommenders.bookmark.view.copyCutPaste;

import java.util.LinkedList;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.widgets.Display;
import org.eclipselabs.recommenders.bookmark.commands.AddElementToModelCommand;
import org.eclipselabs.recommenders.bookmark.model.IBookmark;
import org.eclipselabs.recommenders.bookmark.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.view.BookmarkCommandInvoker;
import org.eclipselabs.recommenders.bookmark.view.tree.RepresentationSwitchableTreeViewer;
import org.eclipselabs.recommenders.bookmark.visitor.GetValueVisitor;

import com.google.common.base.Optional;

public class PasteHandler extends AbstractHandler {
    private final BookmarkCommandInvoker invoker;
    private final RepresentationSwitchableTreeViewer treeViewer;

    public PasteHandler(RepresentationSwitchableTreeViewer treeViewer, BookmarkCommandInvoker invoker) {
        this.treeViewer = treeViewer;
        this.invoker = invoker;
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        Clipboard cb = new Clipboard(Display.getCurrent());
        Object contents = cb.getContents(BookmarkTransfer.getInstance());
        if (isBookmarkTransfer(contents)) {
            IBookmarkModelComponent target = (IBookmarkModelComponent) treeViewer.getSelections().getFirstElement();
            Optional<IBookmarkModelComponent> dropTarget = Optional.absent();
            if (target != null) {
                dropTarget = Optional.of(target);
            }

            BookmarkTransferObject transferData = (BookmarkTransferObject) contents;

            Object[] values = getValues(transferData.bookmarks);

            Optional<String> name = Optional.absent();
            invoker.invoke(new AddElementToModelCommand(values, invoker, false, dropTarget, name));
        }

        return null;
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
