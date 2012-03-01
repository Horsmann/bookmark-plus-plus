package org.eclipselabs.recommenders.bookmark.wizard;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipselabs.recommenders.bookmark.action.RenameCategoryAction;
import org.eclipselabs.recommenders.bookmark.action.SwitchInferredStateAction;
import org.eclipselabs.recommenders.bookmark.commands.DeleteAllBookmarksCommand;
import org.eclipselabs.recommenders.bookmark.commands.DeleteInferredBookmarksCommand;
import org.eclipselabs.recommenders.bookmark.commands.DeleteSingleBookmarkCommand;
import org.eclipselabs.recommenders.bookmark.commands.IBookmarkModelCommand;
import org.eclipselabs.recommenders.bookmark.commands.RenameCategoryCommand;
import org.eclipselabs.recommenders.bookmark.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.model.IBookmark;
import org.eclipselabs.recommenders.bookmark.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.view.BookmarkCommandInvoker;
import org.eclipselabs.recommenders.bookmark.view.BookmarkTreeDragListener;
import org.eclipselabs.recommenders.bookmark.view.BookmarkTreeDropListener;
import org.eclipselabs.recommenders.bookmark.view.DefaultDropStrategy;
import org.eclipselabs.recommenders.bookmark.view.tree.HierarchicalRepresentationMode;
import org.eclipselabs.recommenders.bookmark.view.tree.RepresentationSwitchableTreeViewer;
import org.eclipselabs.recommenders.bookmark.view.tree.SelectionChangedListener;
import org.eclipselabs.recommenders.bookmark.visitor.IsIBookmarkVisitor;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

public abstract class BookmarkWizardPage extends WizardPage implements BookmarkCommandInvoker {

    protected Composite container;
    protected Button selectFileButton;
    protected Text selectedFileTextField;
    protected RepresentationSwitchableTreeViewer leftTreeViewer;
    protected RepresentationSwitchableTreeViewer rightTreeViewer;
    private Button add;
    private BookmarkCommandInvoker invoker;
    private Button addAll;
    private Button remove;
    private Button removeAll;
    private final String rightViewerLabel;
    private final String leftViewerLabel;

    public BookmarkWizardPage(String pageName, String leftViewerLabel, String rightViewerLabel) {
        super(pageName);
        this.rightViewerLabel = rightViewerLabel;
        this.leftViewerLabel = leftViewerLabel;
        invoker = this;
    }

    @Override
    public void createControl(Composite parent) {
        container = initializeContainerComposite(parent);
        addRowWithTextFieldForLoadedFileAndBrowseButton(container);
        addTreeViewerWithAddRemoveButton(container);
        makeAddButtonEnDisableOnSelections();
        makeRemoveButtonEnDisableOnSelections();
        setContextMenu();
        setControl(parent);
        setPageComplete(false);
    }

    private void setContextMenu() {
        RenameCategoryAction renameCategoryAction = new RenameCategoryAction(rightTreeViewer, invoker);
        SwitchInferredStateAction switchInferredStateAction = new SwitchInferredStateAction(rightTreeViewer, invoker);
        MenuManager contextMenu = createContextMenu(renameCategoryAction, switchInferredStateAction);
        SelectionChangedListener selectionChangedListener = createSelectionChangedListener(renameCategoryAction,
                switchInferredStateAction);
        rightTreeViewer.setContextMenu(contextMenu);
        rightTreeViewer.addSelectionChangedListener(selectionChangedListener);
    }

    private SelectionChangedListener createSelectionChangedListener(RenameCategoryAction renameCategoryAction,
            SwitchInferredStateAction switchInferredStateAction) {
        SelectionChangedListener selectionListener = new SelectionChangedListener();
        selectionListener.register(renameCategoryAction);
        selectionListener.register(switchInferredStateAction);
        return selectionListener;
    }

