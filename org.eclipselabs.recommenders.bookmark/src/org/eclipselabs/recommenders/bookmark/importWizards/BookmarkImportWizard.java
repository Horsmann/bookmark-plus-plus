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

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.persistent.BookmarkFileIO;
import org.eclipselabs.recommenders.bookmark.tree.persistent.deserialization.RestoredTree;
import org.eclipselabs.recommenders.bookmark.tree.persistent.deserialization.TreeDeserializerFacade;
import org.eclipselabs.recommenders.bookmark.view.ViewManager;

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
		IFile iFile = mainPage.getSelectedFile();
		if (iFile == null)
			return false;

		performImport(iFile);

		return true;
	}

	private void performImport(IFile iFile)
	{
		File file = iFile.getFullPath().toFile();
		String[] data = BookmarkFileIO.readFromFile(file);

		if (data.length < 1) {
			return;
		}

		String serializedTree = data[0];
		RestoredTree deserialize = TreeDeserializerFacade
				.deserialize(serializedTree);

		ViewManager manager = Activator.getManager();
		TreeModel model = manager.getModel();
		for (BMNode child : deserialize.getRoot().getChildren()) {
			model.getModelRoot().addChild(child);
		}

		manager.getActiveBookmarkView().updateControls();

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
