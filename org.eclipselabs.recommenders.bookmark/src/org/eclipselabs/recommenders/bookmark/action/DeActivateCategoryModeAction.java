package org.eclipselabs.recommenders.bookmark.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.view.tree.HideableComboViewer;
import org.eclipselabs.recommenders.bookmark.view.tree.RepresentationSwitchableTreeViewer;
import org.eclipselabs.recommenders.bookmark.visitor.IsCategoryVisitor;

public class DeActivateCategoryModeAction extends Action implements SelfEnabling {

    private final BookmarkModel model;
    private final RepresentationSwitchableTreeViewer treeViewer;
    private final HideableComboViewer hideableComboViewer;

    public DeActivateCategoryModeAction(HideableComboViewer hideableComboViewer, BookmarkModel model,
            RepresentationSwitchableTreeViewer representationSwitchableTreeViewer) {
        this.hideableComboViewer = hideableComboViewer;
        this.model = model;
        treeViewer = representationSwitchableTreeViewer;
        this.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.ICON_TOGGLE_VIEW));
        this.setToolTipText("Switches between the hierarchical and the category mode");
        this.setText("Switch to category");
    }

    @Override
    public void run() {
        if (hideableComboViewer.isVisible()) {
            hideableComboViewer.hide();
        } else {
            rebuildComboMenu();
            setActiveCategoryAccordingToTreeSelection();
            hideableComboViewer.show();
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
                    hideableComboViewer.selectIndex(i);
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
        hideableComboViewer.setNewSelections(model.getCategories());
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
