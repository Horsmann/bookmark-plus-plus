package org.eclipselabs.recommenders.bookmark.aaa;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.part.ResourceTransfer;
import org.eclipselabs.recommenders.bookmark.aaa.model.Category;
import org.eclipselabs.recommenders.bookmark.aaa.model.FileBookmark;
import org.eclipselabs.recommenders.bookmark.aaa.model.IBookmark;
import org.eclipselabs.recommenders.bookmark.aaa.model.IModelVisitor;
import org.eclipselabs.recommenders.bookmark.aaa.model.JavaElementBookmark;
import org.eclipselabs.recommenders.bookmark.aaa.tree.RepresentationSwitchableTreeViewer;

public class BookmarkTreeDragListener implements DragSourceListener {

    private final RepresentationSwitchableTreeViewer treeViewer;

    public BookmarkTreeDragListener(RepresentationSwitchableTreeViewer treeViewer) {
        this.treeViewer = treeViewer;
    }

    @Override
    public void dragStart(DragSourceEvent event) {
    }

    @Override
    public void dragSetData(DragSourceEvent event) {

        IStructuredSelection selections = treeViewer.getSelections();

        IBookmark dragNode = (IBookmark) selections.getFirstElement();
        

        if (TextTransfer.getInstance().isSupportedType(event.dataType)){
            ValueVisitor visitor = new ValueVisitor();
            dragNode.accept(visitor);
            event.data = visitor.value;
        }
        
        ValueVisitor visitor2 = new ValueVisitor();
        dragNode.accept(visitor2);
        event.data = visitor2.value;
        
        if (event.data instanceof IJavaElement)
            System.out.println("IJavaElement");
        
        if (ResourceTransfer.getInstance().isSupportedType(event.dataType)) {
            ValueVisitor visitor = new ValueVisitor();
            dragNode.accept(visitor);
            event.data = visitor.value;
        }

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

}
