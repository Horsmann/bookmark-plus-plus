package org.eclipselabs.recommenders.bookmark.view.tree.combo;

import org.eclipselabs.recommenders.bookmark.model.Category;
import org.eclipselabs.recommenders.bookmark.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.view.BookmarkCommandInvoker;
import org.eclipselabs.recommenders.bookmark.view.copyCutPaste.DefaultPasteStrategy;
import org.eclipselabs.recommenders.bookmark.view.tree.RepresentationSwitchableTreeViewer;

import com.google.common.base.Optional;

public class CategoryModePasteStrategy extends DefaultPasteStrategy {

    private Category category;

    public CategoryModePasteStrategy(BookmarkCommandInvoker inovker, RepresentationSwitchableTreeViewer treeViewer) {
        super(inovker, treeViewer);
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    protected Optional<IBookmarkModelComponent> getDropTarget() {
        IBookmarkModelComponent target = (IBookmarkModelComponent) treeViewer.getSelections().getFirstElement();
        Optional<IBookmarkModelComponent> dropTarget = Optional.absent();
        if (target != null) {
            dropTarget = Optional.of(target);
        } else {
            dropTarget = Optional.of((IBookmarkModelComponent) category);
        }
        return dropTarget;
    }
}
