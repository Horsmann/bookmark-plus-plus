package org.eclipselabs.recommenders.bookmark.aaa.visitor;

import org.eclipselabs.recommenders.bookmark.aaa.model.Category;
import org.eclipselabs.recommenders.bookmark.aaa.model.FileBookmark;
import org.eclipselabs.recommenders.bookmark.aaa.model.IBookmark;
import org.eclipselabs.recommenders.bookmark.aaa.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.aaa.model.IModelVisitor;
import org.eclipselabs.recommenders.bookmark.aaa.model.JavaElementBookmark;

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
