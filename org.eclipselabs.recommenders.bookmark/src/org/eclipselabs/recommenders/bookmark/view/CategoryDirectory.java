package org.eclipselabs.recommenders.bookmark.view;

import org.eclipselabs.recommenders.bookmark.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.model.Category;
import org.eclipselabs.recommenders.bookmark.view.tree.combo.HideableComboViewer;

import com.google.common.base.Optional;

public class CategoryDirectory {

    private final HideableComboViewer comboViewer;
    private final BookmarkModel model;

    public CategoryDirectory(HideableComboViewer comboViewer, BookmarkModel model){
        this.comboViewer = comboViewer;
        this.model = model;
    }
    
    
    public Optional<Category> getCategory() {
        
        if (comboViewer.isVisible()){
            int index = comboViewer.getIndex();
            return Optional.of(model.getCategories().get(index));
        }
        
        return Optional.absent();
    }
    
}
