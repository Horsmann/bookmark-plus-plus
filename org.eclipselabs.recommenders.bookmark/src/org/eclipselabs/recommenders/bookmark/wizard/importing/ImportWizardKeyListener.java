package org.eclipselabs.recommenders.bookmark.wizard.importing;

import org.eclipse.swt.widgets.TreeItem;
import org.eclipselabs.recommenders.bookmark.commands.DeleteInferredBookmarksCommand;
import org.eclipselabs.recommenders.bookmark.commands.DeleteSingleBookmarkCommand;
import org.eclipselabs.recommenders.bookmark.commands.IBookmarkModelCommand;
import org.eclipselabs.recommenders.bookmark.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.model.IBookmark;
import org.eclipselabs.recommenders.bookmark.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.view.BookmarkCommandInvoker;
import org.eclipselabs.recommenders.bookmark.view.tree.RepresentationSwitchableTreeViewer;
import org.eclipselabs.recommenders.bookmark.visitor.IsIBookmarkVisitor;
import org.eclipselabs.recommenders.bookmark.wizard.WizardKeyListener;

public class ImportWizardKeyListener extends WizardKeyListener {

    private final BookmarkModel model;

    public ImportWizardKeyListener(RepresentationSwitchableTreeViewer treeViewer, BookmarkCommandInvoker invoker,
            BookmarkModel model) {
        super(treeViewer, invoker);
        this.model = model;
    }

    @Override
    protected void searchBookmarkDeleteSelection(TreeItem item) {
        IBookmarkModelComponent component = (IBookmarkModelComponent) item.getData();
        IBookmarkModelComponent parent = component.getParent();
        BookmarkCommandInvoker invoker = getInvokerImplementationWithoutTreeViewerRefresh();
        delete(invoker, component);
        deleteInferredBookmarksRecursively(invoker, parent);
    }

    private void deleteInferredBookmarksRecursively(BookmarkCommandInvoker invoker, IBookmarkModelComponent parent) {
        if (parent == null) {
            return;
        }
        IsIBookmarkVisitor isIBookmark = new IsIBookmarkVisitor();
        parent.accept(isIBookmark);
        if (isIBookmark.isIBookmark()) {
            invoker.invoke(new DeleteInferredBookmarksCommand((IBookmark) parent));
        }
    }

    private void delete(BookmarkCommandInvoker invoker, IBookmarkModelComponent component) {
        invoker.invoke(new DeleteSingleBookmarkCommand(component));
    }

    private BookmarkCommandInvoker getInvokerImplementationWithoutTreeViewerRefresh() {
        return new BookmarkCommandInvoker() {

            @Override
            public void invoke(IBookmarkModelCommand command) {
                command.execute(model);
            }
        };
    }
}
