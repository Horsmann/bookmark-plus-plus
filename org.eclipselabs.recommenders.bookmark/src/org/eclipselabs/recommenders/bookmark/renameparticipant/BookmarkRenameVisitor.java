package org.eclipselabs.recommenders.bookmark.renameparticipant;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipselabs.recommenders.bookmark.model.Category;
import org.eclipselabs.recommenders.bookmark.model.FileBookmark;
import org.eclipselabs.recommenders.bookmark.model.IBookmark;
import org.eclipselabs.recommenders.bookmark.model.JavaElementBookmark;

public class BookmarkRenameVisitor implements IChangeBookmark {

    private String oldId;
    private String newId;

    public BookmarkRenameVisitor(String oldId, String newId) {
        this.oldId = oldId;
        this.newId = newId;
    }

    @Override
    public String toString() {
        return "Old: " + oldId + "\n" + "New: " + newId;
    }

    @Override
    public void visit(FileBookmark fileBookmark) {
        if (isEqual(fileBookmark.getPath())) {
            fileBookmark.setNewPath(newId);
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

        if (isEqual(javaElementBookmark.getHandleId())) {
            javaElementBookmark.setHandleId(newId);
        } else {
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
        updateTypeInformationInId(javaElementBookmark);
    }

    /**
     * If the type is renamed that gives the compilation unit its name the
     * rename events have not necessarily been produced in an order that enables
     * simple before/after updating. Hence, this method also updates the
     * compilation unit name if the change of its name giving type is processed
     * first. Additionally, if the type occurs as parameters in bookmarked
     * methods those type parameters are also updated here.
     */
    private void updateTypeInformationInId(JavaElementBookmark javaElementBookmark) {
        IJavaElement oldEle = JavaCore.create(oldId);
        IJavaElement newEle = JavaCore.create(newId);
        if (isType(oldEle) && isType(newEle)) {
            String oldElementName = oldEle.getElementName();
            String newElementName = newEle.getElementName();
            updateIdsWithRegEx(javaElementBookmark, removeFileEnding(oldElementName), removeFileEnding(newElementName));
        }
    }

    private boolean isType(IJavaElement element) {
        return element instanceof IType || element instanceof ICompilationUnit;
    }

    private void updateIdsWithRegEx(JavaElementBookmark javaElementBookmark, String oldName, String newName) {
        String[][] pairs = { { "Q" + oldName + ";", "Q" + newName + ";" },
                { newName + ".java\\[" + oldName + "\\[", newName + ".java[" + newName + "[" },
                { newName + ".java\\[" + oldName + "~", newName + ".java[" + newName + "~" },
                { newName + ".java\\[" + oldName + "\\^", newName + ".java[" + newName + "^" },
                { newName + ".java\\[" + oldName + "$", newName + ".java[" + newName } };
        String handleId = javaElementBookmark.getHandleId();
        for (int i = 0; i < pairs.length; i++) {
            handleId = handleId.replaceAll(pairs[i][0], pairs[i][1]);
        }
        javaElementBookmark.setHandleId(handleId);
    }

    private String removeFileEnding(String name) {
        int indexOf = name.indexOf(".");
        if (indexOf > -1) {
            return name.substring(0, indexOf);
        }
        return name;
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
