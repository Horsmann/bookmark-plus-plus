/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipselabs.recommenders.bookmark.wizard.exporting;

import java.io.File;

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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
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
import org.eclipselabs.recommenders.bookmark.wizard.AddAllMouseListener;
import org.eclipselabs.recommenders.bookmark.wizard.AddMouseAndDoubleClickerListener;
import org.eclipselabs.recommenders.bookmark.wizard.RemoveAllMouseListener;
import org.eclipselabs.recommenders.bookmark.wizard.RemoveMouseListener;
import org.eclipselabs.recommenders.bookmark.wizard.TreeSelectionDependendButtonEnabler;
import org.eclipselabs.recommenders.bookmark.wizard.WizardDropStrategy;
import org.eclipselabs.recommenders.bookmark.wizard.WizardKeyListener;

public class BookmarkExportWizardPage extends WizardPage implements BookmarkCommandInvoker {

    private Text selectedFileTextField;
    private Button button;
    private File selectedFile;
    private RepresentationSwitchableTreeViewer exportTreeViewer;
    private FilePathModifyListener filePathListener;

    private RepresentationSwitchableTreeViewer localTreeViewer;
    private Composite container;
    private BookmarkModel localClonedModel;
    private BookmarkModel exportModel;
    private Button remove;
    private Button removeAll;
    private Button add;
    private BookmarkCommandInvoker invoker;
    private Button addAll;

    protected BookmarkExportWizardPage(String pageName) {
        super(pageName, pageName, null);
        setDescription("Export bookmarks to an external file");
        localClonedModel = Activator.getClonedModel();
        exportModel = new BookmarkModel();
        invoker = this;
    }

    @Override
    public void createControl(Composite parent) {
        container = initializeContainerComposite(parent);
        addRowWithTextFieldForLoadedFileAndBrowseButton(container);
        addTreeViewerWithAddRemoveButton(container);

        makeAddButtonEnDisableOnSelections();
        makeRemoveButtonEnDisableOnSelections();

        localTreeViewer.setInput(localClonedModel);
        exportTreeViewer.setInput(exportModel);
        RenameCategoryAction renameCategoryAction = new RenameCategoryAction(exportTreeViewer, invoker);
        SwitchInferredStateAction switchInferredStateAction = new SwitchInferredStateAction(exportTreeViewer, invoker);
        addContextMenu(exportTreeViewer, renameCategoryAction, switchInferredStateAction);
        setControl(parent);
        setPageComplete(false);
    }

