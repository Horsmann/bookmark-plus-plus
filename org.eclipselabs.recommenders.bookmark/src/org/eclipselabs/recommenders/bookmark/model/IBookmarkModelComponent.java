package org.eclipselabs.recommenders.bookmark.model;

import java.io.Serializable;

public interface IBookmarkModelComponent extends Serializable{

    void accept(IModelVisitor visitor);

    public IBookmarkModelComponent getParent();

    public void setParent(IBookmarkModelComponent parent);

    public boolean hasParent();

}
