package org.eclipselabs.recommenders.bookmark.aaa.action;

import org.eclipse.jface.action.Action;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.aaa.BookmarkCommandInvoker;
import org.eclipselabs.recommenders.bookmark.aaa.commands.CloseAllOpenEditorsCommand;

public class CloseAllEditorWindowsAction extends Action {

    private final BookmarkCommandInvoker invoker;

    public CloseAllEditorWindowsAction(BookmarkCommandInvoker invoker) {
        this.invoker = invoker;
        this.setImageDescriptor(Activator.getDefault().getImageRegistry()
                .getDescriptor(Activator.ICON_CLOSE_ALL_OPEN_EDITORS));
        this.setToolTipText("Closes all currently opened editor windows");
        this.setText("Close Editors");
    }

    @Override
    public void run() {
        invoker.invoke(new CloseAllOpenEditorsCommand());
    }

}
