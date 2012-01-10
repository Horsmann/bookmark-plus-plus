package org.eclipselabs.recommenders.bookmark.aaa.model;


public class JavaElementBookmark implements IBookmark {

    private final String handleId;

    public JavaElementBookmark(final String handleId) {
        this.handleId = handleId;
    }

    @Override
    public boolean isInferredNode() {
        return false;
    }

    public String getHandleId() {
        return handleId;
    }

    @Override
    public void accept(final IModelVisitor visitor) {
        visitor.visit(this);
    }

}
