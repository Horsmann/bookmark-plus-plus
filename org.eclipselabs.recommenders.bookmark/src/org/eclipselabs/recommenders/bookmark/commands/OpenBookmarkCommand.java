package org.eclipselabs.recommenders.bookmark.commands;

import java.util.Iterator;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.preferences.PreferenceConstants;
import org.eclipselabs.recommenders.bookmark.view.BookmarkCommandInvoker;
import org.eclipselabs.recommenders.bookmark.visitor.IsCategoryVisitor;
import org.eclipselabs.recommenders.bookmark.visitor.OpenInEditorVisitor;

public class OpenBookmarkCommand implements IBookmarkModelCommand {

    private final IStructuredSelection selections;
    private final IWorkbenchPage activePage;
    private final BookmarkCommandInvoker commandInvoker;

    public OpenBookmarkCommand(IStructuredSelection selections, IWorkbenchPage activePage, BookmarkCommandInvoker commandInvoker) {
        this.selections = selections;
        this.activePage = activePage;
        this.commandInvoker = commandInvoker;
    }

    @Override
    public void execute(BookmarkModel model) {
        
        @SuppressWarnings("rawtypes")
        Iterator iterator = selections.iterator();
        while (iterator.hasNext()) {
            Object next = iterator.next();
            if (next instanceof IBookmarkModelComponent) {
                IBookmarkModelComponent component = (IBookmarkModelComponent) next;
                process(component, activePage);
            }
        }
    }

    private void process(IBookmarkModelComponent component, IWorkbenchPage activePage2) {
        IsCategoryVisitor visitor = new IsCategoryVisitor();
        component.accept(visitor);
        OpenInEditorVisitor openVisitor = null;
        if (visitor.isCategory()) {
            closeAllOpenEditors();
            openVisitor = new OpenInEditorVisitor(activePage, true);
            component.accept(openVisitor);
        } else {
            openVisitor = new OpenInEditorVisitor(activePage, false);
            component.accept(openVisitor);
        }
    }

    private void closeAllOpenEditors() {
        IPreferenceStore preferenceStore = Activator.getDefault()
                .getPreferenceStore();
        boolean closeOpenEditors = preferenceStore
                .getBoolean(PreferenceConstants.CLOSE_ALL_OPEN_EDITOR_WINDOWS_IF_BOOKMARK_CATEGORY_IS_OPENED);
        if (closeOpenEditors){
            commandInvoker.invoke(new CloseAllOpenEditorsCommand());
        }
    }

}
