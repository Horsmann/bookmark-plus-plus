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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipselabs.recommenders.bookmark.aaa.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.aaa.tree.FlatRepresentationMode;
import org.eclipselabs.recommenders.bookmark.aaa.tree.HierarchicalRepresentationMode;
import org.eclipselabs.recommenders.bookmark.aaa.tree.RepresentationSwitchableTreeViewer;

public class BookmarkView extends ViewPart {

    private BookmarkModel model;
    private FlatRepresentationMode flatMode;
    private HierarchicalRepresentationMode hierarchicalMode;
    private RepresentationSwitchableTreeViewer treeViewer;

    @Override
    public void createPartControl(final Composite parent) {
        flatMode = new FlatRepresentationMode();
        hierarchicalMode = new HierarchicalRepresentationMode();

        treeViewer = new RepresentationSwitchableTreeViewer(parent, hierarchicalMode);

        loadDefaultModel();
        // activateFlatMode();
        activateHierarchicalMode();
    }

    private void activateHierarchicalMode() {
        treeViewer.setRepresentation(hierarchicalMode);
    }

    private void activateFlatMode() {
        treeViewer.setRepresentation(flatMode);
    }

    private void loadDefaultModel() {
        setModel(BookmarkIO.load());
    }

    private void setModel(final BookmarkModel model) {
        this.model = model;
        treeViewer.setInput(model);
    }

    @Override
    public void setFocus() {
    }

}
