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

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipselabs.recommenders.bookmark.aaa.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.aaa.model.Category;

public class FlatMode extends LabelProvider implements ITreeContentProvider {

    private final TreeViewer treeViewer;
    private BookmarkModel model;

    public FlatMode(final TreeViewer treeViewer) {
        this.treeViewer = treeViewer;
    }

    public void activate() {
        treeViewer.setContentProvider(this);
        treeViewer.setLabelProvider(this);
    }

    public void setModel(final BookmarkModel model) {
        this.model = model;
        treeViewer.setInput(this);
    }

    @Override
    public Object[] getElements(final Object inputElement) {
        return getChildren(inputElement);
    }

    @Override
    public Object[] getChildren(final Object parentElement) {
        if (isRoot(parentElement)) {
            return model.getCategories().toArray();
        } else if (parentElement instanceof Category) {
            final Category category = (Category) parentElement;
            return category.getBookmarks().toArray();
        }
        return new Object[0];
    }

    private boolean isRoot(final Object parentElement) {
        return parentElement == this;
    }

    @Override
    public Object getParent(final Object element) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean hasChildren(final Object element) {
        if (isRoot(element)) {
            return true;
        } else if (element instanceof Category) {
            return ((Category) element).getBookmarks().size() > 0;
        }
        return false;
    }

    @Override
    public Image getImage(final Object element) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getText(final Object element) {
        if (element instanceof Category) {
            return ((Category) element).getLabel();
        } else if (element instanceof FileBookmark) {
            return ((FileBookmark) element).getFile().toString();
        }
        throw new RuntimeException("Not implemented; Should never be reached");
    }

    @Override
    public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
    }

}
