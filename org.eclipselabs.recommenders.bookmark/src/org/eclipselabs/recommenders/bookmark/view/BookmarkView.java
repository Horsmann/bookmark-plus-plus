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
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.action.AddCategoryAction;
import org.eclipselabs.recommenders.bookmark.action.CloseAllEditorWindowsAction;
import org.eclipselabs.recommenders.bookmark.action.DeleteBookmarkAction;
import org.eclipselabs.recommenders.bookmark.action.GoToCategoryModeAction;
import org.eclipselabs.recommenders.bookmark.action.OpenBookmarkAction;
import org.eclipselabs.recommenders.bookmark.action.OpenInFileSystemAction;
import org.eclipselabs.recommenders.bookmark.action.RenameCategoryAction;
import org.eclipselabs.recommenders.bookmark.action.SwitchFlatHierarchicalAction;
import org.eclipselabs.recommenders.bookmark.action.SwitchInferredStateAction;
import org.eclipselabs.recommenders.bookmark.commands.BookmarkDeletionCommand;
import org.eclipselabs.recommenders.bookmark.commands.IBookmarkModelCommand;
import org.eclipselabs.recommenders.bookmark.commands.OpenBookmarkCommand;
import org.eclipselabs.recommenders.bookmark.commands.RenameCategoryCommand;
import org.eclipselabs.recommenders.bookmark.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.model.Category;
import org.eclipselabs.recommenders.bookmark.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.view.handler.copyCut.CopyHandler;
import org.eclipselabs.recommenders.bookmark.view.handler.copyCut.CutHandler;
import org.eclipselabs.recommenders.bookmark.view.handler.paste.DefaultPasteStrategy;
import org.eclipselabs.recommenders.bookmark.view.handler.paste.IPasteStrategy;
import org.eclipselabs.recommenders.bookmark.view.handler.paste.PasteHandler;
import org.eclipselabs.recommenders.bookmark.view.tree.FlatRepresentationMode;
import org.eclipselabs.recommenders.bookmark.view.tree.HierarchicalRepresentationMode;
import org.eclipselabs.recommenders.bookmark.view.tree.RepresentationSwitchableTreeViewer;
import org.eclipselabs.recommenders.bookmark.view.tree.SelectionChangedListener;
import org.eclipselabs.recommenders.bookmark.view.tree.combo.ComboStrategySwapper;
import org.eclipselabs.recommenders.bookmark.view.tree.combo.HideableComboViewer;
import org.eclipselabs.recommenders.bookmark.view.tree.combo.MouseDropStrategyChanger;
import org.eclipselabs.recommenders.bookmark.view.tree.combo.PasteStrategyChanger;

public class BookmarkView extends ViewPart implements BookmarkCommandInvoker {

    private String MEMENTO_GUI_STATE = "GUISTATE";
    private String MEMENTO_IS_FLAT = "isFlat";
    private String MEMENTO_IS_CATEGORY_MODE_ACTIVE = "isCategoryModeActive";
    private String MEMENTO_CATEGORY_MODE_SELECTED_CATEGORY = "selectedCategory";

    private BookmarkModel model;
    private RepresentationSwitchableTreeViewer treeViewer;
    private Action switchFlatHierarchical;
    private boolean isHierarchicalModeActive;
    private CloseAllEditorWindowsAction closeAllEditors;
    private AddCategoryAction addNewCategory;
    private GoToCategoryModeAction categoryMode;
    private RenameCategoryAction renameCategory;
    private OpenBookmarkAction openInEditor;
    private OpenInFileSystemAction openInFileSystem;
    private DeleteBookmarkAction deleteBookmarks;
    private HideableComboViewer comboViewer;
    private GoToCategoryModeAction switchCategory;
    private SwitchInferredStateAction switchInferred;
    private BookmarkTreeDropListener dropListener;
    private DefaultDropStrategy defaultDropStrategy;
    private DefaultPasteStrategy defaultPasteStrategy;
    private PasteHandler pasteHandler;
    private IMemento memento;
    private ComboStrategySwapper comboStrategySwapper;

    @Override
    public void createPartControl(final Composite parent) {
        parent.setLayout(GridLayoutFactory.fillDefaults().create());

        loadModel();
        initGuiComponents(parent);
        setModelForTreeViewer(model);
        configureTreeViewer(treeViewer, model, comboViewer);
        addCopyCutPasteFeatures(pasteHandler);
        enableActionsInToolbar(closeAllEditors, switchFlatHierarchical, categoryMode, addNewCategory);
        activateHierarchicalMode();
        addResourceListener();
        Activator.setBookmarkView(this);

        restoreState(memento);
    }

