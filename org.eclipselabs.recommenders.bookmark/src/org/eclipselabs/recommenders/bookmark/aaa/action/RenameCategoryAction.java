package org.eclipselabs.recommenders.bookmark.aaa.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.aaa.model.Category;
import org.eclipselabs.recommenders.bookmark.aaa.model.FileBookmark;
import org.eclipselabs.recommenders.bookmark.aaa.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.aaa.model.IModelVisitor;
import org.eclipselabs.recommenders.bookmark.aaa.model.JavaElementBookmark;
import org.eclipselabs.recommenders.bookmark.aaa.tree.RepresentationSwitchableTreeViewer;

public class RenameCategoryAction extends Action implements SelfEnabling {

    private final RepresentationSwitchableTreeViewer treeViewer;

    public RenameCategoryAction(RepresentationSwitchableTreeViewer treeViewer) {
        this.treeViewer = treeViewer;
        this.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.ICON_RENAME_CATEGORY));
        this.setToolTipText("Rename Category");
        this.setText("Rename Category");
        this.setEnabled(false);
    }

    @Override
    public void run() {
        treeViewer.editCurrentlySelectedRow();
    }

    @Override
    public void updateEnableStatus() {
        IStructuredSelection selections = treeViewer.getSelections();
        if (selections.size() != 1) {
            this.setEnabled(false);
            return;
        }

        EnablementVisitor visitor = new EnablementVisitor();
        IBookmarkModelComponent component = (IBookmarkModelComponent) selections.getFirstElement();
        component.accept(visitor);

        if (visitor.getShallEnable()) {
            this.setEnabled(true);
        }else {
            this.setEnabled(false);
        }

    }

    private class EnablementVisitor implements IModelVisitor {

        private boolean shallEnable = false;

        public boolean getShallEnable() {
            return shallEnable;
        }

        @Override
        public void visit(FileBookmark fileBookmark) {
        }

        @Override
        public void visit(Category category) {
            shallEnable = true;
        }

        @Override
        public void visit(JavaElementBookmark javaElementBookmark) {
        }

    }
}
