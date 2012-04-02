package org.eclipselabs.recommenders.bookmark.view;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
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

        IJavaElementDelta[] affectedChildren = delta.getAffectedChildren();
        for (IJavaElementDelta affected : affectedChildren) {
            String[] pair = getRenameInformation(affected.getAffectedChildren());

            if (!validBeforeAfterId(pair[0], pair[1])) {
                return;
            }
            BookmarkRenameVisitor visitor = new BookmarkRenameVisitor(pair[0], pair[1]);
            saveChangeInQueueUntilEditorIsSaved(visitor);
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

    private String[] getRenameInformation(IJavaElementDelta[] affectedChildren) {
        String[] pair = new String[] { "", "" };
        if (affectedChildren.length == 2) {
            pair[0] = getIdBeforeChange(affectedChildren);
            pair[1] = getIdAfterChange(affectedChildren);
            System.out.println("Before: " + pair[0] + "\nAfter: " + pair[1]);
        } else if (affectedChildren.length == 1) {
            pair = getRenameInformation(affectedChildren[0].getAffectedChildren());
        } else if (affectedChildren.length == 3) {
            if (didElementBreakDuringRename(affectedChildren)) {
                saveIdBeforeItBroke(affectedChildren);
                System.out.println("broke");
            } else if (isBrokenElementCorrected(affectedChildren)) {
                pair = getValidIdPairFromStorage(affectedChildren);
                System.out.println("fixed, Before: " + pair[0] + "\nAfter: " + pair[1]);
            }
            // System.out.println("Add: " + IJavaElementDelta.ADDED + "\n" +
            // "Remove: " + IJavaElementDelta.REMOVED);
//            System.out.println(affectedChildren[0].getKind() + "Id: "
//                    + affectedChildren[0].getElement().getHandleIdentifier());
//            System.out.println(affectedChildren[1].getKind() + " Id: "
//                    + affectedChildren[1].getElement().getHandleIdentifier());
//            System.out.println(affectedChildren[2].getKind() + " Id: "
//                    + affectedChildren[2].getElement().getHandleIdentifier());
        } else {
            System.out.println("array-length: " + affectedChildren.length);
        }
        // System.out.println("Length: " + affectedChildren.length);
        System.out.println();
        return pair;
    }

    private String[] getValidIdPairFromStorage(IJavaElementDelta[] affectedChildren) {
        String handleIdentifier = affectedChildren[1].getElement().getHandleIdentifier();
        String string = brokenChanges.remove(handleIdentifier);
        if (string != null) {
            return new String[] { string, affectedChildren[0].getElement().getHandleIdentifier() };
        }
        return new String[] { "", "" };
    }

    private boolean isBrokenElementCorrected(IJavaElementDelta[] affectedChildren) {
        IJavaElementDelta child1 = affectedChildren[0];
        IJavaElementDelta child2 = affectedChildren[1];
        IJavaElementDelta child3 = affectedChildren[2];
        return isAdd(child1) && isRemove(child2) && isRemove(child3);
    }

    private void saveIdBeforeItBroke(IJavaElementDelta[] affectedChildren) {
        String idChild1 = affectedChildren[0].getElement().getHandleIdentifier();
        String idChild2 = affectedChildren[1].getElement().getHandleIdentifier();
        String idChild3 = affectedChildren[2].getElement().getHandleIdentifier();
        brokenChanges.put(idChild1, idChild3);
        brokenChanges.put(idChild2, idChild3);
    }

    private boolean didElementBreakDuringRename(IJavaElementDelta[] affectedChildren) {
        IJavaElementDelta child1 = affectedChildren[0];
        IJavaElementDelta child2 = affectedChildren[1];
        IJavaElementDelta child3 = affectedChildren[2];
        return isAdd(child1) && isAdd(child2) && isRemove(child3);
    }

    private boolean isRemove(IJavaElementDelta child) {
        return child.getKind() == IJavaElementDelta.REMOVED;
    }

    private boolean isAdd(IJavaElementDelta child) {
        return child.getKind() == IJavaElementDelta.ADDED;
    }

    private Optional<IEditorPart> getEditorPartsOfActivePage() {
        Optional<IEditorPart> editor = Optional.absent();
        IWorkbench workbench = PlatformUI.getWorkbench();
        IWorkbenchWindow[] workbenchWindows = workbench.getWorkbenchWindows();
        for (IWorkbenchWindow ww : workbenchWindows) {
            IWorkbenchPage activePage = ww.getActivePage();
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
}