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
package org.eclipselabs.recommenders.bookmark.aaa.wizard.importing;

import java.io.File;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.aaa.BookmarkCommandInvoker;
import org.eclipselabs.recommenders.bookmark.aaa.BookmarkIO;
import org.eclipselabs.recommenders.bookmark.aaa.BookmarkTreeDragListener;
import org.eclipselabs.recommenders.bookmark.aaa.commands.IBookmarkModelCommand;
import org.eclipselabs.recommenders.bookmark.aaa.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.aaa.tree.HierarchicalRepresentationMode;
import org.eclipselabs.recommenders.bookmark.aaa.tree.RepresentationSwitchableTreeViewer;

public class BookmarkImportWizardPage extends WizardPage {

    private Text selectedFileTextField;
    private Text nameOfNewCategory;
    private Button button;
    private File selectedFile;
    private RepresentationSwitchableTreeViewer importTreeViewer;
    private FilePathModifyListener filePathListener;

    private RepresentationSwitchableTreeViewer localTreeViewer;
    private Composite container;
    private BookmarkModel clonedModel;

    protected BookmarkImportWizardPage(String pageName) {
        super(pageName, pageName, null);
        setDescription("Imports the bookmark provided in an external file");
        clonedModel = Activator.getClonedModel();
    }

    @Override
    public void createControl(Composite parent) {
        container = initializeContainerComposite(parent);
        addRowWithTextFieldForLoadedFileAndBrowseButton(container);
        addTreeViewerWithAddRemoveButton(container);
        setControl(parent);
        setPageComplete(false);
    }

    public boolean consolidateBookmarksAsSingleCategory() {
        return checkBoxListener.isEnabled();
    }

    public String getCategoryName() {
        return nameOfNewCategory.getText();
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
        button.addListener(SWT.MouseDown, new ButtonMouseDownListener(selectedFileTextField, this));
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
        createLoadedFileTreeViewer(bookmarkSelComposite);
        createPanelWithAddRemoveButtons(bookmarkSelComposite);
        createProspectiveLocalTreeViewer(bookmarkSelComposite);
    }

    private void createProspectiveLocalTreeViewer(Composite bookmarkSelComposite) {
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        localTreeViewer = new RepresentationSwitchableTreeViewer(bookmarkSelComposite,
                new HierarchicalRepresentationMode(), null);
        localTreeViewer.setTreeLayoutData(data);
        final ImportDropListener dropListener = new ImportDropListener(new BookmarkCommandInvoker() {

            @Override
            public void invoke(IBookmarkModelCommand command) {
                command.execute(clonedModel);
                localTreeViewer.refresh();
                importTreeViewer.refresh();
            }
        });
        localTreeViewer.addDropSupport(dropListener.getSupportedOperations(), dropListener.getSupportedTransfers(),
                dropListener);
    }

    private void createPanelWithAddRemoveButtons(Composite bookmarkSelComposite) {
        Composite buttonPanel = new Composite(bookmarkSelComposite, SWT.NONE);
        GridData data = new GridData(SWT.FILL, SWT.CENTER, true, true);
        buttonPanel.setLayout(GridLayoutFactory.fillDefaults().create());
        buttonPanel.setLayoutData(data);

        Button add = new Button(buttonPanel, SWT.CENTER);
        add.setText("Add All");
        add.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

        Button remove = new Button(buttonPanel, SWT.CENTER);
        remove.setText("Remove");
        remove.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

    }

    private void createLoadedFileTreeViewer(Composite bookmarkSelComposite) {
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        importTreeViewer = new RepresentationSwitchableTreeViewer(bookmarkSelComposite,
                new HierarchicalRepresentationMode(), null);
        importTreeViewer.setTreeLayoutData(data);
        final BookmarkTreeDragListener dragListener = new BookmarkTreeDragListener();
        importTreeViewer.addDragSupport(dragListener.getSupportedOperations(), dragListener.getSupportedTransfers(),
                dragListener);
    }

    private void addHeadline(Composite composite) {
        // Composite headline = new Composite(container, SWT.NONE);
        // headline.setLayout(GridLayoutFactory.fillDefaults().numColumns(3).create());
        // headline.setLayoutData(GridDataFactory.fillDefaults().grab(true,
        // false).create());

        GridData data = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        Label left = new Label(composite, SWT.CENTER);
        left.setText("Bookmarks from file");
        left.setLayoutData(data);

        data = new GridData(SWT.CENTER, SWT.CENTER, true, false);
        Label middle = new Label(composite, SWT.CENTER);
        middle.setText("");
        middle.setLayoutData(data);

        data = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        Label right = new Label(composite, SWT.CENTER);
        right.setText("Local bookmarks");
        right.setLayoutData(data);

    }

    public void setModel(BookmarkModel model) {
        importTreeViewer.setInput(model);
        importTreeViewer.refresh();
    }

    public void setImportFile(File file) {
        this.selectedFile = file;
    }

    public File getImportFile() {
        return selectedFile;
    }

    private class ButtonMouseDownListener implements Listener {
        private final Text textField;
        private final BookmarkImportWizardPage bookmarkImportWizardPage;

        public ButtonMouseDownListener(Text textField, BookmarkImportWizardPage bookmarkImportWizardPage) {
            this.textField = textField;
            this.bookmarkImportWizardPage = bookmarkImportWizardPage;
        }

        @Override
        public void handleEvent(Event event) {
            File file = ImportDialog.showDialog();

            if (file != null) {
                textField.setText(file.getAbsolutePath());
                bookmarkImportWizardPage.setImportFile(file);
                BookmarkModel model = BookmarkIO.load(file);

                importTreeViewer.setInput(model);
                localTreeViewer.setInput(clonedModel);
                container.layout(true, true);

                // bookmarkImportWizardPage.getChecker().checkCompletion();
            }

        }
    }

    public IStructuredSelection getSelections() {
        return importTreeViewer.getSelections();
    }

    public File getFile() {
        return selectedFile;
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
                setFileContentForViewer(file);
                selectedFile = file;
                setPageComplete(true);
            } else {
                selectedFile = null;
                importTreeViewer.setInput(null);
                localTreeViewer.setInput(null);
                container.layout(true, true);
            }

        }

        private void setFileContentForViewer(File file) {
            if (isValid()) {
                importTreeViewer.setInput(BookmarkIO.load(file));
                localTreeViewer.setInput(clonedModel);
                container.layout(true, true);
            }
        }

        public boolean isValid() {
            File file = new File(textField.getText());
            return isValid(file);
        }

        private boolean isValid(File file) {
            return file.exists() && !file.isDirectory() && hasCorrectFileEnding(file);
        }

        private boolean hasCorrectFileEnding(File file) {
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

    }

}