package org.eclipselabs.recommenders.bookmark.aaa.visitor;

import org.eclipselabs.recommenders.bookmark.aaa.model.Category;
import org.eclipselabs.recommenders.bookmark.aaa.model.FileBookmark;
import org.eclipselabs.recommenders.bookmark.aaa.model.IModelVisitor;
import org.eclipselabs.recommenders.bookmark.aaa.model.JavaElementBookmark;

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