package org.eclipselabs.recommenders.bookmark.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.eclipselabs.recommenders.bookmark.model.BookmarkModel;

public class BookmarkIO {

    public static BookmarkModel load(File file) throws IOException {
        BookmarkModel model = null;

        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream in = new ObjectInputStream(fis);
            model = (BookmarkModel) in.readObject();
            in.close();
        } catch (FileNotFoundException fileNotFound) {
            model = new BookmarkModel();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (model == null) {
            model = new BookmarkModel();
        }
        return model;
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
