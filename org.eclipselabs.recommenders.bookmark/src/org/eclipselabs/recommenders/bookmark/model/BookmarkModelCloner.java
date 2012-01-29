package org.eclipselabs.recommenders.bookmark.model;

import java.util.List;

import org.eclipselabs.recommenders.bookmark.commands.IBookmarkModelCommand;
import org.eclipselabs.recommenders.bookmark.view.BookmarkCommandInvoker;

import com.google.common.collect.Lists;

public class BookmarkModelCloner implements BookmarkCommandInvoker {

    private final BookmarkModel master;
    private BookmarkModel cloned;

    public BookmarkModelCloner(final BookmarkModel master) {
        this.master = master;
        cloned = new BookmarkModel();
    }

    public BookmarkModel clone() {

        for (Category cat : master.getCategories()) {
            Category newCategory = new Category(cat.getLabel());
            for (IBookmark bookmark : cat.getBookmarks()) {
                IBookmark newBookmark = cloneRecursive(bookmark);
                newCategory.add(newBookmark);
            }
            cloned.add(newCategory);
//
//             HierarchyValueVisitor valueVisitor = new HierarchyValueVisitor();
//             cat.accept(valueVisitor);
//            
//             Optional<IBookmarkModelComponent> dropTarget = Optional.absent();
//             Optional<String> label = Optional.of(cat.getLabel());
//             invoke(new AddElementToModelCommand(dropTarget,
//             valueVisitor.getValues().toArray(), label, this, false));
        }

        return cloned;
    }

    private IBookmark cloneRecursive(IBookmark bookmark) {

        IBookmarkCloneVisitor visitor = new IBookmarkCloneVisitor();
        bookmark.accept(visitor);
        IBookmark newBookmark = visitor.getClone();

        GetChildrenVisitor childrenVisitor = new GetChildrenVisitor();
        bookmark.accept(childrenVisitor);
        for (IBookmark child : childrenVisitor.getChildren()) {
            IBookmark cloned = cloneRecursive(child);
            LinkClonedBookmarksVisitor linkVisitor = new LinkClonedBookmarksVisitor(newBookmark);
            cloned.accept(linkVisitor);
        }

        return newBookmark;
    }

    @Override
    public void invoke(IBookmarkModelCommand command) {
        command.execute(cloned);
    }

    private class LinkClonedBookmarksVisitor implements IModelVisitor {

        private final IBookmark newBookmark;

        public LinkClonedBookmarksVisitor(IBookmark newBookmark) {
            this.newBookmark = newBookmark;
        }

        @Override
        public void visit(FileBookmark fileBookmark) {
        }

        @Override
        public void visit(Category category) {
        }

        @Override
        public void visit(JavaElementBookmark javaElementBookmark) {
            ((JavaElementBookmark) newBookmark).addChildElement(javaElementBookmark);
        }
    }

    private class GetChildrenVisitor implements IModelVisitor {

        private List<IBookmark> bookmarks = Lists.newArrayList();

        public List<IBookmark> getChildren() {
            return bookmarks;
        }

        @Override
        public void visit(FileBookmark fileBookmark) {
        }

        @Override
        public void visit(Category category) {
        }

        @Override
        public void visit(JavaElementBookmark javaElementBookmark) {
            bookmarks.addAll(javaElementBookmark.getChildElements());
        }

    }

    private class IBookmarkCloneVisitor implements IModelVisitor {

        private IBookmark clone;

        private IBookmark getClone() {
            return clone;
        }

        @Override
        public void visit(FileBookmark fileBookmark) {
            clone = new FileBookmark(fileBookmark.getFile());
        }

        @Override
        public void visit(Category category) {
        }

        @Override
        public void visit(JavaElementBookmark javaElementBookmark) {
            JavaElementBookmark newJavaEle = new JavaElementBookmark(javaElementBookmark.getHandleId(),
                    javaElementBookmark.isInferredNode());
            newJavaEle.setExpanded(javaElementBookmark.isExpanded());
            clone = newJavaEle;
        }

    }
}
