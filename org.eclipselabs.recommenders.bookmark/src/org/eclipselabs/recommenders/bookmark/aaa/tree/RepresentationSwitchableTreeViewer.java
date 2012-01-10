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

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipselabs.recommenders.bookmark.aaa.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.aaa.model.Category;

public class RepresentationSwitchableTreeViewer {

    private IRepresentationMode currentMode;
    private final TreeViewer treeViewer;

    public RepresentationSwitchableTreeViewer(final Composite parent, final IRepresentationMode initialMode) {
        this.treeViewer = new TreeViewer(parent);
        currentMode = initialMode;
        treeViewer.setContentProvider(new SwitchableContentProvider());
        treeViewer.setLabelProvider(new SwitchableLabelProvider());
        treeViewer.addTreeListener(new TreeViewListener());

    }

    public void setRepresentation(final IRepresentationMode mode) {
        currentMode = mode;
        treeViewer.refresh();
    }

    public void setInput(final BookmarkModel model) {
        treeViewer.setInput(model);
        treeViewer.setExpandedElements(currentMode.getExpandedItems(model));
    }

    private class TreeViewListener implements ITreeViewerListener {

        @Override
        public void treeCollapsed(final TreeExpansionEvent event) {
            final ITreeExpansionVisitor visitor = currentMode
                    .createTreeExpansionVisitor(ITreeExpansionVisitor.Action.COLLAPSE);
            TreeModelUtil.acceptVisitor(event.getElement(), visitor);
        }

        @Override
        public void treeExpanded(final TreeExpansionEvent event) {
            final ITreeExpansionVisitor visitor = currentMode
                    .createTreeExpansionVisitor(ITreeExpansionVisitor.Action.EXPAND);
            TreeModelUtil.acceptVisitor(event.getElement(), visitor);
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
            TreeModelUtil.acceptVisitor(parentElement, visitor);
            return visitor.getChildren().toArray();
        }

        @Override
        public Object getParent(final Object element) {
            return null;
        }

        @Override
        public boolean hasChildren(final Object element) {
            final IChildrenResolverVisitor visitor = currentMode.createChildrenResolverVisitor();
            TreeModelUtil.acceptVisitor(element, visitor);
            return visitor.hasChildren();
        }
    }

    private class SwitchableLabelProvider extends LabelProvider {
        @Override
        public String getText(final Object element) {
            final ILabelProviderVisitor visitor = currentMode.createLabelProviderVisitor();
            TreeModelUtil.acceptVisitor(element, visitor);
            return visitor.getLabel();
        }
    }

}
