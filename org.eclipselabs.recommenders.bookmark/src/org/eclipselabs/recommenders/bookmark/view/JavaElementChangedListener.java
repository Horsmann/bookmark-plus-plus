package org.eclipselabs.recommenders.bookmark.view;

import java.util.HashMap;
import java.util.LinkedList;

import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.commands.RenameJavaElementsCommand;
import org.eclipselabs.recommenders.bookmark.renameparticipant.BookmarkRenameVisitor;

import com.google.common.base.Optional;

public class JavaElementChangedListener implements IElementChangedListener {

    private HashMap<String, String> brokenChanges = new HashMap<String, String>();
    private String singleRemove = "";

    @Override
    public void elementChanged(ElementChangedEvent event) {
        IJavaElementDelta delta = event.getDelta();
        // traverseAndPrint(delta);
        CompilationUnit compilationUnitAST = delta.getCompilationUnitAST();
        if (compilationUnitAST != null) {
            IProblem[] problems = compilationUnitAST.getProblems();
            System.out.println("Problems: " + problems.length);
            for (IProblem problem : problems) {
                System.out.println("Error: " + problem.isError() + "\n" + problem.getID() + "==?" + IProblem.Syntax);
            }
        }
        LinkedList<DeltaItem> elementChanges = transformEventDataIntoOwnDataStructure(delta);
        elementChanges = reduceEventDataToOneBeforeAndAfterEvent(elementChanges);

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

            if (idBefore == null || idAfter == null || idBefore.equals("") || idAfter.equals("")) {
                int a = 0;
                a++;
            }

            BookmarkRenameVisitor visitor = new BookmarkRenameVisitor(idBefore, idAfter);
            Activator.getCommandInvoker().invoke(new RenameJavaElementsCommand(visitor));
            System.out.println("\n");
        }
    }

    private LinkedList<DeltaItem> reduceEventDataToOneBeforeAndAfterEvent(LinkedList<DeltaItem> elementChanges) {

        if (elementChanges.size() == 1) {
            return processOneChangeEvent(elementChanges.get(0));
        } else if (elementChanges.size() == 2) {
            return processTwoChangeEvents(elementChanges);

        } else if (elementChanges.size() >= 3 /*
                                               * elementChanges.size() == 3 ||
                                               * elementChanges.size() >= 5
                                               */) {
            return processThreeOrMoreChangeEvents(elementChanges);
        }
        // } else if (elementChanges.size() == 4) {
        // return processFourElementEvent(elementChanges);
        // }

        return new LinkedList<DeltaItem>();
    }

    private LinkedList<DeltaItem> processTwoChangeEvents(LinkedList<DeltaItem> elementChanges) {
        DeltaItem itemOne = elementChanges.get(0);
        DeltaItem itemTwo = elementChanges.get(1);
        boolean deltaOneValid = !isTrash(itemOne);
        boolean deltaTwoValid = !isTrash(itemTwo);
        if (deltaOneValid && deltaTwoValid) {
            return processTwoValidChangeEvents(elementChanges);
        }
        if (!deltaOneValid && deltaTwoValid) {
            return processFirstEventInvalidSecondEventValid(itemOne, itemTwo);
        }
        return processFirstEventValidSecondEventInvalid(itemOne, itemTwo);
    }

    private LinkedList<DeltaItem> processFirstEventValidSecondEventInvalid(DeltaItem itemOne, DeltaItem itemTwo) {
        if (itemOne.kind == IJavaElementDelta.REMOVED) {
            singleRemove = itemOne.id;
        } else if (!singleRemove.equals("")) {
            LinkedList<DeltaItem> list = new LinkedList<DeltaItem>();
            list.add(itemOne);
            list.add(new DeltaItem(IJavaElementDelta.REMOVED, getStoredRemoveId()));
            return list;
        }
        return new LinkedList<DeltaItem>();
    }

    private LinkedList<DeltaItem> processFirstEventInvalidSecondEventValid(DeltaItem itemOne, DeltaItem itemTwo) {
        if (itemTwo.kind == IJavaElementDelta.REMOVED) {
            singleRemove = itemTwo.id;
        } else if (!singleRemove.equals("")) {
            LinkedList<DeltaItem> list = new LinkedList<DeltaItem>();
            list.add(itemOne);
            list.add(new DeltaItem(IJavaElementDelta.REMOVED, getStoredRemoveId()));
            return list;
        }
        return new LinkedList<DeltaItem>();
    }

    private String getStoredRemoveId() {
        String savedId = singleRemove;
        singleRemove = "";
        return savedId;
    }

    private LinkedList<DeltaItem> processOneChangeEvent(DeltaItem deltaItem) {

        if (deltaItem.kind == IJavaElementDelta.REMOVED) {
            singleRemove = deltaItem.id;
        } else if (deltaItem.kind == IJavaElementDelta.ADDED && !singleRemove.contentEquals("")) {
            LinkedList<DeltaItem> list = new LinkedList<JavaElementChangedListener.DeltaItem>();
            list.add(deltaItem);
            list.add(new DeltaItem(IJavaElementDelta.REMOVED, singleRemove));
            return list;
        }
        return new LinkedList<DeltaItem>();
    }

    private boolean isTrash(DeltaItem delta) {
        return delta.id.endsWith("|1");
    }

    private LinkedList<DeltaItem> processTwoValidChangeEvents(LinkedList<DeltaItem> elementChanges) {
        if (isConsitentBeforeAfterInformation(elementChanges)) {
            return elementChanges;
        }
        return new LinkedList<JavaElementChangedListener.DeltaItem>();
    }

    private LinkedList<DeltaItem> processThreeOrMoreChangeEvents(LinkedList<DeltaItem> elementChanges) {
        if (is1xAddWithSeveralRemoves(elementChanges)) {
            return recoverOldIdFromFragments(elementChanges);
        } else if (is1xRemoveWithSeveralAdds(elementChanges)) {
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

    private boolean is1xRemoveWithSeveralAdds(LinkedList<DeltaItem> renameInformation) {
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

    private boolean is1xAddWithSeveralRemoves(LinkedList<DeltaItem> renameInformation) {
        boolean add = false;

        for (DeltaItem deltaEvent : renameInformation) {
            if (deltaEvent.kind == IJavaElementDelta.ADDED && add == false) {
                add = true;
            } else if (deltaEvent.kind == IJavaElementDelta.ADDED && add == true) {
                add = false; // expected only 1 add event
                break;
            }
        }
        return add;
    }

    private LinkedList<DeltaItem> transformEventDataIntoOwnDataStructure(IJavaElementDelta delta) {

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
                    list.addAll(transformEventDataIntoOwnDataStructure(child));
                }
            }
            break;
        }

        return list;
    }

    class DeltaItem {
        int kind; // refers to IJavaElementDelta constants
        String id;

        public DeltaItem(int removed, String id) {
            this.kind = removed;
            this.id = id;
        }

        @Override
        public String toString() {
            return getTextForKind() + ": " + id;
        }

        private String getTextForKind() {
            return (kind == IJavaElementDelta.ADDED ? "ADDED" : (kind == IJavaElementDelta.REMOVED) ? "REMOVED"
                    : "UNKNOWN");
        }
    }
}