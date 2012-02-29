package org.eclipselabs.recommenders.bookmark.wizard;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.action.RenameCategoryAction;
import org.eclipselabs.recommenders.bookmark.action.SwitchInferredStateAction;
import org.eclipselabs.recommenders.bookmark.commands.IBookmarkModelCommand;
import org.eclipselabs.recommenders.bookmark.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.view.BookmarkCommandInvoker;
import org.eclipselabs.recommenders.bookmark.view.BookmarkTreeDragListener;
import org.eclipselabs.recommenders.bookmark.view.BookmarkTreeDropListener;
import org.eclipselabs.recommenders.bookmark.view.tree.HierarchicalRepresentationMode;
import org.eclipselabs.recommenders.bookmark.view.tree.RepresentationSwitchableTreeViewer;
import org.eclipselabs.recommenders.bookmark.view.tree.SelectionChangedListener;

public class BookmarkWizardPage extends WizardPage implements BookmarkCommandInvoker {

    private Composite container;
    private Button selectFileButton;
    private Text selectedFileTextField;
    private RepresentationSwitchableTreeViewer leftTreeViewer;
    private RepresentationSwitchableTreeViewer rightTreeViewer;
    private Button add;
    private BookmarkCommandInvoker invoker;
    private Button addAll;
    private Button remove;
    private Button removeAll;

    protected BookmarkWizardPage(String pageName) {
        super(pageName);
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
        SelectionChangedListener selectionChangedListener = createSelectionChangedListener(renameCategoryAction, switchInferredStateAction);
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
        rightTreeViewer.addDoubleclickListener(new AddMouseAndDoubleClickerListener(rightTreeViewer, leftTreeViewer,
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
        // selectFileButton.addListener(SWT.MouseDown, new
        // ButtonMouseDownListener(this));
    }

    private void addFilePathField(Composite fileSelectionComposite) {
        selectedFileTextField = new Text(fileSelectionComposite, SWT.BORDER);
        GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
        selectedFileTextField.setLayoutData(data);

        // addListenerToFilePath(selectedFileTextField);
    }

    // private void addListenerToFilePath(Text selectedFileTextField) {
    // filePathListener = new FilePathModifyListener(selectedFileTextField);
    // selectedFileTextField.addListener(SWT.KeyUp, filePathListener);
    // }

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
        addAll.addMouseListener(new AddAllMouseListener(rightTreeViewer, rightTreeViewer, invoker));
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
        left.setText("Local bookmarks");
        left.setLayoutData(data);

        data = new GridData(SWT.CENTER, SWT.CENTER, false, false);
        Label middle = new Label(composite, SWT.CENTER);
        middle.setText("");
        middle.setLayoutData(data);

        data = new GridData(SWT.LEFT, SWT.CENTER, true, false);
        Label right = new Label(composite, SWT.CENTER);
        right.setText("Export bookmarks");
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
    public void invoke(IBookmarkModelCommand command) {
    }

}
