package org.eclipselabs.recommenders.bookmark.copyPaste;

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
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.TreeModel;
import org.eclipselabs.recommenders.bookmark.tree.commands.AddTreepathsToExistingBookmarkCommand;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeValueConverter;
import org.eclipselabs.recommenders.bookmark.view.BookmarkView;
import org.eclipselabs.recommenders.bookmark.view.ViewManager;

@SuppressWarnings("restriction")
public class PasteHandler
	extends AbstractHandler
{
	private final ViewManager manager;

	private final Transfer[] supportedTransfers = {
			BookmarkNodeTransfer.getInstance(),
			JavaElementTransfer.getInstance(), FileTransfer.getInstance() };

	public PasteHandler(ViewManager manager)
	{
		this.manager = manager;
	}

	@Override
	public Object execute(ExecutionEvent event)
		throws ExecutionException
	{

		Clipboard cb = new Clipboard(Display.getCurrent());

		processAvailableClipboardData(cb);

		cb.dispose();
		return null;
	}

	private void processAvailableClipboardData(Clipboard cb)
	{
		for (Transfer transfer : supportedTransfers) {
			Object object = cb.getContents(transfer);
			if (object instanceof Object[]) {
				Object[] objects = (Object[]) object;
				performPaste(objects);
			}
		}

	}

	private void performPaste(Object[] objects)
	{
		BMNode target = getTargetNode();

		if (target == null) {
			return;
		}

		processClipboardData(objects, target);

		updateView();
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
		}
		return target;
	}

	private void processClipboardData(Object[] objects, BMNode target)
	{
		BookmarkView activeView = manager.getActiveBookmarkView();
		target = TreeUtil.getReference(target);
		BMNode bookmarkOfTarget = TreeUtil.getBookmarkNode(target);

		attemptProcessingForBookmarkNodes(objects, activeView, bookmarkOfTarget);

		if (objects instanceof Object[]) {
			Object[] dataset = (Object[]) objects;
			for (Object data : dataset) {
				attemptProcessingForIJavaElements(data, activeView,
						bookmarkOfTarget);
				attemptProcessingForStrings(data, activeView, bookmarkOfTarget);
			}
		}
	}

	private void attemptProcessingForStrings(Object data,
			BookmarkView activeView, BMNode bookmarkOfTarget)
	{
		if (data instanceof String) {
			attemptProcessingForIFileStringRepresentation(data, activeView,
					bookmarkOfTarget);
		}
	}

	private void attemptProcessingForIFileStringRepresentation(Object data,
			BookmarkView activeView, BMNode bookmarkOfTarget)
	{
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IPath location = Path.fromOSString((String) data);
		IFile ifile = workspace.getRoot().getFileForLocation(location);

		if (ifile != null) {
			paste(ifile, activeView, bookmarkOfTarget);
		}
	}

	private void attemptProcessingForIJavaElements(Object data,
			BookmarkView activeView, BMNode bookmarkOfTarget)
	{
		if (data instanceof IJavaElement) {
			paste(data, activeView, bookmarkOfTarget);
		}
	}

	private void attemptProcessingForBookmarkNodes(Object[] objects,
			BookmarkView activeView, BMNode bookmarkOfTarget)
	{
		if (objects instanceof BookmarkNodeTransferObject[]) {

			BookmarkNodeTransferObject bms = ((BookmarkNodeTransferObject[]) objects)[0];

			for (String id : bms.ids) {
				Object restoredValue = restoreValue(id);
				paste(restoredValue, activeView, bookmarkOfTarget);
			}
		}

	}

	private void paste(Object data, BookmarkView activeView,
			BMNode bookmarkOfTarget)
	{
		TreePath path = new TreePath(new Object[] { data });
		TreePath[] treePath = new TreePath[] { path };
		new AddTreepathsToExistingBookmarkCommand(activeView, bookmarkOfTarget,
				treePath).execute();
	}

	private BMNode createNewBookmark()
	{
		BookmarkView activeView = manager.getActiveBookmarkView();
		BMNode bookmark = TreeUtil.makeBookmarkNode();
		TreeModel model = activeView.getModel();
		model.getModelRoot().addChild(bookmark);
		return bookmark;
	}

	private void updateView()
	{
		BookmarkView activeView = manager.getActiveBookmarkView();
		ViewManager manager = activeView.getManager();
		if (manager.isViewFlattened()) {
			BMNode node = activeView.getModel().getModelHead();
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
