package org.eclipselabs.recommenders.bookmark.action;

import org.eclipse.jface.action.Action;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.commands.RenameCategoryCommand;
import org.eclipselabs.recommenders.bookmark.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.view.BookmarkCommandInvoker;
import org.eclipselabs.recommenders.bookmark.view.tree.RepresentationSwitchableTreeViewer;
import org.eclipselabs.recommenders.bookmark.visitor.IsCategoryVisitor;

public class RenameCategoryAction extends Action implements SelfEnabling {

    private final RepresentationSwitchableTreeViewer treeViewer;
    private final BookmarkCommandInvoker commandInvoker;

    public RenameCategoryAction(RepresentationSwitchableTreeViewer treeViewer, BookmarkCommandInvoker commandInvoker) {
        this.treeViewer = treeViewer;
        this.commandInvoker = commandInvoker;
        this.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.ICON_RENAME_CATEGORY));
        this.setToolTipText("Rename Category");
        this.setText("Rename Category");
        this.setEnabled(false);
    }

    @Override
    public void run() {
        commandInvoker.invoke(new RenameCategoryCommand(treeViewer));
    }

    @Override
    public void updateEnableStatus() {
        setEnabled(false);
        if (isExcactlyOneElementSelected()) {
            enableIfCategoryIsSelected();
        }
    }

    private void enableIfCategoryIsSelected() {
        IsCategoryVisitor categoryVisitor = new IsCategoryVisitor();
        IBookmarkModelComponent component = (IBookmarkModelComponent) treeViewer.getSelections().getFirstElement();
        component.accept(categoryVisitor);
        if (categoryVisitor.isCategory()) {
            this.setEnabled(true);
        }
    }

    private boolean isExcactlyOneElementSelected() {
        return treeViewer.getSelections().size() == 1;
    }

}
