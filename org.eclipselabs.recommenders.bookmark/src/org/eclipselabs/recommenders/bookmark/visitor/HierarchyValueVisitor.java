package org.eclipselabs.recommenders.bookmark.visitor;

import java.util.List;

import org.eclipselabs.recommenders.bookmark.model.Category;
import org.eclipselabs.recommenders.bookmark.model.ExternalFile;
import org.eclipselabs.recommenders.bookmark.model.FileBookmark;
import org.eclipselabs.recommenders.bookmark.model.IBookmark;
import org.eclipselabs.recommenders.bookmark.model.IModelVisitor;
import org.eclipselabs.recommenders.bookmark.model.JavaElementBookmark;

import com.google.common.collect.Lists;

public class HierarchyValueVisitor implements IModelVisitor {
    private List<Object> values = Lists.newArrayList();

    public List<Object> getValues() {
        return values;
    }

    @Override
    public void visit(FileBookmark fileBookmark) {
        if (fileBookmark.isInWorkspace()) {
            values.add(fileBookmark.getFile());
        } else {
            values.add(new ExternalFile(fileBookmark.getFile()));
        }
    }

    @Override
    public void visit(Category category) {
        for (IBookmark bookmark : category.getBookmarks()) {
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