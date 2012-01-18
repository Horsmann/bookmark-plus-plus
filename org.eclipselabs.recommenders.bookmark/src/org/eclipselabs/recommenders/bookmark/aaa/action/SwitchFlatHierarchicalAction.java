package org.eclipselabs.recommenders.bookmark.aaa.action;

import org.eclipse.jface.action.Action;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.aaa.BookmarkView;

public class SwitchFlatHierarchicalAction extends Action {
    private final BookmarkView bookmarkView;

    public SwitchFlatHierarchicalAction(BookmarkView bookmarkView) {
        this.bookmarkView = bookmarkView;
        this.setImageDescriptor(Activator.getDefault().getImageRegistry()
                .getDescriptor(Activator.ICON_TOGGLE_FLAT_HIERARCHY));
        this.setToolTipText("Switches between flat and hierarchical mode");
        this.setText("Switches flat/tree");
    }

    @Override
    public void run() {
        if (bookmarkView.isHierarchicalModeActive()) {
            bookmarkView.activateFlatMode();
        } else {
            bookmarkView.activateHierarchicalMode();
        }
    }

}
