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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipselabs.recommenders.bookmark.aaa.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.aaa.tree.FlatRepresentationMode;
import org.eclipselabs.recommenders.bookmark.aaa.tree.HierarchicalRepresentationMode;
import org.eclipselabs.recommenders.bookmark.aaa.tree.RepresentationSwitchableTreeViewer;

public class BookmarkView extends ViewPart implements BookmarkCommandInvoker {

    private BookmarkModel model;
    private RepresentationSwitchableTreeViewer treeViewer;
    private Action switchFlatHierarchical;
    private boolean isHierarchicalModeActive;

    @Override
    public void createPartControl(final Composite parent) {

        treeViewer = new RepresentationSwitchableTreeViewer(parent, new HierarchicalRepresentationMode(), model);
        addDragDropListeners(treeViewer);

        setUpActions();
        setUpToolbar();

        loadDefaultModel();
        // activateFlatMode();
        activateHierarchicalMode();
    }

    private void setUpActions() {
        switchFlatHierarchical = new SwitchFlatHierarchicalAction(this);
    }

    private void setUpToolbar() {
        IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
        mgr.removeAll();
        mgr.add(switchFlatHierarchical);

    }

    public boolean isHierarchicalModeActive() {
        return isHierarchicalModeActive;
    }

    public void activateHierarchicalMode() {
        treeViewer.setRepresentation(new HierarchicalRepresentationMode());
        isHierarchicalModeActive = true;
    }

    public void activateFlatMode() {
        treeViewer.setRepresentation(new FlatRepresentationMode());
        isHierarchicalModeActive = false;
    }

    public void addDragDropListeners(final RepresentationSwitchableTreeViewer treeViewer) {
        final BookmarkTreeDropListener dropListener = new BookmarkTreeDropListener(this);
        treeViewer.addDropSupport(dropListener.getSupportedOperations(), dropListener.getSupportedTransfers(),
                dropListener);

        final BookmarkTreeDragListener dragListener = new BookmarkTreeDragListener(treeViewer);
        treeViewer.addDragSupport(dragListener.getSupportedOperations(), dragListener.getSupportedTransfers(),
                dragListener);
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

    @Override
    public void invoke(final IBookmarkModelCommand command) {
        command.execute(model);
        treeViewer.refresh();
    }

}
