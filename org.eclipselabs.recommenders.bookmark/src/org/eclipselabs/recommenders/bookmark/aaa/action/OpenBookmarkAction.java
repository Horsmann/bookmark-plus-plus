package org.eclipselabs.recommenders.bookmark.aaa.action;

import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.part.ViewPart;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.aaa.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.aaa.tree.RepresentationSwitchableTreeViewer;
import org.eclipselabs.recommenders.bookmark.aaa.visitor.IsCategoryVisitor;
import org.eclipselabs.recommenders.bookmark.aaa.visitor.OpenInEditorVisitor;

public class OpenBookmarkAction extends Action implements SelfEnabling {

    private final RepresentationSwitchableTreeViewer treeViewer;
    private final ViewPart part;

    public OpenBookmarkAction(RepresentationSwitchableTreeViewer treeViewer, ViewPart part) {
        this.treeViewer = treeViewer;
        this.part = part;
        this.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.ICON_SHOW_IN_EDITOR));
        this.setToolTipText("Opens the referenced object in its default editor");
        this.setText("Show in Editor");
        this.setEnabled(false);
    }

    @Override
    public void run() {

        IStructuredSelection selections = treeViewer.getSelections();
        @SuppressWarnings("rawtypes")
        Iterator iterator = selections.iterator();
        while (iterator.hasNext()) {
            Object next = iterator.next();
            if (next instanceof IBookmarkModelComponent) {
                process((IBookmarkModelComponent) next);
            }
        }
    }

    private void process(IBookmarkModelComponent component) {

        IsCategoryVisitor visitor = new IsCategoryVisitor();
        component.accept(visitor);
        OpenInEditorVisitor openVisitor = null;
        IWorkbenchPage activePage = part.getSite().getWorkbenchWindow().getActivePage();
        if (visitor.isCategory()) {
            openVisitor = new OpenInEditorVisitor(activePage, true);
            component.accept(openVisitor);
        } else {
            openVisitor = new OpenInEditorVisitor(activePage, false);
            component.accept(openVisitor);
        }

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
