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
import org.eclipse.swt.widgets.Display;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.commands.AddTreepathsToExistingBookmarkCommand;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeValueConverter;
import org.eclipselabs.recommenders.bookmark.view.BookmarkView;
import org.eclipselabs.recommenders.bookmark.view.ViewManager;

@SuppressWarnings("restriction")
public class PasteHandler
	extends AbstractHandler
{

	private BookmarkView activeView;

	public PasteHandler(BookmarkView activeView)
	{
		this.activeView = activeView;
	}

	@Override
	public Object execute(ExecutionEvent event)
		throws ExecutionException
	{

		Clipboard cb = new Clipboard(Display.getCurrent());

		processInternalBookmarkTransfer(cb);

		processExternalJavaElementTransfer(cb);

		cb.dispose();
		return null;
	}

	private void processExternalJavaElementTransfer(Clipboard cb)
	{
		JavaElementTransfer javaEleTransfer = JavaElementTransfer.getInstance();
		Object clipBoardContent = cb.getContents(javaEleTransfer);

		if (clipBoardContent instanceof Object[]) {
			Object[] clipBoardData = (Object[]) clipBoardContent;
			processJavaElement(clipBoardData);
		}
	}

	private void processInternalBookmarkTransfer(Clipboard cb)
	{
		BookmarkNodeTransfer bmTransfer = BookmarkNodeTransfer.getInstance();
		Object bms = cb.getContents(bmTransfer);

		if (bms instanceof BookmarkNodeTransferObject[]) {
			BookmarkNodeTransferObject[] bookmarkObjects = (BookmarkNodeTransferObject[]) bms;
			BookmarkNodeTransferObject content = bookmarkObjects[0];
			processBookmarViewItems(content);
		}
	}

	private void processBookmarViewItems(
			BookmarkNodeTransferObject bookmarkObjects)
	{
		List<IStructuredSelection> selections = TreeUtil
				.getTreeSelections(activeView.getView());

		if (selections.size() != 1)
			return;

		BMNode target = (BMNode) selections.get(0);

		for (String id : bookmarkObjects.ids) {
			Object restoredValue = restoreValue(id);
			BMNode bookmarkOfTarget = TreeUtil.getBookmarkNode(target);

			TreePath path = new TreePath(new Object[] { restoredValue });
			TreePath[] treePath = new TreePath[] { path };
			new AddTreepathsToExistingBookmarkCommand(activeView,
					bookmarkOfTarget, treePath).execute();

			updateView();

		}

	}

	private void updateView()
	{
		ViewManager manager = activeView.getManager();
		if (manager.isViewFlattened()) {
			BMNode node = activeView.getModel().getModelHead();
			manager.activateFlattenedView(node);
		}

	}

	private Object restoreValue(String id)
	{
		IJavaElement element = TreeValueConverter
				.attemptTransformationToIJavaElement(id);
		if (element != null) {
			return element;
		}

		IFile ifile = TreeValueConverter.attemptTransformationToIFile(id);
		return ifile;
	}

	private void processJavaElement(Object[] clipBoardData)
	{
		List<IStructuredSelection> selections = TreeUtil
				.getTreeSelections(activeView.getView());

		if (selections.size() != 1)
			return;

		BMNode target = (BMNode) selections.get(0);

		for (Object data : clipBoardData) {

			if (data instanceof IJavaElement) {
				TreePath[] path = new TreePath[1];
				path[0] = new TreePath(new Object[] { data });

				BMNode bookmarkOfTarget = TreeUtil.getBookmarkNode(target);
				new AddTreepathsToExistingBookmarkCommand(activeView,
						bookmarkOfTarget, path).execute();

			}
		}

	}

}
