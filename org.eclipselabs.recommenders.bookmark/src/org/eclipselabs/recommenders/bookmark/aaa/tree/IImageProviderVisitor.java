package org.eclipselabs.recommenders.bookmark.aaa.tree;

import org.eclipse.swt.graphics.Image;
import org.eclipselabs.recommenders.bookmark.aaa.model.IModelVisitor;

public interface IImageProviderVisitor extends IModelVisitor {
    Image getImage();

}
