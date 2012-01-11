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
package org.eclipselabs.recommenders.bookmark.aaa.model;

import org.eclipse.core.resources.IFile;

public class FileBookmark implements IBookmark {

    private final IFile file;

    public FileBookmark(final IFile file) {
        this.file = file;
    }

    @Override
    public boolean isInferredNode() {
        return false;
    }

    public IFile getFile() {
        return file;
    }

    @Override
    public void accept(final IModelVisitor visitor) {
        visitor.visit(this);
    }

}