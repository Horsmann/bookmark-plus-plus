package org.eclipselabs.recommenders.bookmark.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.commands.RenameCategoryCommand;
import org.eclipselabs.recommenders.bookmark.model.Category;
import org.eclipselabs.recommenders.bookmark.model.FileBookmark;
import org.eclipselabs.recommenders.bookmark.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.model.IModelVisitor;
import org.eclipselabs.recommenders.bookmark.model.JavaElementBookmark;
import org.eclipselabs.recommenders.bookmark.view.BookmarkCommandInvoker;
import org.eclipselabs.recommenders.bookmark.view.tree.RepresentationSwitchableTreeViewer;

public class RenameCategoryAction extends Action implements SelfEnabling {

    private final RepresentationSwitchableTreeViewer treeViewer;
    private final BookmarkCommandInvoker invoker;

    public RenameCategoryAction(RepresentationSwitchableTreeViewer treeViewer, BookmarkCommandInvoker invoker) {
        this.treeViewer = treeViewer;
        this.invoker = invoker;
        this.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.ICON_RENAME_CATEGORY));
        this.setToolTipText("Rename Category");
        this.setText("Rename Category");
        this.setEnabled(false);
    }

    @Override
    public void run() {
        invoker.invoke(new RenameCategoryCommand(treeViewer));
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
        } else {
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
