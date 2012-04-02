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
package org.eclipselabs.recommenders.bookmark.model;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

public class Category implements IBookmarkModelComponent {

    private static final long serialVersionUID = -1116061200117187534L;
    private String label;
    private final List<IBookmark> bookmarks = Lists.newLinkedList();
    private boolean expanded = true;

    public Category(final String label, final IBookmark... bookmarks) {
        this.label = label;
        Collections.addAll(this.bookmarks, bookmarks);
    }

    @Override
    public String toString() {
        return getLabel();
    }

    public String getLabel() {
        return label;
    }

    public List<IBookmark> getBookmarks() {
        return bookmarks;
    }

    public void remove(IBookmark bookmark) {
        bookmarks.remove(bookmark);
        bookmark.setParent(null);
    }

    @Override
    public void accept(final IModelVisitor visitor) {
        visitor.visit(this);
    }

    public void setExpanded(final boolean expanded) {
        this.expanded = expanded;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void add(final IBookmark bookmark) {
        bookmarks.add(bookmark);
        bookmark.setParent(this);
    }

    @Override
    public IBookmarkModelComponent getParent() {
        return null;
    }

    @Override
    public void setParent(IBookmarkModelComponent parent) {
    }

    @Override
    public boolean hasParent() {
        return false;
    }

    public void setLabel(String newLabel) {
        label = newLabel;
    }
}
