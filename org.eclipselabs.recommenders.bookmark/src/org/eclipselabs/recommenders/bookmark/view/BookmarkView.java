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

import java.io.IOException;
import java.util.List;

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
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.action.AddCategoryAction;
import org.eclipselabs.recommenders.bookmark.action.BookmarkDeletionAction;
import org.eclipselabs.recommenders.bookmark.action.CloseAllEditorWindowsAction;
import org.eclipselabs.recommenders.bookmark.action.DeActivateCategoryModeAction;
import org.eclipselabs.recommenders.bookmark.action.OpenBookmarkAction;
import org.eclipselabs.recommenders.bookmark.action.OpenInFileSystemAction;
import org.eclipselabs.recommenders.bookmark.action.RenameCategoryAction;
import org.eclipselabs.recommenders.bookmark.action.SelfEnabling;
import org.eclipselabs.recommenders.bookmark.action.SwitchFlatHierarchicalAction;
import org.eclipselabs.recommenders.bookmark.commands.BookmarkDeletionCommand;
import org.eclipselabs.recommenders.bookmark.commands.IBookmarkModelCommand;
import org.eclipselabs.recommenders.bookmark.commands.OpenBookmarkCommand;
import org.eclipselabs.recommenders.bookmark.commands.RenameCategoryCommand;
import org.eclipselabs.recommenders.bookmark.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.model.Category;
import org.eclipselabs.recommenders.bookmark.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.view.copyCutPaste.CopyHandler;
import org.eclipselabs.recommenders.bookmark.view.copyCutPaste.CutHandler;
import org.eclipselabs.recommenders.bookmark.view.copyCutPaste.PasteHandler;
import org.eclipselabs.recommenders.bookmark.view.tree.FlatRepresentationMode;
import org.eclipselabs.recommenders.bookmark.view.tree.HideableComboViewer;
import org.eclipselabs.recommenders.bookmark.view.tree.HierarchicalRepresentationMode;
import org.eclipselabs.recommenders.bookmark.view.tree.RepresentationSwitchableTreeViewer;

import com.google.common.collect.Lists;

public class BookmarkView extends ViewPart implements BookmarkCommandInvoker {

    private BookmarkModel model;
    private RepresentationSwitchableTreeViewer treeViewer;
    private Action switchFlatHierarchical;
    private boolean isHierarchicalModeActive;
    private CloseAllEditorWindowsAction closeAllEditors;
    private AddCategoryAction addNewCategory;
    private DeActivateCategoryModeAction categoryMode;
    private RenameCategoryAction renameCategory;
    private OpenBookmarkAction openInEditor;
    private OpenInFileSystemAction openInFileSystem;
    private BookmarkDeletionAction deleteBookmarks;
    private HideableComboViewer hideableComboViewer;
    private DeActivateCategoryModeAction switchCategory;

    @Override
    public void createPartControl(final Composite parent) {
        parent.setLayout(GridLayoutFactory.fillDefaults().create());

        createTreeViewer(parent);
        try {
            loadModel();
        } catch (IOException e) {
            e.printStackTrace();
        }
        createHideableComboViewer(parent, treeViewer, model);

        addDragDropListeners(treeViewer);
        setUpActions();
        setUpToolbar();
        setUpPluginPreferences();
        activateHierarchicalMode();

        addViewPartListener();
        addResourceListener();
        addCopyCutPasteFeatures();
        Activator.setBookmarkView(this);
    }

    private void createTreeViewer(Composite parent) {
        treeViewer = new RepresentationSwitchableTreeViewer(parent, new HierarchicalRepresentationMode(), model);

        createContextActions();
        addTreeViewerSelectionChangedListener();
        addContextMenu();
        addTreeKeyListener();
        addDoubleClickListener();
    }

    private void addDoubleClickListener() {
        treeViewer.addDoubleclickListener(new DoubleclickListener(this, this));
    }

    private void addTreeKeyListener() {
        TreeKeyListener listener = new TreeKeyListener(this, this);
        treeViewer.addKeyListener(listener);

    }

    private void addTreeViewerSelectionChangedListener() {
        SelectionChangedListener selectionListener = new SelectionChangedListener();
        selectionListener.register(renameCategory);
        selectionListener.register(openInEditor);
        selectionListener.register(openInFileSystem);
        selectionListener.register(deleteBookmarks);
        treeViewer.addSelectionChangedListener(selectionListener);
    }

