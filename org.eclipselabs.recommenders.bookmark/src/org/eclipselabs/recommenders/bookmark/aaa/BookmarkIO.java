/**
 * Copyright (c) 2010 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Johannes Lerch - initial API and implementation.
 */
package org.eclipselabs.recommenders.bookmark.aaa;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.aaa.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.aaa.model.Category;
import org.eclipselabs.recommenders.bookmark.aaa.model.FileBookmark;
import org.eclipselabs.recommenders.bookmark.aaa.model.JavaElementBookmark;

public class BookmarkIO {

    public static BookmarkModel load() {
        final BookmarkModel model = new BookmarkModel();
        
        Category category = new Category("Test 1");
        FileBookmark file = new FileBookmark(ResourcesPlugin.getWorkspace().getRoot().getFile(Path.fromOSString("project/test.bm")), category);

        model.add(category);

        Category category2 = new Category("Test 2");

        JavaElementBookmark jeb1 = new JavaElementBookmark("=LKJLD/src<test.project{IMy.java", true, category2);

        JavaElementBookmark jeb2 = new JavaElementBookmark("=LKJLD/src<test.project{IMy.java[IMy", true, jeb1);

        JavaElementBookmark jeb3 = new JavaElementBookmark("=LKJLD/src<test.project{IMy.java[IMy~add~I", false, jeb2);

        JavaElementBookmark jeb4 = new JavaElementBookmark("=LKJLD/src<test.project{IMy.java[IMy~sub~I", false, jeb2);

        JavaElementBookmark jeb5 = new JavaElementBookmark("=LKJLD/src<test.project{IMy.java[IMy~div~I", false, jeb2);

        model.add(category2);

        return model;
    }

    public static BookmarkModel loadFromDefaultFile() {
        File file = Activator.getDefaultLocationForStoringBookmark();

        return load(file);

    }

    public static BookmarkModel load(File file) {
        BookmarkModel model = null;

        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream in = new ObjectInputStream(fis);
            model = (BookmarkModel) in.readObject();
            in.close();
        } catch (FileNotFoundException fileNotFound){
            model = new BookmarkModel();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        if (model==null){
            model = new BookmarkModel();
        }

        return model;
    }

    public static void writeToDefaultFile(BookmarkModel model) {
        File file = Activator.getDefaultLocationForStoringBookmark();
        try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(model);
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
