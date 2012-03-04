package org.eclipselabs.recommenders.bookmark.model;

public interface IModelVisitor {

    void visit(FileBookmark fileBookmark);

    void visit(Category category);

    void visit(JavaElementBookmark javaElementBookmark);

}
