package org.eclipselabs.recommenders.bookmark.wizard;

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipselabs.recommenders.bookmark.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.view.BookmarkCommandInvoker;
import org.eclipselabs.recommenders.bookmark.view.DefaultDropStrategy;

import com.google.common.base.Optional;

public class WizardDropStrategy extends DefaultDropStrategy {

    public WizardDropStrategy(BookmarkCommandInvoker commandInvoker) {
        super(commandInvoker);
    }

    @Override
    public void performDrop(DropTargetEvent event) {
        
        setDropOperation(event);
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

            commandInvoker.invoke(new ImportSelectedBookmarksCommand(components, commandInvoker, isCopyOperation,
                    insertDropBeforeTarget, dropTarget));
        }
    }

}
