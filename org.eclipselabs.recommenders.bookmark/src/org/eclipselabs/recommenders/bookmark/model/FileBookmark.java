/**
 * Copyright (c) 2010 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Johannes Lerch - initial API and implementation.
 */
package org.eclipselabs.recommenders.bookmark.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class FileBookmark implements IBookmark {

    private static final long serialVersionUID = -224963828339478664L;
    private final String path;
    private boolean isInferred;
    private IBookmarkModelComponent parent;
    private boolean isInWorkspace;

    public FileBookmark(final IFile file, Category parent) {
        this.path = getPath(file);
        this.setParent(parent);
        parent.add(this);
        isInferred = false;
        isInWorkspace = true;
    }

    public FileBookmark(final IFile file) {
        this.path = getPath(file);
    }

    public static String getPath(IFile file) {
        IPath path = file.getFullPath();
        IPath projectRelativePath = file.getProjectRelativePath();
        return path.makeRelativeTo(projectRelativePath).toOSString();
    }

    @Override
    public boolean isInferredNode() {
        return isInferred;
    }

    public IFile getFile() {
        IFile file = createIFileFromRelativePath();
        return file;
    }

    private IFile createIFileFromRelativePath() {
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
}
