package org.eclipselabs.recommenders.bookmark.aaa.model;

import java.io.Serializable;
import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;

import com.google.common.collect.Lists;

public class JavaElementBookmark implements IBookmark, Serializable {

    private static final long serialVersionUID = 4072214438975678506L;
    private final String handleId;
    private boolean isInferred;
    private final List<JavaElementBookmark> childElements = Lists.newLinkedList();

    private boolean expanded = false;

    private IBookmarkModelComponent parent;

    // public JavaElementBookmark(final String handleId, final boolean
    // isInferred) {
    // this.handleId = handleId;
    // this.isInferred = isInferred;
    // }

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

    @Override
    public void remove(IBookmarkModelComponent bookmark) {
        childElements.remove(bookmark);
        bookmark.setParent(null);
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
        return parent.getParent() != null;
    }

    @Override
    public void setParent(IBookmarkModelComponent parent) {
        this.parent = parent;
    }

    @Override
    public IBookmarkModelComponent getParent() {
        return parent;
    }

    @Override
    public IBookmarkModelComponent[] getChildren() {
        return getChildElements().toArray(new IBookmarkModelComponent[0]);
    }

    @Override
    public void add(IBookmarkModelComponent component) {
        if (component instanceof JavaElementBookmark) {
            addChildElement((JavaElementBookmark) component);
        }
    }

}
