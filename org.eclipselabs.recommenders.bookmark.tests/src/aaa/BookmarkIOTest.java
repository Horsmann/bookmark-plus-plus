package aaa;

import static org.junit.Assert.*;

import java.util.List;

import org.eclipselabs.recommenders.bookmark.aaa.BookmarkIO;
import org.eclipselabs.recommenders.bookmark.aaa.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.aaa.model.Category;
import org.eclipselabs.recommenders.bookmark.aaa.model.FileBookmark;
import org.eclipselabs.recommenders.bookmark.aaa.model.IBookmark;
import org.junit.Test;

public class BookmarkIOTest
{

	@Test
	public void testWrite()
	{
		BookmarkModel load = BookmarkIO.load();
		BookmarkIO.writeToDefaultFile(load);

		BookmarkModel model = BookmarkIO.loadFromDefaultFile();
		assertEquals(1, model.getCategories().size());
		List<Category> categories = model.getCategories();
		List<IBookmark> bookmarks = categories.get(0).getBookmarks();

		assertTrue(bookmarks.get(0) instanceof FileBookmark);

	}

}
