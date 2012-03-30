package org.eclipselabs.recommenders.bookmark.view;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartConstants;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.commands.RenameJavaElementsCommand;
import org.eclipselabs.recommenders.bookmark.model.IModelVisitor;
import org.eclipselabs.recommenders.bookmark.renameparticipant.BookmarkRenameVisitor;

import com.google.common.base.Optional;

public class JavaElementChangedListener implements IElementChangedListener {

    private HashMap<IEditorPart, List<IModelVisitor>> outstandingNameChanges = new HashMap<IEditorPart, List<IModelVisitor>>();

    @Override
    public void elementChanged(ElementChangedEvent event) {
        IJavaElementDelta delta = event.getDelta();
        if (!(delta.getKind() == IJavaElementDelta.CHANGED)) {
            return;
        }

        // IEditorInput editorInput = editorPart.getEditorInput();
        // String name = editorInput.getName();
        // if (editorInput instanceof ICompilationUnit) {
        // System.out.println("editorInput is IcompUnit");
        // } else if (editorInput instanceof IFile) {
        // System.out.println("editorInput is ifile");
        // } else if (editorInput instanceof FileEditorInput) {
        // System.out.println("editorInput is fileEditorInput");
        // FileEditorInput fei = (FileEditorInput) editorInput;
        // System.out.println("currently opened: " +
        // fei.getPath().toOSString());
        // } else {
        // System.out.println("non of the above input types");
        // }
        // if (editorPart != null) {
        // if (editorPart.isDirty()) {
        // System.out.println("dirty");
        // } else {
        // System.out.println("saved");
        // }
        // }
        // }

        IJavaElementDelta[] affectedChildren = delta.getAffectedChildren();
        for (IJavaElementDelta affected : affectedChildren) {
            IJavaElementDelta[] beforeAfter = affected.getAffectedChildren();
            String idAfter = getIdAfterChange(beforeAfter);
            String idBefore = getIdBeforeChange(beforeAfter);

            if (!validBeforeAfterId(idBefore, idAfter)) {
                return;
            }
            BookmarkRenameVisitor visitor = new BookmarkRenameVisitor(idBefore, idAfter);
            // ////////

            IEditorPart editorPart;
            Optional<IWorkbenchPage> activePage = getActiveWorkbenchPage();
            if (activePage.isPresent()) {
                editorPart = activePage.get().getActiveEditor();
                registerListenerForDirtyEditorStateHandling(editorPart);
                
                storeChanges(editorPart, visitor);
                System.out.println(editorPart.toString());
            }

            // ////////
            // Activator.getCommandInvoker().invoke(new
            // RenameJavaElementsCommand(visitor));
        }
    }
    
    public void removeOustandingUpdates(IEditorPart editorPart){
        outstandingNameChanges.remove(editorPart);
    }

    private void registerListenerForDirtyEditorStateHandling(IEditorPart editorPart) {
        ActivePagePartListener pageListener = registerPartListenerForDirtyCloses(editorPart);
        EditorPropertyChangeListener propertyListener = registerPropertyListener(editorPart);
        
        propertyListener.setActivePageListener(pageListener);
        pageListener.setPropertyListener(propertyListener);
    }

    private void storeChanges(IEditorPart editorPart, BookmarkRenameVisitor visitor) {
        List<IModelVisitor> list = getList(editorPart);
        list.add(visitor);
    }

    private List<IModelVisitor> getList(IEditorPart editorPart) {
        if (outstandingNameChanges.get(editorPart) == null) {
            List<IModelVisitor> outstanding = new LinkedList<IModelVisitor>();
            outstandingNameChanges.put(editorPart, outstanding);
            return outstanding;
        }
        return outstandingNameChanges.get(editorPart);
    }

    private void executeChanges(IEditorPart part) {
        List<IModelVisitor> list = getList(part);
        outstandingNameChanges.remove(part);
        for (IModelVisitor v : list) {
            Activator.getCommandInvoker().invoke(new RenameJavaElementsCommand(v));
            System.out.println(v);
        }
        System.out.println("Changes executed");
    }

