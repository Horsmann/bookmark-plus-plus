package org.eclipselabs.recommenders.bookmark.view.tree;

import org.eclipse.swt.graphics.Image;
import org.eclipselabs.recommenders.bookmark.model.IModelVisitor;

public interface IImageProviderVisitor extends IModelVisitor {
    Image getImage();

}
