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
package org.eclipselabs.recommenders.bookmark.view;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.action.AddCategoryAction;
import org.eclipselabs.recommenders.bookmark.action.CloseAllEditorWindowsAction;
import org.eclipselabs.recommenders.bookmark.action.DeActivateCategoryModeAction;
import org.eclipselabs.recommenders.bookmark.action.SwitchFlatHierarchicalAction;
import org.eclipselabs.recommenders.bookmark.commands.IBookmarkModelCommand;
import org.eclipselabs.recommenders.bookmark.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.view.copyCutPaste.CopyHandler;
import org.eclipselabs.recommenders.bookmark.view.copyCutPaste.CutHandler;
import org.eclipselabs.recommenders.bookmark.view.copyCutPaste.PasteHandler;
import org.eclipselabs.recommenders.bookmark.view.tree.FlatRepresentationMode;
import org.eclipselabs.recommenders.bookmark.view.tree.HierarchicalRepresentationMode;
import org.eclipselabs.recommenders.bookmark.view.tree.RepresentationSwitchableTreeViewer;

public class BookmarkView extends ViewPart implements BookmarkCommandInvoker {

    private BookmarkModel model;
    private RepresentationSwitchableTreeViewer treeViewer;
    private Action switchFlatHierarchical;
    private boolean isHierarchicalModeActive;
    private CloseAllEditorWindowsAction closeAllEditors;
    private AddCategoryAction addNewCategory;
    private DeActivateCategoryModeAction categoryMode;

    @Override
    public void createPartControl(final Composite parent) {
        treeViewer = new RepresentationSwitchableTreeViewer(parent, new HierarchicalRepresentationMode(), model);
        treeViewer.enabledActionsForViewPart(this, this);
        addDragDropListeners(treeViewer);

        setUpActions();
        setUpToolbar();
        setUpPluginPreferences();
        loadModel();
        activateHierarchicalMode();

        addViewPartListener();
        addResourceListener();
        addCopyCutPasteFeatures();
        Activator.setBookmarkView(this);
    }

    private void addCopyCutPasteFeatures() {
        IHandlerService handlerServ = (IHandlerService) getSite().getService(IHandlerService.class);
        PasteHandler paste = new PasteHandler(treeViewer, this);
        handlerServ.activateHandler("org.eclipse.ui.edit.paste", paste);

        CopyHandler copy = new CopyHandler(treeViewer);
        handlerServ.activateHandler("org.eclipse.ui.edit.copy", copy);

        CutHandler cut = new CutHandler(treeViewer, this);
        handlerServ.activateHandler("org.eclipse.ui.edit.cut", cut);

    }

    public BookmarkModel getModel() {
        return model;
    }

    private void addResourceListener() {
        ResourcesPlugin.getWorkspace().addResourceChangeListener(new ResourceListener());
    }

    private void setUpPluginPreferences() {

    }

    private void addViewPartListener() {
        IPartService service = (IPartService) getSite().getService(IPartService.class);
        service.addPartListener(new ViewPartListener(this));
    }

    private void setUpActions() {
        switchFlatHierarchical = new SwitchFlatHierarchicalAction(this);
        closeAllEditors = new CloseAllEditorWindowsAction(this);
        addNewCategory = new AddCategoryAction(this);
        categoryMode = new DeActivateCategoryModeAction(treeViewer.getComboViewer(), model, treeViewer);
    }

    private void setUpToolbar() {
        IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
        mgr.removeAll();
        mgr.add(closeAllEditors);
        mgr.add(switchFlatHierarchical);
        mgr.add(categoryMode);
        mgr.add(new Separator());
        mgr.add(addNewCategory);

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

        final BookmarkTreeDragListener dragListener = new BookmarkTreeDragListener();
        treeViewer.addDragSupport(dragListener.getSupportedOperations(), dragListener.getSupportedTransfers(),
                dragListener);

        final BookmarkTreeDropListener dropListener = new BookmarkTreeDropListener(this);
        treeViewer.addDropSupport(dropListener.getSupportedOperations(), dropListener.getSupportedTransfers(),
                dropListener);

    }

    private void loadModel() {
        setModel(BookmarkIO.loadFromDefaultFile());
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

    private class ResourceListener implements IResourceChangeListener {

        @Override
        public void resourceChanged(IResourceChangeEvent event) {

            getSite().getShell().getDisplay().asyncExec(new GuiRunnable(event));
        }

        private class GuiRunnable implements Runnable {

            private final IResourceChangeEvent event;

            public GuiRunnable(IResourceChangeEvent event) {
                this.event = event;
            }

            @Override
            public void run() {
                if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
                    treeViewer.refresh();
                }
            }

        }
    }

    public void setNewModelForTreeViewer(BookmarkModel newModel) {
        setModel(newModel);
    }
}
