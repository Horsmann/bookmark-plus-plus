package org.eclipselabs.recommenders.bookmark.renameparticipant;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
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

        if (!(element instanceof IJavaElement)) {
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

    private boolean initSuccessful() {
        return elementType != RenameSupported.NOT_SUPPORTED && !oldHandleId.equals("") && !newHandleId.equals("");
    }

    private String generateNewHandleId(String oldHandleId) {

        String id = "";

        switch (elementType) {
        case IFIELD:
            id = generateNewFieldHandleId(oldHandleId, getArguments().getNewName());
            break;
        case IMETHOD:
            id = generateNewMethodHandleId(oldHandleId, getArguments().getNewName());
            break;
        }

        return id;
    }

    private String generateNewMethodHandleId(String oldHandleId, String newName) {
        int start = oldHandleId.indexOf("~");
        int end = oldHandleId.indexOf("~", start + 1);

        if (start == -1) {
            return "";
        }
        if (end > -1) {
            return oldHandleId.substring(0, start + 1) + newName + oldHandleId.substring(end);
        } else {
            return oldHandleId.substring(0, start + 1) + newName;
        }
    }

    private String generateNewFieldHandleId(String oldHandleId, String newFieldName) {
        int index = oldHandleId.lastIndexOf("^");
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
        if (isField(elementType) || isMethod(elementType)) {
            return new MethodFieldUpdateVisitor(oldHandleId, newHandleId);
        }
        return null;
    }

    private boolean isField(RenameSupported elementType) {
        return elementType == RenameSupported.IFIELD;
    }

    private boolean isMethod(RenameSupported elementType) {
        return elementType == RenameSupported.IMETHOD;
    }

}

enum RenameSupported {
    ICOMPILATION_UNIT, ITYPE, IMETHOD, IFIELD, NOT_SUPPORTED;
}
