package org.eclipselabs.recommenders.bookmark.aaa;

import java.util.List;

import org.eclipselabs.recommenders.bookmark.aaa.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.aaa.model.Category;
import org.eclipselabs.recommenders.bookmark.aaa.model.FileBookmark;
import org.eclipselabs.recommenders.bookmark.aaa.model.IBookmark;
import org.eclipselabs.recommenders.bookmark.aaa.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.aaa.model.IModelVisitor;
import org.eclipselabs.recommenders.bookmark.aaa.model.JavaElementBookmark;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

public class ChangeElementInModleCommand implements IBookmarkModelCommand {

    private final Optional<IBookmarkModelComponent> dropTarget;
    private final IBookmark bookmark;
    private final boolean keepSource;

    public ChangeElementInModleCommand(final Optional<IBookmarkModelComponent> dropTarget, final IBookmark bookmark,
            final boolean keepSource) {
        this.dropTarget = dropTarget;
        this.bookmark = bookmark;
        this.keepSource = keepSource;
    }

    @Override
    public void execute(BookmarkModel model) {

        if (isOperationWithinSameCategory(model)) {
            return;
            // performReorderOperation(model);
        }

        ValueVisitor visitor = new ValueVisitor();
        bookmark.accept(visitor);
        if (!visitor.values.isEmpty()) {
            for (Object value : visitor.values) {
                new AddElementToModelCommand(dropTarget, value).execute(model);
            }
        }

        if (!keepSource) {
            deleteDroppedBookmarks(model);
        }

    }

    // private void performReorderOperation(BookmarkModel model) {
    //
    // // IsSilblingsDropVisitor visitor = new
    // IsSilblingsDropVisitor(dropTarget.get(), bookmark);
    // if (bookmarksAreOnSameHierarchy(model, visitor)) {
    // if (didDropOccurInUpperHalfOfWidgetElement()) {
    // // insertBookmarkBeforeDropTarget();
    // } else {
    // // insertBookmarkAfterDropTarget();
    // }
    // }
    //
    // }

    private boolean isOperationWithinSameCategory(BookmarkModel model) {

        Category category1 = getCategoryOf(dropTarget.get());

        Category category2 = getCategoryOf(bookmark);

        return category1 == category2;
    }

    private Category getCategoryOf(IBookmarkModelComponent component) {

        if (component instanceof Category) {
            return (Category) component;
        }

        return getCategoryOf(component.getParent());
    }

    private boolean didDropOccurInUpperHalfOfWidgetElement() {

        return false;
    }

    // private boolean bookmarksAreOnSameHierarchy(BookmarkModel model,
    // IsSilblingsDropVisitor visitor) {
    //
    // for (Category category : model.getCategories()) {
    // category.accept(visitor);
    // if (visitor.areSilblings()) {
    // return true;
    // }
    // }
    //
    // return false;
    // }
    //
    // private boolean isOperationWithinSameCategory(BookmarkModel model) {
    // for (IBookmarkModelComponent category : model.getCategories()) {
    // FindComponentModelVisitor targetFinderVisitor = new
    // FindComponentModelVisitor(dropTarget.get());
    // category.accept(targetFinderVisitor);
    // if (targetFinderVisitor.foundComponent()) {
    // FindComponentModelVisitor bookmarkFinderVisitor = new
    // FindComponentModelVisitor(bookmark);
    // category.accept(bookmarkFinderVisitor);
    // if (bookmarkFinderVisitor.foundComponent()) {
    // return true;
    // }
    // }
    // }
    // return false;
    // }

    private void deleteDroppedBookmarks(BookmarkModel model) {

        RemoveBookmarkModelComponentVisitor visitor = new RemoveBookmarkModelComponentVisitor(bookmark);
        for (Category category : model.getCategories()) {
            category.accept(visitor);
        }
    }

    private class ValueVisitor implements IModelVisitor {
        List<Object> values = Lists.newArrayList();

        @Override
        public void visit(FileBookmark fileBookmark) {

            Object object = fileBookmark.getFile();
            values.add(object);
        }

        @Override
        public void visit(Category category) {
        }

        @Override
        public void visit(JavaElementBookmark javaElementBookmark) {
            Object object = javaElementBookmark.getJavaElement();
            values.add(object);
            for (IBookmark bookmark : javaElementBookmark.getChildElements()) {
                bookmark.accept(this);
            }
        }

    }

    // private class IsSilblingsDropVisitor implements IModelVisitor {
    //
    // private boolean droppedOnSilbling = false;
    // private Optional<IBookmarkModelComponent> parent = Optional.absent();
    //
    // private boolean foundDrop = false, foundTarget = false;
    // private final IBookmarkModelComponent target;
    // private final IBookmarkModelComponent dropNode;
    //
    // public IsSilblingsDropVisitor(IBookmarkModelComponent target,
    // IBookmarkModelComponent dropNode) {
    // this.target = target;
    // this.dropNode = dropNode;
    //
    // }
    //
    // public boolean areSilblings() {
    // return droppedOnSilbling;
    // }
    //
    // @Override
    // public void visit(FileBookmark fileBookmark) {
    // }
    //
    // @Override
    // public void visit(Category category) {
    //
    // for (IBookmark bookmark : category.getBookmarks()) {
    // bookmark.accept(this);
    // }
    //
    // if (foundDrop && foundTarget) {
    // droppedOnSilbling = true;
    // }
    //
    // }
    //
    // @Override
    // public void visit(JavaElementBookmark javaElementBookmark) {
    //
    // if (javaElementBookmark == target) {
    // foundTarget = true;
    // }
    // if (javaElementBookmark == dropNode) {
    // foundDrop = true;
    // }
    //
    // for (JavaElementBookmark javaElement :
    // javaElementBookmark.getChildElements()) {
    // javaElement.accept(this);
    // }
    //
    // if (foundDrop && foundTarget) {
    // droppedOnSilbling = true;
    // }
    //
    // }
    //
    // }
}
