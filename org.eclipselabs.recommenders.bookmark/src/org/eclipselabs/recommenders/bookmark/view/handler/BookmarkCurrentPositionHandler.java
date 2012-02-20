package org.eclipselabs.recommenders.bookmark.view.handler;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.swt.internal.cocoa.objc_super;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.commands.AddElementToModelCommand;
import org.eclipselabs.recommenders.bookmark.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.view.BookmarkCommandInvoker;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

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
            processTreeSelection((ITreeSelection) selection);
        }

        // if (part instanceof IEditorPart && selection instanceof
        // ITextSelection) {
        // processEditorSelection((IEditorPart)part, (ITextSelection)selection);
        //
        // int a = 0;
        // a++;
        // }

        return null;
    }

    private void processEditorSelection(IEditorPart part, ITextSelection selection) {
        IEditorInput editorInput = ((IEditorPart) part).getEditorInput();
        final ITypeRoot root = (ITypeRoot) JavaUI.getEditorInputJavaElement(editorInput);
        IJavaElement[] elements = null;
        try {
            int offset = ((ITextSelection) selection).getOffset();
            elements = root.codeSelect(offset, 0);
        } catch (JavaModelException e) {
            e.printStackTrace();
        }
        if (elements != null && elements.length > 0) {
            IJavaElement javaElement = elements[0];
            System.out.println(javaElement.getHandleIdentifier());
        }
        String handleIdentifier = root.getHandleIdentifier();
        IJavaElement create = JavaCore.create(handleIdentifier);
    }

    private void processTreeSelection(ITreeSelection selection) {

        List<Object> objects = Lists.newArrayList();
        @SuppressWarnings("rawtypes")
        Iterator iterator = selection.iterator();

        while (iterator.hasNext()) {
            Object next = iterator.next();
            if (next instanceof IJavaElement) {
                objects.add(next);
            } else if (next instanceof IFile) {
                objects.add(next);
            }
        }

        Optional<IBookmarkModelComponent> dropTarget = Optional.absent();
        Optional<String> nameForNewCategory = Optional.absent();
        invoker.invoke(new AddElementToModelCommand(objects.toArray(), invoker, false, dropTarget, nameForNewCategory));
    }
}
