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
package org.eclipselabs.recommenders.bookmark.importWizards;

import java.io.File;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

public class BookmarkImportWizard
	extends Wizard
	implements IImportWizard
{

	BookmarkImportWizardPage mainPage;

	public BookmarkImportWizard()
	{
		super();
	}

	public boolean performFinish()
	{
		File file = mainPage.getSelectedFile();
		if (file == null)
			return false;
		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection)
	{
		setWindowTitle("File Import Wizard");
		setNeedsProgressMonitor(true);
		mainPage = new BookmarkImportWizardPage("Import Bookmarks");
	}

	public void addPages()
	{
		super.addPages();
		addPage(mainPage);
	}

}
