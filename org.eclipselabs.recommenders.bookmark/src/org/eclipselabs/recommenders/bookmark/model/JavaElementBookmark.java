package org.eclipselabs.recommenders.bookmark.model;

import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;

import com.google.common.collect.Lists;

public class JavaElementBookmark implements IBookmark {

    private static final long serialVersionUID = 4072214438975678506L;
    private final String handleId;
    private boolean isInferred;
    private final List<JavaElementBookmark> childElements = Lists.newLinkedList();

    private boolean expanded = false;

    private IBookmarkModelComponent parent;


    public JavaElementBookmark(final String handleId, final boolean isInferred, JavaElementBookmark parent) {
        this.handleId = handleId;
        this.isInferred = isInferred;
        this.parent = parent;
        parent.addChildElement(this);
    }

    public JavaElementBookmark(final String handleId, final boolean isInferred, Category parent) {
        this.handleId = handleId;
        this.isInferred = isInferred;
        this.parent = parent;
        parent.add(this);

    }

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

    public IJavaElement getJavaElement() {
        return JavaCore.create(handleId);
    }

    public void remove(JavaElementBookmark element) {
        childElements.remove(element);
        element.setParent(null);
    }

    public void setExpanded(final boolean expanded) {
        this.expanded = expanded;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void addChildElement(final JavaElementBookmark childElement) {
        childElements.add(childElement);
        childElement.setParent(this);
    }

    public List<JavaElementBookmark> getChildElements() {
        return childElements;
    }

    @Override
    public void accept(final IModelVisitor visitor) {
        visitor.visit(this);
    }

    public void setInferred(boolean inferred) {
        isInferred = inferred;
    }

    @Override
    public boolean hasParent() {
        return parent != null;
    }

    @Override
    public void setParent(IBookmarkModelComponent parent) {
        this.parent = parent;
    }

    @Override
    public IBookmarkModelComponent getParent() {
        return parent;
    }

}
