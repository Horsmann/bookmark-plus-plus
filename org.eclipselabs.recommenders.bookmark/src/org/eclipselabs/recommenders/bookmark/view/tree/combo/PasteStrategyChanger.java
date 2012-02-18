package org.eclipselabs.recommenders.bookmark.view.tree.combo;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipselabs.recommenders.bookmark.model.Category;
import org.eclipselabs.recommenders.bookmark.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.view.BookmarkCommandInvoker;
import org.eclipselabs.recommenders.bookmark.view.copyCutPaste.DefaultPasteStrategy;
import org.eclipselabs.recommenders.bookmark.view.copyCutPaste.IPasteStrategy;
import org.eclipselabs.recommenders.bookmark.view.copyCutPaste.PasteHandler;
import org.eclipselabs.recommenders.bookmark.view.tree.RepresentationSwitchableTreeViewer;

import com.google.common.base.Optional;

public class PasteStrategyChanger implements IChangeableStrategy {

    private final PasteHandler pasteHandler;
    private final IPasteStrategy defaultStrategy;
    private CategoryModePasteStrategy comboPasteStrategy;

    public PasteStrategyChanger(PasteHandler pasteHandler, IPasteStrategy defaultStrategy,
            RepresentationSwitchableTreeViewer treeViewer, BookmarkCommandInvoker invoker) {
        this.pasteHandler = pasteHandler;
        this.defaultStrategy = defaultStrategy;
        comboPasteStrategy = new CategoryModePasteStrategy(invoker, treeViewer);
    }

    @Override
    public void reset() {
        pasteHandler.setNewStrategy(defaultStrategy);
    }

    @Override
    public void change(Category category) {
        update(category);
        pasteHandler.setNewStrategy(comboPasteStrategy);
    }

    @Override
    public void update(Category category) {
        comboPasteStrategy.setCategory(category);
    }

    private class CategoryModePasteStrategy extends DefaultPasteStrategy {

        private Category category;

        public CategoryModePasteStrategy(BookmarkCommandInvoker invoker, RepresentationSwitchableTreeViewer treeViewer) {
            super(invoker, treeViewer);
        }

        public void setCategory(Category category) {
            this.category = category;
        }

        @Override
        protected Optional<IBookmarkModelComponent> getDropTarget() {
            IBookmarkModelComponent target = null;
            IStructuredSelection selections = treeViewer.getSelections();
            if (selections.size() > 0) {
                target = (IBookmarkModelComponent)treeViewer.getSelections().getFirstElement();
            }
            Optional<IBookmarkModelComponent> dropTarget = Optional.absent();
            if (target != null) {
                dropTarget = Optional.of(target);
            } else {
                dropTarget = Optional.of((IBookmarkModelComponent) category);
            }
            return dropTarget;
        }
    }

}
