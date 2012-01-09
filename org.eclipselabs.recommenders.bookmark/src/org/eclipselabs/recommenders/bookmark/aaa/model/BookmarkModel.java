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

import java.util.List;

import com.google.common.collect.Lists;

public class BookmarkModel {

    private final List<Category> categories = Lists.newLinkedList();

    public void add(final Category category) {
        categories.add(category);
    }

    public List<Category> getCategories() {
        return categories;
    }
}
