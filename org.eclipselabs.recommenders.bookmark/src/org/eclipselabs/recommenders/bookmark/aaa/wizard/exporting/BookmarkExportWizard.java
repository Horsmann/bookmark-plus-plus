package org.eclipselabs.recommenders.bookmark.aaa.wizard.exporting;

import java.io.File;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipselabs.recommenders.bookmark.Activator;

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
	    System.out.println("perform finished");
//		File file = mainPage.getExportFile();
//		TreeViewer viewer = mainPage.getView();
//		List<IStructuredSelection> treeSelections = TreeUtil
//				.getTreeSelections(viewer);
//		if (file == null) {
//			return false;
//		}
//
//		if (treeSelections.isEmpty()) {
//			exportEntireModelToFile(file);
//		}
//		else {
//			exportSelectedNodesToFile(file, treeSelections);
//		}

		return true;
	}

//	private void exportSelectedNodesToFile(File file,
//			List<IStructuredSelection> treeSelections)
//	{
//		BMNode[] selectedNodes = WizardUtil.getSelectedNodes(treeSelections);
//		BMNode root = WizardUtil.buildTreeConsistingOfSelection(selectedNodes);
//
//		export(root, file);
//
//	}
//
//	private void export(BMNode root, File file)
//	{
//		TreeModel model = new TreeModel();
//		model.setModelRoot(root);
//
//		Object[] expanded = TreeUtil.getTreeBelowNode(root);
//
//		TreeSerializerFacade.serialize(model, expanded, file, null);
//	}
//
//	private void exportEntireModelToFile(File file)
//	{
//		ViewManager manager = Activator.getManager();
//		TreeModel model = manager.getModel();
//		ExpandedStorage storage = manager.getExpandedStorage();
//		
//		Object[] expanded = storage.getExpandedNodes();
//
//		TreeSerializerFacade.serialize(model, expanded, file, null);
//	}

}
