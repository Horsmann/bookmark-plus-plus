package org.eclipselabs.recommenders.bookmark.wizard.importing;

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.commands.IBookmarkModelCommand;
import org.eclipselabs.recommenders.bookmark.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.view.BookmarkIO;
import org.eclipselabs.recommenders.bookmark.view.tree.RepresentationSwitchableTreeViewer;
import org.eclipselabs.recommenders.bookmark.wizard.BookmarkWizardPage;

public class BookmarkImportWizardPage extends BookmarkWizardPage {

    private BookmarkModel clonedModel;

    public BookmarkImportWizardPage(String pageName) {
        super(pageName, "Import bookmarks", "Local bookmarks");
        setTitle("Import Bookmarks");
        setDescription("Imports the bookmark provided in an external file");
        clonedModel = Activator.getClonedModel();
    }

    @Override
    public void createControl(Composite composite) {
        super.createControl(composite);
        rightTreeViewer.setInput(clonedModel);
        addSpecificListener();
    }

    private void addSpecificListener() {
        addMouseListenerForOpenFileButton();
        addFilePathModifyListener();
    }

    @Override
    protected void addFilePathModifyListener() {
        selectedFileTextField.addListener(SWT.KeyUp, new FilePathModifyListener(selectedFileTextField, leftTreeViewer,
                this));
    }

    public void showErrorOnOpeningFilePopUp(String message, String text) {
        MessageBox messageBox = new MessageBox(leftTreeViewer.getShell(), SWT.ICON_ERROR | SWT.OK);
        messageBox.setMessage("File content could not be read");
        messageBox.setText("Error opening file");
        messageBox.open();
    }

    public void setLeftTreeViewerInput(BookmarkModel model) {
        leftTreeViewer.setInput(model);
    }

    public void setRighTreeViewerInput(BookmarkModel model) {
        rightTreeViewer.setInput(model);
    }

    public void layout() {
        container.layout(true, true);
    }

    @Override
    protected void addMouseListenerForOpenFileButton() {
        selectFileButton.addMouseListener(new OpenFileButtonListener(selectedFileTextField, this));
    }

    @Override
    public void invoke(IBookmarkModelCommand command) {
        command.execute(clonedModel);
        rightTreeViewer.refresh();
    }
    
    public void setPathOfFileIntoTextField(File file) {
        if (file != null) {
            selectedFileTextField.setText(file.getAbsolutePath());
        } else {
            selectedFileTextField.setText("");
        }

    }

    public BookmarkModel getClonedModel() {
        return clonedModel;
    }

    private class OpenFileButtonListener implements MouseListener {
        private final Text textField;
        private final BookmarkImportWizardPage bookmarkImportWizardPage;

        public OpenFileButtonListener(Text textField, BookmarkImportWizardPage bookmarkImportWizardPage) {
            this.textField = textField;
            this.bookmarkImportWizardPage = bookmarkImportWizardPage;
        }

        @Override
        public void mouseDoubleClick(MouseEvent e) {
        }

        @Override
        public void mouseDown(MouseEvent e) {
            File file = ImportDialog.showDialog();

            if (file != null) {
                textField.setText(file.getAbsolutePath());
                bookmarkImportWizardPage.setPathOfFileIntoTextField(file);
                BookmarkModel model;
                try {
                    model = BookmarkIO.load(file);
                } catch (IOException ex) {
                    bookmarkImportWizardPage.showErrorOnOpeningFilePopUp("File content could not be read",
                            "Error opening file");
                    return;
                }
                bookmarkImportWizardPage.setLeftTreeViewerInput(model);
                bookmarkImportWizardPage.layout();
                bookmarkImportWizardPage.setPageComplete(true);
            }

        }

        @Override
        public void mouseUp(MouseEvent e) {
        }

    }

    private class FilePathModifyListener implements Listener {
        private final Text textField;
        private final BookmarkImportWizardPage importPage;
        private final RepresentationSwitchableTreeViewer leftTreeViewer;

        public FilePathModifyListener(Text textField, RepresentationSwitchableTreeViewer leftTreeViewer,
                BookmarkImportWizardPage importPage) {
            this.textField = textField;
            this.leftTreeViewer = leftTreeViewer;
            this.importPage = importPage;
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
                setPageComplete(true);
            } else {
                leftTreeViewer.setInput((BookmarkModel) null);
                container.layout(true, true);
            }

        }

        private void setFileContentForViewer(File file) throws IOException {
            if (isValid(file)) {
                leftTreeViewer.setInput(BookmarkIO.load(file));
                importPage.layout();
            }
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
