package org.eclipselabs.recommenders.bookmark.copyCutPaste;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.corext.refactoring.reorg.JavaElementTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.commands.AddTreepathsToExistingBookmark;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeValueConverter;
import org.eclipselabs.recommenders.bookmark.view.BookmarkView;
import org.eclipselabs.recommenders.bookmark.view.ExpandedStorage;
import org.eclipselabs.recommenders.bookmark.view.ViewManager;

@SuppressWarnings("restriction")
public class PasteHandler
	extends AbstractHandler
{
	private final ViewManager manager;

	private final Transfer[] supportedTransfers = {
			BookmarkNodeTransfer.getInstance(),
			JavaElementTransfer.getInstance(), FileTransfer.getInstance() };

	private boolean createdNewCategoryNodeForPasteOperation = false;

	public PasteHandler(ViewManager manager)
	{
		this.manager = manager;
	}

	@Override
	public Object execute(ExecutionEvent event)
		throws ExecutionException
	{
		createdNewCategoryNodeForPasteOperation = false;
		Clipboard cb = new Clipboard(Display.getCurrent());

		iterateTransferHandlesOnClipboardData(cb);

		cb.dispose();

		updateExpandedStorage();

		return null;
	}

	private void updateExpandedStorage()
	{
		ViewManager manager = Activator.getManager();
		ExpandedStorage storage = manager.getExpandedStorage();

		storage.reinitialize();
		storage.addCurrentlyExpandedNodes();
	}

	private void iterateTransferHandlesOnClipboardData(Clipboard cb)
	{
		BMNode bookmarkOfTarget = getBookmark();
		if (bookmarkOfTarget == null) {
			return;
		}

		boolean somethingAdded = false;
		for (Transfer transfer : supportedTransfers) {
			Object object = cb.getContents(transfer);

			boolean added = false;
			if (object instanceof Object[]) {
				Object[] objects = (Object[]) object;
				added = processClipboardData(objects, bookmarkOfTarget);

				if (added) {
					somethingAdded = true;
				}
			}
		}

		if (newlyCreatedCategoryNodeHasNoChilds(somethingAdded)) {
			TreeUtil.unlink(bookmarkOfTarget);
		}

		updateView();

	}

	private boolean newlyCreatedCategoryNodeHasNoChilds(boolean somethingAdded)
	{
		return somethingAdded == false
				&& createdNewCategoryNodeForPasteOperation;
	}

	private BMNode getBookmark()
	{
		BMNode target = getTargetNode();

		if (target == null) {
			return null;
		}
		BMNode bookmarkOfTarget = TreeUtil.getBookmarkNode(target);

		return bookmarkOfTarget;
	}

	private BMNode getTargetNode()
	{
		TreeViewer viewer = manager.getActiveBookmarkView().getView();
		List<IStructuredSelection> selections = TreeUtil
				.getTreeSelections(viewer);

		if (selections.size() > 1)
			return null;

		BMNode target = null;
		if (selections.size() == 0) { // Also paste in empty area
			target = getTargetBasedOnViewsToggledState();
		}
		else {
			target = (BMNode) selections.get(0);
		}
		return target;
	}

	private BMNode getTargetBasedOnViewsToggledState()
	{
		BMNode target = null;
		if (manager.isViewToggled()) {
			TreeModel model = manager.getModel();
			target = model.getModelHead();
		}
		else {
			target = createNewBookmark();
			createdNewCategoryNodeForPasteOperation = true;
		}
		return target;
	}

	private boolean processClipboardData(Object[] objects,
			BMNode bookmarkOfTarget)
	{
		BookmarkView activeView = manager.getActiveBookmarkView();

		boolean bookmarks = attemptProcessingForBookmarkNodes(objects,
				activeView, bookmarkOfTarget);

		if (bookmarks) {
			return true;
		}

		boolean separatly = false;
		separatly = processObjectsSeparatly(objects, activeView,
				bookmarkOfTarget);

		if (separatly) {
			return true;
		}

		return false;
	}

	private boolean processObjectsSeparatly(Object[] dataset,
			BookmarkView activeView, BMNode bookmarkOfTarget)
	{
		boolean somethingAdded = false;
		for (Object data : dataset) {
			boolean success = false;
			success = attemptProcessingForIJavaElements(data, activeView,
					bookmarkOfTarget);

			if (success) {
				somethingAdded = true;
				continue;
			}

			success = attemptProcessingForStrings(data, activeView,
					bookmarkOfTarget);

			if (success) {
				somethingAdded = true;
			}
		}

		return somethingAdded;
	}

	private boolean attemptProcessingForStrings(Object data,
			BookmarkView activeView, BMNode bookmarkOfTarget)
	{
		if (data instanceof String) {
			return attemptProcessingForIFileStringRepresentation(data,
					activeView, bookmarkOfTarget);
		}
		return false;
	}

	private boolean attemptProcessingForIFileStringRepresentation(Object data,
			BookmarkView activeView, BMNode bookmarkOfTarget)
	{
		String dataString = (String) data;

		/*
		 * JavaElements are also provided as file path which will be casted to
		 * ifiles successfully. To prevent a double creation of files we look
		 * files with '.java' ending
		 */
		if (dataString.indexOf(".java") != -1) {
			return false;
		}

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IPath location = Path.fromOSString(dataString);
		IFile ifile = workspace.getRoot().getFileForLocation(location);

		if (ifile != null) {
			paste(ifile, activeView, bookmarkOfTarget);
			return true;
		}
		return false;
	}

	private boolean attemptProcessingForIJavaElements(Object data,
			BookmarkView activeView, BMNode bookmarkOfTarget)
	{
		if (data instanceof IJavaElement) {
			paste(data, activeView, bookmarkOfTarget);
			return true;
		}
		return false;
	}

	private boolean attemptProcessingForBookmarkNodes(Object[] objects,
			BookmarkView activeView, BMNode bookmarkOfTarget)
	{
		if (objects instanceof BookmarkNodeTransferObject[]) {

			BookmarkNodeTransferObject bms = ((BookmarkNodeTransferObject[]) objects)[0];

			for (String id : bms.ids) {
				Object restoredValue = restoreValue(id);
				paste(restoredValue, activeView, bookmarkOfTarget);
			}
			return true;
		}
		return false;
	}

	private void paste(Object data, BookmarkView activeView,
			BMNode bookmarkOfTarget)
	{
		TreePath path = new TreePath(new Object[] { data });
		TreePath[] treePath = new TreePath[] { path };
		new AddTreepathsToExistingBookmark(manager, bookmarkOfTarget, treePath)
				.execute();
	}

	private BMNode createNewBookmark()
	{
		BMNode bookmark = TreeUtil.makeBookmarkNode();
		TreeModel model = manager.getModel();
		model.getModelRoot().addChild(bookmark);
		return bookmark;
	}

	private void updateView()
	{
		BookmarkView activeView = manager.getActiveBookmarkView();
		ViewManager manager = activeView.getManager();
		if (manager.isViewFlattened()) {
			BMNode node = manager.getModel().getModelHead();
			manager.activateFlattenedModus(node);
		}
		activeView.getView().refresh(true);
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

}
