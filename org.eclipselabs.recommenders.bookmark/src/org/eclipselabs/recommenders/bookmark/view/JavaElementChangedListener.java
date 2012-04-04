package org.eclipselabs.recommenders.bookmark.view;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartConstants;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.commands.RenameJavaElementsCommand;
import org.eclipselabs.recommenders.bookmark.model.IModelVisitor;
import org.eclipselabs.recommenders.bookmark.renameparticipant.BookmarkRenameVisitor;

import com.google.common.base.Optional;

public class JavaElementChangedListener implements IElementChangedListener {

    private HashMap<IEditorPart, List<IModelVisitor>> pendingChanges = new HashMap<IEditorPart, List<IModelVisitor>>();
    private HashMap<String, String> brokenChanges = new HashMap<String, String>();

    @Override
    public void elementChanged(ElementChangedEvent event) {
        IJavaElementDelta delta = event.getDelta();
        // traverseAndPrint(delta);

        LinkedList<DeltaItem> renameInformation = getRenameInformation(delta);

        for (DeltaItem item : renameInformation) {
            System.out.println(item.kind + " " + item.id);
        }

        System.out.println(tagEvent(renameInformation));
        System.out.println();

        // if (!validBeforeAfterId(pair[0], pair[1])) {
        // return;
        // }
        // BookmarkRenameVisitor visitor = new BookmarkRenameVisitor(pair[0],
        // pair[1]);
        // saveChangeInQueueUntilEditorIsSaved(visitor);

    }

    private String tagEvent(LinkedList<DeltaItem> renameInformation) {

        if (renameInformation.size() == 2) {
            if (doesReferToSameType(renameInformation)) {
                return "Correct before/after";
            } else {
                return "malformed event of temp. invalid model";
            }
        } else if (renameInformation.size() == 3) {
            if (isAddRemoveRemove(renameInformation)) {
                return process3_1xAdd_2xRemove(renameInformation);
                // process3_1xAdd_2xR(renameInformation);
            } else if (isAddAddRemove(renameInformation)) {
                return process3_2xAdd_1xRemove(renameInformation);
            }
        }

        return "UNKNOWN-TAG";
    }

    private String process3_1xAdd_2xRemove(LinkedList<DeltaItem> renameInformation) {
        DeltaItem deltaItem = renameInformation.get(0);
        DeltaItem deltaItem2 = renameInformation.get(1);
        DeltaItem deltaItem3 = renameInformation.get(2);
        
        return "obtained 2 fragments that have to be mapped on the added element";
    }

    private String process3_2xAdd_1xRemove(LinkedList<DeltaItem> renameInformation) {
        DeltaItem deltaItem = renameInformation.get(0);
        DeltaItem deltaItem2 = renameInformation.get(1);
        DeltaItem deltaItem3 = renameInformation.get(2);

        if (implementSameIJavaElementSubInterface(deltaItem, deltaItem2)) {
            return "Garbage-ID: " + deltaItem3.id;
        }
        return "Garbage-ID: " + deltaItem2.id;

    }
    
    private boolean doesReferToSameType(LinkedList<DeltaItem> renameInformation) {
        DeltaItem deltaItem = renameInformation.get(0);
        DeltaItem deltaItem2 = renameInformation.get(1);

        return implementSameIJavaElementSubInterface(deltaItem, deltaItem2);
    }

    private boolean implementSameIJavaElementSubInterface(DeltaItem deltaOne, DeltaItem deltaTwo) {
        if (isIField(deltaOne)) {
            return isIField(deltaTwo);
        } else if (isIMethod(deltaOne)) {
            return isIMethod(deltaTwo);
        } else if (isIType(deltaOne)) {
            return isIType(deltaTwo);
        }
        return false;
    }

    private boolean isIType(DeltaItem deltaItem) {
        return JavaCore.create(deltaItem.id) instanceof IType;
    }

