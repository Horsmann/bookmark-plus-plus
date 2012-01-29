package org.eclipselabs.recommenders.bookmark.aaa.copyCutPaste;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipselabs.recommenders.bookmark.aaa.BookmarkCommandInvoker;
import org.eclipselabs.recommenders.bookmark.aaa.commands.BookmarkDeletionCommand;
import org.eclipselabs.recommenders.bookmark.aaa.model.IBookmark;
import org.eclipselabs.recommenders.bookmark.aaa.tree.RepresentationSwitchableTreeViewer;

public class CutHandler extends CopyHandler {

    private final BookmarkCommandInvoker invoker;

    public CutHandler(RepresentationSwitchableTreeViewer treeViewer, BookmarkCommandInvoker invoker) {
        super(treeViewer);
        this.invoker = invoker;
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IBookmark[] selectedBookmarks = getSelectedBookmarks();
        super.execute(event);

        for (IBookmark bookmark : selectedBookmarks) {
            invoker.invoke(new BookmarkDeletionCommand(bookmark));
        }

        return null;
    }

}
