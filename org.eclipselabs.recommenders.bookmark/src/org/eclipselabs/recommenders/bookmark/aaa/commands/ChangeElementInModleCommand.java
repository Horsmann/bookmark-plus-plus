package org.eclipselabs.recommenders.bookmark.aaa.commands;

import org.eclipselabs.recommenders.bookmark.aaa.BookmarkCommandInvoker;
import org.eclipselabs.recommenders.bookmark.aaa.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.aaa.model.Category;
import org.eclipselabs.recommenders.bookmark.aaa.model.IBookmark;
import org.eclipselabs.recommenders.bookmark.aaa.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.aaa.visitor.HierarchyValueVisitor;
import org.eclipselabs.recommenders.bookmark.aaa.visitor.RemoveBookmarkModelComponentVisitor;

import com.google.common.base.Optional;

public class ChangeElementInModleCommand implements IBookmarkModelCommand {

    private final Optional<IBookmarkModelComponent> dropTarget;
    private final IBookmarkModelComponent[] bookmarks;
    private final boolean keepSource;
    private final BookmarkCommandInvoker commandInvoker;
    private final boolean isDropBeforeTarget;
    private final Optional<String> newCategoryName;

    public ChangeElementInModleCommand(final IBookmarkModelComponent[] components,
            final boolean keepSource, BookmarkCommandInvoker commandInvoker, boolean isDropBeforeTarget, final Optional<IBookmarkModelComponent> dropTarget, final Optional<String> newCategoryName) {
        this.dropTarget = dropTarget;
        this.bookmarks = components;
        this.keepSource = keepSource;
        this.commandInvoker = commandInvoker;
        this.isDropBeforeTarget = isDropBeforeTarget;
        this.newCategoryName = newCategoryName;
    }

    @Override
    public void execute(BookmarkModel model) {

        // An unfinished drag which was aborted
        for (IBookmarkModelComponent bookmark : bookmarks) {
            if (dropTarget.isPresent() && isOperationWithinSameCategory(model, bookmark)) {
                return;
            }
        }

        HierarchyValueVisitor visitor = new HierarchyValueVisitor();
        for (IBookmarkModelComponent bookmark : bookmarks) {
            bookmark.accept(visitor);
        }

        if (!visitor.getValues().isEmpty()) {
            commandInvoker.invoke(new AddElementToModelCommand(visitor.getValues().toArray(), commandInvoker, isDropBeforeTarget,dropTarget,newCategoryName));
        }

        if (!keepSource) {
            deleteDroppedBookmarks(model);
        }

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

    

}
