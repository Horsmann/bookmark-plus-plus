package org.eclipselabs.recommenders.bookmark.view.handler.copyCut;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipselabs.recommenders.bookmark.commands.BookmarkDeletionCommand;
import org.eclipselabs.recommenders.bookmark.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.view.BookmarkCommandInvoker;
import org.eclipselabs.recommenders.bookmark.view.tree.RepresentationSwitchableTreeViewer;

public class CutHandler extends CopyHandler {

    private final BookmarkCommandInvoker invoker;

    public CutHandler(RepresentationSwitchableTreeViewer treeViewer, BookmarkCommandInvoker invoker) {
        super(treeViewer);
        this.invoker = invoker;
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IBookmarkModelComponent[] selectedComponents = getSelectedComponents();
        super.execute(event);

        for (IBookmarkModelComponent component : selectedComponents) {
            invoker.invoke(new BookmarkDeletionCommand(component));
        }

        return null;
    }

}
