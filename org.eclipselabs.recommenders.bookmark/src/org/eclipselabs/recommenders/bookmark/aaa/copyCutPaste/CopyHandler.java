package org.eclipselabs.recommenders.bookmark.aaa.copyCutPaste;

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
import org.eclipselabs.recommenders.bookmark.aaa.model.Category;
import org.eclipselabs.recommenders.bookmark.aaa.model.FileBookmark;
import org.eclipselabs.recommenders.bookmark.aaa.model.IBookmark;
import org.eclipselabs.recommenders.bookmark.aaa.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.aaa.model.IModelVisitor;
import org.eclipselabs.recommenders.bookmark.aaa.model.JavaElementBookmark;
import org.eclipselabs.recommenders.bookmark.aaa.tree.RepresentationSwitchableTreeViewer;

public class CopyHandler extends AbstractHandler {

    private final RepresentationSwitchableTreeViewer treeViewer;

    public CopyHandler(RepresentationSwitchableTreeViewer treeViewer) {
        this.treeViewer = treeViewer;
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {

        IBookmark[] bookmarks = getSelectedBookmarks();
        addDataToClipboard(bookmarks);

        return null;
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

    protected IBookmark[] getSelectedBookmarks() {
        LinkedList<IBookmark> bookmarks = new LinkedList<IBookmark>();

        IStructuredSelection selections = treeViewer.getSelections();
        @SuppressWarnings("rawtypes")
        Iterator iterator = selections.iterator();
        while (iterator.hasNext()) {
            IBookmarkModelComponent component = (IBookmarkModelComponent) iterator.next();
            GetBookmarksVisitor visitor = new GetBookmarksVisitor();
            component.accept(visitor);
            bookmarks.addAll(visitor.getBookmarks());
        }

        return bookmarks.toArray(new IBookmark[0]);
    }

    private class GetBookmarksVisitor implements IModelVisitor {

        private LinkedList<IBookmark> bookmarks = new LinkedList<IBookmark>();
        
        public LinkedList<IBookmark> getBookmarks() {
            return bookmarks;
        }

        @Override
        public void visit(FileBookmark fileBookmark) {
            bookmarks.add(fileBookmark);
        }

        @Override
        public void visit(Category category) {
            for (IBookmark bookmark : category.getBookmarks()) {
                bookmarks.add(bookmark);
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
