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
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeValueConverter;
import org.eclipselabs.recommenders.dialog.ImportDialog;

public class BookmarkImportWizardPage
	extends WizardPage
{

	private Composite container;
	private Text textField;
	private Button button;

	// private FileFieldEditor fileFieldEditor;

	protected BookmarkImportWizardPage(String pageName)
	{
		super(pageName, pageName, null);
		setDescription("Imports the bookmark provided in an external file");
	}

	@Override
	public void createControl(Composite parent)
	{

		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		container.setLayout(layout);

		// fileFieldEditor = new FileFieldEditor("Import Bookmark",
		// "Select file",
		// container);
		// fileFieldEditor.setFileExtensions(new String[] { "*.bm" });

		textField = new Text(container, SWT.BORDER);
		GridData data = new GridData(GridData.GRAB_HORIZONTAL);
		textField.setLayoutData(data);

		data = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		button = new Button(container, SWT.NONE);
		button.setText("Select File");
		button.setLayoutData(data);

		button.addListener(SWT.MouseDown, new Listener()
		{

			@Override
			public void handleEvent(Event event)
			{
				File file = ImportDialog.showDialog();
				IFile iFile = TreeValueConverter
						.attemptTransformationToIFile(file.getAbsolutePath());

				if (iFile != null) {
					textField.setText(iFile.getFullPath().toOSString());
					setPageComplete(true);
				}

			}
		});

		// Required to avoid an error in the system
		setControl(container);
		setPageComplete(false);

	}

	public File getSelectedFile()
	{
		System.out.println("getSelectedFile()");
		return null;
	}

}
