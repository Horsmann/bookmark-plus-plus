package org.eclipselabs.recommenders.bookmark.handler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TransferData;

public class BookmarkNodeTransfer
	extends ByteArrayTransfer
{

	private static final String MYTYPENAME = "BookmarkNodeTransferObject";
	private static final int MYTYPEID = registerType(MYTYPENAME);
	private static BookmarkNodeTransfer _instance = new BookmarkNodeTransfer();

	public static BookmarkNodeTransfer getInstance()
	{
		return _instance;
	}

	public void javaToNative(Object object, TransferData transferData)
	{
		if (!checkMyType(object) || !isSupportedType(transferData)) {
			DND.error(DND.ERROR_INVALID_DATA);
		}
		BookmarkNodeTransferObject myType = (BookmarkNodeTransferObject) object;
		try {
			// write data to a byte array and then ask super to convert to
			// pMedium
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			DataOutputStream writeOut = new DataOutputStream(out);
			// for (int i = 0, length = myType.length; i < length; i++) {
			// byte[] buffer = myType[i].id.getBytes();

			String idsString="";
			for (int i = 0; i < myType.ids.length; i++){
				idsString += myType.ids[i];
				if (i+1 < myType.ids.length){
					idsString += "\n";
				}
			}
			
			byte[] buffer = idsString.getBytes();
			writeOut.writeInt(buffer.length);
			writeOut.write(buffer);
			// }
			byte[] outBuffer = out.toByteArray();
			writeOut.close();
			super.javaToNative(outBuffer, transferData);
		}
		catch (IOException e) {
		}
	}

	public Object nativeToJava(TransferData transferData)
	{
		if (isSupportedType(transferData)) {
			byte[] buffer = (byte[]) super.nativeToJava(transferData);
			if (buffer == null)
				return null;

			BookmarkNodeTransferObject[] myData = new BookmarkNodeTransferObject[0];
			try {
				ByteArrayInputStream in = new ByteArrayInputStream(buffer);
				DataInputStream readIn = new DataInputStream(in);
				while (readIn.available() > 20) {
					BookmarkNodeTransferObject bm = new BookmarkNodeTransferObject();
					int size = readIn.readInt();
					byte[] id = new byte[size];
					readIn.read(id);
					String idsString = new String(id);
					String [] ids = idsString.split("\n");
					bm.ids = ids;
					
					BookmarkNodeTransferObject[] newMyData = new BookmarkNodeTransferObject[myData.length + 1];
					System.arraycopy(myData, 0, newMyData, 0, myData.length);
					newMyData[myData.length] = bm;
					myData = newMyData;
				}
				readIn.close();
			}
			catch (IOException ex) {
				return null;
			}
			return myData;
		}

		return null;
	}

	protected String[] getTypeNames()
	{
		return new String[] { MYTYPENAME };
	}

	protected int[] getTypeIds()
	{
		return new int[] { MYTYPEID };
	}

	boolean checkMyType(Object object)
	{
		if (object == null || !(object instanceof BookmarkNodeTransferObject)) {
			return false;
		}
		BookmarkNodeTransferObject bm = (BookmarkNodeTransferObject) object;
		if (bm.ids == null) {
			return false;
		}
		return true;
	}

	protected boolean validate(Object object)
	{
		return checkMyType(object);
	}
}