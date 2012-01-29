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

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.model.BookmarkModel;

public class BookmarkImportWizard extends Wizard implements IImportWizard {

    BookmarkImportWizardPage mainPage;

    public BookmarkImportWizard() {
        super();
    }

    public boolean performFinish() {
        BookmarkModel newModel = mainPage.getClonedModel();
        Activator.setNewModel(newModel);
        return true;
    }


    public void init(IWorkbench workbench, IStructuredSelection selection) {
        setWindowTitle("File Import Wizard");
        setNeedsProgressMonitor(true);
        mainPage = new BookmarkImportWizardPage("Import Bookmarks");
    }

    @Override
    public void addPages() {
        super.addPages();
        addPage(mainPage);
    }

}
