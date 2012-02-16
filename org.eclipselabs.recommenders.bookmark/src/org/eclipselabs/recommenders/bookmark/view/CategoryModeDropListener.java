package org.eclipselabs.recommenders.bookmark.view;

import org.eclipselabs.recommenders.bookmark.commands.AddElementToModelCommand;
import org.eclipselabs.recommenders.bookmark.model.Category;
import org.eclipselabs.recommenders.bookmark.model.IBookmarkModelComponent;

import com.google.common.base.Optional;

public class CategoryModeDropListener extends BookmarkTreeDropListener {

    private Category category;

    public CategoryModeDropListener(BookmarkCommandInvoker commandInvoker) {
        super(commandInvoker);
    }

    @Override
    protected void processDroppedElementOriginatedFromOutsideTheView(Optional<IBookmarkModelComponent> dropTarget,
            final Object[] elements, boolean isDropBeforeTarget) {
        Optional<String> categoryName = Optional.absent();
        dropTarget = Optional.of((IBookmarkModelComponent) category);
        commandInvoker.invoke(new AddElementToModelCommand(elements, commandInvoker, isDropBeforeTarget, dropTarget,
                categoryName));
    }

    public void setTargetCategory(Category category) {
        this.category = category;
    }

}
