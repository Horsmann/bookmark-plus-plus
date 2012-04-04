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

        LinkedList<DeltaItem> elementChanges = getChangeInformation(delta);
        elementChanges = extractChangesFromDeltaInformation(elementChanges);

        if (elementChanges.size() == 2) {
            for (DeltaItem item : elementChanges) {
                System.out.println(item.kind + " " + item.id);
            }
            String idBefore = (elementChanges.get(0).kind == IJavaElementDelta.REMOVED ? elementChanges.get(0).id
                    : elementChanges.get(1).id);
            String idAfter = (elementChanges.get(0).kind == IJavaElementDelta.ADDED ? elementChanges.get(0).id
                    : elementChanges.get(1).id);

            System.out.println("Before: " + idBefore);
            System.out.println("After: " + idAfter);
            BookmarkRenameVisitor visitor = new BookmarkRenameVisitor(idBefore, idAfter);
            Activator.getCommandInvoker().invoke(new RenameJavaElementsCommand(visitor));
        }
        System.out.println("\n");
        // saveChangeInQueueUntilEditorIsSaved(visitor);

    }

    private LinkedList<DeltaItem> extractChangesFromDeltaInformation(LinkedList<DeltaItem> elementChanges) {

        if (elementChanges.size() == 2) {
            return processTwoElementEvent(elementChanges);
        } else if (elementChanges.size() == 3) {
            return processThreeElementEvent(elementChanges);
        } else if (elementChanges.size() == 4) {
            return processFourElementEvent(elementChanges);
        } else if (elementChanges.size() >= 5) {
            return processThreeElementEvent(elementChanges);
        }

        return new LinkedList<JavaElementChangedListener.DeltaItem>();
    }

    private LinkedList<DeltaItem> processTwoElementEvent(LinkedList<DeltaItem> elementChanges) {
        if (isConsitentBeforeAfterInformation(elementChanges)) {
            return elementChanges;
        }
        return new LinkedList<JavaElementChangedListener.DeltaItem>();
    }

    private LinkedList<DeltaItem> processThreeElementEvent(LinkedList<DeltaItem> elementChanges) {
        if (is_1xAdd_2xRemove(elementChanges)) {
            return recoverOldIdFromFragments(elementChanges);
        } else if (is_1xRemove_with_several_adds(elementChanges)) {
            return mapAddedFragmentsToRemovedId(elementChanges);
        }
        return new LinkedList<DeltaItem>();
    }

    private LinkedList<DeltaItem> processFourElementEvent(LinkedList<DeltaItem> elementChanges) {
        elementChanges.remove(1);
        elementChanges.remove(2);
        return elementChanges;
    }

    private LinkedList<DeltaItem> recoverOldIdFromFragments(LinkedList<DeltaItem> elementChanges) {
        Optional<DeltaItem> added = getFirstOccurenceOfAddedDelta(elementChanges);
        if (!added.isPresent()) {
            try {
                throw new Exception("no added found");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // perform mapping of remove-paths to the added one
        String removedId = getChangeBeforeItBrokeWithFragmentIds(elementChanges, added.get());

        if (removedId != null) {
            System.out.println("recovered changes");
            return assembleCompleteChangeEvent(removedId, added.get());
        }

        // "obtained 2 fragments that have to be mapped on the added element";
        return new LinkedList<DeltaItem>();
    }

    private LinkedList<DeltaItem> assembleCompleteChangeEvent(String removedId, DeltaItem added) {
        LinkedList<DeltaItem> completeChange = new LinkedList<DeltaItem>();
        completeChange.add(new DeltaItem(IJavaElementDelta.REMOVED, removedId));
        completeChange.add(new DeltaItem(IJavaElementDelta.ADDED, added.id));
        return completeChange;
    }

    private String getChangeBeforeItBrokeWithFragmentIds(LinkedList<DeltaItem> elementChanges, DeltaItem added) {
        String removedId = null;
        for (DeltaItem item : elementChanges) {
            if (item != added) {
                String mapValue = brokenChanges.remove(item.id);
                removedId = assignIfStillNull(mapValue, removedId);
            }
        }
        return removedId;
    }

    private String assignIfStillNull(String mapValue, String removedId) {
        if (mapValue != null && removedId == null) {
            removedId = mapValue;
        }
        return removedId;
    }

    private Optional<DeltaItem> getFirstOccurenceOfAddedDelta(LinkedList<DeltaItem> elementChanges) {
        Optional<DeltaItem> added = Optional.absent();
        for (DeltaItem item : elementChanges) {
            if (item.kind == IJavaElementDelta.ADDED) {
                added = Optional.of(item);
                break;
            }
        }
        return added;
    }

    private LinkedList<DeltaItem> mapAddedFragmentsToRemovedId(LinkedList<DeltaItem> elementChanges) {
        System.out.println("process3_1xRemove_severalAdds");

        Optional<DeltaItem> removed = getFirstOccurenceOfRemovedDelta(elementChanges);
        if (!removed.isPresent()) {
            try {
                throw new Exception("no remove found");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // perform mapping of remove-paths to the added one
        for (DeltaItem item : elementChanges) {
            if (item != removed.get() && item.kind == IJavaElementDelta.ADDED) {
                brokenChanges.put(item.id, removed.get().id);
            }
        }

        // DeltaItem deltaItem = elementChanges.get(0);
        // DeltaItem deltaItem2 = elementChanges.get(1);
        // DeltaItem deltaItem3 = elementChanges.get(2);
        //
        // if (implementSameIJavaElementSubInterface(deltaItem, deltaItem2)) {
        // elementChanges.remove(deltaItem3);
        // } else {
        // elementChanges.remove(deltaItem2);
        // }
        System.out.println("saved to storage");

        return new LinkedList<JavaElementChangedListener.DeltaItem>();
    }

    private Optional<DeltaItem> getFirstOccurenceOfRemovedDelta(LinkedList<DeltaItem> elementChanges) {
        Optional<DeltaItem> removed = Optional.absent();
        for (DeltaItem item : elementChanges) {
            if (item.kind == IJavaElementDelta.REMOVED) {
                removed = Optional.of(item);
                break;
            }
        }
        return removed;
    }

    private boolean isConsitentBeforeAfterInformation(LinkedList<DeltaItem> renameInformation) {
        DeltaItem deltaItem = renameInformation.get(0);
        DeltaItem deltaItem2 = renameInformation.get(1);

        return implementSameIJavaElementSubInterface(deltaItem, deltaItem2)
                && isOneAddOneRemoveDelta(deltaItem, deltaItem2);
    }

    private boolean isOneAddOneRemoveDelta(DeltaItem deltaItem, DeltaItem deltaItem2) {
        return (deltaItem.kind == IJavaElementDelta.ADDED && deltaItem2.kind == IJavaElementDelta.REMOVED)
                || (deltaItem.kind == IJavaElementDelta.REMOVED && deltaItem2.kind == IJavaElementDelta.ADDED);
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

    private boolean is_1xRemove_with_several_adds(LinkedList<DeltaItem> renameInformation) {
        boolean remove = false;

        for (DeltaItem deltaEvent : renameInformation) {
            if (deltaEvent.kind == IJavaElementDelta.REMOVED && remove == false) {
                remove = true;
            } else {
                remove = false; // expected only 1 add event
            }
        }
        return remove;
    }

    private boolean is_1xAdd_2xRemove(LinkedList<DeltaItem> renameInformation) {
        boolean add = false;

        for (DeltaItem deltaEvent : renameInformation) {
            if (deltaEvent.kind == IJavaElementDelta.ADDED && add == false) {
                add = true;
            } else if (deltaEvent.kind == IJavaElementDelta.ADDED && add == true) {
                add = false; // expected only 1 add event
            }
        }
        return add;
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

    private LinkedList<DeltaItem> getChangeInformation(IJavaElementDelta delta) {

        LinkedList<DeltaItem> list = new LinkedList<JavaElementChangedListener.DeltaItem>();

        switch (delta.getKind()) {
        case IJavaElementDelta.ADDED: {
            String id = delta.getElement().getHandleIdentifier();
            list.add(new DeltaItem(IJavaElementDelta.ADDED, id));
            break;
        }
        case IJavaElementDelta.REMOVED: {
            String id = delta.getElement().getHandleIdentifier();
            list.add(new DeltaItem(IJavaElementDelta.REMOVED, id));
            break;
        }
        case IJavaElementDelta.CHANGED:
            if ((delta.getFlags() & IJavaElementDelta.F_CHILDREN) != 0) {
                for (IJavaElementDelta child : delta.getAffectedChildren()) {
                    list.addAll(getChangeInformation(child));
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