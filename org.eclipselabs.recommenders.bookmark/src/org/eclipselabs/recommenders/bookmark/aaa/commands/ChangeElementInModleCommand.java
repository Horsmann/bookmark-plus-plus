package org.eclipselabs.recommenders.bookmark.aaa.commands;

import java.util.List;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Tree;
import org.eclipselabs.recommenders.bookmark.aaa.BookmarkCommandInvoker;
import org.eclipselabs.recommenders.bookmark.aaa.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.aaa.model.Category;
import org.eclipselabs.recommenders.bookmark.aaa.model.FileBookmark;
import org.eclipselabs.recommenders.bookmark.aaa.model.IBookmark;
import org.eclipselabs.recommenders.bookmark.aaa.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.aaa.model.IModelVisitor;
import org.eclipselabs.recommenders.bookmark.aaa.model.JavaElementBookmark;
import org.eclipselabs.recommenders.bookmark.aaa.visitor.HierarchyValueVisitor;
import org.eclipselabs.recommenders.bookmark.aaa.visitor.RemoveBookmarkModelComponentVisitor;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

public class ChangeElementInModleCommand implements IBookmarkModelCommand {

    private final Optional<IBookmarkModelComponent> dropTarget;
    private final IBookmark[] bookmarks;
    private final boolean keepSource;
    private final BookmarkCommandInvoker commandInvoker;
    private final Tree tree;
    private final Point dropLocation;

    public ChangeElementInModleCommand(final Optional<IBookmarkModelComponent> dropTarget, final IBookmark[] bookmarks,
            final boolean keepSource, BookmarkCommandInvoker commandInvoker, Tree tree, Point dropLocation) {
        this.dropTarget = dropTarget;
        this.bookmarks = bookmarks;
        this.keepSource = keepSource;
        this.commandInvoker = commandInvoker;
        this.tree = tree;
        this.dropLocation = dropLocation;
    }

    @Override
    public void execute(BookmarkModel model) {

        // An unfinished drag which was aborted
        for (IBookmark bookmark : bookmarks) {
            if (dropTarget.isPresent() && isOperationWithinSameCategory(model, bookmark)) {
                return;
            }
        }

        HierarchyValueVisitor visitor = new HierarchyValueVisitor();
        for (IBookmark bookmark : bookmarks) {
            bookmark.accept(visitor);
        }

        if (!visitor.getValues().isEmpty()) {
            Optional<DropTargetData> dropData = Optional.of(new DropTargetData(dropTarget.get(), tree, dropLocation));
            Optional<String> nameForNewCategory = Optional.absent();
            commandInvoker.invoke(new AddElementToModelCommand(dropData, visitor.getValues().toArray(), nameForNewCategory, commandInvoker));
        }

        if (!keepSource) {
            deleteDroppedBookmarks(model);
        }

    }

    private boolean isOperationWithinSameCategory(BookmarkModel model, IBookmark bookmark) {

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

        for (IBookmark bookmark : bookmarks) {
            RemoveBookmarkModelComponentVisitor visitor = new RemoveBookmarkModelComponentVisitor(bookmark);
            for (Category category : model.getCategories()) {
                category.accept(visitor);
            }
        }
    }

    

}
