package org.eclipselabs.recommenders.bookmark.commands;

import org.eclipselabs.recommenders.bookmark.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.model.Category;
import org.eclipselabs.recommenders.bookmark.model.FileBookmark;
import org.eclipselabs.recommenders.bookmark.model.IModelVisitor;
import org.eclipselabs.recommenders.bookmark.model.JavaElementBookmark;

public class CollapseCategoriesCommand implements IBookmarkModelCommand {

    @Override
    public void execute(BookmarkModel model) {
        for (Category category : model.getCategories()) {
            category.accept(new IModelVisitor() {

                @Override
                public void visit(JavaElementBookmark javaElementBookmark) {
                }

                @Override
                public void visit(Category category) {
                    category.setExpanded(false);
                }

                @Override
                public void visit(FileBookmark fileBookmark) {

                }
            });
        }
    }

    @Override
    public void execute(BookmarkModel model, Category category) {
        execute(model);
    }

}
