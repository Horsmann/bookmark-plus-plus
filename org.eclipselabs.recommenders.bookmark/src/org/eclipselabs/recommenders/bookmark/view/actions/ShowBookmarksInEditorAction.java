package org.eclipselabs.recommenders.bookmark.view.actions;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.util.ResourceAvailabilityValidator;

public class ShowBookmarksInEditorAction extends Action {

	private TreeViewer viewer;
	private ViewPart part;

	public ShowBookmarksInEditorAction(ViewPart part, TreeViewer viewer) {
		this.viewer = viewer;
		this.part = part;

		this.setImageDescriptor(Activator.getDefault().getImageRegistry()
				.getDescriptor(Activator.ICON_SHOW_IN_EDITOR));
	}

	@Override
	public void run() {

		List<IStructuredSelection> selectedList = TreeUtil
				.getTreeSelections(viewer);
		for (int i = 0; i < selectedList.size(); i++) {
			TreeNode node = (TreeNode) selectedList.get(i);

			if (!isReferencedResourceAvailable(node)) {
				continue;
			}

			if (node.isBookmarkNode())
				openAllEntriesOfBookmark(node);
			else
				openSingleEntry(node);
		}

	}

	private boolean isReferencedResourceAvailable(TreeNode node) {

		return ResourceAvailabilityValidator.isResourceAvailable(node
				.getValue());
	}

	private void openAllEntriesOfBookmark(TreeNode bookmark) {
		for (TreeNode child : bookmark.getChildren()) {
			LinkedList<TreeNode> leafs = TreeUtil.getLeafs(child);
			for (TreeNode leaf : leafs)
				openInEditor(leaf);
		}
	}

	private void openSingleEntry(TreeNode node) {
		openInEditor(node);
	}

	private void openInEditor(TreeNode node) {
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
		} catch (PartInitException e) {
			e.printStackTrace();
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}

}
