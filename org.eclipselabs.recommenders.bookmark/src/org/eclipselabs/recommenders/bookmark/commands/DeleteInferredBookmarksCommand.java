package org.eclipselabs.recommenders.bookmark.commands;

import java.util.LinkedList;

import org.eclipselabs.recommenders.bookmark.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.model.Category;
import org.eclipselabs.recommenders.bookmark.model.FileBookmark;
import org.eclipselabs.recommenders.bookmark.model.IBookmark;
import org.eclipselabs.recommenders.bookmark.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.model.IModelVisitor;
import org.eclipselabs.recommenders.bookmark.model.JavaElementBookmark;
import org.eclipselabs.recommenders.bookmark.visitor.IsCategoryVisitor;
import org.eclipselabs.recommenders.bookmark.visitor.RemoveBookmarkModelComponentVisitor;

import com.google.common.base.Optional;

public class DeleteInferredBookmarksCommand implements IBookmarkModelCommand {

    private final IBookmark bookmark;

    public DeleteInferredBookmarksCommand(IBookmark bookmark) {
        this.bookmark = bookmark;
    }

    @Override
    public void execute(BookmarkModel model) {
        deleteIfParentIsCategory();
        deleteRecursive();
    }

    private void deleteRecursive() {
        LinkedList<IBookmark> bookmarks = new LinkedList<IBookmark>();
        bookmarks.add(bookmark);
        IBookmarkModelComponent theBoomark = bookmark;
        while (theBoomark.hasParent()) {
            theBoomark = theBoomark.getParent();

            if (!(theBoomark instanceof IBookmark)) {
                break;
            }

            HasMoreThanOneChildVisitor visitor = new HasMoreThanOneChildVisitor();
            theBoomark.accept(visitor);
            if (visitor.hasMoreThanOneChild()) {
                break;
            } else {
                bookmarks.add((IBookmark) theBoomark);
            }
        }

        Optional<Category> category = Optional.absent();
        IBookmarkModelComponent component = bookmark;
        while (component.hasParent()) {
            component = component.getParent();
            IsCategoryVisitor isCat = new IsCategoryVisitor();
            component.accept(isCat);
            if (isCat.isCategory()) {
                category = Optional.of((Category) component);
                break;
            }
        }

        if (category.isPresent()) {

            for (IBookmark bm : bookmarks) {
                RemoveBookmarkModelComponentVisitor removeVisitor = new RemoveBookmarkModelComponentVisitor(bm);
                category.get().accept(removeVisitor);
            }
        }
    }

    private void deleteIfParentIsCategory() {
        IBookmarkModelComponent parent = bookmark.getParent();
        IsCategoryVisitor isCategoryVisitor = new IsCategoryVisitor();
        parent.accept(isCategoryVisitor);
        if (isCategoryVisitor.isCategory()) {
            ((Category) parent).remove(bookmark);
        }
    }

    @Override
    public void execute(BookmarkModel model, Category category) {
        execute(model);
    }

    private class HasMoreThanOneChildVisitor implements IModelVisitor {

        private boolean hasMoreThanOneChild;

        public boolean hasMoreThanOneChild() {
            return hasMoreThanOneChild;
        }

        @Override
        public void visit(FileBookmark fileBookmark) {
            hasMoreThanOneChild = false;
        }

        @Override
        public void visit(Category category) {
            if (category.getBookmarks().size() > 1) {
                hasMoreThanOneChild = true;
            } else {
                hasMoreThanOneChild = false;
            }
        }

        @Override
        public void visit(JavaElementBookmark javaElementBookmark) {
            if (javaElementBookmark.getChildElements().size() > 1) {
                hasMoreThanOneChild = true;
            } else {
                hasMoreThanOneChild = false;
            }
        }

    }

}
