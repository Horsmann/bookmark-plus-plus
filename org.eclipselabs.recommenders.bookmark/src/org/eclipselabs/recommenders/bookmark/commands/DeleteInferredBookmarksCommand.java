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

        // down
        boolean continueDeletion = deleteDownwardsExcludingTriggeringBookmark();

        // up
        if (continueDeletion) {
            deleteUpwardsIncludingTriggeringBookmark();
        }

    }

    private boolean deleteDownwardsExcludingTriggeringBookmark() {
        HasChildrenVisitor visitor = new HasChildrenVisitor();
        bookmark.accept(visitor);
        if (visitor.hasChildren) {
            return false;
        }
        return true;
    }

    private void deleteUpwardsIncludingTriggeringBookmark() {
        LinkedList<IBookmark> bookmarks = new LinkedList<IBookmark>();
        bookmarks.add(bookmark);
        IBookmarkModelComponent theBoomark = bookmark;
        while (theBoomark.hasParent()) {
            theBoomark = theBoomark.getParent();

            if (!(theBoomark instanceof IBookmark)) {
                break;
            }

            HasMoreThanOneChildVisitor moreThanOneChildVisitor = new HasMoreThanOneChildVisitor();
            theBoomark.accept(moreThanOneChildVisitor);
            if (moreThanOneChildVisitor.hasMoreThanOneChild() || !((IBookmark) theBoomark).isInferredNode()) {
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

    private class HasChildrenVisitor implements IModelVisitor {

        private boolean hasChildren;

        public boolean hasChildren() {
            return hasChildren;
        }

        @Override
        public void visit(FileBookmark fileBookmark) {
            hasChildren = false;
        }

        @Override
        public void visit(Category category) {
            if (category.getBookmarks().size() > 0) {
                hasChildren = true;
            } else {
                hasChildren = false;
            }
        }

        @Override
        public void visit(JavaElementBookmark javaElementBookmark) {
            if (javaElementBookmark.getChildElements().size() > 0) {
                hasChildren = true;
            } else {
                hasChildren = false;
            }
        }

    }

}
