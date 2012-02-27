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

    private final IBookmark commandTriggeringBookmark;

    public DeleteInferredBookmarksCommand(IBookmark bookmark) {
        this.commandTriggeringBookmark = bookmark;
    }

    @Override
    public void execute(BookmarkModel model) {
        boolean continueDeletion = deleteDownwardsExcludingTriggeringBookmark();
        if (continueDeletion) {
            deleteUpwardsIncludingTriggeringBookmark();
        }
    }

    private boolean deleteDownwardsExcludingTriggeringBookmark() {
        HasChildrenVisitor visitor = new HasChildrenVisitor();
        commandTriggeringBookmark.accept(visitor);
        if (visitor.hasChildren()) {
            return false;
        }
        return true;
    }

    private void deleteUpwardsIncludingTriggeringBookmark() {
        LinkedList<IBookmark> bookmarks = getCandidatesForDeletion();
        Optional<Category> category = getCategory();
        if (category.isPresent()) {
            removeBookmarks(category.get(), bookmarks);
        }
    }

    private void removeBookmarks(Category category, LinkedList<IBookmark> bookmarks) {
        for (IBookmark bm : bookmarks) {
            RemoveBookmarkModelComponentVisitor removeVisitor = new RemoveBookmarkModelComponentVisitor(bm);
            category.accept(removeVisitor);
        }
    }

    private Optional<Category> getCategory() {
        Optional<Category> category = Optional.absent();
        IBookmarkModelComponent component = commandTriggeringBookmark;
        while (component.hasParent()) {
            component = component.getParent();
            IsCategoryVisitor isCat = new IsCategoryVisitor();
            component.accept(isCat);
            if (isCat.isCategory()) {
                category = Optional.of((Category) component);
                break;
            }
        }
        return category;
    }

    private LinkedList<IBookmark> getCandidatesForDeletion() {
        LinkedList<IBookmark> bookmarks = new LinkedList<IBookmark>();
        bookmarks.add(commandTriggeringBookmark);

        IBookmarkModelComponent theBoomark = commandTriggeringBookmark;
        while (theBoomark.hasParent()) {
            theBoomark = theBoomark.getParent();
            if (!(theBoomark instanceof IBookmark)) {
                break;
            }

            if (isUndeletable(theBoomark)) {
                break;
            } else {
                bookmarks.add((IBookmark) theBoomark);
            }
        }
        return bookmarks;
    }

    private boolean isUndeletable(IBookmarkModelComponent theBoomark) {
        HasMoreThanOneChildVisitor moreThanOneChildVisitor = new HasMoreThanOneChildVisitor();
        theBoomark.accept(moreThanOneChildVisitor);
        return moreThanOneChildVisitor.hasMoreThanOneChild() || !((IBookmark) theBoomark).isInferredNode();
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