    private void setModelForTreeViewer(BookmarkModel model) {
        setModel(model);
    }

    private void loadModel() {
        model = Activator.getModel();
    }

    private void initGuiComponents(Composite parent) {
        initTreeViewerWithDropAndPasteHandlingComponents(parent);
        initHideableComboViewer(parent, model, treeViewer, dropListener, defaultDropStrategy, pasteHandler,
                defaultPasteStrategy);
        initActions(treeViewer, model, comboViewer);
    }

    private void initTreeViewerWithDropAndPasteHandlingComponents(Composite parent) {
        initTreeViewer(parent);
        initDropListenerAndDefaultDropStrategy();
        initPasteHandlerAndDefaultPasteStrategy();
    }

    private void initPasteHandlerAndDefaultPasteStrategy() {
        defaultPasteStrategy = new DefaultPasteStrategy(this, treeViewer);
        pasteHandler = new PasteHandler(defaultPasteStrategy);
    }

    private void initDropListenerAndDefaultDropStrategy() {
        defaultDropStrategy = new DefaultDropStrategy(this);
        dropListener = new BookmarkTreeDropListener(defaultDropStrategy);
    }

    private void initTreeViewer(Composite parent) {
        treeViewer = new RepresentationSwitchableTreeViewer(parent, new HierarchicalRepresentationMode());
    }

    private void configureTreeViewer(RepresentationSwitchableTreeViewer treeViewer, BookmarkModel model,
            HideableComboViewer hideableComboViewer) {
        createContextActions(treeViewer, model, hideableComboViewer);
        addTreeKeyListener(treeViewer);
        addDoubleClickListener(treeViewer);

        addTreeViewerSelectionChangedListener();

        addDragDropListeners(treeViewer);
    }

    private void addDoubleClickListener(RepresentationSwitchableTreeViewer treeViewer) {
        treeViewer.addDoubleclickListener(new DoubleclickListener(this, this));
    }

    private void addTreeKeyListener(RepresentationSwitchableTreeViewer treeViewer) {
        TreeKeyListener listener = new TreeKeyListener(this, this);
        treeViewer.addKeyListener(listener);

    }

    private void addTreeViewerSelectionChangedListener() {
        SelectionChangedListener selectionListener = new SelectionChangedListener();
        selectionListener.register(renameCategory);
        selectionListener.register(openInEditor);
        selectionListener.register(openInFileSystem);
        selectionListener.register(deleteBookmarks);
        selectionListener.register(switchCategory);
        selectionListener.register(switchInferred);
        treeViewer.addSelectionChangedListener(selectionListener);
    }

    private void initHideableComboViewer(Composite parent, BookmarkModel model,
            RepresentationSwitchableTreeViewer treeViewer, BookmarkTreeDropListener dropListener,
            IDropStrategy defaultDropStrategy, PasteHandler pasteHandler, IPasteStrategy defaultPasteStrategy) {

        MouseDropStrategyChanger mouseDropStrategy = new MouseDropStrategyChanger(dropListener, defaultDropStrategy,
                this);
        PasteStrategyChanger pasteStrategy = new PasteStrategyChanger(pasteHandler, defaultPasteStrategy, treeViewer,
                this);
        comboStrategySwapper = new ComboStrategySwapper();
        comboStrategySwapper.register(mouseDropStrategy);
        comboStrategySwapper.register(pasteStrategy);

        comboViewer = new HideableComboViewer(parent, SWT.NONE, model, treeViewer, comboStrategySwapper);
    }

    private void createContextActions(RepresentationSwitchableTreeViewer treeViewer, BookmarkModel model,
            HideableComboViewer hideableComboViewer) {
        renameCategory = new RenameCategoryAction(treeViewer, this);
        openInEditor = new OpenBookmarkAction(treeViewer, this, this);
        openInFileSystem = new OpenInFileSystemAction(treeViewer);
        deleteBookmarks = new DeleteBookmarkAction(treeViewer, this);
        switchCategory = new GoToCategoryModeAction(hideableComboViewer, model, treeViewer);
        switchInferred = new SwitchInferredStateAction(treeViewer);
        addContextMenu(treeViewer, renameCategory, openInEditor, openInFileSystem, deleteBookmarks, switchCategory,
                switchInferred);
    }

