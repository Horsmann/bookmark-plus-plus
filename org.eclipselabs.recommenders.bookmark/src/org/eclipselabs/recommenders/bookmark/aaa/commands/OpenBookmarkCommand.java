package org.eclipselabs.recommenders.bookmark.aaa.commands;

import java.util.Iterator;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipselabs.recommenders.bookmark.aaa.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.aaa.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.aaa.visitor.IsCategoryVisitor;
import org.eclipselabs.recommenders.bookmark.aaa.visitor.OpenInEditorVisitor;

public class OpenBookmarkCommand implements IBookmarkModelCommand {

    private final IStructuredSelection selections;
    private final IWorkbenchPage activePage;

    public OpenBookmarkCommand(IStructuredSelection selections, IWorkbenchPage activePage) {
        this.selections = selections;
        this.activePage = activePage;
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
            openVisitor = new OpenInEditorVisitor(activePage, true);
            component.accept(openVisitor);
        } else {
            openVisitor = new OpenInEditorVisitor(activePage, false);
            component.accept(openVisitor);
        }
    }

}
