package org.eclipselabs.recommenders.bookmark.view.tree;

import java.util.List;

import org.eclipselabs.recommenders.bookmark.model.IBookmark;
import org.eclipselabs.recommenders.bookmark.model.IModelVisitor;

public interface IChildrenResolverVisitor extends IModelVisitor {

    public abstract boolean hasChildren();

    public abstract List<? extends IBookmark> getChildren();

}