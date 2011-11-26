package org.eclipselabs.recommenders.bookmark;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipselabs.recommenders.bookmark.view.save_restore.SaveBookmarksToLocalDefaultFile;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipselabs.recommenders.bookmark"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	public static final String ICON_DEFAULT = "default";
	public static final String ICON_BOOKMARK = "bookmark";
	public static final String ICON_SHOW_IN_EDITOR = "openBookmarksInEditor";
	public static final String ICON_CLOSE_ALL_OPEN_EDITORS = "closeAllOpenEditors";
	public static final String ICON_SAVE_BOOKMARKS = "exportBookmarks";
	public static final String ICON_LOAD_BOOKMARKS = "importBookmarks";
	
	public static final String AUTOSAVE_FILE = "bookmarkModel.bm";

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static Activator getDefault() {
		return plugin;
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry registry) {
		super.initializeImageRegistry(registry);
		Bundle bundle = Platform.getBundle(PLUGIN_ID);

		ImageDescriptor imgDesc = ImageDescriptor.createFromURL(FileLocator
				.find(bundle, new Path("icons/sample.gif"), null));
		registry.put(ICON_DEFAULT, imgDesc);

		imgDesc = ImageDescriptor.createFromURL(FileLocator.find(bundle,
				new Path("icons/bkmrk_nav.gif"), null));
		registry.put(ICON_BOOKMARK, imgDesc);

		imgDesc = ImageDescriptor.createFromURL(FileLocator.find(bundle,
				new Path("icons/openBookmarkInEditor.png"), null));
		registry.put(ICON_SHOW_IN_EDITOR, imgDesc);

		imgDesc = ImageDescriptor.createFromURL(FileLocator.find(bundle,
				new Path("icons/export_wiz.gif"), null));
		registry.put(ICON_SAVE_BOOKMARKS, imgDesc);

		imgDesc = ImageDescriptor.createFromURL(FileLocator.find(bundle,
				new Path("icons/import_wiz.gif"), null));
		registry.put(ICON_LOAD_BOOKMARKS, imgDesc);

		imgDesc = ImageDescriptor.createFromURL(FileLocator.find(bundle,
				new Path("icons/clear_co.gif"), null));
		registry.put(ICON_CLOSE_ALL_OPEN_EDITORS, imgDesc);

	}
}
