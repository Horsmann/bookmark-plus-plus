package org.eclipselabs.recommenders.bookmark.aaa;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.ui.part.ResourceTransfer;
import org.eclipselabs.recommenders.bookmark.aaa.model.Category;
import org.eclipselabs.recommenders.bookmark.aaa.model.FileBookmark;
import org.eclipselabs.recommenders.bookmark.aaa.model.IBookmark;
import org.eclipselabs.recommenders.bookmark.aaa.model.IModelVisitor;
import org.eclipselabs.recommenders.bookmark.aaa.model.JavaElementBookmark;
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
        
        Iterator iterator = selections.iterator();
        
        List<IBookmark> draggedNodes = new LinkedList<IBookmark>();
        
        while(iterator.hasNext()){
            
            Object object = iterator.next();
            
            if(object instanceof IBookmark) {
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

    private class ValueVisitor implements IModelVisitor {

        Object value;

        @Override
        public void visit(FileBookmark fileBookmark) {
            value = fileBookmark.getFile();
        }

        @Override
        public void visit(Category category) {
            value = category.getLabel();
        }

        @Override
        public void visit(JavaElementBookmark javaElementBookmark) {
            value = JavaCore.create(javaElementBookmark.getHandleId());
        }

    }

    private class TransferTypeVisitor implements IModelVisitor {

        Optional<TransferData> type = Optional.absent();

        @Override
        public void visit(FileBookmark fileBookmark) {
            TransferData[] supportedTypes = FileTransfer.getInstance().getSupportedTypes();

            type = Optional.of(supportedTypes[0]);
        }

        @Override
        public void visit(Category category) {
        }

        @Override
        public void visit(JavaElementBookmark javaElementBookmark) {
            TransferData[] supportedTypes = LocalSelectionTransfer.getTransfer().getSupportedTypes();

            type = Optional.of(supportedTypes[0]);

        }

    }

}
