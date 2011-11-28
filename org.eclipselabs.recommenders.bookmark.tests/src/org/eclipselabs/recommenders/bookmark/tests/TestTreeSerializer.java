package org.eclipselabs.recommenders.bookmark.tests;

import static org.junit.Assert.*;

import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.tree.persistent.GsonConverter;
import org.eclipselabs.recommenders.bookmark.tree.persistent.serialization.TreeSerializer;
import org.junit.Test;

public class TestTreeSerializer {
	
	@Test
	public void testSerialization() {
		TreeNode node = new TreeNode("");
		node.addChild(new TreeNode("A"));
		node.addChild(new TreeNode("B"));
	
		Object [] expanded = new Object [] {node};
		GsonConverter converter = new GsonConverter();
		
		String serialized = TreeSerializer.serializeTree(node, expanded, converter);
		assertEquals("{\"isExpanded\":true,\"children\":[{\"isExpanded\":false,\"children\":[],\"value\":\"A\",\"isBookmarkNode\":false},{\"isExpanded\":false,\"children\":[],\"value\":\"B\",\"isBookmarkNode\":false}],\"value\":\"\",\"isBookmarkNode\":false}", serialized);
		
	}
	

}
