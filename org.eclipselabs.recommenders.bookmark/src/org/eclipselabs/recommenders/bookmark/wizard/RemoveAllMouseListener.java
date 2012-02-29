package org.eclipselabs.recommenders.bookmark.wizard;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipselabs.recommenders.bookmark.commands.DeleteAllBookmarksCommand;
import org.eclipselabs.recommenders.bookmark.view.BookmarkCommandInvoker;

public class RemoveAllMouseListener implements MouseListener {

    private final BookmarkCommandInvoker invoker;

    public RemoveAllMouseListener(BookmarkCommandInvoker invoker) {
        this.invoker = invoker;
    }

    @Override
    public void mouseUp(MouseEvent e) {
    }

    @Override
    public void mouseDown(MouseEvent e) {
        invoker.invoke(new DeleteAllBookmarksCommand());
    }

    @Override
    public void mouseDoubleClick(MouseEvent e) {
    }
}
