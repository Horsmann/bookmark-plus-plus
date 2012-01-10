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
package org.eclipselabs.recommenders.bookmark.aaa.tree;

import org.eclipselabs.recommenders.bookmark.aaa.model.IBookmarkModelComponent;
import org.eclipselabs.recommenders.bookmark.aaa.model.IModelVisitor;

public class TreeModelUtil {

    public static void acceptVisitor(final Object modelElement, final IModelVisitor visitor) {
        if (modelElement instanceof IBookmarkModelComponent) {
            ((IBookmarkModelComponent) modelElement).accept(visitor);
        } else {
            throw new RuntimeException("Should not be reachable. Visitor can only be used on IBookmarkModelComponents.");
        }
    }
}