    private void createHideableComboViewer(Composite parent, RepresentationSwitchableTreeViewer treeViewer, BookmarkModel model) {
        hideableComboViewer = new HideableComboViewer(parent, SWT.NONE, model, treeViewer);
    }

    private void createContextActions() {
        renameCategory = new RenameCategoryAction(treeViewer, this);
        openInEditor = new OpenBookmarkAction(treeViewer, this, this);
        openInFileSystem = new OpenInFileSystemAction(treeViewer);
        deleteBookmarks = new BookmarkDeletionAction(treeViewer, this);
        switchCategory = new DeActivateCategoryModeAction(hideableComboViewer, model, treeViewer);
    }

    private void addContextMenu() {

        final MenuManager menuMgr = new MenuManager();
        menuMgr.setRemoveAllWhenShown(true);

        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager mgr) {
                menuMgr.add(openInEditor);
                menuMgr.add(renameCategory);
                menuMgr.add(switchCategory);
                menuMgr.add(openInFileSystem);
                menuMgr.add(deleteBookmarks);
            }
        });

        menuMgr.update(true);
        treeViewer.setContextMenu(menuMgr);
        getSite().registerContextMenu(menuMgr, treeViewer.getSelectionProvider());
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
        categoryMode = new DeActivateCategoryModeAction(hideableComboViewer, model, treeViewer);
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

    private void loadModel() throws IOException {
        setModel(BookmarkIO.loadFromDefaultFile());
    }

    private void setModel(final BookmarkModel model) {
        if (this.model == null) {
            this.model = model;
        } else {
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

    private class SelectionChangedListener implements ISelectionChangedListener {

        List<SelfEnabling> clients = Lists.newArrayList();

        public void register(SelfEnabling selfEnabling) {
            clients.add(selfEnabling);
        }

        @Override
        public void selectionChanged(SelectionChangedEvent event) {
            for (SelfEnabling client : clients) {
                client.updateEnableStatus();
            }
        }

    }

    // public void renameCategory() {
    //
    // IBookmarkModelComponent component = (IBookmarkModelComponent)
    // treeViewer.getSelections().getFirstElement();
    // if (!isRenameValid(component)) {
    // return;
    // }
    // String oldLabel = getOldCategoryLabel(component);
    //
    // InputDialog dialog = makeDialog(oldLabel);
    // dialog.setBlockOnOpen(true);
    // if (dialog.open() == Window.OK) {
    // updateCategoryName(component, dialog);
    // treeViewer.refresh();
    // }
    // }

    // private void updateCategoryName(IBookmarkModelComponent component,
    // InputDialog dialog) {
    // String value = dialog.getValue();
    // SetNewCategoryNameVisitor categoryVisitor = new
    // SetNewCategoryNameVisitor(value);
    // component.accept(categoryVisitor);
    // }
    //
    // private String getOldCategoryLabel(IBookmarkModelComponent component) {
    // GetValueVisitor valueVisitor = new GetValueVisitor();
    // component.accept(valueVisitor);
    // return (String) valueVisitor.getValue();
    // }
    //
    // private InputDialog makeDialog(String oldLabel) {
    // return new InputDialog(treeViewer.getShell(), "Rename Category",
    // "Enter a new category name:", oldLabel,
    // new InputValidator());
    // }
    //
    // private boolean isRenameValid(IBookmarkModelComponent component) {
    // if (treeViewer.getSelections().size() != 1) {
    // return false;
    // }
    //
    // IsCategoryVisitor isCategoryVisitor = new IsCategoryVisitor();
    // component.accept(isCategoryVisitor);
    // return isCategoryVisitor.isCategory();
    // }

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
                // happens if a parent node of the current one was delete before
                // than the current child is already
                // disposed
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

    // private class InputValidator implements IInputValidator {
    //
    // @Override
    // public String isValid(String newText) {
    // if (newText.equals("")) {
    // return "Blank is an invalid category name";
    // }
    // return null;
    // }
    //
    // }

    // private class SetNewCategoryNameVisitor implements IModelVisitor {
    //
    // private final String categoryName;
    //
    // public SetNewCategoryNameVisitor(String categoryName) {
    // this.categoryName = categoryName;
    // }
    //
    // @Override
    // public void visit(FileBookmark fileBookmark) {
    // }
    //
    // @Override
    // public void visit(Category category) {
    // category.setLabel(categoryName);
    // }
    //
    // @Override
    // public void visit(JavaElementBookmark javaElementBookmark) {
    // }
    //
    // }

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
}
