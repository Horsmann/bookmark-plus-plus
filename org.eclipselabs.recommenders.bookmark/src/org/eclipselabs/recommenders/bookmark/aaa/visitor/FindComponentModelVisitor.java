package org.eclipselabs.recommenders.bookmark.aaa.visitor;

import org.eclipselabs.recommenders.bookmark.aaa.model.Category;
import org.eclipselabs.recommenders.bookmark.aaa.model.FileBookmark;
import org.eclipselabs.recommenders.bookmark.aaa.model.IBookmark;
import org.eclipselabs.recommenders.bookmark.aaa.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.aaa.model.IModelVisitor;
import org.eclipselabs.recommenders.bookmark.aaa.model.JavaElementBookmark;

public class FindComponentModelVisitor implements IModelVisitor {

    private boolean foundComponent;
    private final IBookmarkModelComponent component;

    public FindComponentModelVisitor(final IBookmarkModelComponent component) {
        this.component = component;
    }

    public boolean foundComponent() {
        return foundComponent;
    }

    @Override
    public void visit(final FileBookmark fileBookmark) {
        if (fileBookmark == component) {
            foundComponent = true;
        }
    }

    @Override
    public void visit(final Category category) {
        for (IBookmark bookmark : category.getBookmarks()) {
            bookmark.accept(this);
        }
    }

    @Override
    public void visit(final JavaElementBookmark javaElementBookmark) {
        if (javaElementBookmark == component) {
            foundComponent = true;
        } else {
            for (final JavaElementBookmark childElement : javaElementBookmark.getChildElements()) {
                childElement.accept(this);
            }
        }
    }

}
