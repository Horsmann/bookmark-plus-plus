package org.eclipselabs.recommenders.bookmark.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipselabs.recommenders.bookmark.Activator;

public class DeActivateCategoryModeAction extends Action {

    private final Composite comboboxComposite;
    private final Composite mainComposite;

    public DeActivateCategoryModeAction(Composite comboboxComposite, Composite mainComposite) {
        this.comboboxComposite = comboboxComposite;
        this.mainComposite = mainComposite;
        this.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.ICON_TOGGLE_VIEW));
        this.setToolTipText("Switches between the hierarchical and the category mode");
        this.setText("Switch to category");
    }

    @Override
    public void run() {
        if (comboboxComposite.isVisible()) {
            comboboxComposite.setVisible(false);
            GridData data = new GridData(0, 0);
            comboboxComposite.setLayoutData(data);
            mainComposite.layout(true, true);
        } else {
            comboboxComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
            comboboxComposite.setVisible(true);
            mainComposite.layout(true, true);
        }
    }

}
