package org.eclipselabs.recommenders.bookmark.exportWizards;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.tree.commands.AddTreeNodesToExistingBookmark;
import org.eclipselabs.recommenders.bookmark.tree.persistent.deserialization.TreeDeserializerFacade;
import org.eclipselabs.recommenders.bookmark.tree.persistent.serialization.TreeSerializerFacade;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.view.ViewManager;

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
		BMNode root = new TreeNode("", false, true);

		// selected category nodes
		LinkedList<BMNode> listOfSelectedCategories = new LinkedList<BMNode>();

		for (int i = 0; i < treeSelections.size(); i++) {
			BMNode node = (BMNode) treeSelections.get(i);

			if (node.isBookmarkNode()) {
				listOfSelectedCategories.add(node);
			}
		}

		// single nodes which are not below one of the selected categories and
		// need to be pressed separatly
		LinkedList<BMNode> singleSelectedNodes = new LinkedList<BMNode>();

		BMNode[] selectedCategories = listOfSelectedCategories
				.toArray(new BMNode[0]);
		for (int i = 0; i < treeSelections.size(); i++) {
			BMNode node = (BMNode) treeSelections.get(i);

			if (node.isBookmarkNode()) {
				continue;
			}

			// compare remaining nodes whether they are below a selected
			// category or not,
			// if they are, do nothing since the entire category will be
			// exported anyway
			// if they are not store them separatly
			boolean isContainedInAlreadySelectedCat = false;
			for (BMNode category : selectedCategories) {
				isContainedInAlreadySelectedCat = TreeUtil.isDescendant(
						category, node);
				if (isContainedInAlreadySelectedCat) {
					break;
				}
			}

			if (!isContainedInAlreadySelectedCat) {
				singleSelectedNodes.add(node);
			}
		}
		
		//copy entire bookmarks add to root 
		for (BMNode cat : listOfSelectedCategories){
			BMNode copy = TreeUtil.copyTreeBelowNode(cat, true);
			root.addChild(copy);
		}
		
		
		BMNode bm;
		LinkedList<BMNode> shareSameBM;
		while(!singleSelectedNodes.isEmpty()) {
			BMNode select = singleSelectedNodes.pollFirst();
			bm = TreeUtil.getBookmarkNode(select);
			shareSameBM = new LinkedList<BMNode>();
			shareSameBM.add(select);
			
			for (BMNode remain : singleSelectedNodes){
				BMNode remainBM = TreeUtil.getBookmarkNode(remain);
				if (remainBM == bm){
					shareSameBM.add(remain);
				}
			}
			
			//delete
			for (BMNode node:shareSameBM){
				singleSelectedNodes.remove(node);
			}
			
			if (!shareSameBM.isEmpty()) {
				BMNode shared = shareSameBM.pollFirst();
				BMNode newBookmark= TreeUtil.copyTreeBelowNode(shared, true);
				while(!shareSameBM.isEmpty()) {
					BMNode next = shareSameBM.pollFirst();
					BMNode copy = TreeUtil.copyTreeBelowNode(next, false);

					BMNode merged = null;
					if ((merged = TreeUtil.attemptMerge(newBookmark, copy)) == null) {
						newBookmark.addChild(copy);
					}
					
					
				}
				root.addChild(newBookmark);
			}
			
		}
		
		TreeModel model = new TreeModel();
		model.setModelRoot(root);
		TreeSerializerFacade.serialize(model, null, file);
		
	}

	private void exportEntireModelToFile(File file)
	{
		ViewManager manager = Activator.getManager();
		TreeModel model = manager.getModel();
		Object[] expanded = manager.getNodesFromExpandedStorage();

		TreeSerializerFacade.serialize(model, expanded, file);
	}

}
