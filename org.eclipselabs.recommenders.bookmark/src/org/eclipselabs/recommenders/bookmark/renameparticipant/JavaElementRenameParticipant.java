package org.eclipselabs.recommenders.bookmark.renameparticipant;

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

    private String[] change = new String[2];

    private RenameSupported elementType = RenameSupported.NOT_SUPPORTED;

    @Override
    protected boolean initialize(Object element) {

        if (!isJavaElement(element)) {
            return false;
        }

        String oldHandleId = ((IJavaElement) element).getHandleIdentifier();
        detectElementType((IJavaElement) element);
        change = generateNewHandleId(oldHandleId);
        if (!initSuccessful()) {
            return false;
        }
        return true;
    }

    private boolean isJavaElement(Object element) {
        return element instanceof IJavaElement;
    }

    private boolean initSuccessful() {
        return elementType != RenameSupported.NOT_SUPPORTED && !change[0].equals("") && !change[1].equals("");
    }

    private String[] generateNewHandleId(String oldHandleId) {

        String id = "";

        String newName = getArguments().getNewName();
        switch (elementType) {
        case IFIELD:
            id = updateIdStem(oldHandleId, newName, "^", false);
            return new String[] { oldHandleId, id };
        case ICOMPILATION_UNIT:
            id = updateIdStem(oldHandleId, newName, "{", true);
            return new String[] { oldHandleId, id };
        case IPACKAGE_FRAGMENT:
            id = updateIdStem(oldHandleId, newName, "<", true);
            return new String[] { oldHandleId, id };
        case IPACKAGE_FRAGMENT_ROOT:
            id = updateIdStem(oldHandleId, newName, "/", true);
            return new String[] { oldHandleId, id };
        case ITYPE:
            return updateTypeIdStem(oldHandleId, newName);
        case IMETHOD:
            id = updateMethodIdStem(oldHandleId, newName);
            return new String[] { oldHandleId, newName };
        }

        return new String[] { "", "" };
    }

    private String[] updateTypeIdStem(String oldHandleId, String newName) {
        String interimStem = updateIdStem(oldHandleId, newName, "[", false);
        if (isTypePreceededByCompilationUnitName(interimStem, newName)) {
            String newId = alsoUpdatePreceedingCompilationUnitName(interimStem, newName);
            String oldId = updateOldIdsCompilationUnitNameToNewName(oldHandleId, newName);
            return new String[] { oldId, newId };
        }

        return new String[] { oldHandleId, interimStem };
    }

    /**
     * The updates of ids in the model happens incremental, the id part of the
     * compilation unit will already be updated once the type information is
     * processed. the old id is still referencing to the old compilation unit
     * name. to enable a matching the old compilation unit name has to be
     * updated here otherwise the type information is not updated correctly
     */
    private String updateOldIdsCompilationUnitNameToNewName(String oldHandleId, String newName) {
        int start = oldHandleId.indexOf("{") + 1;
        int end = oldHandleId.indexOf(".java[");
        return oldHandleId.substring(0, start) + newName + oldHandleId.substring(end);
    }

    private String alsoUpdatePreceedingCompilationUnitName(String interimStem, String newName) {
        int endIndex = interimStem.indexOf(".java[" + newName);
        if (endIndex == -1) {
            return interimStem;
        }
        int startIndex = interimStem.indexOf("{") + 1;
        return interimStem.substring(0, startIndex) + newName + interimStem.substring(endIndex);
    }

    private boolean isTypePreceededByCompilationUnitName(String interimStem, String newName) {
        return interimStem.indexOf(".java[" + newName) > -1;
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
        return new BookmarkChangeItem(getVisitor());
    }

    private IChangeBookmark getVisitor() {
        return new BookmarkRenameVisitor(change[0], change[1]);
    }

}

enum RenameSupported {
    IPACKAGE_FRAGMENT_ROOT, IPACKAGE_FRAGMENT, ICOMPILATION_UNIT, ITYPE, IMETHOD, IFIELD, NOT_SUPPORTED;
}
