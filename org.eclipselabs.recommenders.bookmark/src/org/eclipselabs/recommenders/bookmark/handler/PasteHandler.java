package org.eclipselabs.recommenders.bookmark.handler;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.corext.refactoring.reorg.JavaElementTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ResourceTransfer;
import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.tree.commands.AddTreeNodesToExistingBookmark;
import org.eclipselabs.recommenders.bookmark.tree.commands.AddTreepathsToExistingBookmarkCommand;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.view.BookmarkView;

public class PasteHandler extends AbstractHandler {

	private BookmarkView activeView;

	public PasteHandler(BookmarkView activeView) {
		this.activeView = activeView;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		Clipboard cb = new Clipboard(Display.getCurrent());

		JavaElementTransfer javaEleTransfer = JavaElementTransfer.getInstance();
		Object clipBoardContent = cb.getContents(javaEleTransfer);

		if (clipBoardContent instanceof Object[]) {
			Object[] clipBoardData = (Object[]) clipBoardContent;
			processJavaElement(clipBoardData);
		}

		FileTransfer fileTransfer = FileTransfer.getInstance();
		clipBoardContent = cb.getContents(fileTransfer);

		if (clipBoardContent instanceof Object[]) {
			Object[] clipBoardData = (Object[]) clipBoardContent;
			processFiles(clipBoardData);
		}

		cb.dispose();
		return null;
	}

	private void processFiles(Object[] clipBoardData) {
		List<IStructuredSelection> selections = TreeUtil
				.getTreeSelections(activeView.getView());

		if (selections.size() != 1)
			return;

		TreeNode target = (TreeNode) selections.get(0);

		for (Object data : clipBoardData) {

			if (data instanceof IFile) {
				TreeNode node = new TreeNode(data, false, false);
				TreeNode bookmarkOfTarget = TreeUtil.getBookmarkNode(target);
				new AddTreeNodesToExistingBookmark(activeView,
						bookmarkOfTarget, node, true).execute();
			}
		}

	}

	private void processJavaElement(Object[] clipBoardData) {
		List<IStructuredSelection> selections = TreeUtil
				.getTreeSelections(activeView.getView());

		if (selections.size() != 1)
			return;

		TreeNode target = (TreeNode) selections.get(0);

		for (Object data : clipBoardData) {

			if (data instanceof IJavaElement) {
				TreePath[] path = new TreePath[1];
				path[0] = new TreePath(new Object[] { data });

				TreeNode bookmarkOfTarget = TreeUtil.getBookmarkNode(target);
				new AddTreepathsToExistingBookmarkCommand(activeView,
						bookmarkOfTarget, path).execute();

			}
		}

	}

}
