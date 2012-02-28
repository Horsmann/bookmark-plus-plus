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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.BookmarkUtil;
import org.eclipselabs.recommenders.bookmark.commands.AddBookmarksCommand;
import org.eclipselabs.recommenders.bookmark.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.view.BookmarkCommandInvoker;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

public class BookmarkCurrentPositionHandler extends AbstractHandler {

    private BookmarkCommandInvoker invoker;

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        invoker = Activator.getCommandInvoker();
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
        if (root != null) {
            processJavaElements(root, selection);
        } else {
            processIFile(editorInput);
        }
    }

    private void processIFile(IEditorInput editorInput) {
        Object adapter = editorInput.getAdapter(IFile.class);
        if (adapter != null) {
            bookmark(new Object[] { adapter });
        }
    }

    private void processJavaElements(ITypeRoot root, ITextSelection selection) {

        IJavaElement element;
        element = getEnclosingJavaElement(root, selection.getOffset());
        if (element == null) {
            String handleIdentifier = root.getHandleIdentifier();
            element = JavaCore.create(handleIdentifier);
        }
        if (element != null && BookmarkUtil.isInternalElement(element)) {
            bookmark(new Object[] { element });
        }
    }

    private IJavaElement getEnclosingJavaElement(ITypeRoot root, int offset) {
        try {
            return root.getElementAt(offset);
        } catch (JavaModelException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void bookmark(Object[] objects) {

        if (objects.length == 0) {
            return;
        }

        Optional<IBookmarkModelComponent> dropTarget = getDropTarget();
        Optional<String> nameForNewCategory = Optional.absent();
        invoker.invoke(new AddBookmarksCommand(objects, invoker, false, dropTarget, nameForNewCategory));
    }

    private Optional<IBookmarkModelComponent> getDropTarget() {
        IStructuredSelection currentTreeSelection = Activator.getCurrentTreeSelection();
        IBookmarkModelComponent target = (IBookmarkModelComponent) currentTreeSelection.getFirstElement();
        if (target != null) {
            return Optional.of(target);
        }
        return Optional.absent();
    }

    private void processTreeSelection(ITreeSelection selection) {

        List<Object> objects = Lists.newArrayList();
        @SuppressWarnings("rawtypes")
        Iterator iterator = selection.iterator();

        while (iterator.hasNext()) {
            Object next = iterator.next();
            if (next instanceof IJavaElement && BookmarkUtil.isBookmarkable(next)
                    && BookmarkUtil.isInternalElement((IJavaElement) next)) {
                objects.add(next);
            } else if (next instanceof IFile) {
                objects.add(next);
            }
        }

        if (objects.size() > 0) {
            bookmark(objects.toArray());
        }
    }

}
