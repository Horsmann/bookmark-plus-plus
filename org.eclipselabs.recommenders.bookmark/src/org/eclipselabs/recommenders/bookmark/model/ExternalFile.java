package org.eclipselabs.recommenders.bookmark.model;

import org.eclipse.core.resources.IFile;

public class ExternalFile {
    public IFile file;

    public ExternalFile(IFile file) {
        this.file = file;
    }

}