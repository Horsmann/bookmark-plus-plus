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
package org.eclipselabs.recommenders.bookmark.aaa.model;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

public class Category {

    private final String label;
    private final List<IBookmark> bookmarks = Lists.newLinkedList();
    private boolean expanded;

    public Category(final String label, final IBookmark... bookmarks) {
        this.label = label;
        Collections.addAll(this.getBookmarks(), bookmarks);
    }

    public String getLabel() {
        return label;
    }

    public List<IBookmark> getBookmarks() {
        return bookmarks;
    }
}
