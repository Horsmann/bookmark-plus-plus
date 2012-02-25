package org.eclipselabs.recommenders.bookmark.commands;

import org.eclipselabs.recommenders.bookmark.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.model.Category;
import org.eclipselabs.recommenders.bookmark.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.view.BookmarkCommandInvoker;
import org.eclipselabs.recommenders.bookmark.visitor.HierarchyValueVisitor;
import org.eclipselabs.recommenders.bookmark.visitor.RemoveBookmarkModelComponentVisitor;

import com.google.common.base.Optional;

public class ChangeElementCommand implements IBookmarkModelCommand {

    private final Optional<IBookmarkModelComponent> dropTarget;
    private final IBookmarkModelComponent[] bookmarks;
    private final boolean keepSource;
    private final BookmarkCommandInvoker commandInvoker;
    private final boolean isDropBeforeTarget;
    private final Optional<String> newCategoryName;

    public ChangeElementCommand(final IBookmarkModelComponent[] components, final boolean keepSource,
            BookmarkCommandInvoker commandInvoker, boolean isDropBeforeTarget,
            final Optional<IBookmarkModelComponent> dropTarget, final Optional<String> newCategoryName) {
        this.dropTarget = dropTarget;
        this.bookmarks = components;
        this.keepSource = keepSource;
        this.commandInvoker = commandInvoker;
        this.isDropBeforeTarget = isDropBeforeTarget;
        this.newCategoryName = newCategoryName;
    }

    @Override
    public void execute(BookmarkModel model) {

        if (isAbortedDrag(model)) {
            return;
        }

        HierarchyValueVisitor visitor = new HierarchyValueVisitor();
        for (IBookmarkModelComponent bookmark : bookmarks) {
            bookmark.accept(visitor);
        }

        if (!visitor.getValues().isEmpty()) {
            commandInvoker.invoke(new AddElementCommand(visitor.getValues().toArray(), commandInvoker,
                    isDropBeforeTarget, dropTarget, newCategoryName));
        }

        if (!keepSource) {
            deleteDroppedBookmarks(model);
        }

    }

    private boolean isAbortedDrag(BookmarkModel model) {
        for (IBookmarkModelComponent bookmark : bookmarks) {
            if (dropTarget.isPresent() && isOperationWithinSameCategory(model, bookmark)) {
                return true;
            }
        }
        return false;
    }

    private boolean isOperationWithinSameCategory(BookmarkModel model, IBookmarkModelComponent bookmark) {
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

    private void deleteDroppedBookmarks(BookmarkModel model) {

        for (IBookmarkModelComponent bookmark : bookmarks) {
            RemoveBookmarkModelComponentVisitor visitor = new RemoveBookmarkModelComponentVisitor(bookmark);
            for (Category category : model.getCategories()) {
                category.accept(visitor);
            }
        }
    }

    @Override
    public void execute(BookmarkModel model, Category category) {
        execute(model);
    }

}
