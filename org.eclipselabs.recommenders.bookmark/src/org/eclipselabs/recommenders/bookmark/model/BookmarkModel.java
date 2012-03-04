package org.eclipselabs.recommenders.bookmark.model;

import java.io.Serializable;
import java.util.List;

import com.google.common.collect.Lists;

public class BookmarkModel implements Serializable {

    private static final long serialVersionUID = 723478350155587860L;
    private final List<Category> categories = Lists.newLinkedList();

    public void add(final Category category) {
        categories.add(category);
    }
    
    public void remove(IBookmarkModelComponent component) {
        categories.remove(component);
    }
    
    public void removeAll() {
        categories.removeAll(getCategories());
    }

    public List<Category> getCategories() {
        return categories;
    }
}
