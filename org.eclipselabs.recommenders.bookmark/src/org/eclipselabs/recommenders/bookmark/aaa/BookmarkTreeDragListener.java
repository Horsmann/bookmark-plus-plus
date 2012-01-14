package org.eclipselabs.recommenders.bookmark.aaa;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.part.ResourceTransfer;
import org.eclipselabs.recommenders.bookmark.aaa.model.IBookmark;
import org.eclipselabs.recommenders.bookmark.aaa.tree.RepresentationSwitchableTreeViewer;

import com.google.common.base.Optional;

public class BookmarkTreeDragListener implements DragSourceListener {

    private final RepresentationSwitchableTreeViewer treeViewer;

    private Optional<IBookmark[]> draggedBookmarks = Optional.absent();

    public BookmarkTreeDragListener(RepresentationSwitchableTreeViewer treeViewer) {
        this.treeViewer = treeViewer;
    }

    @Override
    public void dragStart(DragSourceEvent event) {
    }

    public Optional<IBookmark[]> getDragData() {
        return draggedBookmarks;
    }

    @Override
    public void dragSetData(DragSourceEvent event) {

        IStructuredSelection selections = treeViewer.getSelections();

        @SuppressWarnings("rawtypes")
        Iterator iterator = selections.iterator();

        List<IBookmark> draggedNodes = new LinkedList<IBookmark>();

        while (iterator.hasNext()) {

            Object object = iterator.next();

            if (object instanceof IBookmark) {
                draggedNodes.add((IBookmark) object);
            }

        }

        draggedBookmarks = Optional.of(draggedNodes.toArray(new IBookmark[0]));

    }

    @Override
    public void dragFinished(DragSourceEvent event) {

    }

    public int getSupportedOperations() {
        return DND.DROP_MOVE | DND.DROP_COPY;
    }

    public Transfer[] getSupportedTransfers() {
        return new Transfer[] { ResourceTransfer.getInstance(), LocalSelectionTransfer.getTransfer() };
    }

}
