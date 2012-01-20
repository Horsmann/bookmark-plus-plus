package org.eclipselabs.recommenders.bookmark.aaa.action;

import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.part.ViewPart;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.aaa.BookmarkCommandInvoker;
import org.eclipselabs.recommenders.bookmark.aaa.commands.OpenBookmarkCommand;
import org.eclipselabs.recommenders.bookmark.aaa.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.aaa.tree.RepresentationSwitchableTreeViewer;

public class OpenBookmarkAction extends Action implements SelfEnabling {

    private final RepresentationSwitchableTreeViewer treeViewer;
    private final ViewPart part;
    private final BookmarkCommandInvoker commandInvoker;

    public OpenBookmarkAction(RepresentationSwitchableTreeViewer treeViewer, ViewPart part,
            BookmarkCommandInvoker commandInvoker) {
        this.treeViewer = treeViewer;
        this.part = part;
        this.commandInvoker = commandInvoker;
        this.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.ICON_SHOW_IN_EDITOR));
        this.setToolTipText("Opens the referenced object in its default editor");
        this.setText("Show in Editor");
        this.setEnabled(false);
    }

    @Override
    public void run() {

        IWorkbenchPage activePage = part.getSite().getWorkbenchWindow().getActivePage();
        commandInvoker.invoke(new OpenBookmarkCommand(treeViewer.getSelections(), activePage));
    }

    @Override
    public void updateEnableStatus() {
        IStructuredSelection selections = treeViewer.getSelections();
        if (selections.isEmpty() || isNotSupportedType(selections)) {
            this.setEnabled(false);
            return;
        }

        this.setEnabled(true);
    }

    private boolean isNotSupportedType(IStructuredSelection selections) {

        @SuppressWarnings("rawtypes")
        Iterator iterator = selections.iterator();

        while (iterator.hasNext()) {
            if (!(iterator.next() instanceof IBookmarkModelComponent)) {
                return true;
            }
        }

        return false;
    }

}
