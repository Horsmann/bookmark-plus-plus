package org.eclipselabs.recommenders.bookmark.view;

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.part.ResourceTransfer;

public class BookmarkTreeDragListener implements DragSourceListener {

    @Override
    public void dragStart(DragSourceEvent event) {
    }

    @Override
    public void dragSetData(DragSourceEvent event) {

        DragSource source = (DragSource) event.getSource();
        Tree tree = (Tree) source.getControl();
        
        ISelection selection = new StructuredSelection(tree.getSelection());
        LocalSelectionTransfer.getTransfer().setSelection(selection);
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
