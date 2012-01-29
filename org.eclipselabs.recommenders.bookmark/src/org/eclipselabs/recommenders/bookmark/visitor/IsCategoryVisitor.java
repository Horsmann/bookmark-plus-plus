package org.eclipselabs.recommenders.bookmark.visitor;

import org.eclipselabs.recommenders.bookmark.model.Category;
import org.eclipselabs.recommenders.bookmark.model.FileBookmark;
import org.eclipselabs.recommenders.bookmark.model.IModelVisitor;
import org.eclipselabs.recommenders.bookmark.model.JavaElementBookmark;

public class IsCategoryVisitor implements IModelVisitor {
    private boolean isCategory = false;

    public boolean isCategory() {
        return isCategory;
    }

    @Override
    public void visit(FileBookmark fileBookmark) {

    }

    @Override
    public void visit(Category category) {
        isCategory = true;
    }

    @Override
    public void visit(JavaElementBookmark javaElementBookmark) {

    }
}