package org.eclipselabs.recommenders.bookmark.view.tree;

import org.eclipselabs.recommenders.bookmark.model.IModelVisitor;

public interface ILabelProviderVisitor extends IModelVisitor {
    String getLabel();
}