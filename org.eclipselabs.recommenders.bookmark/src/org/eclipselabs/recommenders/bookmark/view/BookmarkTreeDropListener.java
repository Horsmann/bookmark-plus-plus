package org.eclipselabs.recommenders.bookmark.view;

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.part.ResourceTransfer;

public class BookmarkTreeDropListener implements DropTargetListener {

    private IDropStrategy strategy;

    public BookmarkTreeDropListener(IDropStrategy initialStrategy) {
        this.strategy = initialStrategy;
    }

    @Override
    public void dragEnter(final DropTargetEvent event) {
        strategy.performDropEnter(event);
    }

    @Override
    public void dragLeave(final DropTargetEvent event) {
    }

    @Override
    public void dragOperationChanged(final DropTargetEvent event) {
    }

    @Override
    public void dragOver(final DropTargetEvent event) {
        event.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL;
        if (event.item != null) {
            TreeItem item = (TreeItem) event.item;
            Display display = item.getDisplay();
            Point pt = display.map(null, item.getParent(), event.x, event.y);
            Rectangle bounds = item.getBounds();
            if (pt.y < bounds.y + bounds.height / 2) {
                event.feedback |= DND.FEEDBACK_INSERT_BEFORE;
            } else {
                event.feedback |= DND.FEEDBACK_INSERT_AFTER;
            }
        }

    }

    @Override
    public void drop(final DropTargetEvent event) {
        strategy.performDrop(event);
    }

    @Override
    public void dropAccept(DropTargetEvent event) {
    }

    public int getSupportedOperations() {
        return DND.DROP_MOVE | DND.DROP_COPY;
    }

    public Transfer[] getSupportedTransfers() {
        return new Transfer[] { FileTransfer.getInstance(), ResourceTransfer.getInstance(), LocalSelectionTransfer.getTransfer() };
    }

    public void setNewStrategy(IDropStrategy strategy) {
        this.strategy = strategy;
    }
}
