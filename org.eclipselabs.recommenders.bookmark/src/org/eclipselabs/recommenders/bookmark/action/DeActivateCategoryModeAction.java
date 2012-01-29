package org.eclipselabs.recommenders.bookmark.action;

import org.eclipse.jface.action.Action;
import org.eclipselabs.recommenders.bookmark.Activator;

public class DeActivateCategoryModeAction extends Action {

    public DeActivateCategoryModeAction() {
        this.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.ICON_TOGGLE_VIEW));
        this.setToolTipText("Switches between the hierarchical and the category mode");
        this.setText("Switch to category");
    }

    @Override
    public void run() {
        System.out.println("switch");
    }

}