    private void addContextMenu(RepresentationSwitchableTreeViewer treeViewer,
            final RenameCategoryAction renameCategory, final OpenBookmarkAction openInEditor,
            final OpenInFileSystemAction openInFileSystem, final DeleteBookmarkAction deleteBookmarks,
            final GoToCategoryModeAction switchCategory, final SwitchInferredStateAction switchInferred) {

        final MenuManager menuMgr = new MenuManager();
        menuMgr.setRemoveAllWhenShown(true);

        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager mgr) {
                menuMgr.add(openInEditor);
                menuMgr.add(renameCategory);
                menuMgr.add(switchCategory);
                menuMgr.add(switchInferred);
                menuMgr.add(openInFileSystem);
                menuMgr.add(deleteBookmarks);
            }
        });

        menuMgr.update(true);
        treeViewer.setContextMenu(menuMgr);
        getSite().registerContextMenu(menuMgr, treeViewer.getSelectionProvider());
    }

    private void addCopyCutPasteFeatures(PasteHandler pasteHandler) {
        IHandlerService handlerServ = (IHandlerService) getSite().getService(IHandlerService.class);
        addCopyFeature(handlerServ);
        addCutFeature(handlerServ);
        addPasteFeature(handlerServ, pasteHandler);
    }

    private void addPasteFeature(IHandlerService handlerServ, PasteHandler pasteHandler) {
        handlerServ.activateHandler("org.eclipse.ui.edit.paste", pasteHandler);
    }

    private void addCutFeature(IHandlerService handlerServ) {
        CutHandler cut = new CutHandler(treeViewer, this);
        handlerServ.activateHandler("org.eclipse.ui.edit.cut", cut);
    }

    private void addCopyFeature(IHandlerService handlerServ) {
        CopyHandler copy = new CopyHandler(treeViewer);
        handlerServ.activateHandler("org.eclipse.ui.edit.copy", copy);
    }

    public BookmarkModel getModel() {
        return model;
    }

    private void addResourceListener() {
        ResourcesPlugin.getWorkspace().addResourceChangeListener(new ResourceListener());
    }

    private void initActions(RepresentationSwitchableTreeViewer treeViewer, BookmarkModel model,
            HideableComboViewer hideableComboViewer) {
        switchFlatHierarchical = new SwitchFlatHierarchicalAction(this);
        closeAllEditors = new CloseAllEditorWindowsAction(this);
        addNewCategory = new AddCategoryAction(this, hideableComboViewer);
        categoryMode = new GoToCategoryModeAction(hideableComboViewer, model, treeViewer);

    }

    private void enableActionsInToolbar(Action closeAllEditors, Action switchFlatHierarchical, Action categoryMode,
            Action addNewCategory) {
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
        addDragSupportToViewer();
        addDropSupportToViewer();
    }

    private void addDropSupportToViewer() {
        treeViewer.addDropSupport(dropListener.getSupportedOperations(), dropListener.getSupportedTransfers(),
                dropListener);
    }

    private void addDragSupportToViewer() {
        final BookmarkTreeDragListener dragListener = new BookmarkTreeDragListener();
        treeViewer.addDragSupport(dragListener.getSupportedOperations(), dragListener.getSupportedTransfers(),
                dragListener);

    }

    private void setModel(final BookmarkModel model) {
        if (this.model != model) {
            this.model.removeAll();
            for (Category category : model.getCategories()) {
                this.model.add(category);
            }
        }
        treeViewer.setInput(model);
    }

    @Override
    public void setFocus() {
    }

    @Override
    public void invoke(final IBookmarkModelCommand command) {
        Object input = treeViewer.getInput();
        if (input instanceof Category) {
            command.execute(model, (Category) input);
        } else {
            command.execute(model);
        }
        treeViewer.refresh();
    }

    @Override
    public void saveState(IMemento memento) {
        super.saveState(memento);
        memento = memento.createChild(MEMENTO_GUI_STATE);
        memento.putBoolean(MEMENTO_IS_FLAT, !isHierarchicalModeActive());
        memento.putBoolean(MEMENTO_IS_CATEGORY_MODE_ACTIVE, comboViewer.isVisible());
        memento.putInteger(MEMENTO_CATEGORY_MODE_SELECTED_CATEGORY, comboViewer.getNumberOfSelectedCategory());
    }

    private void restoreState(IMemento memento) {
        if (memento == null) {
            return;
        }

        memento = memento.getChild(MEMENTO_GUI_STATE);
        if (memento != null) {
            processFlatState(memento);
            processCategoryMode(memento);
        }
    }

    private void processCategoryMode(IMemento memento) {
        Boolean isCategoryModeActive = memento.getBoolean(MEMENTO_IS_CATEGORY_MODE_ACTIVE);
        if (isCategoryModeActive != null && isCategoryModeActive) {
            Integer selectedCategory = memento.getInteger(MEMENTO_CATEGORY_MODE_SELECTED_CATEGORY);
            if (isCategoryNumberValid(selectedCategory)) {
                comboViewer.show(model.getCategories().get(selectedCategory));
            }
        }
    }

    private void processFlatState(IMemento memento) {
        Boolean isFlat = memento.getBoolean(MEMENTO_IS_FLAT);
        if (isFlat != null && isFlat) {
            activateFlatMode();
        }
    }

    private boolean isCategoryNumberValid(Integer selectedCategory) {
        if (selectedCategory == null || selectedCategory < 0 || selectedCategory >= model.getCategories().size()) {
            return false;
        }
        return true;
    }

    @Override
    public void init(IViewSite site, IMemento memento) throws PartInitException {
        this.memento = memento;
        init(site);
    }

    private class ResourceListener implements IResourceChangeListener {

        @Override
        public void resourceChanged(IResourceChangeEvent event) {
            IWorkbenchPartSite site = getSite();
            if (site == null) {
                return;
            }
            Shell shell = site.getShell();
            if (shell == null || shell.isDisposed()) {
                return;
            }
            Display display = shell.getDisplay();
            if (display == null || display.isDisposed()) {
                return;
            }
            display.asyncExec(new GuiRunnable(event));
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

    private class TreeKeyListener implements KeyListener {

        private final ViewPart part;
        private final BookmarkCommandInvoker commandInvoker;

        public TreeKeyListener(ViewPart part, BookmarkCommandInvoker commandInvoker) {
            this.part = part;
            this.commandInvoker = commandInvoker;
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (isDeletion(e)) {
                processDeletion(e);
            } else if (isRename(e)) {
                processRename(e);
            } else if (isEnter(e)) {
                processEnter(e);
            }

            treeViewer.refresh();
        }

        private void processEnter(KeyEvent e) {
            commandInvoker.invoke(new OpenBookmarkCommand(treeViewer.getSelections(), part.getSite()
                    .getWorkbenchWindow().getActivePage(), commandInvoker));
        }

        private boolean isEnter(KeyEvent e) {
            return e.keyCode == SWT.CR;
        }

        private void processRename(KeyEvent e) {
            commandInvoker.invoke(new RenameCategoryCommand(treeViewer));
        }

        private boolean isRename(KeyEvent e) {
            return e.keyCode == SWT.F2;
        }

        private void processDeletion(KeyEvent e) {
            TreeItem[] selections = getTreeSelections(e);
            for (TreeItem item : selections) {
                searchBookmarkDeleteSelection(item);
            }
        }

        private TreeItem[] getTreeSelections(KeyEvent e) {
            Tree tree = (Tree) e.getSource();
            TreeItem[] selections = tree.getSelection();
            return selections;
        }

        private void searchBookmarkDeleteSelection(TreeItem item) {
            if (item.isDisposed()) {
                return;
            }
            IBookmarkModelComponent component = (IBookmarkModelComponent) item.getData();
            commandInvoker.invoke(new BookmarkDeletionCommand(component));
        }

        private boolean isDeletion(KeyEvent e) {
            int BACKSPACE = 8;
            return (e.keyCode == SWT.DEL || e.keyCode == BACKSPACE);
        }

        @Override
        public void keyReleased(KeyEvent e) {

        }

    }

    public IStructuredSelection getSelections() {
        return treeViewer.getSelections();
    }

    private class DoubleclickListener implements IDoubleClickListener {

        private final ViewPart part;
        private final BookmarkCommandInvoker commandInvoker;

        public DoubleclickListener(ViewPart part, BookmarkCommandInvoker commandInvoker) {
            this.part = part;
            this.commandInvoker = commandInvoker;
        }

        @Override
        public void doubleClick(DoubleClickEvent event) {
            commandInvoker.invoke(new OpenBookmarkCommand(getSelections(), part.getSite().getWorkbenchWindow()
                    .getActivePage(), commandInvoker));
        }

    }

    public void resetGui() {
        treeViewer.setRepresentation(new HierarchicalRepresentationMode());
        comboViewer.hide();
    }
}