    private boolean isIMethod(DeltaItem deltaItem) {
        return JavaCore.create(deltaItem.id) instanceof IMethod;
    }

    private boolean isIField(DeltaItem deltaItem) {
        return JavaCore.create(deltaItem.id) instanceof IField;
    }

    private boolean isAddAddRemove(LinkedList<DeltaItem> renameInformation) {
        boolean remove = false;
        boolean add1 = false;
        boolean add2 = false;

        for (DeltaItem di : renameInformation) {
            if (di.kind == IJavaElementDelta.REMOVED) {
                remove = true;
            }
            if (di.kind == IJavaElementDelta.ADDED) {
                if (add1 == true) {
                    add2 = true;
                } else {
                    add1 = true;
                }
            }
        }

        return remove && add1 && add2;
    }

    private boolean isAddRemoveRemove(LinkedList<DeltaItem> renameInformation) {
        boolean add = false;
        boolean remove1 = false;
        boolean remove2 = false;

        for (DeltaItem di : renameInformation) {
            if (di.kind == IJavaElementDelta.ADDED) {
                add = true;
            }
            if (di.kind == IJavaElementDelta.REMOVED) {
                if (remove1 == true) {
                    remove2 = true;
                } else {
                    remove1 = true;
                }
            }
        }

        return add && remove1 && remove2;
    }

    void traverseAndPrint(IJavaElementDelta delta) {
        switch (delta.getKind()) {
        case IJavaElementDelta.ADDED:
            System.out.println(delta.getElement().getHandleIdentifier() + " was added");
            break;
        case IJavaElementDelta.REMOVED:
            System.out.println(delta.getElement().getHandleIdentifier() + " was removed");
            break;
        case IJavaElementDelta.CHANGED:
            // System.out.println(delta.getElement().getHandleIdentifier() +
            // " was changed");
            if ((delta.getFlags() & IJavaElementDelta.F_CHILDREN) != 0) {
                System.out.println("The change was in its children");
            }
            break;
        }
        IJavaElementDelta[] children = delta.getAffectedChildren();
        for (int i = 0; i < children.length; i++) {
            traverseAndPrint(children[i]);
        }
    }

    private void saveChangeInQueueUntilEditorIsSaved(BookmarkRenameVisitor visitor) {
        Optional<IEditorPart> editorPart = getEditorPartsOfActivePage();
        if (editorPart.isPresent()) {
            setUpDirtyEditorHandling(editorPart);
            storeChanges(visitor, editorPart.get());
        }
    }

    private void setUpDirtyEditorHandling(Optional<IEditorPart> editorPart) {
        PropertyListener registerPropertyListener = registerPropertyListener(editorPart.get());
        ActivePagePartListener registerPageCloseListener = registerPageCloseListener(editorPart.get());
        registerPageCloseListener.setPropertyListener(registerPropertyListener);
    }

    private void storeChanges(BookmarkRenameVisitor visitor, IEditorPart iEditorPart) {
        List<IModelVisitor> list = getList(iEditorPart);
        list.add(visitor);
    }

    private ActivePagePartListener registerPageCloseListener(IEditorPart iEditorPart) {
        IWorkbenchPage page = iEditorPart.getSite().getPage();
        ActivePagePartListener activePageListener = new ActivePagePartListener(iEditorPart, this);
        page.addPartListener(activePageListener);
        return activePageListener;
    }

    private PropertyListener registerPropertyListener(IEditorPart iEditorPart) {
        PropertyListener propertyListener = new PropertyListener(iEditorPart);
        iEditorPart.addPropertyListener(propertyListener);
        return propertyListener;
    }

