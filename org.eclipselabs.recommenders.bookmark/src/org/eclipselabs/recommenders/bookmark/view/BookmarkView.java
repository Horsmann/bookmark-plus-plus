package org.eclipselabs.recommenders.bookmark.view;

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.JavaCore;
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
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.IHandlerActivation;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.action.AddCategoryAction;
import org.eclipselabs.recommenders.bookmark.action.CloseAllEditorWindowsAction;
import org.eclipselabs.recommenders.bookmark.action.CollapseAllAction;
import org.eclipselabs.recommenders.bookmark.action.DeleteBookmarkAction;
import org.eclipselabs.recommenders.bookmark.action.GoToCategoryModeAction;
import org.eclipselabs.recommenders.bookmark.action.OpenBookmarkAction;
import org.eclipselabs.recommenders.bookmark.action.OpenInSystemExplorerAction;
import org.eclipselabs.recommenders.bookmark.action.RenameCategoryAction;
import org.eclipselabs.recommenders.bookmark.action.SwitchFlatHierarchicalAction;
import org.eclipselabs.recommenders.bookmark.action.SwitchInferredStateAction;
import org.eclipselabs.recommenders.bookmark.commands.DeleteSingleBookmarkCommand;
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

import com.google.common.collect.Lists;

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
    private OpenInSystemExplorerAction openInFileSystem;
    private DeleteBookmarkAction deleteBookmarks;
    private HideableComboViewer comboViewer;
    private SwitchInferredStateAction switchInferred;
    private BookmarkTreeDropListener dropListener;
    private DefaultDropStrategy defaultDropStrategy;
    private DefaultPasteStrategy defaultPasteStrategy;
    private PasteHandler pasteHandler;
    private IMemento memento;
    private ComboStrategySwapper comboStrategySwapper;
    private List<IHandlerActivation> removeOnDispose = Lists.newArrayList();
    private CollapseAllAction collapseAll;

    @Override
    public void createPartControl(final Composite parent) {
        parent.setLayout(GridLayoutFactory.fillDefaults().create());
        loadModel();
        initGuiComponents(parent);
        setContentForTreeViewer(model);
        configureTreeViewer(treeViewer, model, comboViewer);
        addCopyCutPasteFeatures(pasteHandler);
        enableActionsInToolbar(closeAllEditors, switchFlatHierarchical, categoryMode, addNewCategory);
        activateHierarchicalMode();
        addResourceListener();
        addViewPartListener();
//        addJavaElementChangeListener();
        Activator.setBookmarkView(this);
        restoreGUIState();
    }

    private void addJavaElementChangeListener() {
        JavaCore.addElementChangedListener(new JavaElementChangedListener());
    }

    private void addViewPartListener() {
        IPartService adapter = (IPartService) getSite().getService(IPartService.class);
        adapter.addPartListener(new ViewPartListener(this));
    }

    void loadModel() {
        model = Activator.getModel();
    }

    private void initGuiComponents(Composite parent) {
        initTreeViewerWithDropAndPasteHandlingComponents(parent);
        initHideableComboViewer(parent, treeViewer, dropListener, defaultDropStrategy, pasteHandler,
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
        selectionListener.register(categoryMode);
        selectionListener.register(switchInferred);
        treeViewer.addSelectionChangedListener(selectionListener);
    }

    private void initHideableComboViewer(Composite parent, RepresentationSwitchableTreeViewer treeViewer,
            BookmarkTreeDropListener dropListener, IDropStrategy defaultDropStrategy, PasteHandler pasteHandler,
            IPasteStrategy defaultPasteStrategy) {

        MouseDropStrategyChanger mouseDropStrategy = new MouseDropStrategyChanger(dropListener, defaultDropStrategy,
                this);
        PasteStrategyChanger pasteStrategy = new PasteStrategyChanger(pasteHandler, defaultPasteStrategy, treeViewer,
                this);
        comboStrategySwapper = new ComboStrategySwapper();
        comboStrategySwapper.register(mouseDropStrategy);
        comboStrategySwapper.register(pasteStrategy);

        comboViewer = new HideableComboViewer(parent, SWT.NONE, this, comboStrategySwapper);
    }

    private void createContextActions(RepresentationSwitchableTreeViewer treeViewer, BookmarkModel model,
            HideableComboViewer hideableComboViewer) {
        renameCategory = new RenameCategoryAction(treeViewer, this);
        openInEditor = new OpenBookmarkAction(treeViewer, this, this);
        openInFileSystem = new OpenInSystemExplorerAction(treeViewer);
        deleteBookmarks = new DeleteBookmarkAction(treeViewer, this);
        switchInferred = new SwitchInferredStateAction(treeViewer, this);
        addContextMenu(treeViewer, renameCategory, openInEditor, openInFileSystem, deleteBookmarks, categoryMode,
                switchInferred);
    }

    private void addContextMenu(RepresentationSwitchableTreeViewer treeViewer,
            final RenameCategoryAction renameCategory, final OpenBookmarkAction openInEditor,
            final OpenInSystemExplorerAction openInFileSystem, final DeleteBookmarkAction deleteBookmarks,
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
        IHandlerActivation activateHandler = handlerServ.activateHandler("org.eclipse.ui.edit.paste", pasteHandler);
        if (activateHandler != null) {
            removeOnDispose.add(activateHandler);
        }
    }

    private void addCutFeature(IHandlerService handlerServ) {
        CutHandler cut = new CutHandler(treeViewer, this);
        IHandlerActivation activateHandler = handlerServ.activateHandler("org.eclipse.ui.edit.cut", cut);
        if (activateHandler != null) {
            removeOnDispose.add(activateHandler);
        }
    }

    private void addCopyFeature(IHandlerService handlerServ) {
        CopyHandler copy = new CopyHandler(treeViewer);
        IHandlerActivation activateHandler = handlerServ.activateHandler("org.eclipse.ui.edit.copy", copy);
        if (activateHandler != null) {
            removeOnDispose.add(activateHandler);
        }
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
        collapseAll = new CollapseAllAction();
    }

    private void enableActionsInToolbar(Action closeAllEditors, Action switchFlatHierarchical, Action categoryMode,
            Action addNewCategory) {
        IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
        mgr.removeAll();
        mgr.add(closeAllEditors);
        mgr.add(switchFlatHierarchical);
        mgr.add(categoryMode);
        mgr.add(new Separator());
        mgr.add(collapseAll);
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

    @Override
    public void invoke(final IBookmarkModelCommand command) {
        Object input = treeViewer.getInput();
        if (input instanceof Category) {
            command.execute(model, (Category) input);
        } else {
            command.execute(model);
        }

        refreshTreeViewer();
    }

    private void refreshTreeViewer() {
        Display display = getDisplay();
        refreshTreeViewer(display);
    }

    // Some events cause a refresh outside the SWT-thread, which causes an
    // SWT-Exception. This makes sure every
    // refresh is done in the correct thread
    private void refreshTreeViewer(Display display) {
        display.asyncExec(new Runnable() {

            @Override
            public void run() {
                treeViewer.refresh();
            }
        });

    }

    private Display getDisplay() {
        Display display = Display.getCurrent();
        if (display == null) {
            display = Display.getDefault();
        }
        return display;
    }

    @Override
    public void dispose() {
        IHandlerService handlerServ = (IHandlerService) getSite().getService(IHandlerService.class);
        for (IHandlerActivation activation : removeOnDispose) {
            handlerServ.deactivateHandler(activation);
        }
    }

    @Override
    public void saveState(IMemento memento) {
        super.saveState(memento);
        memento = memento.createChild(MEMENTO_GUI_STATE);
        memento.putBoolean(MEMENTO_IS_FLAT, !isHierarchicalModeActive());
        memento.putBoolean(MEMENTO_IS_CATEGORY_MODE_ACTIVE, comboViewer.isComboViewerVisible());
        memento.putInteger(MEMENTO_CATEGORY_MODE_SELECTED_CATEGORY, comboViewer.getNumberOfSelectedCategory());
    }

    void restoreGUIState() {
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
                categoryMode.setChecked(true);
            }
        }
    }

    private void processFlatState(IMemento memento) {
        Boolean isFlat = memento.getBoolean(MEMENTO_IS_FLAT);
        if (isFlat != null && isFlat) {
            activateFlatMode();
            switchFlatHierarchical.setChecked(true);
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
        File stateFile = Activator.getLocationForStoringGUIState();
        this.memento = GuiStateIO.readGuiStateMementoFromFile(stateFile);
        init(site);
    }

    public void resetGui() {
        Activator.getLocationForStoringGUIState().delete();
        if (!treeViewer.isDisposed()) {
            treeViewer.setRepresentation(new HierarchicalRepresentationMode());
            comboViewer.hide();
            categoryMode.setChecked(false);
            switchFlatHierarchical.setChecked(false);
        }
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

    public void setContentForTreeViewer(BookmarkModel newModel) {
        updateModel(newModel);
        if (!treeViewer.isDisposed()) {
            treeViewer.setInput(newModel);
        }

    }

    private void updateModel(BookmarkModel newModel) {
        if (model != newModel) {
            model.removeAll();
            for (Category category : newModel.getCategories()) {
                model.add(category);
            }
        }
    }

    public void setContentForTreeViewer(Category category) {
        treeViewer.setInput(category);
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
            commandInvoker.invoke(new DeleteSingleBookmarkCommand(component, commandInvoker));
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

    @Override
    public void setFocus() {
    }

}
