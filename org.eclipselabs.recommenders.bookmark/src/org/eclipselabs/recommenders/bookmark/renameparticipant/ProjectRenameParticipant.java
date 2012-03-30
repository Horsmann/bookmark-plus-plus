package org.eclipselabs.recommenders.bookmark.renameparticipant;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;

public class ProjectRenameParticipant extends RenameParticipant {
    
    private String oldHandleId="";
    private String newHandleId="";


    @Override
    protected boolean initialize(Object element) {

        if (!isSupported(element)) {
            return false;
        }
        IProject project = (IProject) element;
        oldHandleId = createOldId(project);
        newHandleId = createNewId();

        if (!initSuccessful()) {
            return false;
        }
        
        return true;
    }

    private boolean initSuccessful() {
        return oldHandleId.length() > 1 && newHandleId.length() > 1;
    }

    private String createNewId() {
        return "=" + getArguments().getNewName();
    }

    private String createOldId(IProject project) {
        return "=" + project.getName();
    }

    private boolean isSupported(Object element) {
        return element instanceof IProject;
    }

    @Override
    public String getName() {
        return "Project Rename";
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
        return new ElementChange(getVisitor());
    }
    
    private ChangeElementVisitor getVisitor() {
        return new JavaElementRenameVisitor(oldHandleId, newHandleId);
    }

}
