package org.eclipselabs.recommenders.bookmark.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.model.Category;
import org.eclipselabs.recommenders.bookmark.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.view.tree.RepresentationSwitchableTreeViewer;
import org.eclipselabs.recommenders.bookmark.view.tree.combo.HideableComboViewer;
import org.eclipselabs.recommenders.bookmark.visitor.IsCategoryVisitor;

public class GoToCategoryModeAction extends Action implements SelfEnabling {

    private final BookmarkModel model;
    private final RepresentationSwitchableTreeViewer treeViewer;
    private final HideableComboViewer hideableComboViewer;

    public GoToCategoryModeAction(HideableComboViewer hideableComboViewer, BookmarkModel model,
            RepresentationSwitchableTreeViewer representationSwitchableTreeViewer) {
        this.hideableComboViewer = hideableComboViewer;
        this.model = model;
        treeViewer = representationSwitchableTreeViewer;
        this.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.ICON_TOGGLE_VIEW));
        this.setToolTipText("Switches between the hierarchical and the category mode");
        this.setText("Go to Category");
    }

    @Override
    public void run() {
        
        if (model.getCategories().size() == 0){
            return;
        }
        
        if (hideableComboViewer.isVisible()) {
            hideableComboViewer.hide();
            setSelection();
        } else {
            Category category = getActivatedCategory();
            hideableComboViewer.show(category);
        }
    }

    private void setSelection() {
        int index = hideableComboViewer.getIndex();
        if (index < 0) {
            return;
        }
        StructuredSelection selection = new StructuredSelection(model.getCategories().get(index));
        treeViewer.selectComponent(selection);
    }

    private Category getActivatedCategory() {
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

        IsCategoryVisitor visitor = new IsCategoryVisitor();
        component.accept(visitor);
        if (visitor.isCategory()) {
            return (Category) component;
        }
        return null;
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