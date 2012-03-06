package org.eclipselabs.recommenders.bookmark.wizard.importing;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.view.BookmarkIO;

public class BookmarkImportWizard extends Wizard implements IImportWizard {

    BookmarkImportWizardPage mainPage;

    public BookmarkImportWizard() {
        super();
    }

    public boolean performFinish() {
        BookmarkModel newModel = mainPage.getClonedModel();
        BookmarkIO.writeModel(Activator.getLocationForStoringBookmark(), newModel);
        Activator.setNewModel(newModel);
        return true;
    }

    public void init(IWorkbench workbench, IStructuredSelection selection) {
        setNeedsProgressMonitor(true);
        mainPage = new BookmarkImportWizardPage("Import");

    }

    @Override
    public void addPages() {
        super.addPages();
        addPage(mainPage);
    }

}