    private ActivePagePartListener registerPartListenerForDirtyCloses(IEditorPart editorPart) {
        ActivePagePartListener activePagePartListener = new ActivePagePartListener(editorPart, this);
        editorPart.getSite().getPage().addPartListener(activePagePartListener);
        return activePagePartListener;
    }

    private EditorPropertyChangeListener registerPropertyListener(IEditorPart editorPart) {
        EditorPropertyChangeListener propertyListener = new EditorPropertyChangeListener(editorPart, this);
        editorPart.addPropertyListener(propertyListener);
        return propertyListener;
    }

    private Optional<IWorkbenchPage> getActiveWorkbenchPage() {
        Optional<IWorkbenchPage> page = Optional.absent();
        IWorkbench workbench = PlatformUI.getWorkbench();
        if (workbench != null) {
            IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
            if (activeWorkbenchWindow != null) {
                IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
                page = Optional.of(activePage);
            }
        }
        return page;
    }

    private boolean validBeforeAfterId(String idBefore, String idAfter) {
        return !idBefore.equals("") && !idAfter.equals("");
    }

    private String getIdAfterChange(IJavaElementDelta[] beforeAfter) {
        if (beforeAfter.length != 2) {
            return "";
        }
        IJavaElementDelta after = beforeAfter[0];
        IJavaElement element = after.getElement();
        if (element != null) {
            return element.getHandleIdentifier();
        }
        return "";
    }

    private String getIdBeforeChange(IJavaElementDelta[] beforeAfter) {
        if (beforeAfter.length != 2) {
            return "";
        }
        IJavaElementDelta before = beforeAfter[1];
        IJavaElement element = before.getElement();
        if (element != null) {
            return element.getHandleIdentifier();
        }
        return "";
    }

    class EditorPropertyChangeListener implements IPropertyListener {

        private final IEditorPart editorPart;
        private final JavaElementChangedListener javaElementChangedListener;
        private ActivePagePartListener pageListener;

        public EditorPropertyChangeListener(IEditorPart editorPart, JavaElementChangedListener javaElementChangedListener) {
            this.editorPart = editorPart;
            this.javaElementChangedListener = javaElementChangedListener;
        }
        
        public void setActivePageListener(ActivePagePartListener pageListener){
            this.pageListener = pageListener;
        }

        @Override
        public void propertyChanged(Object source, int propId) {
            if (!editorPart.isDirty() && propId == IWorkbenchPartConstants.PROP_DIRTY) {
                System.out.println("Fire update");
                javaElementChangedListener.executeChanges(editorPart);
                pageListener.removeListener();
            }
        }

        public void removeListener() {
            editorPart.removePropertyListener(this);
        }
    }

    class ActivePagePartListener implements IPartListener2 {

        private EditorPropertyChangeListener propertyListener;
        private final JavaElementChangedListener javaElementChangedListener;
        private IEditorPart editorPart;

        public ActivePagePartListener(IEditorPart editorPart, JavaElementChangedListener javaElementChangedListener) {
            this.editorPart = editorPart;
            this.javaElementChangedListener = javaElementChangedListener;
        }

        public void setPropertyListener(EditorPropertyChangeListener propertyListener) {
            this.propertyListener = propertyListener;
        }

        @Override
        public void partActivated(IWorkbenchPartReference partRef) {
        }

        @Override
        public void partBroughtToTop(IWorkbenchPartReference partRef) {
        }

        @Override
        public void partClosed(IWorkbenchPartReference partRef) {
            if (partRef.isDirty()) {
                javaElementChangedListener.removeOustandingUpdates(editorPart);
                System.out.println("Discard updates");
            } else {
                System.out.println("Do nothing everything is up-to-date");
            }
            removeListener();
            propertyListener.removeListener();
        }

        @Override
        public void partDeactivated(IWorkbenchPartReference partRef) {
        }

        @Override
        public void partOpened(IWorkbenchPartReference partRef) {
        }

        @Override
        public void partHidden(IWorkbenchPartReference partRef) {
        }

        @Override
        public void partVisible(IWorkbenchPartReference partRef) {
        }

        @Override
        public void partInputChanged(IWorkbenchPartReference partRef) {
        }

        public void removeListener() {
            editorPart.getSite().getPage().removePartListener(this);
        }

    }
}