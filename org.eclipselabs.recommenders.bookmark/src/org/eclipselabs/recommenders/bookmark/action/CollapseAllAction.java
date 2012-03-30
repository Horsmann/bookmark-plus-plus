package org.eclipselabs.recommenders.bookmark.action;

import org.eclipse.jface.action.Action;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.commands.CollapseCategoriesCommand;

public class CollapseAllAction extends Action {

    public CollapseAllAction() {
        this.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.ICON_COLLAPSE_ALL));
        this.setToolTipText("Collapse all Categories");
        this.setText("Collapse all Categories");
    }

    @Override
    public void run() {
        Activator.getCommandInvoker().invoke(new CollapseCategoriesCommand());
    }
}
