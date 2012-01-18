package org.eclipselabs.recommenders.bookmark.aaa.action;

import org.eclipse.jface.action.Action;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.aaa.tree.RepresentationSwitchableTreeViewer;

public class RenameCategoryAction extends Action {

    private final RepresentationSwitchableTreeViewer treeViewer;

    public RenameCategoryAction(RepresentationSwitchableTreeViewer treeViewer) {
        this.treeViewer = treeViewer;
        this.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.ICON_RENAME_CATEGORY));
        this.setToolTipText("Rename Category");
        this.setText("Rename Category");
    }

    @Override
    public void run() {
        treeViewer.editCurrentlySelectedRow();
    }
}
