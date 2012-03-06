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

import com.google.common.base.Optional;

public class GoToCategoryModeAction extends Action implements SelfEnabling {

    private final BookmarkModel model;
    private final RepresentationSwitchableTreeViewer treeViewer;
    private final HideableComboViewer hideableComboViewer;

    public GoToCategoryModeAction(HideableComboViewer hideableComboViewer, BookmarkModel model,
            RepresentationSwitchableTreeViewer treeViewer) {
        super("", AS_CHECK_BOX);
        this.hideableComboViewer = hideableComboViewer;
        this.model = model;
        this.treeViewer = treeViewer;
        this.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.ICON_TOGGLE_VIEW));
        this.setToolTipText("Switches between showing all bookmarks or only those in the currently selected category");
    }

    @Override
    public void run() {
        if (model.getCategories().size() == 0) {
            return;
        }
        if (hideableComboViewer.isVisible()) {
            hide();
        } else {
            show();
        }
    }

    private void show() {
        this.setChecked(true);
        Optional<Category> category = getActivatedCategory();
        if (category.isPresent()) {
            hideableComboViewer.show(category.get());
        }
    }

    private void hide() {
        this.setChecked(false);
        hideableComboViewer.hide();
        setSelection();
    }

    private void setSelection() {
        int index = hideableComboViewer.getIndex();
        if (index < 0) {
            return;
        }
        StructuredSelection selection = new StructuredSelection(model.getCategories().get(index));
        treeViewer.selectComponent(selection);
    }

    private Optional<Category> getActivatedCategory() {
        IBookmarkModelComponent component = getCategoryAccordingToSelection();
        if (isCategory(component)) {
            return Optional.of((Category) component);
        }
        return Optional.absent();
    }

    private boolean isCategory(IBookmarkModelComponent component) {
        IsCategoryVisitor visitor = new IsCategoryVisitor();
        component.accept(visitor);
        return visitor.isCategory();
    }

    private IBookmarkModelComponent getCategoryAccordingToSelection() {
        IStructuredSelection selections = treeViewer.getSelections();
        IBookmarkModelComponent component = getSelectedComponent(selections);
        return getHighestElementInHierarchy(component);
    }

    private IBookmarkModelComponent getHighestElementInHierarchy(IBookmarkModelComponent component) {
        while (component.hasParent()) {
            component = component.getParent();
        }
        return component;
    }

    private IBookmarkModelComponent getSelectedComponent(IStructuredSelection selections) {
        if (selections.isEmpty()) {
            return model.getCategories().get(0);
        } else {
            return (IBookmarkModelComponent) selections.getFirstElement();
        }
    }

    @Override
    public void updateEnableStatus() {

        if (hideableComboViewer.isVisible()) {
            setEnabled(true);
            this.setText("Leave Category");
        } else {
            if (model.getCategories().isEmpty()) {
                setEnabled(false);
                this.setText("Not Available");
            } else {
                setEnabled(true);
                this.setText("Go to Category");
            }
        }
    }

}
