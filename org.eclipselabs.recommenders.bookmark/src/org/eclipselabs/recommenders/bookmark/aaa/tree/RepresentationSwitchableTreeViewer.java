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
package org.eclipselabs.recommenders.bookmark.aaa.tree;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipselabs.recommenders.bookmark.aaa.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.aaa.model.Category;
import org.eclipselabs.recommenders.bookmark.aaa.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.aaa.model.IModelVisitor;

public class RepresentationSwitchableTreeViewer {

    private IRepresentationMode currentMode;
    private final TreeViewer treeViewer;
    private BookmarkModel model;

    public RepresentationSwitchableTreeViewer(final Composite parent, final IRepresentationMode initialMode,
            final BookmarkModel model) {
        this.model = model;
        this.treeViewer = new TreeViewer(parent);
        currentMode = initialMode;
        treeViewer.setContentProvider(new SwitchableContentProvider());
        treeViewer.setLabelProvider(new SwitchableLabelProvider());
        treeViewer.addTreeListener(new TreeViewListener());
    }

    public void setRepresentation(final IRepresentationMode mode) {
        currentMode = mode;
        treeViewer.refresh();
        updateExpansions(model);
    }

    public void setInput(final BookmarkModel model) {
        this.model = model;
        treeViewer.setInput(model);
        updateExpansions(model);
    }

    private void updateExpansions(final BookmarkModel model) {
        treeViewer.setExpandedElements(currentMode.getExpandedItems(model));
    }

    public void addDropSupport(final int operations, final Transfer[] transferTypes,
            final DropTargetListener dropListener) {
        treeViewer.addDropSupport(operations, transferTypes, dropListener);
    }

    public void addDragSupport(final int operations, final Transfer[] transferTypes,
            final DragSourceListener dragListener) {
        treeViewer.addDragSupport(operations, transferTypes, dragListener);
    }

    public IStructuredSelection getSelections() {

        return (IStructuredSelection) treeViewer.getSelection();
    }

    public void refresh() {
        treeViewer.refresh();
        updateExpansions(model);
    }

    private class TreeViewListener implements ITreeViewerListener {

        @Override
        public void treeCollapsed(final TreeExpansionEvent event) {
            final ITreeExpansionVisitor visitor = currentMode
                    .createTreeExpansionVisitor(ITreeExpansionVisitor.Action.COLLAPSE);
            acceptVisitor(event.getElement(), visitor);
        }

        @Override
        public void treeExpanded(final TreeExpansionEvent event) {
            final ITreeExpansionVisitor visitor = currentMode
                    .createTreeExpansionVisitor(ITreeExpansionVisitor.Action.EXPAND);
            acceptVisitor(event.getElement(), visitor);
        }

    }

    private class SwitchableContentProvider implements ITreeContentProvider {

        @Override
        public void dispose() {
        }

        @Override
        public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
        }

        @Override
        public Object[] getElements(final Object inputElement) {
            if (inputElement == null) {
                return new Object[0];
            }

            if (inputElement instanceof BookmarkModel) {
                final BookmarkModel model = (BookmarkModel) inputElement;
                final List<Category> categories = model.getCategories();
                return categories.toArray();
            } else {
                throw new IllegalStateException(String.format("Input element of TreeViewer is not of type '%s'.",
                        BookmarkModel.class.toString()));
            }
        }

        @Override
        public Object[] getChildren(final Object parentElement) {
            final IChildrenResolverVisitor visitor = currentMode.createChildrenResolverVisitor();
            acceptVisitor(parentElement, visitor);
            return visitor.getChildren().toArray();
        }

        @Override
        public Object getParent(final Object element) {
            return null;
        }

        @Override
        public boolean hasChildren(final Object element) {
            final IChildrenResolverVisitor visitor = currentMode.createChildrenResolverVisitor();
            acceptVisitor(element, visitor);
            return visitor.hasChildren();
        }
    }

    private class SwitchableLabelProvider extends LabelProvider {
        @Override
        public String getText(final Object element) {
            final ILabelProviderVisitor visitor = currentMode.createLabelProviderVisitor();
            acceptVisitor(element, visitor);
            return visitor.getLabel();
        }

        @Override
        public Image getImage(final Object element) {
            final IImageProviderVisitor visitor = currentMode.createImageProviderVisitor();
            acceptVisitor(element, visitor);
            return visitor.getImage();
        }
    }

    private static void acceptVisitor(final Object element, final IModelVisitor visitor) {
        if (element instanceof IBookmarkModelComponent) {
            ((IBookmarkModelComponent) element).accept(visitor);
        } else {
            throw new RuntimeException("Should not be reachable. Visitor can only be used on IBookmarkModelComponents.");
        }
    }
}
