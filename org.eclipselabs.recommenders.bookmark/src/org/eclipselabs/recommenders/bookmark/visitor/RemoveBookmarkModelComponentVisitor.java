package org.eclipselabs.recommenders.bookmark.visitor;

import org.eclipselabs.recommenders.bookmark.model.Category;
import org.eclipselabs.recommenders.bookmark.model.FileBookmark;
import org.eclipselabs.recommenders.bookmark.model.IBookmark;
import org.eclipselabs.recommenders.bookmark.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.model.IModelVisitor;
import org.eclipselabs.recommenders.bookmark.model.JavaElementBookmark;

public class RemoveBookmarkModelComponentVisitor implements IModelVisitor {

    private final IBookmarkModelComponent removeTarget;

    public RemoveBookmarkModelComponentVisitor(IBookmarkModelComponent component) {
        this.removeTarget = component;
    }

    @Override
    public void visit(FileBookmark fileBookmark) {
    }

    @Override
    public void visit(Category category) {
        if (removeTarget instanceof IBookmark) {
            category.remove((IBookmark) removeTarget);
            for (IBookmark bookmark : category.getBookmarks()) {
                bookmark.accept(this);
            }
        }
    }

    @Override
    public void visit(JavaElementBookmark javaElementBookmark) {
        if (removeTarget instanceof JavaElementBookmark) {
            javaElementBookmark.remove((JavaElementBookmark) removeTarget);
            for (IBookmark bookmark : javaElementBookmark.getChildElements()) {
                bookmark.accept(this);
            }
        }
    }
}
