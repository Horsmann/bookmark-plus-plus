package org.eclipselabs.recommenders.bookmark.action;

import org.eclipse.jface.action.Action;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.view.BookmarkView;

public class SwitchFlatHierarchicalAction extends Action {
    private final BookmarkView bookmarkView;

    public SwitchFlatHierarchicalAction(BookmarkView bookmarkView) {
        super("", AS_CHECK_BOX);
        this.bookmarkView = bookmarkView;
        this.setImageDescriptor(Activator.getDefault().getImageRegistry()
                .getDescriptor(Activator.ICON_SWITCH_FLAT_HIERARCHY));
        this.setToolTipText("Switches between flat and hierarchical mode");
        this.setText("Switches flat/tree");
    }

    @Override
    public void run() {
        if (bookmarkView.isHierarchicalModeActive()) {
            this.setChecked(true);
            bookmarkView.activateFlatMode();
        } else {
            this.setChecked(false);
            bookmarkView.activateHierarchicalMode();
        }
    }

}