    private LinkedList<DeltaItem> getRenameInformation(IJavaElementDelta delta) {

        LinkedList<DeltaItem> list = new LinkedList<JavaElementChangedListener.DeltaItem>();

        switch (delta.getKind()) {
        case IJavaElementDelta.ADDED: {
            String id = delta.getElement().getHandleIdentifier();
            list.add(new DeltaItem(IJavaElementDelta.ADDED, id));
            // System.out.println(delta.getElement().getHandleIdentifier() +
            // " was added");
            break;
        }
        case IJavaElementDelta.REMOVED: {
            String id = delta.getElement().getHandleIdentifier();
            list.add(new DeltaItem(IJavaElementDelta.REMOVED, id));

            // System.out.println(delta.getElement().getHandleIdentifier() +
            // " was removed");
            break;
        }
        case IJavaElementDelta.CHANGED:
            // System.out.println(delta.getElement().getHandleIdentifier() +
            // " was changed");
            if ((delta.getFlags() & IJavaElementDelta.F_CHILDREN) != 0) {
                IJavaElementDelta[] children = delta.getAffectedChildren();
                for (int i = 0; i < children.length; i++) {
                    list.addAll(getRenameInformation(children[i]));
                }
            }
            break;
        }

        return list;
    }

    private Optional<IEditorPart> getEditorPartsOfActivePage() {
        Optional<IEditorPart> editor = Optional.absent();
        IWorkbench workbench = PlatformUI.getWorkbench();
        IWorkbenchWindow[] workbenchWindows = workbench.getWorkbenchWindows();
        for (IWorkbenchWindow window : workbenchWindows) {
            IWorkbenchPage activePage = window.getActivePage();
            if (activePage != null) {
                return Optional.of(activePage.getActiveEditor());
            }
        }
        return editor;
    }

    public void removeAllChangesFor(IEditorPart editorPart) {
        pendingChanges.remove(editorPart);
    }

    private List<IModelVisitor> getList(IEditorPart editorPart) {
        if (pendingChanges.get(editorPart) == null) {
            List<IModelVisitor> outstanding = new LinkedList<IModelVisitor>();
            pendingChanges.put(editorPart, outstanding);
            return outstanding;
        }
        return pendingChanges.get(editorPart);
    }

    private void executeChanges(IEditorPart part) {
        List<IModelVisitor> changes = getList(part);
        pendingChanges.remove(part);
        System.out.println("Execute");
        for (IModelVisitor change : changes) {
            System.out.println(change);
            Activator.getCommandInvoker().invoke(new RenameJavaElementsCommand(change));
        }
        System.out.println("Execute-End");
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

    class PropertyListener implements IPropertyListener {

        private final IEditorPart editorPart;

        public PropertyListener(IEditorPart editorPart) {
            this.editorPart = editorPart;
        }

        @Override
        public void propertyChanged(Object source, int propId) {
            if (editorContentSaved(propId)) {
                removeListener();
                executeChanges(editorPart);
            }
        }

        private boolean editorContentSaved(int propId) {
            return (propId == IWorkbenchPartConstants.PROP_DIRTY && !editorPart.isDirty());
        }

        public void removeListener() {
            editorPart.removePropertyListener(this);
        }

    }

    class ActivePagePartListener implements IPartListener2 {

        private PropertyListener propertyListener;
        private final JavaElementChangedListener changeListener;
        private IEditorPart editorPart;

        public ActivePagePartListener(IEditorPart editorPart, JavaElementChangedListener javaElementChangedListener) {
            this.editorPart = editorPart;
            this.changeListener = javaElementChangedListener;
        }

        public void setPropertyListener(PropertyListener propertyListener) {
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

            IWorkbenchPart part = partRef.getPart(false);
            if (part instanceof IEditorPart) {
                IEditorPart eventEditorPart = (IEditorPart) part;
                if (editorPart == eventEditorPart) {
                    changeListener.removeAllChangesFor(editorPart);
                    propertyListener.removeListener();
                }
            }

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

    }

    class DeltaItem {
        int kind; // refers to IJavaElementDelta constants
        String id;

        public DeltaItem(int removed, String id) {
            this.kind = removed;
            this.id = id;
        }
    }
}