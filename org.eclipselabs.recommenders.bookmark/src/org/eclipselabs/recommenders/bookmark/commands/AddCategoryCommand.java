package org.eclipselabs.recommenders.bookmark.commands;

import org.eclipselabs.recommenders.bookmark.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.model.Category;

public class AddCategoryCommand implements IBookmarkModelCommand{
    
    @Override
    public void execute(BookmarkModel model) {
        model.add(new Category("New Category"));
    }

}
