package org.eclipselabs.recommenders.bookmark.view.copyCutPaste;

import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipselabs.recommenders.bookmark.model.Category;
import org.eclipselabs.recommenders.bookmark.model.FileBookmark;
import org.eclipselabs.recommenders.bookmark.model.IBookmark;
import org.eclipselabs.recommenders.bookmark.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.model.IModelVisitor;
import org.eclipselabs.recommenders.bookmark.model.JavaElementBookmark;
import org.eclipselabs.recommenders.bookmark.view.tree.RepresentationSwitchableTreeViewer;

public class CopyHandler extends AbstractHandler {

    private final RepresentationSwitchableTreeViewer treeViewer;

    public CopyHandler(RepresentationSwitchableTreeViewer treeViewer) {
        this.treeViewer = treeViewer;
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IBookmarkModelComponent[] components = getSelectedComponents();
        IBookmark[] bookmarks = getSelectedBookmarks(components);
        addDataToClipboard(bookmarks);
        return null;
    }

    private IBookmark[] getSelectedBookmarks(IBookmarkModelComponent[] components) {
        LinkedList<IBookmark> bookmarks = new LinkedList<IBookmark>();
        for (IBookmarkModelComponent component : components) {
            if (component instanceof IBookmark) {
                bookmarks.add((IBookmark) component);
            }
        }

        return bookmarks.toArray(new IBookmark[0]);
    }

    private void addDataToClipboard(IBookmark[] bookmarks) {
        Clipboard cb = new Clipboard(Display.getCurrent());

        Object[] data = prepareDataForAddingToClipboard(bookmarks);
        Transfer[] transfer = prepareTransferHandles();

        cb.setContents(data, transfer, DND.CLIPBOARD);

        cb.dispose();
    }

    private Transfer[] prepareTransferHandles() {
        return new Transfer[] { BookmarkTransfer.getInstance() };
    }

    private Object[] prepareDataForAddingToClipboard(IBookmark[] bookmarks) {
        BookmarkTransferObject bm = new BookmarkTransferObject(bookmarks);
        Object[] data = new Object[] { bm };
        return data;
    }

    protected IBookmarkModelComponent[] getSelectedComponents() {
        LinkedList<IBookmarkModelComponent> bookmarks = new LinkedList<IBookmarkModelComponent>();

        IStructuredSelection selections = treeViewer.getSelections();
        @SuppressWarnings("rawtypes")
        Iterator iterator = selections.iterator();
        while (iterator.hasNext()) {
            IBookmarkModelComponent component = (IBookmarkModelComponent) iterator.next();
            GetBookmarksVisitor visitor = new GetBookmarksVisitor();
            component.accept(visitor);
            bookmarks.addAll(visitor.getBookmarks());
        }

        return bookmarks.toArray(new IBookmarkModelComponent[0]);
    }

    private class GetBookmarksVisitor implements IModelVisitor {

        private LinkedList<IBookmarkModelComponent> bookmarks = new LinkedList<IBookmarkModelComponent>();

        public LinkedList<IBookmarkModelComponent> getBookmarks() {
            return bookmarks;
        }

        @Override
        public void visit(FileBookmark fileBookmark) {
            bookmarks.add(fileBookmark);
        }

        @Override
        public void visit(Category category) {
            bookmarks.add(category);
            for (IBookmark bookmark : category.getBookmarks()) {
                bookmarks.add(bookmark);
                bookmark.accept(this);
            }
        }

        @Override
        public void visit(JavaElementBookmark javaElementBookmark) {
            bookmarks.add(javaElementBookmark);
            for (IBookmark bookmark : javaElementBookmark.getChildElements()) {
                bookmark.accept(this);
            }
        }

    }
}
