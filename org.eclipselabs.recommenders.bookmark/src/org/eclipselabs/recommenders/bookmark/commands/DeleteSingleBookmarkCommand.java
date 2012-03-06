package org.eclipselabs.recommenders.bookmark.commands;

import org.eclipselabs.recommenders.bookmark.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.model.Category;
import org.eclipselabs.recommenders.bookmark.model.IBookmark;
import org.eclipselabs.recommenders.bookmark.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.view.BookmarkCommandInvoker;
import org.eclipselabs.recommenders.bookmark.visitor.IsIBookmarkVisitor;
import org.eclipselabs.recommenders.bookmark.visitor.RemoveBookmarkModelComponentVisitor;

public class DeleteSingleBookmarkCommand implements IBookmarkModelCommand {

    private final IBookmarkModelComponent component;
    private final BookmarkCommandInvoker commandInvoker;

    public DeleteSingleBookmarkCommand(IBookmarkModelComponent component, BookmarkCommandInvoker invoker) {
        this.component = component;
        this.commandInvoker = invoker;
    }

    @Override
    public void execute(BookmarkModel model, Category category) {
        execute(model);
    }
    
    @Override
    public void execute(BookmarkModel model) {
        IBookmarkModelComponent parent = component.getParent();
        deletionForCategories(component, model);
        deletionForIBookmarks(component, model);
        deleteInferred(parent);
    }

    private void deleteInferred(IBookmarkModelComponent parent) {
        if (parent == null) {
            return;
        }
        IsIBookmarkVisitor visitor = new IsIBookmarkVisitor();
        parent.accept(visitor);
        if (visitor.isIBookmark()) {
            commandInvoker.invoke(new DeleteInferredBookmarksCommand((IBookmark) parent));
        }
    }


    private void deletionForIBookmarks(IBookmarkModelComponent component, BookmarkModel model) {
        RemoveBookmarkModelComponentVisitor visitor = new RemoveBookmarkModelComponentVisitor(component);
        for (Category category : model.getCategories()) {
            category.accept(visitor);
        }
    }

    private void deletionForCategories(IBookmarkModelComponent selection, BookmarkModel model) {
        model.remove(selection);
    }

}
