package org.eclipselabs.recommenders.bookmark.view.tree.combo;

import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipselabs.recommenders.bookmark.model.Category;
import org.eclipselabs.recommenders.bookmark.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.view.BookmarkCommandInvoker;
import org.eclipselabs.recommenders.bookmark.view.BookmarkTreeDropListener;
import org.eclipselabs.recommenders.bookmark.view.DefaultDropStrategy;
import org.eclipselabs.recommenders.bookmark.view.IDropStrategy;

import com.google.common.base.Optional;

public class MouseDropStrategyChanger implements IChangeableStrategy {

    private final BookmarkTreeDropListener dropLister;
    private final IDropStrategy defaulDropStrategy;
    private CategoryModeDropStrategy categoryDropStrategy;

    public MouseDropStrategyChanger(BookmarkTreeDropListener dropLister, IDropStrategy defaulDropStrategy,
            BookmarkCommandInvoker commandInvoker) {
        this.dropLister = dropLister;
        this.defaulDropStrategy = defaulDropStrategy;
        categoryDropStrategy = new CategoryModeDropStrategy(commandInvoker);
    }

    @Override
    public void reset() {
        dropLister.setNewStrategy(defaulDropStrategy);
    }

    @Override
    public void change(Category category) {
        dropLister.setNewStrategy(categoryDropStrategy);
        update(category);
    }

    @Override
    public void update(Category category) {
        categoryDropStrategy.setCategory(category);
    }

    private class CategoryModeDropStrategy extends DefaultDropStrategy {

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

}
