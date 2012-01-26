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

import org.eclipselabs.recommenders.bookmark.Activator;
import org.eclipselabs.recommenders.bookmark.aaa.model.BookmarkModel;

public class BookmarkIO {

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
        } catch (FileNotFoundException fileNotFound) {
            model = new BookmarkModel();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (model == null) {
            model = new BookmarkModel();
        }

        return model;
    }

    public static void writeToDefaultFile(BookmarkModel model) {
        File file = Activator.getDefaultLocationForStoringBookmark();
        writeModel(file, model);
    }

    public static void writeModel(File file, BookmarkModel model) {
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
