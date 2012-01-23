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
import org.eclipselabs.recommenders.bookmark.aaa.BookmarkTreeDropListener;
import org.eclipselabs.recommenders.bookmark.aaa.commands.IBookmarkModelCommand;
import org.eclipselabs.recommenders.bookmark.aaa.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.aaa.tree.HierarchicalRepresentationMode;
import org.eclipselabs.recommenders.bookmark.aaa.tree.RepresentationSwitchableTreeViewer;

public class BookmarkImportWizardPage extends WizardPage {

    private Text selectedFileTextField;
    private Text nameOfNewCategory;
    private Button button;
    private Button checkbox;
    private File file;
    private RepresentationSwitchableTreeViewer importTreeViewer;
    private FilePathModifyListener filePathListener;
    private NameOfCategoryTextFieldListener nameOfCategoryListener;
    private CheckboxListener checkBoxListener;

    private CompletionChecker checker;
    private RepresentationSwitchableTreeViewer localTreeViewer;
    private Composite container;
    private BookmarkModel clonedModel;

    protected BookmarkImportWizardPage(String pageName) {
        super(pageName, pageName, null);
        setDescription("Imports the bookmark provided in an external file\nNo selection = import all");
        clonedModel = Activator.getClonedModel();
    }

    @Override
    public void createControl(Composite parent) {

        container = initializeContainerComposite(parent);
        addRowWithTextFieldForLoadedFileAndBrowseButton(container);
        addHeadline(container);
        addTreeViewerWithAddRemoveButton(container);
        // addRowWithCheckboxAndNewNameTextfield();
        // checker = new CompletionChecker(this, filePathListener,
        // checkBoxListener, nameOfCategoryListener);
        // Required to avoid an error in the system
        setControl(parent);
        setPageComplete(false);

    }

    public boolean consolidateBookmarksAsSingleCategory() {
        return checkBoxListener.isEnabled();
    }

    public String getCategoryName() {
        return nameOfNewCategory.getText();
    }

    public CompletionChecker getChecker() {
        return checker;
    }

    private Composite initializeContainerComposite(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(GridLayoutFactory.fillDefaults().create());
        GridData compData = new GridData(SWT.FILL, SWT.FILL, true, true);
        container.setLayoutData(compData);
        return container;
    }

    private void addRowWithTextFieldForLoadedFileAndBrowseButton(Composite container) {
        Composite fileSelectionComposite = new Composite(container, SWT.NONE);
        fileSelectionComposite.setLayout(GridLayoutFactory.fillDefaults().numColumns(3).create());
        fileSelectionComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

        GridData data = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        Label label = new Label(fileSelectionComposite, SWT.CENTER);
        label.setText("File: ");
        label.setLayoutData(data);

        selectedFileTextField = new Text(fileSelectionComposite, SWT.BORDER);
        data = new GridData(SWT.FILL, SWT.CENTER, true, false);
        selectedFileTextField.setLayoutData(data);

        data = new GridData(SWT.CENTER, SWT.CENTER, false, false);
        button = new Button(fileSelectionComposite, SWT.NONE);
        button.setText("Select File");
        button.setLayoutData(data);

        button.addListener(SWT.MouseDown, new ButtonMouseDownListener(selectedFileTextField, this));

        filePathListener = new FilePathModifyListener(selectedFileTextField, this);
        selectedFileTextField.addListener(SWT.KeyUp, filePathListener);
    }

