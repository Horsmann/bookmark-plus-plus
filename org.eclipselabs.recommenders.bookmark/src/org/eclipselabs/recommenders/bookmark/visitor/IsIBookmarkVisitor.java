package org.eclipselabs.recommenders.bookmark.visitor;

import org.eclipselabs.recommenders.bookmark.model.Category;
import org.eclipselabs.recommenders.bookmark.model.FileBookmark;
import org.eclipselabs.recommenders.bookmark.model.IBookmark;
import org.eclipselabs.recommenders.bookmark.model.IModelVisitor;
import org.eclipselabs.recommenders.bookmark.model.JavaElementBookmark;

public class IsIBookmarkVisitor implements IModelVisitor {

    boolean isIBookmark;

    public boolean isIBookmark() {
        return isIBookmark;
    }

    @Override
    public void visit(FileBookmark fileBookmark) {
        if (fileBookmark instanceof IBookmark) {
            isIBookmark = true;
        }
    }

    @Override
    public void visit(Category category) {
    }

    @Override
    public void visit(JavaElementBookmark javaElementBookmark) {
        if (javaElementBookmark instanceof IBookmark) {
            isIBookmark = true;
        }
    }

}
