package org.eclipselabs.recommenders.bookmark.visitor;

import org.eclipselabs.recommenders.bookmark.model.Category;
import org.eclipselabs.recommenders.bookmark.model.FileBookmark;
import org.eclipselabs.recommenders.bookmark.model.IModelVisitor;
import org.eclipselabs.recommenders.bookmark.model.JavaElementBookmark;

public class GetValueVisitor implements IModelVisitor{
    
    private Object value;
    
    public Object getValue() {
        return value;
    }

    @Override
    public void visit(FileBookmark fileBookmark) {
        value = fileBookmark.getFile();
    }

    @Override
    public void visit(Category category) {
        value = category.getLabel();
    }

    @Override
    public void visit(JavaElementBookmark javaElementBookmark) {
        value = javaElementBookmark.getJavaElement();
    }

}