    private void addTreeViewerWithAddRemoveButton(Composite container) {

        Composite bookmarkSelComposite = new Composite(container, SWT.NONE);
        bookmarkSelComposite.setLayout(GridLayoutFactory.fillDefaults().numColumns(3).create());
        bookmarkSelComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        importTreeViewer = new RepresentationSwitchableTreeViewer(bookmarkSelComposite,
                new HierarchicalRepresentationMode(), null);
        importTreeViewer.getTree().setLayoutData(data);
        final BookmarkTreeDragListener dragListener = new BookmarkTreeDragListener();
        importTreeViewer.addDragSupport(dragListener.getSupportedOperations(), dragListener.getSupportedTransfers(),
                dragListener);

        Composite buttonPanel = new Composite(bookmarkSelComposite, SWT.CENTER);
        data = new GridData(SWT.FILL, SWT.FILL, true, true);
        buttonPanel.setLayout(GridLayoutFactory.fillDefaults().create());
        Button add = new Button(buttonPanel, SWT.NONE);
        add.setText("Add all");
        add.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
        Button remove = new Button(buttonPanel, SWT.NONE);
        remove.setText("Remove");
        remove.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

        data = new GridData(SWT.FILL, SWT.FILL, true, true);
        localTreeViewer = new RepresentationSwitchableTreeViewer(bookmarkSelComposite,
                new HierarchicalRepresentationMode(), null);
        localTreeViewer.getTree().setLayoutData(data);
        final BookmarkTreeDropListener dropListener = new BookmarkTreeDropListener(new BookmarkCommandInvoker() {
            
            @Override
            public void invoke(IBookmarkModelCommand command) {
                command.execute(clonedModel);
            }
        });
        localTreeViewer.addDropSupport(dropListener.getSupportedOperations(), dropListener.getSupportedTransfers(),
                dropListener);
//        localTreeViewer.getTree().addMouseListener(new MouseListener() {
//            
//            @Override
//            public void mouseUp(MouseEvent e) {
//                localTreeViewer.refresh();
//            }
//            
//            @Override
//            public void mouseDown(MouseEvent e) {
//            }
//            
//            @Override
//            public void mouseDoubleClick(MouseEvent e) {
//            }
//        });
    }

    //
    // private void addRowWithCheckboxAndNewNameTextfield() {
    // Composite subContainer = new Composite(container, SWT.NONE);
    // GridLayout layout = new GridLayout();
    // layout.numColumns = 2;
    // layout.makeColumnsEqualWidth = true;
    // GridData compData = new GridData(SWT.FILL, SWT.CENTER, true, false);
    // compData.horizontalSpan = 2;
    // subContainer.setLayout(layout);
    // subContainer.setLayoutData(compData);
    //
    // checkbox = new Button(subContainer, SWT.CHECK);
    // checkbox.setText("Import as single category with name: ");
    // GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
    // checkbox.setLayoutData(data);
    //
    // nameOfNewCategory = new Text(subContainer, SWT.NONE);
    // nameOfNewCategory.setText("Import");
    // nameOfNewCategory.setEnabled(false);
    // data = new GridData(SWT.FILL, SWT.CENTER, true, false);
    // nameOfNewCategory.setLayoutData(data);
    //
    // nameOfCategoryListener = new NameOfCategoryTextFieldListener(this,
    // nameOfNewCategory);
    // nameOfNewCategory.addModifyListener(nameOfCategoryListener);
    //
    // checkBoxListener = new CheckboxListener(this, nameOfNewCategory);
    // checkbox.addListener(SWT.Selection, checkBoxListener);
    // }

    private void addHeadline(Composite container) {
        Composite headline = new Composite(container, SWT.NONE);
        headline.setLayout(GridLayoutFactory.fillDefaults().numColumns(3).create());
        headline.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

        GridData data = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        Label left = new Label(headline, SWT.CENTER);
        left.setText("Bookmarks from file");
        left.setLayoutData(data);
        
        data = new GridData(SWT.CENTER, SWT.CENTER, true, false);
        Label middle = new Label(headline, SWT.CENTER);
        middle.setText("");
        middle.setLayoutData(data);
        
        data = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        Label right = new Label(headline, SWT.CENTER);
        right.setText("Local bookmarks");
        right.setLayoutData(data);

    }

    public void setModel(BookmarkModel model) {
        importTreeViewer.setInput(model);
        importTreeViewer.refresh();
    }

    public void setImportFile(File file) {
        this.file = file;
    }

    public File getImportFile() {
        return file;
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
        return file;
    }

}