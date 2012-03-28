package org.eclipselabs.recommenders.bookmark.renameparticipant;

import org.eclipselabs.recommenders.bookmark.model.Category;
import org.eclipselabs.recommenders.bookmark.model.FileBookmark;
import org.eclipselabs.recommenders.bookmark.model.IBookmark;
import org.eclipselabs.recommenders.bookmark.model.JavaElementBookmark;

public class MethodFieldUpdateVisitor implements IJavaElementChangeVisitor {

    private String oldId;
    private String newId;

    public MethodFieldUpdateVisitor(String oldId, String newId) {
        this.oldId = oldId;
        this.newId = newId;
    }

    @Override
    public void visit(FileBookmark fileBookmark) {
    }

    @Override
    public void visit(Category category) {
        for (IBookmark bookmark : category.getBookmarks()) {
            bookmark.accept(this);
        }
    }

    @Override
    public void visit(JavaElementBookmark javaElementBookmark) {
        if (oldId.equals(javaElementBookmark.getHandleId())) {
            javaElementBookmark.setHandleId(newId);
        } else {
            for (JavaElementBookmark jeb : javaElementBookmark.getChildElements()) {
                jeb.accept(this);
            }
        }
    }

    @Override
    public String getOldId() {
        return oldId;
    }

    @Override
    public String getNewId() {
        return newId;
    }

    @Override
    public String setOldId(String id) {
        return oldId = id;
    }

    @Override
    public String setNewId(String id) {
        return newId = id;
    }

}
