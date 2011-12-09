package org.eclipselabs.recommenders.bookmark.tests;

import static org.junit.Assert.*;

import org.eclipselabs.recommenders.bookmark.tree.TreeNode;
import org.eclipselabs.recommenders.bookmark.tree.persistent.GsonConverter;
import org.eclipselabs.recommenders.bookmark.tree.persistent.deserialization.RestoredTree;
import org.eclipselabs.recommenders.bookmark.tree.persistent.deserialization.TreeDeserializer;
import org.junit.Test;

public class TestTreeDeserializer {

	@Test
	public void testDeserialization() {
		String serializedTree = "{\"isExpanded\":true,\"children\":[{\"isExpanded\":false,\"children\":[],\"value\":\"A\",\"isBookmarkNode\":false},{\"isExpanded\":false,\"children\":[],\"value\":\"B\",\"isBookmarkNode\":false}],\"value\":\"\",\"isBookmarkNode\":false}";

		RestoredTree restoredTree = TreeDeserializer.deSerializeTree(
				serializedTree, new GsonConverter());

		TreeNode root = restoredTree.getRoot();
		assertEquals(2, root.getChildren().length);
		assertEquals(1, restoredTree.getExpanded().length);

	}

}
