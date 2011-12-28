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

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.view.tree.TreeContentProvider;
import org.eclipselabs.recommenders.bookmark.view.tree.TreeLabelProvider;

public class BookmarkImportWizardPage
	extends WizardPage
{

	private Composite container;
	private Text selectedFileTextField;
	private Text nameOfNewCategory;
	private Button button;
	private Button checkbox;
	private File file;
	private TreeViewer treeViewer;
	private FilePathModifyListener filePathListener;
	private NameOfCategoryTextFieldListener nameOfCategoryListener;
	private CheckboxListener checkBoxListener;

	private CompletionChecker checker;

	protected BookmarkImportWizardPage(String pageName)
	{
		super(pageName, pageName, null);
		setDescription("Imports the bookmark provided in an external file\nNo selection = import all");
	}

	@Override
	public void createControl(Composite parent)
	{

		initializeTwoColumnContainerComposite(parent);

		addRowWithTextFieldForLoadedFileAndBrowseButton();

		addTwoRowSpanningTreeViewer();

		addRowWithCheckboxAndNewNameTextfield();

		checker = new CompletionChecker(this, filePathListener,
				checkBoxListener, nameOfCategoryListener);

		// Required to avoid an error in the system
		setControl(container);
		setPageComplete(false);

	}
	
	public boolean consolidateBookmarksAsSingleBookmark() {
		return checkBoxListener.isEnabled();
	}
	
	public String getNameOfNewBookmarkName() {
		return nameOfNewCategory.getText();
	}

	public CompletionChecker getChecker()
	{
		return checker;
	}

	private void addRowWithCheckboxAndNewNameTextfield()
	{
		Composite subContainer = new Composite(container, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = true;
		GridData compData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		compData.horizontalSpan = 2;
		subContainer.setLayout(layout);
		subContainer.setLayoutData(compData);

		checkbox = new Button(subContainer, SWT.CHECK);
		checkbox.setText("Import as single category with name: ");
		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		checkbox.setLayoutData(data);

		nameOfNewCategory = new Text(subContainer, SWT.NONE);
		nameOfNewCategory.setText("Import");
		nameOfNewCategory.setEnabled(false);
		data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		nameOfNewCategory.setLayoutData(data);

		nameOfCategoryListener = new NameOfCategoryTextFieldListener(this,
				nameOfNewCategory);
		nameOfNewCategory.addModifyListener(nameOfCategoryListener);

		checkBoxListener = new CheckboxListener(this, nameOfNewCategory);
		checkbox.addListener(SWT.Selection, checkBoxListener);
	}

	private void addTwoRowSpanningTreeViewer()
	{
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.horizontalSpan = 2;
		treeViewer = new TreeViewer(container, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);
		treeViewer.setLabelProvider(new TreeLabelProvider(Activator
				.getManager()));
		treeViewer.setContentProvider(new TreeContentProvider());
		treeViewer.getTree().setLayoutData(data);
	}

	private void addRowWithTextFieldForLoadedFileAndBrowseButton()
	{
		selectedFileTextField = new Text(container, SWT.BORDER);
		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		data.grabExcessHorizontalSpace = true;
		selectedFileTextField.setLayoutData(data);

		data = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		button = new Button(container, SWT.NONE);
		button.setText("Select File");
		button.setLayoutData(data);

		button.addListener(SWT.MouseDown, new ButtonMouseDownListener(
				selectedFileTextField, this));

		filePathListener = new FilePathModifyListener(selectedFileTextField,
				this);
		selectedFileTextField.addListener(SWT.KeyUp, filePathListener);
	}

	private void initializeTwoColumnContainerComposite(Composite parent)
	{
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		GridData compData = new GridData(SWT.FILL, SWT.FILL, true, true);
		container.setLayout(layout);
		container.setLayoutData(compData);
	}

	public TreeViewer getView()
	{
		return treeViewer;
	}

	public void setImportFile(File file)
	{
		this.file = file;
	}

	public File getImportFile()
	{
		return file;
	}

}