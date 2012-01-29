package org.eclipselabs.recommenders.bookmark.visitor;

import org.eclipselabs.recommenders.bookmark.model.Category;
import org.eclipselabs.recommenders.bookmark.model.FileBookmark;
import org.eclipselabs.recommenders.bookmark.model.IBookmark;
import org.eclipselabs.recommenders.bookmark.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.model.IModelVisitor;
import org.eclipselabs.recommenders.bookmark.model.JavaElementBookmark;

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
