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
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.commands.AddElementCommand;
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
        } else if (part instanceof IEditorPart && selection instanceof ITextSelection) {
            processEditorSelection((IEditorPart) part, (ITextSelection) selection);
        }

        return null;
    }

    private void processEditorSelection(IEditorPart part, ITextSelection selection) {
        IEditorInput editorInput = ((IEditorPart) part).getEditorInput();
        final ITypeRoot root = (ITypeRoot) JavaUI.getEditorInputJavaElement(editorInput);
        if (root == null) {
            return;
        }
        //TODO: wie krieg ich nicht-java dateien?
        Optional<IJavaElement[]> elements = getJavaElements(root, selection);
        List<Object> objects = Lists.newArrayList();
        if (elements.isPresent()) {
            for (IJavaElement element : elements.get()) {
                objects.add(element);
            }
        } else {
            String handleIdentifier = root.getHandleIdentifier();
            IJavaElement file = JavaCore.create(handleIdentifier);
            objects.add(file);
        }
        bookmark(objects.toArray());
    }

    private void bookmark(Object[] array) {
        Optional<IBookmarkModelComponent> dropTarget = Optional.absent();
        Optional<String> nameForNewCategory = Optional.absent();
        invoker.invoke(new AddElementCommand(array, invoker, false, dropTarget, nameForNewCategory));
    }

    private Optional<IJavaElement[]> getJavaElements(ITypeRoot root, ITextSelection selection) {
        Optional<IJavaElement[]> javaEle = Optional.absent();
        try {
            int offset = (selection).getOffset();
            IJavaElement[] codeSelect = root.codeSelect(offset, 0);
            if (codeSelect.length > 0) {
                javaEle = Optional.of(codeSelect);
            }
        } catch (JavaModelException e) {
            e.printStackTrace();
        }
        return javaEle;
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

        bookmark(objects.toArray());
    }
}
