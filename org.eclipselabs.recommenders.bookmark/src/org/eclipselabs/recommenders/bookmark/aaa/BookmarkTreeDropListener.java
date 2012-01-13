/**
 * Copyright (c) 2010 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Johannes Lerch - initial API and implementation.
 */
package org.eclipselabs.recommenders.bookmark.aaa;

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.part.ResourceTransfer;
import org.eclipselabs.recommenders.bookmark.aaa.model.Category;
import org.eclipselabs.recommenders.bookmark.aaa.model.FileBookmark;
import org.eclipselabs.recommenders.bookmark.aaa.model.IBookmark;
import org.eclipselabs.recommenders.bookmark.aaa.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.aaa.model.IModelVisitor;
import org.eclipselabs.recommenders.bookmark.aaa.model.JavaElementBookmark;

import com.google.common.base.Optional;

public class BookmarkTreeDropListener implements DropTargetListener {

    private final BookmarkCommandInvoker commandInvoker;

    public BookmarkTreeDropListener(final BookmarkCommandInvoker commandInvoker) {
        this.commandInvoker = commandInvoker;
    }

    @Override
    public void dragEnter(final DropTargetEvent event) {
        if (event.operations == DND.DROP_COPY) {
            event.detail = DND.DROP_COPY;
            return;
        }

        event.detail = DND.DROP_LINK;
    }

    @Override
    public void dragLeave(final DropTargetEvent event) {
    }

    @Override
    public void dragOperationChanged(final DropTargetEvent event) {
    }

    @Override
    public void dragOver(final DropTargetEvent event) {
    }

    @Override
    public void drop(final DropTargetEvent event) {
        final Optional<IBookmarkModelComponent> dropTarget = getDropTarget(event);

        if (event.data instanceof TreeSelection) {
            processTreeSelection(dropTarget, (TreeSelection) event.data);
        } else if (event.data instanceof IBookmark) {
            processBookmark(dropTarget, (IBookmark) event.data);
        }
    }

    private void processBookmark(Optional<IBookmarkModelComponent> dropTarget, IBookmark dropData) {

//        ValueVisitor visitor = new ValueVisitor();
//        dropData.accept(visitor);
//        processDroppedElement(dropTarget, visitor);
    }

    private Optional<IBookmarkModelComponent> getDropTarget(final DropTargetEvent event) {
        if (event.item == null) {
            return Optional.absent();
        } else {
            return Optional.of((IBookmarkModelComponent) event.item.getData());
        }
    }

    private void processTreeSelection(final Optional<IBookmarkModelComponent> dropTarget,
            final TreeSelection treeSelection) {
        final TreePath[] treePath = treeSelection.getPaths();
        for (int i = 0; i < treePath.length; i++) {
            processDroppedElement(dropTarget, treePath[i].getLastSegment());
        }
    }

    private void processDroppedElement(final Optional<IBookmarkModelComponent> dropTarget, final Object element) {
        commandInvoker.invoke(new AddElementToModelCommand(dropTarget, element));
    }

    @Override
    public void dropAccept(final DropTargetEvent event) {
    }

    public int getSupportedOperations() {
        return DND.DROP_LINK | DND.DROP_COPY;
    }

    public Transfer[] getSupportedTransfers() {
        return new Transfer[] { ResourceTransfer.getInstance(), LocalSelectionTransfer.getTransfer() };
    }

   

}
