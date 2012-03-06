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
    private final BookmarkCommandInvoker invoker;

    public DeleteSingleBookmarkCommand(IBookmarkModelComponent component, BookmarkCommandInvoker invoker) {
        this.component = component;
        this.invoker = invoker;
    }

    @Override
    public void execute(BookmarkModel model) {
        deletionForCategories(component, model);
        deletionForIBookmarks(component, model);
        deleteInferred(component.getParent());
    }

    private void deleteInferred(IBookmarkModelComponent parent) {
        if (parent == null) {
            return;
        }
        IsIBookmarkVisitor visitor = new IsIBookmarkVisitor();
        parent.accept(visitor);
        if (visitor.isIBookmark()) {
            invoker.invoke(new DeleteInferredBookmarksCommand((IBookmark) component));
        }
    }

    @Override
    public void execute(BookmarkModel model, Category category) {
        execute(model);
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
