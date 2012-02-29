package org.eclipselabs.recommenders.bookmark.wizard;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipselabs.recommenders.bookmark.commands.IBookmarkModelCommand;
import org.eclipselabs.recommenders.bookmark.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.view.BookmarkCommandInvoker;
import org.eclipselabs.recommenders.bookmark.view.tree.RepresentationSwitchableTreeViewer;

import com.google.common.base.Optional;

public class AddAllMouseListener implements MouseListener {

    private final RepresentationSwitchableTreeViewer leftViewer;
    private final RepresentationSwitchableTreeViewer rightViewer;
    private final BookmarkCommandInvoker invoker;

    public AddAllMouseListener(RepresentationSwitchableTreeViewer leftViewer,
            RepresentationSwitchableTreeViewer rightViewer, BookmarkCommandInvoker invoker) {
        this.leftViewer = leftViewer;
        this.rightViewer = rightViewer;
        this.invoker = invoker;
    }

    @Override
    public void mouseUp(MouseEvent e) {
    }

    @Override
    public void mouseDown(MouseEvent e) {
        BookmarkModel source = (BookmarkModel) leftViewer.getInput();
        BookmarkModel target = (BookmarkModel) rightViewer.getInput();
        if (source == null || target == null) {
            return;
        }

        Optional<IBookmarkModelComponent> dropTarget = Optional.absent();
        IBookmarkModelComponent[] components = source.getCategories().toArray(new IBookmarkModelComponent[0]);
        invoker.invoke(new ImportSelectedBookmarksCommand(components, getInvoker(target), true, false, dropTarget));
        rightViewer.refresh();
    }

    // the default invoker refreshes the view after each command is
    // executed. The invoker is also provided as parameter for cascading
    // command invocations what causes gui flickering on "add all" which
    // is prevented
    // with this one
    private BookmarkCommandInvoker getInvoker(final BookmarkModel model) {
        return new BookmarkCommandInvoker() {

            @Override
            public void invoke(IBookmarkModelCommand command) {
                command.execute(model);
            }
        };
    }

    @Override
    public void mouseDoubleClick(MouseEvent e) {
    }
}
