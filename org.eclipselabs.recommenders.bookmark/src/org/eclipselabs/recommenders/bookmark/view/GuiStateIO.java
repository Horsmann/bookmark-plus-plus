package org.eclipselabs.recommenders.bookmark.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.eclipse.ui.IMemento;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;
import org.eclipselabs.recommenders.bookmark.Activator;

public class GuiStateIO {

    public static void writeGuiStateToMemento(File stateFile, BookmarkView bookmarkView) {
        XMLMemento memento = XMLMemento.createWriteRoot(Activator.PLUGIN_ID);
        bookmarkView.saveState(memento);
        if (stateFile != null) {
            try {
                FileOutputStream stream = new FileOutputStream(stateFile);
                OutputStreamWriter writer = new OutputStreamWriter(stream, "utf-8"); //$NON-NLS-1$
                memento.save(writer);
                writer.close();
            } catch (IOException ioe) {
                stateFile.delete();
            }
        }
    }

    public static IMemento readGuiStateMementoFromFile(File stateFile) {
        if (stateFile != null) {
            try {
                FileInputStream fileStream = new FileInputStream(stateFile);
                InputStreamReader inStream = new InputStreamReader(fileStream, "utf-8"); //$NON-NLS-1$
                XMLMemento redMem = XMLMemento.createReadRoot(inStream);
                return redMem;
            } catch (IOException ioe) {
                stateFile.delete();
            } catch (WorkbenchException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
