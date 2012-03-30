package org.eclipselabs.recommenders.bookmark.renameparticipant;

import org.eclipselabs.recommenders.bookmark.model.Category;
import org.eclipselabs.recommenders.bookmark.model.FileBookmark;
import org.eclipselabs.recommenders.bookmark.model.IBookmark;
import org.eclipselabs.recommenders.bookmark.model.JavaElementBookmark;

public class JavaElementRenameVisitor implements ChangeElementVisitor {

    private String oldId;
    private String newId;

    public JavaElementRenameVisitor(String oldId, String newId) {
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
        if (isEqual(javaElementBookmark.getHandleId())) {
            javaElementBookmark.setHandleId(newId);
        } else if (idIsContained(javaElementBookmark)){
            updateElement(javaElementBookmark);
        }
        
        for (JavaElementBookmark jeb : javaElementBookmark.getChildElements()) {
            jeb.accept(this);
        }
    }

    private boolean isEqual(String currentId) {
        return currentId.equals(oldId);
    }

    private void updateElement(JavaElementBookmark javaElementBookmark) {
        String handleId = javaElementBookmark.getHandleId();
        String replace = handleId.replace(oldId, newId);
        javaElementBookmark.setHandleId(replace);
    }

    private boolean idIsContained(JavaElementBookmark javaElementBookmark) {
        return javaElementBookmark.getHandleId().startsWith(oldId);
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
