package org.eclipselabs.recommenders.bookmark.aaa.copyCutPaste;

import java.io.Serializable;

import org.eclipselabs.recommenders.bookmark.aaa.model.IBookmark;

public class BookmarkTransferObject implements Serializable
{
    private static final long serialVersionUID = -8853397995428796821L;
    public IBookmark[] bookmarks;

	public BookmarkTransferObject(IBookmark[] bookmarks)
	{
		this.bookmarks = bookmarks;
	}

	public BookmarkTransferObject()
	{

	}
}
