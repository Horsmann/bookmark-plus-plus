package org.eclipselabs.recommenders.bookmark.view.tree;

import org.eclipselabs.recommenders.bookmark.model.BookmarkModel;

public interface IRepresentationMode {

    IChildrenResolverVisitor createChildrenResolverVisitor();
    ILabelProviderVisitor createLabelProviderVisitor();
    IImageProviderVisitor createImageProviderVisitor();
    ITreeExpansionVisitor createTreeExpansionVisitor(ITreeExpansionVisitor.Action expansion);
    Object[] getExpandedItems(BookmarkModel model);
}
