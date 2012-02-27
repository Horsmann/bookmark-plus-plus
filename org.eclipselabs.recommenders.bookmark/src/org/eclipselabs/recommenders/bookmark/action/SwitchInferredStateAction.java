package org.eclipselabs.recommenders.bookmark.action;

import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.commands.DeleteInferredBookmarksCommand;
import org.eclipselabs.recommenders.bookmark.model.Category;
import org.eclipselabs.recommenders.bookmark.model.FileBookmark;
import org.eclipselabs.recommenders.bookmark.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.model.IModelVisitor;
import org.eclipselabs.recommenders.bookmark.model.JavaElementBookmark;
import org.eclipselabs.recommenders.bookmark.view.BookmarkCommandInvoker;
import org.eclipselabs.recommenders.bookmark.view.tree.RepresentationSwitchableTreeViewer;
import org.eclipselabs.recommenders.bookmark.visitor.IsCategoryVisitor;

public class SwitchInferredStateAction extends Action implements SelfEnabling {

    private final RepresentationSwitchableTreeViewer treeViewer;
    private final BookmarkCommandInvoker invoker;

    public SwitchInferredStateAction(RepresentationSwitchableTreeViewer treeViewer, BookmarkCommandInvoker invoker) {
        this.treeViewer = treeViewer;
        this.invoker = invoker;
        this.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.ICON_SINGLE_BOOKMARK));
        this.setToolTipText("Switches the inferred state of a bookmark");
        this.setText("Switch inferred");
    }

    @Override
    public void run() {
        @SuppressWarnings("rawtypes")
        Iterator iterator = treeViewer.getSelections().iterator();
        while (iterator.hasNext()) {
            IBookmarkModelComponent component = (IBookmarkModelComponent) iterator.next();
            SwapInferredStateVisitor visitor = new SwapInferredStateVisitor();
            component.accept(visitor);
        }
        treeViewer.refresh();
        updateEnableStatus();
    }

    @Override
    public void updateEnableStatus() {
        setText("Switch inferred state");
        setEnabled(false);

        if (treeViewer.getSelections().size() == 1) {
            IBookmarkModelComponent component = (IBookmarkModelComponent) treeViewer.getSelections().getFirstElement();
            IsCategoryVisitor isCatVisitor = new IsCategoryVisitor();
            component.accept(isCatVisitor);
            if (!isCatVisitor.isCategory()) {
                setEnabled(true);
                setTextAccordingToCurrentState(component);
            }
        } else if (treeViewer.getSelections().size() >= 2) {
            @SuppressWarnings("rawtypes")
            Iterator iterator = treeViewer.getSelections().iterator();
            while (iterator.hasNext()) {
                IBookmarkModelComponent component = (IBookmarkModelComponent) iterator.next();
                IsCategoryVisitor visitor = new IsCategoryVisitor();
                component.accept(visitor);
                if (!visitor.isCategory()) {
                    setEnabled(true);
                    break;
                }
            }
        }

    }

    private void setTextAccordingToCurrentState(IBookmarkModelComponent component) {
        IsInferredVisitor visitor = new IsInferredVisitor();
        component.accept(visitor);
        if (visitor.isInferred()) {
            setText("Set to Bookmarked");
        } else {
            setText("Un-Bookmark");
        }
    }

    private class IsInferredVisitor implements IModelVisitor {

        private boolean isInferred = false;

        public boolean isInferred() {
            return isInferred;
        }

        @Override
        public void visit(FileBookmark fileBookmark) {
            isInferred = fileBookmark.isInferredNode();
        }

        @Override
        public void visit(Category category) {
        }

        @Override
        public void visit(JavaElementBookmark javaElementBookmark) {
            isInferred = javaElementBookmark.isInferredNode();
        }

    }

    private class SwapInferredStateVisitor implements IModelVisitor {

        @Override
        public void visit(FileBookmark fileBookmark) {
            if (fileBookmark.isInferredNode()) {
                fileBookmark.setInferred(false);
            } else {
                fileBookmark.setInferred(true);
                invoker.invoke(new DeleteInferredBookmarksCommand(fileBookmark));
            }
        }

        @Override
        public void visit(Category category) {
        }

        @Override
        public void visit(JavaElementBookmark javaElementBookmark) {
            if (javaElementBookmark.isInferredNode()) {
                javaElementBookmark.setInferred(false);
            } else {
                javaElementBookmark.setInferred(true);
                invoker.invoke(new DeleteInferredBookmarksCommand(javaElementBookmark));
            }
        }

    }

}
