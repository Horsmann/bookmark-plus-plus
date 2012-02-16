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
package org.eclipselabs.recommenders.bookmark.wizard.importing;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.commands.BookmarkDeletionCommand;
import org.eclipselabs.recommenders.bookmark.commands.DeleteAllBookmarksCommand;
import org.eclipselabs.recommenders.bookmark.commands.IBookmarkModelCommand;
import org.eclipselabs.recommenders.bookmark.commands.RenameCategoryCommand;
import org.eclipselabs.recommenders.bookmark.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.model.Category;
import org.eclipselabs.recommenders.bookmark.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.view.BookmarkCommandInvoker;
import org.eclipselabs.recommenders.bookmark.view.BookmarkIO;
import org.eclipselabs.recommenders.bookmark.view.BookmarkTreeDragListener;
import org.eclipselabs.recommenders.bookmark.view.tree.HierarchicalRepresentationMode;
import org.eclipselabs.recommenders.bookmark.view.tree.RepresentationSwitchableTreeViewer;
import org.eclipselabs.recommenders.bookmark.wizard.ImportSelectedBookmarksCommand;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

public class BookmarkImportWizardPage extends WizardPage implements BookmarkCommandInvoker {

    private Text selectedFileTextField;
    private Button button;
    private File selectedFile;
    private RepresentationSwitchableTreeViewer importTreeViewer;
    private FilePathModifyListener filePathListener;

    private RepresentationSwitchableTreeViewer localTreeViewer;
    private Composite container;
    private BookmarkModel clonedModel;
    private Button remove;
    private Button removeAll;
    private Button add;
    private BookmarkCommandInvoker invoker;

    protected BookmarkImportWizardPage(String pageName) {
        super(pageName, pageName, null);
        setDescription("Imports the bookmark provided in an external file");
        clonedModel = Activator.getClonedModel();
        invoker = this;
    }

    @Override
    public void createControl(Composite parent) {
        container = initializeContainerComposite(parent);
        addRowWithTextFieldForLoadedFileAndBrowseButton(container);
        addTreeViewerWithAddRemoveButton(container);

        makeAddButtonEnDisabledOnSelections();
        makeRemoveButtonEnDisabledOnSelections();

        localTreeViewer.setInput(clonedModel);

        setControl(parent);
        setPageComplete(false);
    }

