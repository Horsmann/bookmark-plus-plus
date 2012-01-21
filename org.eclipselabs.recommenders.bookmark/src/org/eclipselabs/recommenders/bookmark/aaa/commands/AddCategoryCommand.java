package org.eclipselabs.recommenders.bookmark.aaa.commands;

import org.eclipselabs.recommenders.bookmark.aaa.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.aaa.model.Category;

public class AddCategoryCommand implements IBookmarkModelCommand{
    
    @Override
    public void execute(BookmarkModel model) {
        model.add(new Category("New Category"));
    }

}
