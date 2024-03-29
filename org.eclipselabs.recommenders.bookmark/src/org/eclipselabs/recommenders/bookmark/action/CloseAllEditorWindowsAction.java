package org.eclipselabs.recommenders.bookmark.action;

import org.eclipse.jface.action.Action;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.commands.CloseAllOpenEditorsCommand;
import org.eclipselabs.recommenders.bookmark.view.BookmarkCommandInvoker;

public class CloseAllEditorWindowsAction extends Action {

    private final BookmarkCommandInvoker commandInvoker;

    public CloseAllEditorWindowsAction(BookmarkCommandInvoker commandInvoker) {
        this.commandInvoker = commandInvoker;
        this.setImageDescriptor(Activator.getDefault().getImageRegistry()
                .getDescriptor(Activator.ICON_CLOSE_ALL_OPEN_EDITORS));
        this.setToolTipText("Closes all currently opened editor windows");
        this.setText("Close Editors");
    }

    @Override
    public void run() {
        commandInvoker.invoke(new CloseAllOpenEditorsCommand());
    }

}
