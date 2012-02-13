package org.eclipselabs.recommenders.bookmark.commands;

import org.eclipselabs.recommenders.bookmark.model.BookmarkModel;

public class DeleteAllBookmarksCommand implements IBookmarkModelCommand{

    @Override
    public void execute(BookmarkModel model) {
        model.removeAll();
    };

}
