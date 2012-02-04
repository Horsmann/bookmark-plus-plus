package org.eclipselabs.recommenders.bookmark.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.model.Category;
import org.eclipselabs.recommenders.bookmark.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.view.tree.RepresentationSwitchableTreeViewer;
import org.eclipselabs.recommenders.bookmark.visitor.IsCategoryVisitor;

public class DeActivateCategoryModeAction extends Action implements SelfEnabling {

    private final Composite comboboxComposite;
    private final Composite mainComposite;
    private final ComboViewer combo;
    private final BookmarkModel model;
    private final RepresentationSwitchableTreeViewer treeViewer;

    public DeActivateCategoryModeAction(ComboViewer combo, BookmarkModel model,
            RepresentationSwitchableTreeViewer treeViewer) {
        this.combo = combo;
        this.model = model;
        this.treeViewer = treeViewer;
        this.comboboxComposite = treeViewer.getComboboxComposite();
        this.mainComposite = treeViewer.getTreeComposite();
        this.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.ICON_TOGGLE_VIEW));
        this.setToolTipText("Switches between the hierarchical and the category mode");
        this.setText("Switch to category");
    }

    @Override
    public void run() {
        if (comboboxComposite.isVisible()) {
            setInvisibleLayout();
        } else {
            rebuildComboMenu();
            setActiveCategoryAccordingToTreeSelection();
            setVisibleLayout();
        }
    }

    private void setActiveCategoryAccordingToTreeSelection() {
        IBookmarkModelComponent component = getActivatedCategory();

        IsCategoryVisitor visitor = new IsCategoryVisitor();
        component.accept(visitor);
        if (visitor.isCategory()) {
            int i = 0;
            for (; i < model.getCategories().size(); i++) {
                if (model.getCategories().get(i) == component) {
                    combo.getCombo().select(i);
                }
            }
        }
    }

    private IBookmarkModelComponent getActivatedCategory() {
        IStructuredSelection selections = treeViewer.getSelections();
        IBookmarkModelComponent component = null;
        if (selections.isEmpty()) {
            component = model.getCategories().get(0);
        } else {
            component = (IBookmarkModelComponent) selections.getFirstElement();
        }
        while (component.hasParent()) {
            component = component.getParent();
        }
        return component;
    }

    private void rebuildComboMenu() {
        combo.getCombo().removeAll();
        for (Category category : model.getCategories()) {
            combo.add(category.getLabel());
        }
    }

    private void setVisibleLayout() {
        comboboxComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
        comboboxComposite.setVisible(true);
        mainComposite.layout(true, true);
    }

    private void setInvisibleLayout() {
        comboboxComposite.setVisible(false);
        GridData data = new GridData(0, 0);
        comboboxComposite.setLayoutData(data);
        mainComposite.layout(true, true);
    }

    @Override
    public void updateEnableStatus() {
        if (model.getCategories().isEmpty()) {
            setEnabled(false);
        } else {
            setEnabled(true);
        }
    }

}
