package org.eclipselabs.recommenders.bookmark.commands;

import org.eclipselabs.recommenders.bookmark.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.model.Category;

public interface IBookmarkModelCommand {
    void execute(BookmarkModel model);
    void execute(BookmarkModel model, Category category);

}
