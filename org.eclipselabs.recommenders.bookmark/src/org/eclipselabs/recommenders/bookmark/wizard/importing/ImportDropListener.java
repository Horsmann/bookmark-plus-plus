package org.eclipselabs.recommenders.bookmark.wizard.importing;

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipselabs.recommenders.bookmark.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.view.BookmarkCommandInvoker;
import org.eclipselabs.recommenders.bookmark.view.BookmarkTreeDropListener;
import org.eclipselabs.recommenders.bookmark.wizard.ImportSelectedBookmarksCommand;

import com.google.common.base.Optional;

public class ImportDropListener extends BookmarkTreeDropListener {

    public ImportDropListener(BookmarkCommandInvoker commandInvoker) {
        super(commandInvoker);
    }

    @Override
    public void drop(final DropTargetEvent event) {
        final Optional<IBookmarkModelComponent> dropTarget = getDropTarget(event);

        boolean insertDropBeforeTarget = determineInsertLocation(event);

        ISelection selections = LocalSelectionTransfer.getTransfer().getSelection();
        if (selections instanceof IStructuredSelection) {
            processStructuredSelection(dropTarget, (IStructuredSelection) selections, isCopyOperation(event),
                    insertDropBeforeTarget);
        }

    }

    protected void processDroppedElementOriginatedFromInsideTheView(Optional<IBookmarkModelComponent> dropTarget,
            IBookmarkModelComponent[] components, boolean isCopyOperation, boolean insertDropBeforeTarget) {

        if (!causeRecursion(components, dropTarget)) {

                commandInvoker.invoke(new ImportSelectedBookmarksCommand(components, commandInvoker,
                        isCopyOperation, insertDropBeforeTarget, dropTarget));
        }
    }
}
