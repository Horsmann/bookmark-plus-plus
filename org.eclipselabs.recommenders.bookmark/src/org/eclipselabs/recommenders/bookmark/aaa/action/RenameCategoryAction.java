package org.eclipselabs.recommenders.bookmark.aaa.action;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.aaa.BookmarkView;
import org.eclipselabs.recommenders.bookmark.aaa.commands.RenameCategoryCommand;
import org.eclipselabs.recommenders.bookmark.aaa.tree.RepresentationSwitchableTreeViewer;

public class RenameCategoryAction extends Action {

    private final RepresentationSwitchableTreeViewer treeViewer;
    private final BookmarkView bookmarkView;

    public RenameCategoryAction(BookmarkView bookmarkView, RepresentationSwitchableTreeViewer treeViewer) {
        this.bookmarkView = bookmarkView;
        this.treeViewer = treeViewer;
        this.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.ICON_RENAME_CATEGORY));
        this.setToolTipText("Rename Category");
        this.setText("Rename Category");
    }

    @Override
    public void run() {
        
        treeViewer.doit();
        
//        TreeItem[] selections = treeViewer.getTree().getSelection();
//        if (isValidSelection(selections)) {
//            bookmarkView.invoke(new RenameCategoryCommand(treeViewer.getViewer(), treeViewer.getTree(), selections[0]));
//
//        }
    }

//    private boolean isValidSelection(TreeItem[] selections) {
//        return selections.length == 1 && selections[0] instanceof TreeItem;
//    }
}
