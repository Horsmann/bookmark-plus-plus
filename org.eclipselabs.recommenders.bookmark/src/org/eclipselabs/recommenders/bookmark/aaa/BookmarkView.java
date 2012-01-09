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

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipselabs.recommenders.bookmark.aaa.model.BookmarkModel;

public class BookmarkView extends ViewPart {

    private TreeViewer treeViewer;
    private BookmarkModel model;
    private FlatMode flatMode;

    @Override
    public void createPartControl(final Composite parent) {
        treeViewer = new TreeViewer(parent);
        flatMode = new FlatMode(treeViewer);

        activateFlatMode();
        loadDefaultModel();
    }

    private void activateFlatMode() {
        flatMode.activate();
    }

    private void loadDefaultModel() {
        final BookmarkModel model = BookmarkIO.load();
        setModel(model);
    }

    private void setModel(final BookmarkModel model) {
        this.model = model;
        flatMode.setModel(model);
    }

    @Override
    public void setFocus() {
        // TODO Auto-generated method stub
    }

}