    private void makeAddButtonEnDisabledOnSelections() {
        importTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                if (importTreeViewer.getSelections().size() == 0) {
                    add.setEnabled(false);
                } else {
                    add.setEnabled(true);
                }
            }
        });
    }

    private void makeRemoveButtonEnDisabledOnSelections() {
        localTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

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

        addDragDropSupportToLocalTreeViewer();
        setLocalTreeViewerKeyListener();
    }

    private void addDragDropSupportToLocalTreeViewer() {
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

        final BookmarkTreeDragListener dragListener = new BookmarkTreeDragListener();
        localTreeViewer.addDragSupport(dragListener.getSupportedOperations(), dragListener.getSupportedTransfers(),
                dragListener);
    }

    private void setLocalTreeViewerKeyListener() {
        localTreeViewer.overrideKeyListener(new KeyListener() {

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
                localTreeViewer.refresh();
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
                invoke(new RenameCategoryCommand(localTreeViewer));
            }

            private TreeItem[] getTreeSelections(KeyEvent e) {
                Tree tree = (Tree) e.getSource();
                TreeItem[] selections = tree.getSelection();
                return selections;
            }

            private void searchBookmarkDeleteSelection(TreeItem item) {
                IBookmarkModelComponent component = (IBookmarkModelComponent) item.getData();
                BookmarkCommandInvoker invoker = new BookmarkCommandInvoker() {

                    @Override
                    public void invoke(IBookmarkModelCommand command) {
                        command.execute(clonedModel);
                    }
                };
                invoker.invoke(new BookmarkDeletionCommand(component));
            }

            private boolean isDeletion(KeyEvent e) {
                int BACKSPACE = 8;
                return (e.keyCode == SWT.DEL || e.keyCode == BACKSPACE);
            }
        });
    }

    private void createPanelWithAddRemoveButtons(Composite bookmarkSelComposite) {
        Composite buttonPanel = new Composite(bookmarkSelComposite, SWT.NONE);
        GridData data = new GridData(SWT.CENTER, SWT.CENTER, false, false);
        buttonPanel.setLayout(new GridLayout());
        buttonPanel.setLayoutData(data);

        addDragExplainingLabel(buttonPanel);
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
        addMouseListenerToAddButton(add);
    }

    private void addMouseListenerToAddButton(Button add) {
        add.addMouseListener(new MouseListener() {

            @Override
            public void mouseUp(MouseEvent e) {
            }

            @Override
            public void mouseDown(MouseEvent e) {
                IBookmarkModelComponent[] components = getSelectedBookmarkComponents();

                if (localTreeViewer.getSelections().size() == 0) {
                    Optional<IBookmarkModelComponent> dropTarget = Optional.absent();
                    invoker.invoke(new ImportSelectedBookmarksCommand(components, invoker, true, false, dropTarget));
                } else {
                    Optional<IBookmarkModelComponent> dropTarget = Optional
                            .of((IBookmarkModelComponent) localTreeViewer.getSelections().getFirstElement());
                    invoker.invoke(new ImportSelectedBookmarksCommand(components, invoker, true, false, dropTarget));
                }
            }

            private IBookmarkModelComponent[] getSelectedBookmarkComponents() {
                IStructuredSelection selections = importTreeViewer.getSelections();
                List<IBookmarkModelComponent> components = Lists.newArrayList();
                @SuppressWarnings("rawtypes")
                Iterator iterator = selections.iterator();
                while (iterator.hasNext()) {
                    components.add((IBookmarkModelComponent) iterator.next());
                }
                return components.toArray(new IBookmarkModelComponent[0]);
            }

            @Override
            public void mouseDoubleClick(MouseEvent e) {
            }
        });
    }

    private void addRemoveAllButton(Composite buttonPanel) {
        removeAll = new Button(buttonPanel, SWT.CENTER);
        removeAll.setText("Remove All");
        GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
        removeAll.setLayoutData(data);
        addMouseListenerToRemoveAllButton(removeAll);
    }

    private void addMouseListenerToRemoveAllButton(Button removeAll) {
        removeAll.addMouseListener(new MouseListener() {

            @Override
            public void mouseUp(MouseEvent e) {
            }

            @Override
            public void mouseDown(MouseEvent e) {
                invoker.invoke(new DeleteAllBookmarksCommand());
                localTreeViewer.refresh();
            }

            @Override
            public void mouseDoubleClick(MouseEvent e) {
            }
        });
    }

    private void addRemoveButton(Composite buttonPanel) {
        remove = new Button(buttonPanel, SWT.CENTER);
        remove.setText("Remove");
        GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
        remove.setLayoutData(data);
        remove.setEnabled(false);
        addMouseListenerToRemoveButton(remove);
    }

    private void addAddAllButton(Composite buttonPanel) {
        Button add = new Button(buttonPanel, SWT.CENTER);
        add.setText("Add All");
        GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
        add.setLayoutData(data);
        addMouseListenerToAddAllButton(add);
    }

    private void addDragExplainingLabel(Composite buttonPanel) {
        Label manual1 = new Label(buttonPanel, SWT.CENTER);
        manual1.setText("drag to import");
        GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
        manual1.setLayoutData(data);

        Label manual2 = new Label(buttonPanel, SWT.CENTER);
        manual2.setText("===DRAG==>");
        data = new GridData(SWT.FILL, SWT.CENTER, true, false);
        manual2.setLayoutData(data);

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
                    BookmarkModel model = null;
                    try {
                        model = BookmarkIO.load(file);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
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
        GridData data = new GridData(SWT.LEFT, SWT.CENTER, true, false);
        Label left = new Label(composite, SWT.CENTER);
        left.setText("Import bookmarks");
        left.setLayoutData(data);

        data = new GridData(SWT.CENTER, SWT.CENTER, false, false);
        Label middle = new Label(composite, SWT.CENTER);
        middle.setText("");
        middle.setLayoutData(data);

        data = new GridData(SWT.LEFT, SWT.CENTER, true, false);
        Label right = new Label(composite, SWT.CENTER);
        right.setText("Local bookm. (F2=Rename Cat.)");
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
                BookmarkModel model;
                try {
                    model = BookmarkIO.load(file);
                } catch (IOException e) {
                    MessageBox messageBox = new MessageBox(importTreeViewer.getShell(), SWT.ICON_ERROR | SWT.OK);
                    messageBox.setMessage("File content could not be read");
                    messageBox.setText("Error opening file");
                    messageBox.open();
                    return;
                }

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
                try {
                    setFileContentForViewer(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                selectedFile = file;
                setPageComplete(true);
            } else {
                selectedFile = null;
                importTreeViewer.setInput((BookmarkModel)null);
                container.layout(true, true);
            }

        }

        private void setFileContentForViewer(File file) throws IOException {
            if (isValid(file)) {
                importTreeViewer.setInput(BookmarkIO.load(file));
                localTreeViewer.setInput(clonedModel);
                container.layout(true, true);
            }
        }

    }

    @Override
    public void invoke(IBookmarkModelCommand command) {
        command.execute(clonedModel);
        localTreeViewer.refresh();
    }

}