package org.eclipselabs.recommenders.bookmark.wizard.exporting;

import java.io.File;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.commands.IBookmarkModelCommand;
import org.eclipselabs.recommenders.bookmark.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.wizard.BookmarkWizardPage;

public class BookmarkExportWizardPage extends BookmarkWizardPage {

    private BookmarkModel exportModel;
    private BookmarkModel localClonedModel;

    public BookmarkExportWizardPage(String pageName, String rightViewerLabel, String leftViewerLabel) {
        super(pageName, rightViewerLabel, leftViewerLabel);
        setTitle("Export Bookmarks");
        setDescription("Export bookmarks to an external file");
        localClonedModel = Activator.getClonedModel();
        exportModel = new BookmarkModel();
    }

    @Override
    public void createControl(Composite parent) {
        super.createControl(parent);
        addSpecificListener();
        leftTreeViewer.setInput(localClonedModel);
        rightTreeViewer.setInput(exportModel);
    }

    private void addSpecificListener() {
        addFilePathModifyListener();
        addMouseListenerForOpenFileButton();
    }

    @Override
    protected void addMouseListenerForOpenFileButton() {
        selectFileButton.addMouseListener(new OpenFileButtonListener(this));
    }

    @Override
    protected void addFilePathModifyListener() {
        selectedFileTextField.addListener(SWT.KeyUp, new FilePathModifyListener(selectedFileTextField, this));
    }

    @Override
    public void invoke(IBookmarkModelCommand command) {
        command.execute(exportModel);
        rightTreeViewer.refresh();
        layout();
    }

    public File getExportFile() {
        return new File(selectedFileTextField.getText());
    }

    public BookmarkModel getExportModel() {
        return exportModel;
    }

    private class OpenFileButtonListener implements MouseListener {
        private final BookmarkExportWizardPage page;

        public OpenFileButtonListener(BookmarkExportWizardPage page) {
            this.page = page;
        }

        @Override
        public void mouseDoubleClick(MouseEvent e) {
        }

        @Override
        public void mouseDown(MouseEvent e) {
        }

        @Override
        public void mouseUp(MouseEvent e) {
            File file = ExportDialog.showDialog();
            if (file != null) {
                page.selectedFileTextField.setText(file.getAbsolutePath());
                setPageComplete(true);
            }
        }
    }

    private class FilePathModifyListener implements Listener {
        private final Text textField;
        private final WizardPage wizard;

        public FilePathModifyListener(Text textField, WizardPage wizard) {
            this.textField = textField;
            this.wizard = wizard;
        }

        @Override
        public void handleEvent(Event event) {
            File file = new File(textField.getText());

            if (isValid(file)) {
                wizard.setPageComplete(true);
            } else {
                wizard.setPageComplete(false);
            }

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
    }

}
