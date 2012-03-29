package org.eclipselabs.recommenders.bookmark.renameparticipant;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;

public class JavaElementRenameParticipant extends RenameParticipant {

    private String oldHandleId;
    private String newHandleId;

    private RenameSupported elementType = RenameSupported.NOT_SUPPORTED;

    @Override
    protected boolean initialize(Object element) {

        if (!isJavaElement(element)) {
            return false;
        }

        oldHandleId = ((IJavaElement) element).getHandleIdentifier();
        detectElementType((IJavaElement) element);
        newHandleId = generateNewHandleId(oldHandleId);
        if (!initSuccessful()) {
            return false;
        }
        return true;
    }

    private boolean isJavaElement(Object element) {
        return element instanceof IJavaElement;
    }

    private boolean initSuccessful() {
        return elementType != RenameSupported.NOT_SUPPORTED && !oldHandleId.equals("") && !newHandleId.equals("");
    }

    private String generateNewHandleId(String oldHandleId) {

        String id = "";

        String newName = getArguments().getNewName();
        switch (elementType) {
        case IFIELD:
            id = updateIdStem(oldHandleId, newName, "^", false);
            break;
        case ITYPE:
            id = updateIdStem(oldHandleId, newName, "[", false);
            break;
        case ICOMPILATION_UNIT:
            id = updateIdStem(oldHandleId, newName, "{", true);
            break;
        case IPACKAGE_FRAGMENT:
            id = updateIdStem(oldHandleId, newName, "<", true);
            break;
        case IPACKAGE_FRAGMENT_ROOT:
            id = updateIdStem(oldHandleId, newName, "/", true);
            break;
        case IMETHOD:
            id = updateMethodIdStem(oldHandleId, newName);
            break;
        }

        return id;
    }

    private String updateMethodIdStem(String oldHandleId, String newName) {
        String id = "";

        int start = oldHandleId.indexOf("~");
        int end = oldHandleId.indexOf("~", start + 1);

        if (start > -1 && end > -1) {
            id = oldHandleId.substring(0, start + 1) + newName + oldHandleId.substring(end);
        } else if (start > -1) {
            id = oldHandleId.substring(0, start + 1) + newName;
        }
        return id;
    }

    private String updateIdStem(String oldHandleId, String newFieldName, String sign, boolean searchFromLeftSide) {
        int index = -1;
        if (searchFromLeftSide) {
            index = oldHandleId.indexOf(sign);
        } else {
            index = oldHandleId.lastIndexOf(sign);
        }
        String newId = oldHandleId.substring(0, index + 1) + newFieldName;
        return newId;
    }

    private void detectElementType(IJavaElement element) {

        if (element instanceof IField) {
            elementType = RenameSupported.IFIELD;
        } else if (element instanceof IMethod) {
            elementType = RenameSupported.IMETHOD;
        } else if (element instanceof IType) {
            elementType = RenameSupported.ITYPE;
        } else if (element instanceof ICompilationUnit) {
            elementType = RenameSupported.ICOMPILATION_UNIT;
        } else if (element instanceof IPackageFragment) {
            elementType = RenameSupported.IPACKAGE_FRAGMENT;
        } else if (element instanceof IPackageFragmentRoot) {
            elementType = RenameSupported.IPACKAGE_FRAGMENT_ROOT;
        }

    }

    @Override
    public String getName() {
        return "JavaElement Rename Participant";
    }

    @Override
    public RefactoringStatus checkConditions(IProgressMonitor pm, CheckConditionsContext context)
            throws OperationCanceledException {

        if (pm == null) {
            pm = new NullProgressMonitor();
        }

        pm.beginTask("Update bookmarks", 1);
        pm.worked(1);
        pm.done();

        return new RefactoringStatus();
    }

    @Override
    public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        return new JavaElementChange(getVisitor(elementType, oldHandleId, newHandleId));
    }

    private IJavaElementChangeVisitor getVisitor(RenameSupported elementType, String oldHandleId, String newHandleId) {
        return new JavaElementRenameVisitor(oldHandleId, newHandleId);
    }

}

enum RenameSupported {
    IPACKAGE_FRAGMENT_ROOT, IPACKAGE_FRAGMENT, ICOMPILATION_UNIT, ITYPE, IMETHOD, IFIELD, NOT_SUPPORTED;
}
