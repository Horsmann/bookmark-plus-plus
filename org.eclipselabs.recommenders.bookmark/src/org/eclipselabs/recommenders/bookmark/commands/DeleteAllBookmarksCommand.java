package org.eclipselabs.recommenders.bookmark.commands;

import org.eclipselabs.recommenders.bookmark.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.model.Category;

public class DeleteAllBookmarksCommand implements IBookmarkModelCommand {

    @Override
    public void execute(BookmarkModel model, Category category) {
        execute(model);
    };

    @Override
    public void execute(BookmarkModel model) {
        model.removeAll();
    }

}
