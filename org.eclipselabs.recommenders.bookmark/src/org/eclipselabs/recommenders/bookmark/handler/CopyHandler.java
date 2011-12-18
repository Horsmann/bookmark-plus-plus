package org.eclipselabs.recommenders.bookmark.handler;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipselabs.recommenders.bookmark.tree.BMNode;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeUtil;
import org.eclipselabs.recommenders.bookmark.tree.util.TreeValueConverter;
import org.eclipselabs.recommenders.bookmark.view.ViewManager;

public class CopyHandler
	extends AbstractHandler
{

	private ViewManager manager;

	private BookmarkNodeTransfer bmTransfer = BookmarkNodeTransfer
			.getInstance();

	public CopyHandler(ViewManager manager)
	{
		this.manager = manager;
	}

	@Override
	public Object execute(ExecutionEvent event)
		throws ExecutionException
	{
		TreeViewer viewer = manager.getActiveBookmarkView().getView();
		List<IStructuredSelection> selections = TreeUtil
				.getTreeSelections(viewer);

		LinkedList<BookmarkNodeTransferObject> transferObjects = new LinkedList<BookmarkNodeTransferObject>();
		LinkedList<Transfer> transferHandles = new LinkedList<Transfer>();
		for (int i = 0; i < selections.size(); i++) {
			BMNode node = (BMNode) selections.get(i);
			node = TreeUtil.getReference(node);

			if (node.isBookmarkNode()) {
				continue;
			}
			Object value = node.getValue();
			String id = TreeValueConverter.getStringIdentification(value);
			BookmarkNodeTransferObject bm = new BookmarkNodeTransferObject(id);
			transferObjects.add(bm);
			transferHandles.add(bmTransfer);

		}

		Clipboard cb = new Clipboard(Display.getCurrent());
		Object[] data = transferObjects.toArray();
		Transfer[] transfer = transferHandles.toArray(new Transfer[0]);
		cb.setContents(data, transfer);
		cb.dispose();
		return null;
	}

}
