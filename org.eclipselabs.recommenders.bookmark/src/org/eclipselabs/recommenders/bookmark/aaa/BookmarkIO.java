/**
 * Copyright (c) 2010 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Johannes Lerch - initial API and implementation.
 */
package org.eclipselabs.recommenders.bookmark.aaa;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipselabs.recommenders.bookmark.aaa.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.aaa.model.Category;
import org.eclipselabs.recommenders.bookmark.aaa.model.FileBookmark;
import org.eclipselabs.recommenders.bookmark.aaa.model.JavaElementBookmark;

public class BookmarkIO {

    public static BookmarkModel load()
	{
		final BookmarkModel model = new BookmarkModel();
		model.add(new Category("Test", new FileBookmark(ResourcesPlugin
				.getWorkspace().getRoot()
				.getFile(Path.fromOSString("project/test.bm"))),
				new JavaElementBookmark("=LKJLD/src<test.project{MyTest.java"),
				new JavaElementBookmark(
						"=LKJLD/src<test.project{IMy.java[IMy~add~I")));
		return model;
	}
}
