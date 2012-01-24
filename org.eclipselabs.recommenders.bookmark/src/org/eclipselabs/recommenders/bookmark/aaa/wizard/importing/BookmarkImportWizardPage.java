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
import java.util.Iterator;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
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
import org.eclipselabs.recommenders.bookmark.aaa.commands.BookmarkDeletionCommand;
import org.eclipselabs.recommenders.bookmark.aaa.commands.IBookmarkModelCommand;
import org.eclipselabs.recommenders.bookmark.aaa.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.aaa.model.Category;
import org.eclipselabs.recommenders.bookmark.aaa.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.aaa.tree.HierarchicalRepresentationMode;
import org.eclipselabs.recommenders.bookmark.aaa.tree.RepresentationSwitchableTreeViewer;

public class BookmarkImportWizardPage extends WizardPage {

    private Text selectedFileTextField;
    private Button button;
    private File selectedFile;
    private RepresentationSwitchableTreeViewer importTreeViewer;
    private FilePathModifyListener filePathListener;

    private RepresentationSwitchableTreeViewer localTreeViewer;
    private Composite container;
    private BookmarkModel clonedModel;
    private Button remove;

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

        makeRemoveButtonEnDisableOnSelections();

        localTreeViewer.setInput(clonedModel);

        setControl(parent);
        setPageComplete(false);
    }

    private void makeRemoveButtonEnDisableOnSelections() {
        localTreeViewer.overrideSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                if (localTreeViewer.getSelections().size() == 0) {
                    remove.setEnabled(false);
                } else {
                    remove.setEnabled(true);
                }
            }
        });
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
        addMouseListenerToAddAllButton(add);

        remove = new Button(buttonPanel, SWT.CENTER);
        remove.setText("Remove");
        remove.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
        remove.setEnabled(false);
        addMouseListenerToRemoveButton(remove);
    }

    private void addMouseListenerToAddAllButton(Button add) {
        add.addMouseListener(new MouseListener() {

            @Override
            public void mouseUp(MouseEvent e) {
            }

            @Override
            public void mouseDown(MouseEvent e) {

                File file = new File(selectedFileTextField.getText());
                if (isValid(file)) {
                    BookmarkModel model = BookmarkIO.load(file);
                    for (Category cat : model.getCategories()) {
                        clonedModel.add(cat);
                    }
                    localTreeViewer.refresh();
                }
            }

            @Override
            public void mouseDoubleClick(MouseEvent e) {
            }
        });
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

    private void addMouseListenerToRemoveButton(Button remove) {
        remove.addMouseListener(new MouseListener() {

            BookmarkCommandInvoker invoker = new BookmarkCommandInvoker() {

                @Override
                public void invoke(IBookmarkModelCommand command) {
                    command.execute(clonedModel);
                }
            };

            @Override
            public void mouseUp(MouseEvent e) {
            }

            @Override
            public void mouseDown(MouseEvent e) {
                IStructuredSelection selections = localTreeViewer.getSelections();
                @SuppressWarnings("rawtypes")
                Iterator iterator = selections.iterator();
                while (iterator.hasNext()) {
                    invoker.invoke(new BookmarkDeletionCommand((IBookmarkModelComponent) iterator.next()));
                    localTreeViewer.refresh();
                }
            }

            @Override
            public void mouseDoubleClick(MouseEvent e) {
            }
        });
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
                // localTreeViewer.setInput(clonedModel);
                container.layout(true, true);
                setPageComplete(true);
            }

        }
    }

    public BookmarkModel getClonedModel() {
        return clonedModel;
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
                container.layout(true, true);
            }

        }

        private void setFileContentForViewer(File file) {
            if (isValid(file)) {
                importTreeViewer.setInput(BookmarkIO.load(file));
                localTreeViewer.setInput(clonedModel);
                container.layout(true, true);
            }
        }

    }

}