package org.eclipselabs.recommenders.bookmark.copyCutPaste;

import java.util.LinkedList;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeValueConverter;
import org.eclipselabs.recommenders.bookmark.view.ViewManager;

public class CopyHandler
	extends AbstractHandler
{

	protected ViewManager manager;

	public CopyHandler(ViewManager manager)
	{
		this.manager = manager;
	}

	@Override
	public Object execute(ExecutionEvent event)
		throws ExecutionException
	{

		LinkedList<String> transferIDs = getIDsOfSelection();

		addDataToClipboard(transferIDs);

		return null;
	}

	private void addDataToClipboard(LinkedList<String> transferIDs)
	{
		Clipboard cb = new Clipboard(Display.getCurrent());

		Object[] data = prepareDataForAddingToClipboard(transferIDs);
		Transfer[] transfer = prepareTransferHandles();

		cb.setContents(data, transfer, DND.CLIPBOARD);

		cb.dispose();
	}

	private Transfer[] prepareTransferHandles()
	{
		return new Transfer[] { BookmarkNodeTransfer.getInstance() };
	}

	private Object[] prepareDataForAddingToClipboard(
			LinkedList<String> transferIDs)
	{
		String[] ids = transferIDs.toArray(new String[0]);
		BookmarkNodeTransferObject bm = new BookmarkNodeTransferObject(ids);
		Object[] data = new Object[] { bm };
		return data;
	}

	private LinkedList<String> getIDsOfSelection()
	{
		TreeViewer viewer = manager.getActiveBookmarkView().getView();
		BMNode[] selections = TreeUtil
				.getNodesFromModelThatAreSelectedInTreeViewer(viewer);

		LinkedList<String> transferObjects = new LinkedList<String>();
		for (BMNode node : selections) {

			if (node.isBookmarkNode()) {
				continue;
			}
			Object value = node.getValue();
			String id = TreeValueConverter.getStringIdentification(value);

			transferObjects.add(id);

			BMNode[] tree = TreeUtil.getTreeBelowNode(node);
			for (BMNode child : tree) {
				value = child.getValue();
				id = TreeValueConverter.getStringIdentification(value);
				transferObjects.add(id);
			}

		}
		return transferObjects;
	}

}