    private MenuManager createContextMenu(final RenameCategoryAction renameCategoryAction,
            final SwitchInferredStateAction switchInferredStateAction) {
        final MenuManager menuMgr = new MenuManager();
        menuMgr.setRemoveAllWhenShown(true);

        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager mgr) {
                menuMgr.add(renameCategoryAction);
                menuMgr.add(switchInferredStateAction);
            }
        });

        menuMgr.update(true);
        return menuMgr;
    }

    private void makeRemoveButtonEnDisableOnSelections() {
        rightTreeViewer.addSelectionChangedListener(new TreeSelectionDependendButtonEnabler(rightTreeViewer, remove));
    }

    private void makeAddButtonEnDisableOnSelections() {
        leftTreeViewer.addSelectionChangedListener(new TreeSelectionDependendButtonEnabler(leftTreeViewer, add));
        leftTreeViewer.addDoubleclickListener(new AddMouseAndDoubleClickerListener(leftTreeViewer, rightTreeViewer,
                invoker));
    }

    private Composite initializeContainerComposite(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(GridLayoutFactory.fillDefaults().create());
        GridData compData = new GridData(SWT.FILL, SWT.FILL, true, true);
        container.setLayoutData(compData);
        return container;
    }

    private void addRowWithTextFieldForLoadedFileAndBrowseButton(Composite container) {
        Composite fileSelectionComposite = new Composite(container, SWT.BORDER);
        fileSelectionComposite.setLayout(GridLayoutFactory.fillDefaults().numColumns(3).create());
        fileSelectionComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

        addFileLabel(fileSelectionComposite);
        addFilePathField(fileSelectionComposite);
        addOpenFileButton(fileSelectionComposite);

    }

    private void addOpenFileButton(Composite fileSelectionComposite) {
        GridData data = new GridData(SWT.CENTER, SWT.CENTER, false, false);
        selectFileButton = new Button(fileSelectionComposite, SWT.NONE);
        selectFileButton.setText("Select File");
        selectFileButton.setLayoutData(data);
    }

    protected abstract void addMouseListenerForOpenFileButton();

    protected abstract void addFilePathModifyListener();

    private void addFilePathField(Composite fileSelectionComposite) {
        selectedFileTextField = new Text(fileSelectionComposite, SWT.BORDER);
        GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
        selectedFileTextField.setLayoutData(data);
    }

    private void addFileLabel(Composite fileSelectionComposite) {
        GridData data = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        Label label = new Label(fileSelectionComposite, SWT.CENTER);
        label.setText("File: ");
        label.setLayoutData(data);
    }

    private void addTreeViewerWithAddRemoveButton(Composite container) {

        Composite bookmarkSelComposite = new Composite(container, SWT.NONE);
        bookmarkSelComposite.setLayout(GridLayoutFactory.fillDefaults().numColumns(3).create());
        bookmarkSelComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

        addHeadline(bookmarkSelComposite);

        createLeftTreeViewer(bookmarkSelComposite);
        createPanelWithAddRemoveButtons(bookmarkSelComposite);
        createRightTreeViewer(bookmarkSelComposite);
        addMouseListenerToRemoveButton(remove);
        addMouseListenerToRemoveAllButton(removeAll);
        addMouseListenerToAddButton(add);
        addMouseListenerToAddAllButton(addAll);
        addDoubleclickListenerToLeftTreeViewer(leftTreeViewer);

    }

    private void addDoubleclickListenerToLeftTreeViewer(RepresentationSwitchableTreeViewer leftTreeViewer) {
        leftTreeViewer.addDoubleclickListener(new AddMouseAndDoubleClickerListener(leftTreeViewer, leftTreeViewer,
                invoker));

    }

    private void addMouseListenerToAddButton(Button add) {
        add.addMouseListener(new AddMouseAndDoubleClickerListener(leftTreeViewer, rightTreeViewer, invoker));
    }

    private void addMouseListenerToAddAllButton(Button addAll) {
        addAll.addMouseListener(new AddAllMouseListener(leftTreeViewer, rightTreeViewer, invoker));
    }

    private void addMouseListenerToRemoveButton(Button remove) {
        remove.addMouseListener(new RemoveMouseListener(rightTreeViewer, invoker));
    }

    private void addMouseListenerToRemoveAllButton(Button removeAll) {
        removeAll.addMouseListener(new RemoveAllMouseListener(invoker));
    }

    private void addHeadline(Composite composite) {
        GridData data = new GridData(SWT.LEFT, SWT.CENTER, true, false);
        Label left = new Label(composite, SWT.CENTER);
        left.setText(leftViewerLabel);
        left.setLayoutData(data);

        data = new GridData(SWT.CENTER, SWT.CENTER, false, false);
        Label middle = new Label(composite, SWT.CENTER);
        middle.setText("");
        middle.setLayoutData(data);

        data = new GridData(SWT.LEFT, SWT.CENTER, true, false);
        Label right = new Label(composite, SWT.CENTER);
        right.setText(rightViewerLabel);
        right.setLayoutData(data);

    }

    private void createLeftTreeViewer(Composite bookmarkSelComposite) {
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        leftTreeViewer = new RepresentationSwitchableTreeViewer(bookmarkSelComposite,
                new HierarchicalRepresentationMode());
        leftTreeViewer.setTreeLayoutData(data);
        addDragSupportToLeftTreeViewer();

    }

    private void createRightTreeViewer(Composite bookmarkSelComposite) {
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        rightTreeViewer = new RepresentationSwitchableTreeViewer(bookmarkSelComposite,
                new HierarchicalRepresentationMode());
        rightTreeViewer.setTreeLayoutData(data);

        addDragDropSupportToRightTreeViewer();
        setRightTreeViewerKeyListener();
    }

    private void addDragDropSupportToRightTreeViewer() {
        final BookmarkTreeDragListener dragListener = new BookmarkTreeDragListener();
        rightTreeViewer.addDragSupport(dragListener.getSupportedOperations(), dragListener.getSupportedTransfers(),
                dragListener);
        final WizardDropStrategy strategy = new WizardDropStrategy(invoker);
        final BookmarkTreeDropListener dropListener = new BookmarkTreeDropListener(strategy);
        rightTreeViewer.addDropSupport(dropListener.getSupportedOperations(), dropListener.getSupportedTransfers(),
                dropListener);
    }

    private void addDragSupportToLeftTreeViewer() {
        final BookmarkTreeDragListener dragListener = new BookmarkTreeDragListener();
        leftTreeViewer.addDragSupport(dragListener.getSupportedOperations(), dragListener.getSupportedTransfers(),
                dragListener);
    }

    private void setRightTreeViewerKeyListener() {
        rightTreeViewer.addKeyListener(new WizardKeyListener(rightTreeViewer, invoker));
    }

    private void createPanelWithAddRemoveButtons(Composite bookmarkSelComposite) {
        Composite buttonPanel = new Composite(bookmarkSelComposite, SWT.NONE);
        GridData data = new GridData(SWT.CENTER, SWT.CENTER, false, false);
        buttonPanel.setLayout(new GridLayout());
        buttonPanel.setLayoutData(data);

        addAddButton(buttonPanel);
        addAddAllButton(buttonPanel);
        addRemoveButton(buttonPanel);
        addRemoveAllButton(buttonPanel);

    }

    private void addAddButton(Composite buttonPanel) {
        add = new Button(buttonPanel, SWT.CENTER);
        add.setText("Add");
        GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
        add.setLayoutData(data);
        add.setEnabled(false);
    }

    private void addRemoveButton(Composite buttonPanel) {
        remove = new Button(buttonPanel, SWT.CENTER);
        remove.setText("Remove");
        GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
        remove.setLayoutData(data);
        remove.setEnabled(false);
    }

    private void addRemoveAllButton(Composite buttonPanel) {
        removeAll = new Button(buttonPanel, SWT.CENTER);
        removeAll.setText("Remove All");
        GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
        removeAll.setLayoutData(data);
    }

    private void addAddAllButton(Composite buttonPanel) {
        addAll = new Button(buttonPanel, SWT.CENTER);
        addAll.setText("Add All");
        GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
        addAll.setLayoutData(data);
    }

    @Override
    public abstract void invoke(IBookmarkModelCommand command);

    public Text getTextField() {
        return selectedFileTextField;
    }

    private class AddMouseAndDoubleClickerListener implements MouseListener, IDoubleClickListener {

        private final RepresentationSwitchableTreeViewer leftTreeViewer;
        private final RepresentationSwitchableTreeViewer rightTreeViewer;
        private final BookmarkCommandInvoker invoker;

        public AddMouseAndDoubleClickerListener(RepresentationSwitchableTreeViewer leftTreeViewer,
                RepresentationSwitchableTreeViewer rightTreeViewer, BookmarkCommandInvoker invoker) {
            this.leftTreeViewer = leftTreeViewer;
            this.rightTreeViewer = rightTreeViewer;
            this.invoker = invoker;

        }

        @Override
        public void doubleClick(DoubleClickEvent event) {
            addSelections();
        }

        @Override
        public void mouseDoubleClick(MouseEvent e) {
        }

        @Override
        public void mouseDown(MouseEvent e) {
            addSelections();
        }

        @Override
        public void mouseUp(MouseEvent e) {
        }

        private void addSelections() {
            IBookmarkModelComponent[] components = getSelectedBookmarkComponents();

            if (rightTreeViewer.getSelections().size() == 0) {
                Optional<IBookmarkModelComponent> dropTarget = Optional.absent();
                invoker.invoke(new ImportSelectedBookmarksCommand(components, invoker, true, false, dropTarget));
            } else {
                Optional<IBookmarkModelComponent> dropTarget = Optional.of((IBookmarkModelComponent) rightTreeViewer
                        .getSelections().getFirstElement());
                invoker.invoke(new ImportSelectedBookmarksCommand(components, invoker, true, false, dropTarget));
            }
        }

        private IBookmarkModelComponent[] getSelectedBookmarkComponents() {
            IStructuredSelection selections = leftTreeViewer.getSelections();
            List<IBookmarkModelComponent> components = Lists.newArrayList();
            @SuppressWarnings("rawtypes")
            Iterator iterator = selections.iterator();
            while (iterator.hasNext()) {
                components.add((IBookmarkModelComponent) iterator.next());
            }
            return components.toArray(new IBookmarkModelComponent[0]);
        }

    }

    private class AddAllMouseListener implements MouseListener {

        private final RepresentationSwitchableTreeViewer leftViewer;
        private final RepresentationSwitchableTreeViewer rightViewer;
        private final BookmarkCommandInvoker invoker;

        public AddAllMouseListener(RepresentationSwitchableTreeViewer leftViewer,
                RepresentationSwitchableTreeViewer rightViewer, BookmarkCommandInvoker invoker) {
            this.leftViewer = leftViewer;
            this.rightViewer = rightViewer;
            this.invoker = invoker;
        }

        @Override
        public void mouseUp(MouseEvent e) {
        }

        @Override
        public void mouseDown(MouseEvent e) {
            BookmarkModel source = (BookmarkModel) leftViewer.getInput();
            BookmarkModel target = (BookmarkModel) rightViewer.getInput();
            if (source == null || target == null) {
                return;
            }

            Optional<IBookmarkModelComponent> dropTarget = Optional.absent();
            IBookmarkModelComponent[] components = source.getCategories().toArray(new IBookmarkModelComponent[0]);
            invoker.invoke(new ImportSelectedBookmarksCommand(components, getInvoker(target), true, false, dropTarget));
            rightViewer.refresh();
        }

        // the default invoker refreshes the view after each command is
        // executed. The invoker is also provided as parameter for cascading
        // command invocations what causes gui flickering on "add all" which
        // is prevented
        // with this one
        private BookmarkCommandInvoker getInvoker(final BookmarkModel model) {
            return new BookmarkCommandInvoker() {

                @Override
                public void invoke(IBookmarkModelCommand command) {
                    command.execute(model);
                }
            };
        }

        @Override
        public void mouseDoubleClick(MouseEvent e) {
        }
    }

    private class RemoveMouseListener implements MouseListener {

        private final RepresentationSwitchableTreeViewer treeViewer;
        private final BookmarkCommandInvoker invoker;

        public RemoveMouseListener(RepresentationSwitchableTreeViewer treeViewer, BookmarkCommandInvoker invoker) {
            this.treeViewer = treeViewer;
            this.invoker = invoker;
        }

        @Override
        public void mouseUp(MouseEvent e) {
        }

        @Override
        public void mouseDown(MouseEvent e) {
            IStructuredSelection selections = treeViewer.getSelections();
            @SuppressWarnings("rawtypes")
            Iterator iterator = selections.iterator();
            while (iterator.hasNext()) {
                IBookmarkModelComponent component = (IBookmarkModelComponent) iterator.next();
                IBookmarkModelComponent parent = component.getParent();
                invoker.invoke(new DeleteSingleBookmarkCommand(component));
                deleteInferredNodes(parent);

                treeViewer.refresh();
            }
        }

        private void deleteInferredNodes(IBookmarkModelComponent parent) {
            if (parent == null) {
                return;
            }
            IsIBookmarkVisitor isBookmark = new IsIBookmarkVisitor();
            parent.accept(isBookmark);
            if (isBookmark.isIBookmark()) {
                invoker.invoke(new DeleteInferredBookmarksCommand((IBookmark) parent));
            }
        }

        @Override
        public void mouseDoubleClick(MouseEvent e) {
        }
    }

    private class RemoveAllMouseListener implements MouseListener {

        private final BookmarkCommandInvoker invoker;

        public RemoveAllMouseListener(BookmarkCommandInvoker invoker) {
            this.invoker = invoker;
        }

        @Override
        public void mouseUp(MouseEvent e) {
        }

        @Override
        public void mouseDown(MouseEvent e) {
            invoker.invoke(new DeleteAllBookmarksCommand());
        }

        @Override
        public void mouseDoubleClick(MouseEvent e) {
        }
    }

    private class TreeSelectionDependendButtonEnabler implements ISelectionChangedListener {

        private final RepresentationSwitchableTreeViewer treeViewer;
        private final Button button;

        public TreeSelectionDependendButtonEnabler(RepresentationSwitchableTreeViewer treeViewer, Button button) {
            this.treeViewer = treeViewer;
            this.button = button;
        }

        @Override
        public void selectionChanged(SelectionChangedEvent event) {
            if (treeViewer.getSelections().size() == 0) {
                button.setEnabled(false);
            } else {
                button.setEnabled(true);
            }
        }
    }

    private class WizardDropStrategy extends DefaultDropStrategy {

        public WizardDropStrategy(BookmarkCommandInvoker commandInvoker) {
            super(commandInvoker);
        }

        @Override
        public void performDrop(DropTargetEvent event) {

            setDropOperation(event);
            final Optional<IBookmarkModelComponent> dropTarget = getDropTarget(event);
            boolean insertDropBeforeTarget = determineInsertLocation(event);
            ISelection selections = LocalSelectionTransfer.getTransfer().getSelection();
            if (selections instanceof IStructuredSelection) {
                processStructuredSelection(dropTarget, (IStructuredSelection) selections, isCopyOperation(event),
                        insertDropBeforeTarget);
            }
        }

        protected void processDroppedElementOriginatedFromInsideTheView(Optional<IBookmarkModelComponent> dropTarget,
                IBookmarkModelComponent[] components, boolean isCopyOperation, boolean insertDropBeforeTarget) {

            if (!causeRecursion(components, dropTarget)) {

                commandInvoker.invoke(new ImportSelectedBookmarksCommand(components, commandInvoker, isCopyOperation,
                        insertDropBeforeTarget, dropTarget));
            }
        }

    }

    private class WizardKeyListener implements KeyListener {

        private final RepresentationSwitchableTreeViewer treeViewer;
        private final BookmarkCommandInvoker invoker;

        public WizardKeyListener(RepresentationSwitchableTreeViewer treeViewer, BookmarkCommandInvoker invoker) {
            this.treeViewer = treeViewer;
            this.invoker = invoker;
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (isDeletion(e)) {
                processDeletion(e);
            } else if (isRename(e)) {
                processRename(e);
            }
            treeViewer.refresh();
        }

        private void processDeletion(KeyEvent e) {
            TreeItem[] selections = getTreeSelections(e);
            for (TreeItem item : selections) {
                searchBookmarkDeleteSelection(item);
            }
        }

        private boolean isRename(KeyEvent e) {
            return e.keyCode == SWT.F2;
        }

        private void processRename(KeyEvent e) {
            invoker.invoke(new RenameCategoryCommand(treeViewer));
        }

        private TreeItem[] getTreeSelections(KeyEvent e) {
            Tree tree = (Tree) e.getSource();
            TreeItem[] selections = tree.getSelection();
            return selections;
        }

        protected void searchBookmarkDeleteSelection(TreeItem item) {
            if (!item.isDisposed()) {
                IBookmarkModelComponent component = (IBookmarkModelComponent) item.getData();
                IBookmarkModelComponent parent = component.getParent();
                delete(component);
                deleteInferredBookmarksRecursively(parent);
            }
        }

        private void deleteInferredBookmarksRecursively(IBookmarkModelComponent parent) {
            if (parent == null) {
                return;
            }
            IsIBookmarkVisitor isIBookmark = new IsIBookmarkVisitor();
            parent.accept(isIBookmark);
            if (isIBookmark.isIBookmark()) {
                invoker.invoke(new DeleteInferredBookmarksCommand((IBookmark) parent));
            }
        }

        private void delete(IBookmarkModelComponent component) {
            invoker.invoke(new DeleteSingleBookmarkCommand(component));
        }

        private boolean isDeletion(KeyEvent e) {
            int BACKSPACE = 8;
            return (e.keyCode == SWT.DEL || e.keyCode == BACKSPACE);
        }
    }

}
