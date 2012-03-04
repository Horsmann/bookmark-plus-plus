package org.eclipselabs.recommenders.bookmark.model;

import org.eclipse.core.resources.IFile;

//This is just a 'shell' object to enable a distinction by casting of IFiles referring to external or internal files
public class ExternalFile {
    public IFile file;

    public ExternalFile(IFile file) {
        this.file = file;
    }

}