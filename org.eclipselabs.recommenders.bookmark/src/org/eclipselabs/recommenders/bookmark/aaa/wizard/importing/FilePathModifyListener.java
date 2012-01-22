package org.eclipselabs.recommenders.bookmark.aaa.wizard.importing;

import java.io.File;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.aaa.BookmarkIO;
import org.eclipselabs.recommenders.bookmark.aaa.model.BookmarkModel;

public class FilePathModifyListener implements Listener {
    private final BookmarkImportWizardPage bookmarkImportWizardPage;
    private final Text textField;

    public FilePathModifyListener(Text textField, BookmarkImportWizardPage bookmarkImportWizardPage) {
        this.textField = textField;
        this.bookmarkImportWizardPage = bookmarkImportWizardPage;
    }

    @Override
    public void handleEvent(Event event) {
        File file = new File(textField.getText());

        if (isValid(file)) {

            setFileContentForViewer(file);

            bookmarkImportWizardPage.setImportFile(file);
            bookmarkImportWizardPage.setPageComplete(true);
            bookmarkImportWizardPage.getChecker().checkCompletion();
        } else {
            bookmarkImportWizardPage.setModel(new BookmarkModel());
            bookmarkImportWizardPage.setImportFile(null);
            bookmarkImportWizardPage.setPageComplete(false);
        }

    }

    private void setFileContentForViewer(File file) {
        if (isValid()) {
            BookmarkModel model = BookmarkIO.load(file);
            bookmarkImportWizardPage.setModel(model);
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