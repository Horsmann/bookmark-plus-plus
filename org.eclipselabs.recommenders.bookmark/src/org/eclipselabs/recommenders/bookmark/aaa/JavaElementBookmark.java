package org.eclipselabs.recommenders.bookmark.aaa;

import org.eclipselabs.recommenders.bookmark.aaa.model.IBookmark;

public class JavaElementBookmark
	implements IBookmark
{

	private String handleId;

	public JavaElementBookmark(String handleId)
	{
		this.handleId = handleId;
	}

	@Override
	public boolean isInferredNode()
	{
		return false;
	}

	public String getHandleId()
	{
		return handleId;
	}

}
