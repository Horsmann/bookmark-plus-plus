package org.eclipselabs.recommenders.bookmark.renameparticipant;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.commands.RenameJavaElementsCommand;

class ElementChange extends Change {

    private final ChangeElementVisitor visitor;

    public ElementChange(ChangeElementVisitor visitor) {
        this.visitor = visitor;
    }

    @Override
    public String getName() {
        return "JavaElement Change";
    }

    @Override
    public void initializeValidationData(IProgressMonitor pm) {
    }

    @Override
    public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        return new RefactoringStatus();
    }

    @Override
    public Change perform(IProgressMonitor pm) throws CoreException {
        Activator.getCommandInvoker().invoke(new RenameJavaElementsCommand(visitor));
        return new ElementChange(swapIdParameterForVisitor(visitor));
    }

    private ChangeElementVisitor swapIdParameterForVisitor(ChangeElementVisitor visitor) {
        String oldId = visitor.getOldId();
        String newId = visitor.getNewId();
        visitor.setNewId(oldId);
        visitor.setOldId(newId);
        return visitor;
    }

    @Override
    public Object getModifiedElement() {
        return null;
    }

}
