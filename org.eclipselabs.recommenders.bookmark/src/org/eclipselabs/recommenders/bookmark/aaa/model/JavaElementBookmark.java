package org.eclipselabs.recommenders.bookmark.aaa.model;

import java.io.Serializable;
import java.util.List;

import com.google.common.collect.Lists;

public class JavaElementBookmark implements IBookmark, Serializable {

    private static final long serialVersionUID = 4072214438975678506L;
    private final String handleId;
    private final boolean isInferred;
    private final List<JavaElementBookmark> childElements = Lists.newLinkedList();

    public JavaElementBookmark(final String handleId, final boolean isInferred) {
        this.handleId = handleId;
        this.isInferred = isInferred;
    }

    @Override
    public boolean isInferredNode() {
        return isInferred;
    }

    public String getHandleId() {
        return handleId;
    }

    public void addChildElement(final JavaElementBookmark childElement) {
        childElements.add(childElement);
    }

    public List<JavaElementBookmark> getChildElements() {
        return childElements;
    }

    @Override
    public void accept(final IModelVisitor visitor) {
        visitor.visit(this);
    }

}
