package org.eclipselabs.recommenders.bookmark.commands;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipselabs.recommenders.bookmark.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.model.Category;

public class CloseAllOpenEditorsCommand implements IBookmarkModelCommand {

    @Override
    public void execute(BookmarkModel model) {
        PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();

        for (IWorkbenchWindow window : windows) {
            for (IWorkbenchPage page : window.getPages()) {
                page.closeAllEditors(true);
            }
        }
    }

    @Override
    public void execute(BookmarkModel model, Category category) {
        execute(model);
    }
}
