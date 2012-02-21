package org.eclipselabs.recommenders.bookmark.commands;

import org.eclipselabs.recommenders.bookmark.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.model.Category;
import org.eclipselabs.recommenders.bookmark.view.tree.combo.HideableComboViewer;

public class AddCategoryCommand implements IBookmarkModelCommand {

    private final HideableComboViewer hideableComboViewer;

    public AddCategoryCommand(HideableComboViewer hideableComboViewer) {
        this.hideableComboViewer = hideableComboViewer;
    }

    @Override
    public void execute(BookmarkModel model) {
        Category category = new Category("New Category");
        model.add(category);
        updateComboboxIfVisible(category, model);
    }

    private void updateComboboxIfVisible(Category category, BookmarkModel model) {
        if (hideableComboViewer.isVisible()) {
            hideableComboViewer.show(category);
        }
    }

    @Override
    public void execute(BookmarkModel model, Category category) {
        execute(model);
    }

}
