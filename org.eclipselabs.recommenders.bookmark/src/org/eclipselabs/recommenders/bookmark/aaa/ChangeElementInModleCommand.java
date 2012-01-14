package org.eclipselabs.recommenders.bookmark.aaa;

import org.eclipselabs.recommenders.bookmark.aaa.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.aaa.model.Category;
import org.eclipselabs.recommenders.bookmark.aaa.model.FileBookmark;
import org.eclipselabs.recommenders.bookmark.aaa.model.IBookmark;
import org.eclipselabs.recommenders.bookmark.aaa.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.aaa.model.IModelVisitor;
import org.eclipselabs.recommenders.bookmark.aaa.model.JavaElementBookmark;

import com.google.common.base.Optional;

public class ChangeElementInModleCommand implements IBookmarkModelCommand {

    private final Optional<IBookmarkModelComponent> dropTarget;
    private final IBookmark[] bookmarks;
    private final boolean keepSource;

    public ChangeElementInModleCommand(final Optional<IBookmarkModelComponent> dropTarget, final IBookmark[] bookmarks,
            final boolean keepSource) {
        this.dropTarget = dropTarget;
        this.bookmarks = bookmarks;
        this.keepSource = keepSource;
    }

    @Override
    public void execute(BookmarkModel model) {

        for (IBookmark bookmark : bookmarks) {
            ValueVisitor visitor = new ValueVisitor();
            bookmark.accept(visitor);
            if (visitor.value.isPresent()) {
                new AddElementToModelCommand(dropTarget, visitor.value.get()).execute(model);
            }
        }

        if (!keepSource) {
            deleteDroppedBookmarks(model);
        }

    }

    private void deleteDroppedBookmarks(BookmarkModel model) {

        for (IBookmark bookmark : bookmarks) {
            RemoveBookmarkModelComponentVisitor visitor = new RemoveBookmarkModelComponentVisitor(bookmark);
            for (Category category : model.getCategories()) {
                category.accept(visitor);
            }
        }
    }

    private class ValueVisitor implements IModelVisitor {
        Optional<Object> value = Optional.absent();

        @Override
        public void visit(FileBookmark fileBookmark) {

            Object object = fileBookmark.getFile();
            value = Optional.of(object);
        }

        @Override
        public void visit(Category category) {
        }

        @Override
        public void visit(JavaElementBookmark javaElementBookmark) {
            Object object = javaElementBookmark.getJavaElement();
            value = Optional.of(object);
        }

    }
}
