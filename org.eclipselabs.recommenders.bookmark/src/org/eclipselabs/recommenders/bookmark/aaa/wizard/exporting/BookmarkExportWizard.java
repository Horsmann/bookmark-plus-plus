package org.eclipselabs.recommenders.bookmark.aaa.wizard.exporting;

import java.io.File;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipselabs.recommenders.bookmark.aaa.BookmarkIO;
import org.eclipselabs.recommenders.bookmark.aaa.model.BookmarkModel;

public class BookmarkExportWizard extends Wizard implements IExportWizard {

    private BookmarkExportWizardPage mainPage;

    public BookmarkExportWizard() {
        super();

    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        mainPage = new BookmarkExportWizardPage("Export Bookmarks");
    }

    @Override
    public void addPages() {
        super.addPages();
        addPage(mainPage);
    }

    @Override
    public boolean performFinish() {
        File exportFile = mainPage.getExportFile();
        BookmarkModel exportModel = mainPage.getExportModel();
        BookmarkIO.writeModel(exportFile, exportModel);
        return true;
    }

}
