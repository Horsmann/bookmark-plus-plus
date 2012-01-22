package org.eclipselabs.recommenders.bookmark.aaa.visitor;

import java.util.List;

import org.eclipselabs.recommenders.bookmark.aaa.model.Category;
import org.eclipselabs.recommenders.bookmark.aaa.model.FileBookmark;
import org.eclipselabs.recommenders.bookmark.aaa.model.IBookmark;
import org.eclipselabs.recommenders.bookmark.aaa.model.IModelVisitor;
import org.eclipselabs.recommenders.bookmark.aaa.model.JavaElementBookmark;

import com.google.common.collect.Lists;

public class HierarchyValueVisitor implements IModelVisitor {
    private List<Object> values = Lists.newArrayList();
    
    public List<Object> getValues() {
        return values;
    }

    @Override
    public void visit(FileBookmark fileBookmark) {

        Object object = fileBookmark.getFile();
        values.add(object);
    }

    @Override
    public void visit(Category category) {
        for (IBookmark bookmark : category.getBookmarks()){
            bookmark.accept(this);
        }
    }

    @Override
    public void visit(JavaElementBookmark javaElementBookmark) {
        Object object = javaElementBookmark.getJavaElement();
        values.add(object);
        for (IBookmark bookmark : javaElementBookmark.getChildElements()) {
            bookmark.accept(this);
        }
    }

}