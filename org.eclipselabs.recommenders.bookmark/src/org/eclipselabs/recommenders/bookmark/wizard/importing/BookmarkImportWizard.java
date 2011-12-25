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
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.persistent.BookmarkFileIO;
import org.eclipselabs.recommenders.bookmark.tree.persistent.deserialization.RestoredTree;
import org.eclipselabs.recommenders.bookmark.tree.persistent.deserialization.TreeDeserializerFacade;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.view.BookmarkView;
import org.eclipselabs.recommenders.bookmark.view.ViewManager;
import org.eclipselabs.recommenders.bookmark.wizard.WizardUtil;

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
		File file = mainPage.getImportFile();
		TreeViewer viewer = mainPage.getView();
		List<IStructuredSelection> treeSelections = TreeUtil
				.getTreeSelections(viewer);
		if (file == null)
			return false;

		performImport(file, treeSelections);

		return true;
	}

	private void performImport(File file,
			List<IStructuredSelection> treeSelections)
	{

		if (treeSelections.isEmpty()) {
			importAll(file);
		}
		else {
			importSelected(file, treeSelections);
		}

	}

	private void importSelected(File file,
			List<IStructuredSelection> treeSelections)
	{
		BMNode root = WizardUtil.buildTreeConsistingOfSelection(treeSelections);

		ViewManager manager = Activator.getManager();
		TreeModel model = manager.getModel();
		for (BMNode bookmark : root.getChildren()) {
			model.getModelRoot().addChild(bookmark);
		}

		Object[] nodesToExpand = TreeUtil.getTreeBelowNode(root);

		TreeViewer viewer = manager.getActiveBookmarkView().getView();
		for (Object o : nodesToExpand) {
			TreeUtil.showNodeExpanded(viewer, (BMNode) o);
		}
	}

	private void importAll(File file)
	{
		String[] data = BookmarkFileIO.readFromFile(file);

		if (data.length < 1) {
			return;
		}

		String serializedTree = data[0];
		RestoredTree deserialized = TreeDeserializerFacade
				.deserialize(serializedTree);

		addBookmarksToCurrentModel(deserialized);

		BMNode[] expanded = deserialized.getExpanded();
		updateView(expanded);

	}

	private void addBookmarksToCurrentModel(RestoredTree deserialized)
	{
		ViewManager manager = Activator.getManager();
		TreeModel model = manager.getModel();
		for (BMNode child : deserialized.getRoot().getChildren()) {
			model.getModelRoot().addChild(child);
		}
	}

	private void updateView(BMNode[] expanded)
	{
		ViewManager manager = Activator.getManager();
		BookmarkView bmView = manager.getActiveBookmarkView();

		if (manager.isViewFlattened()) {
			updateFlatView(expanded);
		}
		else {

			for (BMNode expand : expanded) {
				TreeUtil.showNodeExpanded(bmView.getView(), expand);
			}
		}

		bmView.updateControls();

	}

	private void updateFlatView(BMNode[] expanded)
	{
		ViewManager manager = Activator.getManager();
		TreeModel model = manager.getModel();
		BMNode head = model.getModelHead();
		manager.activateFlattenedModus(head);

		// there won't be any expanded nodes in the flat view, place the
		// restored-expanded nodes in the storage for a later switch-back to the
		// tree-representation
		for (BMNode expand : expanded) {
			manager.addNodeToExpandedStorage(expand);
		}
	}

	public void init(IWorkbench workbench, IStructuredSelection selection)
	{
		setWindowTitle("File Import Wizard");
		setNeedsProgressMonitor(true);
		mainPage = new BookmarkImportWizardPage("Import Bookmarks");
	}

	@Override
	public void addPages()
	{
		super.addPages();
		addPage(mainPage);
	}

}
