package org.eclipselabs.recommenders.bookmark.renameparticipant;

import org.eclipselabs.recommenders.bookmark.model.IModelVisitor;

interface IJavaElementChangeVisitor extends IModelVisitor{
    
    String getOldId();
    String setOldId(String id);
    String getNewId();
    String setNewId(String id);

}
