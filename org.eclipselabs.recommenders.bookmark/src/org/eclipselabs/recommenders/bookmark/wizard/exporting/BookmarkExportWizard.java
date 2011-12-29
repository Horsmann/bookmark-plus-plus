package org.eclipselabs.recommenders.bookmark.wizard.exporting;

import java.io.File;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.persistent.serialization.TreeSerializerFacade;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.view.ViewManager;
import org.eclipselabs.recommenders.bookmark.wizard.WizardUtil;

public class BookmarkExportWizard
	extends Wizard
	implements IExportWizard
{

	private BookmarkExportWizardPage mainPage;

	public BookmarkExportWizard()
	{
		super();
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection)
	{
		mainPage = new BookmarkExportWizardPage("Export Bookmarks");
	}

	@Override
	public void addPages()
	{
		super.addPages();
		addPage(mainPage);
	}

	@Override
	public boolean performFinish()
	{
		File file = mainPage.getExportFile();
		TreeViewer viewer = mainPage.getView();
		List<IStructuredSelection> treeSelections = TreeUtil
				.getTreeSelections(viewer);
		if (file == null) {
			return false;
		}

		if (treeSelections.isEmpty()) {
			exportEntireModelToFile(file);
		}
		else {
			exportSelectedNodesToFile(file, treeSelections);
		}

		return true;
	}

	private void exportSelectedNodesToFile(File file,
			List<IStructuredSelection> treeSelections)
	{
		BMNode[] selectedNodes = WizardUtil.getSelectedNodes(treeSelections);
		BMNode root = WizardUtil.buildTreeConsistingOfSelection(selectedNodes);

		export(root, file);

	}

	private void export(BMNode root, File file)
	{
		TreeModel model = new TreeModel();
		model.setModelRoot(root);

		Object[] expanded = TreeUtil.getTreeBelowNode(root);

		TreeSerializerFacade.serialize(model, expanded, file);
	}

	private void exportEntireModelToFile(File file)
	{
		ViewManager manager = Activator.getManager();
		TreeModel model = manager.getModel();
		Object[] expanded = manager.getNodesFromExpandedStorage();

		TreeSerializerFacade.serialize(model, expanded, file);
	}

}
