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

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.part.ResourceTransfer;
import org.eclipselabs.recommenders.bookmark.aaa.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.aaa.model.Category;
import org.eclipselabs.recommenders.bookmark.aaa.model.FileBookmark;
import org.eclipselabs.recommenders.bookmark.aaa.model.IBookmark;
import org.eclipselabs.recommenders.bookmark.aaa.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.aaa.model.IModelVisitor;
import org.eclipselabs.recommenders.bookmark.aaa.model.JavaElementBookmark;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

public class BookmarkTreeDropListener implements DropTargetListener {

    private final BookmarkCommandInvoker commandInvoker;

    public BookmarkTreeDropListener(final BookmarkCommandInvoker commandInvoker) {
        this.commandInvoker = commandInvoker;
    }

    @Override
    public void dragEnter(final DropTargetEvent event) {

        if ((event.operations & DND.DROP_MOVE) != 0) {
            event.detail = DND.DROP_MOVE;
        } else {
            event.detail = DND.DROP_COPY;
        }
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
        }

        ISelection selections = LocalSelectionTransfer.getTransfer().getSelection();
        if (selections instanceof IStructuredSelection) {
            processStructuredSelection(dropTarget, (IStructuredSelection) selections, isCopyOperation(event), event);
        }

    }

    private boolean isCopyOperation(DropTargetEvent event) {
        return !((event.operations & DND.DROP_MOVE) != 0);
    }

    private void processStructuredSelection(Optional<IBookmarkModelComponent> dropTarget,
            IStructuredSelection selections, boolean keepSource, DropTargetEvent event) {

        IBookmarkModelComponent[] components = getModelComponentsFromSelection(selections);

        if (dropTarget.isPresent() && areBookmarksSortedByHand(dropTarget.get(), components)) {
            
            DropTarget dropTargetSource = (DropTarget) event.getSource();
            Tree tree = (Tree) dropTargetSource.getControl();
            
            processReorderingofNodes(dropTarget.get(), components, new Point(event.x, event.y), tree);

            System.out.println("Sort");
        } else {

            IBookmark[] bookmarks = getBookmarksFromSelection(selections);
            processDroppedElementOriginatedFromInsideTheView(dropTarget, bookmarks, keepSource);
        }
    }

    private IBookmark[] getBookmarksFromSelection(IStructuredSelection selections) {
        @SuppressWarnings("rawtypes")
        Iterator iterator = selections.iterator();

        List<IBookmark> items = Lists.newArrayList();
        while (iterator.hasNext()) {
            Object object = iterator.next();
            if (isTreeItemWithValidData(object)) {
                TreeItem item = (TreeItem) object;
                items.add((IBookmark) item.getData());
            }
        }
        return items.toArray(new IBookmark[0]);
    }

    private IBookmarkModelComponent[] getModelComponentsFromSelection(IStructuredSelection selections) {
        @SuppressWarnings("rawtypes")
        Iterator iterator = selections.iterator();

        List<IBookmarkModelComponent> items = Lists.newArrayList();
        while (iterator.hasNext()) {
            Object object = iterator.next();
            if (hasEncapsulatedModelComponent(object)) {
                TreeItem item = (TreeItem) object;
                items.add((IBookmarkModelComponent) item.getData());
            }
        }
        return items.toArray(new IBookmarkModelComponent[0]);

    }

    private boolean hasEncapsulatedModelComponent(Object object) {

        if (object instanceof TreeItem) {
            if (((TreeItem) object).getData() instanceof IBookmarkModelComponent) {
                return true;
            }
        } else {
            return false;
        }

        return false;
    }

    private void processReorderingofNodes(IBookmarkModelComponent target, IBookmarkModelComponent[] components, Point dropPoint, Tree tree) {
        commandInvoker.invoke(new RelocateNodesCommand(target, components, dropPoint, tree));

    }

    private boolean areBookmarksSortedByHand(IBookmarkModelComponent target, IBookmarkModelComponent[] components) {
        for (IBookmarkModelComponent bookmark : components) {
            if (!haveSameParent(target, bookmark)) {
                return false;
            }
        }

        return true;
    }

    private boolean haveSameParent(IBookmarkModelComponent target, IBookmarkModelComponent bookmark) {

        return target.getParent() == bookmark.getParent();
    }

    private boolean isTreeItemWithValidData(Object object) {

        if (object instanceof TreeItem) {
            if (((TreeItem) object).getData() instanceof IBookmark) {
                return true;
            }
        } else {
            return false;
        }

        return false;
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

        Object[] elements = new Object[treePath.length];

        for (int i = 0; i < treePath.length; i++) {
            elements[i] = treePath[i].getLastSegment();
        }

        processDroppedElementOriginatedFromOutsideTheView(dropTarget, elements);
    }

    private void processDroppedElementOriginatedFromOutsideTheView(final Optional<IBookmarkModelComponent> dropTarget,
            final Object[] elements) {
        commandInvoker.invoke(new AddElementToModelCommand(dropTarget, elements));
    }

    private void processDroppedElementOriginatedFromInsideTheView(Optional<IBookmarkModelComponent> dropTarget,
            IBookmark[] bookmarks, boolean isCopyOperation) {

        if (!causeRecursion(bookmarks, dropTarget)) {
            commandInvoker.invoke(new ChangeElementInModleCommand(dropTarget, bookmarks, isCopyOperation,
                    commandInvoker));
        }
    }

    private boolean causeRecursion(IBookmark[] bookmarks, Optional<IBookmarkModelComponent> dropTarget) {

        if (!dropTarget.isPresent()) {
            return false;
        }

        for (IBookmark bookmark : bookmarks) {

            RecursionPreventerVisitor visitor = new RecursionPreventerVisitor(dropTarget.get());
            bookmark.accept(visitor);
            if (visitor.recursionFound) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void dropAccept(final DropTargetEvent event) {

    }

    public int getSupportedOperations() {
        return DND.DROP_MOVE | DND.DROP_COPY;
    }

    public Transfer[] getSupportedTransfers() {
        return new Transfer[] { ResourceTransfer.getInstance(), LocalSelectionTransfer.getTransfer() };
    }

    private class RecursionPreventerVisitor implements IModelVisitor {

        boolean recursionFound = false;
        private final IBookmarkModelComponent target;

        public RecursionPreventerVisitor(IBookmarkModelComponent target) {
            this.target = target;
        }

        @Override
        public void visit(FileBookmark fileBookmark) {
        }

        @Override
        public void visit(Category category) {

        }

        @Override
        public void visit(JavaElementBookmark javaElementBookmark) {
            if (javaElementBookmark == target) {
                recursionFound = true;
            }
            for (IBookmark child : javaElementBookmark.getChildElements()) {
                child.accept(this);
            }

        }

    }
}
