package org.eclipselabs.recommenders.bookmark.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

public class CategoryTest {

    private Category category;
    private IBookmark bm2;
    
    @Test
    public void testRename() {
        category.setLabel("A");
        assertEquals("A", category.getLabel());
    }
    
    @Test
    public void testRemoveBookmark() {
        category.remove(bm2);
        assertEquals(1, category.getBookmarks().size());
    }
    
    @Test
    public void testAddBookmark() {
        category.add(mock(IBookmark.class));
        assertEquals(3, category.getBookmarks().size());
    }
    
    @Test
    public void testParentRelationship() {
        assertFalse(category.hasParent());
        category.setParent(category);
        assertFalse(category.hasParent());
        assertNull(category.getParent());
    }
    
    @Test
    public void testExpansion() {
        assertTrue(category.isExpanded());
        category.setExpanded(false);
        assertFalse(category.isExpanded());
    }

    @Before
    public void setUp() {
        IBookmark bm1 = mock(IBookmark.class);
        bm2 = mock(IBookmark.class);
        category = new Category("Test", new IBookmark[] { bm1, bm2 });
    }

}
