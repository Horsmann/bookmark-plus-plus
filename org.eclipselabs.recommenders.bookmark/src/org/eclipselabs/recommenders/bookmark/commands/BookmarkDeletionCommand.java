package org.eclipselabs.recommenders.bookmark.commands;

import org.eclipselabs.recommenders.bookmark.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.model.Category;
import org.eclipselabs.recommenders.bookmark.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.visitor.RemoveBookmarkModelComponentVisitor;

public class BookmarkDeletionCommand implements IBookmarkModelCommand {

    private final IBookmarkModelComponent component;

    public BookmarkDeletionCommand(IBookmarkModelComponent component) {
        this.component = component;
    }

    @Override
    public void execute(BookmarkModel model) {
        deletionForCategories(component, model);
        deletionForIBookmarks(component, model);
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
