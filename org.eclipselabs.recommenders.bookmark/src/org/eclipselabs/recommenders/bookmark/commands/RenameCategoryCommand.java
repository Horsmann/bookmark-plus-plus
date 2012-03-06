package org.eclipselabs.recommenders.bookmark.commands;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipselabs.recommenders.bookmark.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.model.Category;
import org.eclipselabs.recommenders.bookmark.model.FileBookmark;
import org.eclipselabs.recommenders.bookmark.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.model.IModelVisitor;
import org.eclipselabs.recommenders.bookmark.model.JavaElementBookmark;
import org.eclipselabs.recommenders.bookmark.view.tree.RepresentationSwitchableTreeViewer;
import org.eclipselabs.recommenders.bookmark.visitor.GetValueVisitor;
import org.eclipselabs.recommenders.bookmark.visitor.IsCategoryVisitor;

public class RenameCategoryCommand implements IBookmarkModelCommand {

    private final RepresentationSwitchableTreeViewer treeViewer;

    public RenameCategoryCommand(RepresentationSwitchableTreeViewer treeViewer) {
        this.treeViewer = treeViewer;
    }
    

    @Override
    public void execute(BookmarkModel model, Category category) {
        execute(model);
    }

    @Override
    public void execute(BookmarkModel model) {
        IBookmarkModelComponent component = (IBookmarkModelComponent) treeViewer.getSelections().getFirstElement();
        if (!isRenameValid(component)) {
            return;
        }
        String oldLabel = getOldCategoryLabel(component);

        InputDialog dialog = makeDialog(oldLabel);
        dialog.setBlockOnOpen(true);
        if (dialog.open() == Window.OK) {
            updateCategoryName(component, dialog);
            treeViewer.refresh();
        }
    }

    private void updateCategoryName(IBookmarkModelComponent component, InputDialog dialog) {
        String value = dialog.getValue();
        SetNewCategoryNameVisitor categoryVisitor = new SetNewCategoryNameVisitor(value);
        component.accept(categoryVisitor);
    }

    private String getOldCategoryLabel(IBookmarkModelComponent component) {
        GetValueVisitor valueVisitor = new GetValueVisitor();
        component.accept(valueVisitor);
        return (String) valueVisitor.getValue();
    }

    private InputDialog makeDialog(String oldLabel) {
        return new InputDialog(treeViewer.getShell(), "Rename Category", "Enter a new category name:", oldLabel,
                new InputValidator());
    }

    private boolean isRenameValid(IBookmarkModelComponent component) {
        if (treeViewer.getSelections().size() != 1) {
            return false;
        }

        IsCategoryVisitor isCategoryVisitor = new IsCategoryVisitor();
        component.accept(isCategoryVisitor);
        return isCategoryVisitor.isCategory();
    }

    private class SetNewCategoryNameVisitor implements IModelVisitor {

        private final String categoryName;

        public SetNewCategoryNameVisitor(String categoryName) {
            this.categoryName = categoryName;
        }

        @Override
        public void visit(FileBookmark fileBookmark) {
        }

        @Override
        public void visit(Category category) {
            category.setLabel(categoryName);
        }

        @Override
        public void visit(JavaElementBookmark javaElementBookmark) {
        }

    }

    private class InputValidator implements IInputValidator {

        @Override
        public String isValid(String newText) {
            if (newText.equals("")) {
                return "Blank is an invalid category name";
            }
            return null;
        }

    }

}
