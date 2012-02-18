package org.eclipselabs.recommenders.bookmark.view.tree.combo;

import org.eclipselabs.recommenders.bookmark.model.Category;

public interface IChangeableStrategy {
    
    public void reset();
    
    public void change(Category category);
    
    public void update(Category category);
    
}
