package org.eclipselabs.recommenders.bookmark.commands;

import org.eclipselabs.recommenders.bookmark.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.model.Category;
import org.eclipselabs.recommenders.bookmark.model.IModelVisitor;

public class RenameJavaElementsCommand implements IBookmarkModelCommand {

    private final IModelVisitor visitor;

    public RenameJavaElementsCommand(IModelVisitor visitor) {
        this.visitor = visitor;
    }

    @Override
    public void execute(BookmarkModel model) {
        for (Category category : model.getCategories()) {
            category.accept(visitor);
        }
    }

    @Override
    public void execute(BookmarkModel model, Category category) {
        execute(model);
    }

}