    private void addContextMenu(final RepresentationSwitchableTreeViewer exportTreeViewer,
            final RenameCategoryAction renameCategoryAction, final SwitchInferredStateAction switchInferredStateAction) {
        final MenuManager menuMgr = new MenuManager();
        menuMgr.setRemoveAllWhenShown(true);

        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager mgr) {
                menuMgr.add(renameCategoryAction);
                menuMgr.add(switchInferredStateAction);
            }
        });

        menuMgr.update(true);
        exportTreeViewer.setContextMenu(menuMgr);
        addTreeViewerSelectionChangedListener(exportTreeViewer, renameCategoryAction, switchInferredStateAction);
    }

    private void addTreeViewerSelectionChangedListener(RepresentationSwitchableTreeViewer exportTreeViewer,
            RenameCategoryAction renameCategory, SwitchInferredStateAction switchInferredStateAction) {
        SelectionChangedListener selectionListener = new SelectionChangedListener();
        selectionListener.register(renameCategory);
        selectionListener.register(switchInferredStateAction);
        exportTreeViewer.addSelectionChangedListener(selectionListener);
    }

    private void makeAddButtonEnDisableOnSelections() {
        localTreeViewer.addSelectionChangedListener(new TreeSelectionDependendButtonEnabler(localTreeViewer, add));
        localTreeViewer.addDoubleclickListener(new AddMouseAndDoubleClickerListener(localTreeViewer, exportTreeViewer,
                invoker));
    }

    private void makeRemoveButtonEnDisableOnSelections() {
        exportTreeViewer.addSelectionChangedListener(new TreeSelectionDependendButtonEnabler(exportTreeViewer, remove));
    }

    public BookmarkModel getExportModel() {
        return exportModel;
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
        button = new Button(fileSelectionComposite, SWT.NONE);
        button.setText("Select File");
        button.setLayoutData(data);
        button.addListener(SWT.MouseDown, new ButtonMouseDownListener(this));
    }

    private void addFilePathField(Composite fileSelectionComposite) {
        selectedFileTextField = new Text(fileSelectionComposite, SWT.BORDER);
        GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
        selectedFileTextField.setLayoutData(data);

        addListenerToFilePath(selectedFileTextField);
    }

    private void addListenerToFilePath(Text selectedFileTextField) {
        filePathListener = new FilePathModifyListener(selectedFileTextField);
        selectedFileTextField.addListener(SWT.KeyUp, filePathListener);
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

        createLocalTreeViewer(bookmarkSelComposite);
        createPanelWithAddRemoveButtons(bookmarkSelComposite);
        createProspectiveExportTreeViewer(bookmarkSelComposite);
        addMouseListenerToRemoveButton(remove);
        addMouseListenerToRemoveAllButton(removeAll);
        addMouseListenerToAddButton(add);
        addMouseListenerToAddAllButton(addAll);

    }

    private void createLocalTreeViewer(Composite bookmarkSelComposite) {
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        localTreeViewer = new RepresentationSwitchableTreeViewer(bookmarkSelComposite,
                new HierarchicalRepresentationMode());
        localTreeViewer.setTreeLayoutData(data);

        addDragSupportToLocalTreeViewer();

    }

    private void addDragSupportToLocalTreeViewer() {
        final BookmarkTreeDragListener dragListener = new BookmarkTreeDragListener();
        localTreeViewer.addDragSupport(dragListener.getSupportedOperations(), dragListener.getSupportedTransfers(),
                dragListener);
    }

    private void setExportTreeViewerKeyListener() {
        exportTreeViewer.addKeyListener(new WizardKeyListener(exportTreeViewer, invoker));
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

    private void addMouseListenerToAddButton(Button add) {
        add.addMouseListener(new AddMouseAndDoubleClickerListener(localTreeViewer, exportTreeViewer, invoker));
    }

    private void addRemoveAllButton(Composite buttonPanel) {
        removeAll = new Button(buttonPanel, SWT.CENTER);
        removeAll.setText("Remove All");
        GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
        removeAll.setLayoutData(data);
    }

    private void addMouseListenerToRemoveAllButton(Button removeAll) {
        removeAll.addMouseListener(new RemoveAllMouseListener(invoker));
    }

    private void addRemoveButton(Composite buttonPanel) {
        remove = new Button(buttonPanel, SWT.CENTER);
        remove.setText("Remove");
        GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
        remove.setLayoutData(data);
        remove.setEnabled(false);
    }

    private void addAddAllButton(Composite buttonPanel) {
        addAll = new Button(buttonPanel, SWT.CENTER);
        addAll.setText("Add All");
        GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
        addAll.setLayoutData(data);
    }

    private void addMouseListenerToAddAllButton(Button addAll) {
        addAll.addMouseListener(new AddAllMouseListener(exportTreeViewer, localTreeViewer, invoker));
    }

    private boolean isValid(File file) {

        String fileEnding = Activator.fileEnding;
        int suffixLen = fileEnding.length();

        String filePath = file.getAbsolutePath();
        int pathLen = filePath.length();

        if (pathLen - suffixLen < 0) {
            return false;
        }

        String currentFileEnding = filePath.substring(pathLen - suffixLen);
        boolean isEqual = currentFileEnding.compareTo(fileEnding) == 0;
        return isEqual;
    }

    private void addMouseListenerToRemoveButton(Button remove) {
        remove.addMouseListener(new RemoveMouseListener(exportTreeViewer, invoker));
    }

    private void createProspectiveExportTreeViewer(Composite bookmarkSelComposite) {
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        exportTreeViewer = new RepresentationSwitchableTreeViewer(bookmarkSelComposite,
                new HierarchicalRepresentationMode());
        exportTreeViewer.setTreeLayoutData(data);

        addDragDropSupportToExportView();
        setExportTreeViewerKeyListener();
    }

    private void addDragDropSupportToExportView() {
        final BookmarkTreeDragListener dragListener = new BookmarkTreeDragListener();
        exportTreeViewer.addDragSupport(dragListener.getSupportedOperations(), dragListener.getSupportedTransfers(),
                dragListener);
        final WizardDropStrategy strategy = new WizardDropStrategy(invoker);
        final BookmarkTreeDropListener dropListener = new BookmarkTreeDropListener(strategy);
        exportTreeViewer.addDropSupport(dropListener.getSupportedOperations(), dropListener.getSupportedTransfers(),
                dropListener);
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

    public void setExportFile(File file) {
        this.selectedFile = file;
    }

    public File getExportFile() {
        return selectedFile;
    }

    private class ButtonMouseDownListener implements Listener {
        private final BookmarkExportWizardPage exportPage;

        public ButtonMouseDownListener(BookmarkExportWizardPage exportPage) {
            this.exportPage = exportPage;
        }

        @Override
        public void handleEvent(Event event) {
            File file = ExportDialog.showDialog();

            if (file != null) {
                exportPage.setExportFile(file);
                exportPage.selectedFileTextField.setText(file.getAbsolutePath());
                setPageComplete(true);
            }

        }
    }

    private class FilePathModifyListener implements Listener {
        private final Text textField;

        public FilePathModifyListener(Text textField) {
            this.textField = textField;
        }

        @Override
        public void handleEvent(Event event) {
            File file = new File(textField.getText());

            if (isValid(file)) {
                setPageComplete(true);
            } else {
                setPageComplete(false);
            }

        }
    }

    @Override
    public void invoke(IBookmarkModelCommand command) {
        command.execute(exportModel);
        exportTreeViewer.refresh();
    }

}