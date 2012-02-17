package org.eclipselabs.recommenders.bookmark.view.tree.combo;

import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipselabs.recommenders.bookmark.model.Category;
import org.eclipselabs.recommenders.bookmark.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.view.BookmarkCommandInvoker;
import org.eclipselabs.recommenders.bookmark.view.DefaultDropStrategy;

import com.google.common.base.Optional;

public class CategoryModeDropStrategy extends DefaultDropStrategy {
    
    private Category activeCategory;

    public CategoryModeDropStrategy(BookmarkCommandInvoker commandInvoker) {
        super(commandInvoker);
    }

    @Override
    protected Optional<IBookmarkModelComponent> getDropTarget(final DropTargetEvent event) {
        if (event.item == null) {
            return Optional.of((IBookmarkModelComponent) activeCategory);
        } else {
            return Optional.of((IBookmarkModelComponent) event.item.getData());
        }
    }

    public void setCategory(Category category) {
        activeCategory = category;
    }

}
