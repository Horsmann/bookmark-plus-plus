package org.eclipselabs.recommenders.bookmark;

import java.io.File;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipselabs.recommenders.bookmark.view.ViewManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.eclipselabs.recommenders.bookmark"; //$NON-NLS-1$

    public static final String fileEnding = ".bm";

    // The shared instance
    private static Activator plugin;

    private static ViewManager manager;

    public static final String ICON_DEFAULT = "default";
    public static final String ICON_BOOKMARK = "bookmark";
    public static final String ICON_SHOW_IN_EDITOR = "openBookmarksInEditor";
    public static final String ICON_CLOSE_ALL_OPEN_EDITORS = "closeAllOpenEditors";
    public static final String ICON_SAVE_BOOKMARKS = "exportBookmarks";
    public static final String ICON_LOAD_BOOKMARKS = "importBookmarks";
    public static final String ICON_ASSOCIATED_PROJECT_CLOSED = "projectClosed";
    public static final String ICON_ASSOCIATED_RESOURCE_NOT_AVAILABLE = "projectNotAvailable";
    public static final String ICON_REFRESH_VIEW = "refreshView";
    public static final String ICON_OPEN_IN_SYSTEM_EXPLORER = "openInSystemExplorer";
    public static final String ICON_TOGGLE_VIEW = "toggleLevel";
    public static final String ICON_DELETE = "delete";
    public static final String ICON_BOOKMARK_RENAME = "rename";
    public static final String ICON_TOGGLE_FLAT_HIERARCHY = "toggleBetweenFlatAndTreeModus";
    public static final String ICON_SAVE = "saveChanges";

    public static final String AUTOSAVE_FILE = "bookmarkModel" + fileEnding;

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

    public static File getDefaultLocationForStoringBookmark() {
        IPath stateLocation = Activator.getDefault().getStateLocation();
        File stateFile = stateLocation.append(Activator.AUTOSAVE_FILE).toFile();
        return stateFile;
    }

    public static void setManager(ViewManager manager) {
        Activator.manager = manager;
    }

    public static ViewManager getManager() {
        return Activator.manager;
    }

    @Override
    protected void initializeImageRegistry(ImageRegistry registry) {
        super.initializeImageRegistry(registry);
        Bundle bundle = Platform.getBundle(PLUGIN_ID);

        ImageDescriptor imgDesc = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/sample.gif"),
                null));
        registry.put(ICON_DEFAULT, imgDesc);

        imgDesc = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/bkmrk_nav.gif"), null));
        registry.put(ICON_BOOKMARK, imgDesc);

        imgDesc = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/read_obj.gif"), null));
        registry.put(ICON_SHOW_IN_EDITOR, imgDesc);

        imgDesc = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/export_wiz.gif"), null));
        registry.put(ICON_SAVE_BOOKMARKS, imgDesc);

        imgDesc = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/import_wiz.gif"), null));
        registry.put(ICON_LOAD_BOOKMARKS, imgDesc);

        imgDesc = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/clear_co.gif"), null));
        registry.put(ICON_CLOSE_ALL_OPEN_EDITORS, imgDesc);

        imgDesc = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/deadlock_view.gif"), null));
        registry.put(ICON_ASSOCIATED_PROJECT_CLOSED, imgDesc);

        imgDesc = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/errorwarning_tab.gif"), null));
        registry.put(ICON_ASSOCIATED_RESOURCE_NOT_AVAILABLE, imgDesc);

        imgDesc = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/refresh_tab.gif"), null));
        registry.put(ICON_REFRESH_VIEW, imgDesc);

        imgDesc = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/opentype.gif"), null));
        registry.put(ICON_OPEN_IN_SYSTEM_EXPLORER, imgDesc);

        imgDesc = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/synced.gif"), null));
        registry.put(ICON_TOGGLE_VIEW, imgDesc);

        imgDesc = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/delete_obj.gif"), null));
        registry.put(ICON_DELETE, imgDesc);

        imgDesc = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/write_obj.gif"), null));
        registry.put(ICON_BOOKMARK_RENAME, imgDesc);

        imgDesc = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/tree_explorer.gif"), null));
        registry.put(ICON_TOGGLE_FLAT_HIERARCHY, imgDesc);

        imgDesc = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/save_edit.gif"), null));
        registry.put(ICON_SAVE, imgDesc);

    }
}
