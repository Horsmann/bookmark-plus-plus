package org.eclipselabs.recommenders.bookmark.view.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.view.BookmarkCommandInvoker;

public class BookmarkCurrentPositionHandler extends AbstractHandler {

    private BookmarkCommandInvoker invoker;

    public BookmarkCurrentPositionHandler() {
        invoker = Activator.getCommandInvoker();
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {

        final IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        IWorkbenchPart part = activePage.getActivePart();
        ISelection selection = activePage.getSelection();

        if (selection instanceof ITreeSelection) {
            Object firstElement = ((ITreeSelection) selection).getFirstElement();
            IJavaElement javaElement = null;
            if (firstElement instanceof IJavaElement) {
                javaElement = (IJavaElement) firstElement;
                System.out.println(javaElement.getHandleIdentifier());
            }
        } //else if (part instanceof JavaEditor && selection instanceof ITextSelection) {
//            final IEditorInput input = ((JavaEditor)part).getEditorInput();
//            final ITypeRoot root = (ITypeRoot) JavaUI.getEditorInputJavaElement(input);
//            final IJavaElement[] elements = root.codeSelect(selection.getOffset(), 0);
//            if (elements.length > 0) {
//                javaElement = elements[0];
//                System.out.println(javaElement.getHandleIdentifier());
//            }
//        }

        System.err.println("Executing BookmarkCurrentPositionHandler");
        return null;
    }

}
