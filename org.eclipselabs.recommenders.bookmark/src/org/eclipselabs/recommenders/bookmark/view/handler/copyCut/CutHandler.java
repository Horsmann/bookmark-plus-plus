package org.eclipselabs.recommenders.bookmark.view.handler.copyCut;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipselabs.recommenders.bookmark.commands.DeleteSingleBookmarkCommand;
import org.eclipselabs.recommenders.bookmark.commands.DeleteInferredBookmarksCommand;
import org.eclipselabs.recommenders.bookmark.model.IBookmark;
import org.eclipselabs.recommenders.bookmark.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.view.BookmarkCommandInvoker;
import org.eclipselabs.recommenders.bookmark.view.tree.RepresentationSwitchableTreeViewer;
import org.eclipselabs.recommenders.bookmark.visitor.IsCategoryVisitor;

public class CutHandler extends CopyHandler {

    private final BookmarkCommandInvoker commandInvoker;

    public CutHandler(RepresentationSwitchableTreeViewer treeViewer, BookmarkCommandInvoker invoker) {
        super(treeViewer);
        this.commandInvoker = invoker;
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IBookmarkModelComponent[] selectedComponents = getSelectedComponents();
        super.execute(event);

        for (IBookmarkModelComponent component : selectedComponents) {
            IBookmarkModelComponent parent = component.getParent();

            commandInvoker.invoke(new DeleteSingleBookmarkCommand(component, commandInvoker));
            if (needsCheckForRecursiveDeletion(parent)) {
                commandInvoker.invoke(new DeleteInferredBookmarksCommand((IBookmark) parent));
            }

        }

        return null;
    }

    private boolean needsCheckForRecursiveDeletion(IBookmarkModelComponent parent) {
        IsCategoryVisitor isCat = new IsCategoryVisitor();
        parent.accept(isCat);
        return (isCat.isCategory() == false);
    }

}
