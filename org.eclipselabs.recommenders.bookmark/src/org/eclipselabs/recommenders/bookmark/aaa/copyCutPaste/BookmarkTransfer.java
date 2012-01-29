package org.eclipselabs.recommenders.bookmark.aaa.copyCutPaste;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TransferData;

public class BookmarkTransfer extends ByteArrayTransfer {

    private static final String MYTYPENAME = "BookmarkTransferObject";
    private static final int MYTYPEID = registerType(MYTYPENAME);
    private static BookmarkTransfer _instance = new BookmarkTransfer();

    public static BookmarkTransfer getInstance() {
        return _instance;
    }

    public void javaToNative(Object object, TransferData transferData) {
        if (!checkMyType(object) || !isSupportedType(transferData)) {
            DND.error(DND.ERROR_INVALID_DATA);
        }
        BookmarkTransferObject myType = (BookmarkTransferObject) object;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream writeOut = new ObjectOutputStream(out);

            writeOut.writeObject(myType);
            byte[] outBuffer = out.toByteArray();
            writeOut.close();
            super.javaToNative(outBuffer, transferData);
        } catch (IOException e) {
        }
    }

    public Object nativeToJava(TransferData transferData) {
        if (isSupportedType(transferData)) {
            byte[] buffer = (byte[]) super.nativeToJava(transferData);
            if (buffer == null)
                return null;

            BookmarkTransferObject myData = null;
            try {
                ByteArrayInputStream in = new ByteArrayInputStream(buffer);
                ObjectInputStream readIn = new ObjectInputStream(in);

                myData = (BookmarkTransferObject) readIn.readObject();
                readIn.close();
            } catch (IOException ex) {
                return null;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return myData;
        }

        return null;
    }

    protected String[] getTypeNames() {
        return new String[] { MYTYPENAME };
    }

    protected int[] getTypeIds() {
        return new int[] { MYTYPEID };
    }

    boolean checkMyType(Object object) {
        if (object == null || !(object instanceof BookmarkTransferObject)) {
            return false;
        }
        BookmarkTransferObject bm = (BookmarkTransferObject) object;
        if (bm.bookmarks == null) {
            return false;
        }
        return true;
    }

    protected boolean validate(Object object) {
        return checkMyType(object);
    }
}