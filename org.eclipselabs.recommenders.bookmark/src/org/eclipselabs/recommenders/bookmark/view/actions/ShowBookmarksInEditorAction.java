package org.eclipselabs.recommenders.bookmark.view.actions;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.commands.CloseAllOpenEditors;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.util.ResourceAvailabilityValidator;

public class ShowBookmarksInEditorAction
	extends Action
	implements SelfEnabling
{

	private TreeViewer viewer;
	private ViewPart part;

	public ShowBookmarksInEditorAction(ViewPart part, TreeViewer viewer)
	{
		this.viewer = viewer;
		this.part = part;

		this.setImageDescriptor(Activator.getDefault().getImageRegistry()
				.getDescriptor(Activator.ICON_SHOW_IN_EDITOR));
		this.setToolTipText("Opens the referenced object in its default editor");
		this.setText("Show in Editor");
		this.setEnabled(false);
	}

	@Override
	public void run()
	{

		List<IStructuredSelection> selectedList = TreeUtil
				.getTreeSelections(viewer);
		for (int i = 0; i < selectedList.size(); i++) {
			BMNode node = (BMNode) selectedList.get(i);

			if (node.isBookmarkNode()) {

				checkPreferenceStore();

				openAllEntriesOfBookmark(node);
			}
			else {

				if (!isReferencedResourceAvailable(node)) {
					continue;
				}
				openSingleEntry(node);
			}
		}

	}

	private void checkPreferenceStore()
	{
		boolean closeEditorWindows = closeEditorWindows();

		if (closeEditorWindows) {
			new CloseAllOpenEditors().execute();
		}

	}

	private boolean closeEditorWindows()
	{
		IPreferenceStore preferenceStore = Activator.getDefault()
				.getPreferenceStore();
		boolean closeEditorWindows = preferenceStore
				.getBoolean(org.eclipselabs.recommenders.bookmark.preferences.PreferenceConstants.CLOSE_ALL_OPEN_EDITOR_WINDOWS_IF_BOOKMARK_CATEGORY_IS_OPENED);
		return closeEditorWindows;
	}

	private boolean isReferencedResourceAvailable(BMNode node)
	{

		return ResourceAvailabilityValidator.isResourceAvailable(node
				.getValue());
	}

	private void openAllEntriesOfBookmark(BMNode bookmark)
	{
		for (BMNode child : bookmark.getChildren()) {
			LinkedList<BMNode> leafs = TreeUtil.getLeafs(child);
			for (BMNode leaf : leafs)
				openInEditor(leaf);
		}
	}

	private void openSingleEntry(BMNode node)
	{
		openInEditor(node);
	}

	private void openInEditor(BMNode node)
	{
		try {
			if (node.getValue() instanceof IJavaElement) {
				IEditorPart part = JavaUI.openInEditor((IJavaElement) node
						.getValue());
				JavaUI.revealInEditor(part, (IJavaElement) node.getValue());
			}

			if (node.getValue() instanceof IFile) {
				IFile file = (IFile) node.getValue();
				IDE.openEditor(part.getViewSite().getWorkbenchWindow()
						.getActivePage(), file);
			}
		}
		catch (PartInitException e) {
			e.printStackTrace();
		}
		catch (JavaModelException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateEnabledStatus()
	{
		List<IStructuredSelection> list = TreeUtil.getTreeSelections(viewer);
		if (list.size() == 0) {
			this.setEnabled(false);
			return;
		}

		this.setEnabled(true);
	}

}
