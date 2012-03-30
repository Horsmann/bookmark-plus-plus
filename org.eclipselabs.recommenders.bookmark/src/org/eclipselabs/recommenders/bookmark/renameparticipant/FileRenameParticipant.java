package org.eclipselabs.recommenders.bookmark.renameparticipant;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;
import org.eclipselabs.recommenders.bookmark.model.FileBookmark;

public class FileRenameParticipant extends RenameParticipant {

    private String oldFilePath;
    private String newFilePath;

    @Override
    protected boolean initialize(Object element) {

        if (!isSupported(element)) {
            return false;
        }

        oldFilePath = getOldFilePath((IFile) element);
        newFilePath = getNewFilePath(oldFilePath);

        if (!isValid(oldFilePath, newFilePath)) {
            return false;
        }

        return true;
    }

    private boolean isValid(String oldFilePath, String newFilePath) {
        return !oldFilePath.equals("") && !newFilePath.equals("");
    }

    private String getNewFilePath(String oldFilePath) {
        String newName = getArguments().getNewName();
        int lastIndexOf = oldFilePath.lastIndexOf("/");
        if (lastIndexOf > -1) {
            return oldFilePath.substring(0, lastIndexOf + 1) + newName;
        }
        return "";
    }

    private String getOldFilePath(IFile file) {
        return FileBookmark.getPath(file, true);
    }

    private boolean isSupported(Object element) {
        return element instanceof IFile;
    }

    @Override
    public String getName() {
        return "File renames";
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
        return new BookmarkChangeItem(new BookmarkRenameVisitor(oldFilePath, newFilePath));
    }

}
