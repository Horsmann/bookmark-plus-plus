package org.eclipselabs.recommenders.bookmark.view.tree;

import org.eclipselabs.recommenders.bookmark.model.IModelVisitor;

public interface ITreeExpansionVisitor extends IModelVisitor {

    public enum Action {
        EXPAND, COLLAPSE
    }
}
