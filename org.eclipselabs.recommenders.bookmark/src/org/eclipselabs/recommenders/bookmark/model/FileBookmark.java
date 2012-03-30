package org.eclipselabs.recommenders.bookmark.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class FileBookmark implements IBookmark {

    private static final long serialVersionUID = -224963828339478664L;
    private String path;
    private boolean isInferred;
    private IBookmarkModelComponent parent;
    private boolean isInWorkspace;

    public FileBookmark(final IFile file, Category parent, boolean isInWorkspace) {
        this.setParent(parent);
        parent.add(this);
        this.isInferred = false;
        this.isInWorkspace = isInWorkspace;
        setPath(file);
    }

    private void setPath(IFile file) {
        if (isInWorkspace) {
            this.path = getRelativePath(file);
        } else {
            this.path = getAbsolutePath(file);
        }
    }

    private String getAbsolutePath(IFile file) {
        return file.getFullPath().toString();
    }

    public FileBookmark(final IFile file, boolean isInWorkspace) {
        this.isInWorkspace = isInWorkspace;
        setPath(file);
    }

    public String getPath() {
        return path;
    }

    private String getRelativePath(IFile file) {
        IPath path = file.getFullPath();
        IPath projectRelativePath = file.getProjectRelativePath();
        return path.makeRelativeTo(projectRelativePath).toOSString();
    }

    @Override
    public boolean isInferredNode() {
        return isInferred;
    }

    public IFile getFile() {
        IFile file = createIFileFromPath();
        return file;
    }

    private IFile createIFileFromPath() {
        IPath location = Path.fromOSString(path);
        IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(location);
        return file;
    }

    @Override
    public void accept(final IModelVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public IBookmarkModelComponent getParent() {
        return parent;
    }

    @Override
    public void setParent(IBookmarkModelComponent parent) {
        this.parent = parent;
    }

    @Override
    public boolean hasParent() {
        return parent != null;
    }

    public void setInferred(boolean isInferred) {
        this.isInferred = isInferred;
    }

    public void setLocatedInWorkspace(boolean isInWorkspace) {
        this.isInWorkspace = isInWorkspace;
    }

    public boolean isInWorkspace() {
        return isInWorkspace;
    }

    public static String getPath(IFile file, boolean isInWorkspace) {
        return new FileBookmark(file, isInWorkspace).getPath();
    }

    public void setNewPath(String newPath) {
        this.path = newPath;
    }
}
