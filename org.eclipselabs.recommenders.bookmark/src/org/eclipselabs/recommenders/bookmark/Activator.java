package org.eclipselabs.recommenders.bookmark;

import java.io.File;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipselabs.recommenders.bookmark.model.BookmarkModel;
import org.eclipselabs.recommenders.bookmark.model.BookmarkModelCloner;
import org.eclipselabs.recommenders.bookmark.view.BookmarkCommandInvoker;
import org.eclipselabs.recommenders.bookmark.view.BookmarkView;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.eclipselabs.recommenders.bookmark"; //$NON-NLS-1$

    public static final String fileEnding = ".bm";

    // The shared instance
    private static Activator plugin;

    private static BookmarkView bookmarkView;

    public static final String ICON_INFERRED = "inferred";
    public static final String ICON_CATEGORY = "category";
    public static final String ICON_SINGLE_BOOKMARK = "singleBookmark";
    public static final String ICON_SINGLE_BOOKMARK_OVERLAY = "singleBookmarkOverlay";
    public static final String ICON_SHOW_IN_EDITOR = "openBookmarksInEditor";
    public static final String ICON_CLOSE_ALL_OPEN_EDITORS = "closeAllOpenEditors";
    public static final String ICON_OPEN_IN_SYSTEM_EXPLORER = "openInSystemExplorer";
    public static final String ICON_TOGGLE_VIEW = "toggleLevel";
    public static final String ICON_DELETE = "delete";
    public static final String ICON_RENAME_CATEGORY = "rename";
    public static final String ICON_SWITCH_FLAT_HIERARCHY = "toggleBetweenFlatAndTreeModus";
    public static final String ICON_SAVE = "save";

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

    public static BookmarkModel getClonedModel() {
        return new BookmarkModelCloner(Activator.bookmarkView.getModel()).clone();
    }

    public static void setBookmarkView(BookmarkView bookmarkView) {
        Activator.bookmarkView = bookmarkView;
    }

    public static BookmarkCommandInvoker getCommandInvoker() {
        return Activator.bookmarkView;
    }

    public static void setNewModel(BookmarkModel newModel) {
        bookmarkView.setNewModelForTreeViewer(newModel);
        bookmarkView.resetGui();
    }

    public static IStructuredSelection getCurrentTreeSelection() {
        return bookmarkView.getSelections();
    }

    @Override
    protected void initializeImageRegistry(ImageRegistry registry) {
        super.initializeImageRegistry(registry);
        Bundle bundle = Platform.getBundle(PLUGIN_ID);

        ImageDescriptor imgDesc = ImageDescriptor.createFromURL(FileLocator.find(bundle,
                new Path("icons/bkmrk_nav.gif"), null));
        registry.put(ICON_CATEGORY, imgDesc);

        imgDesc = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/bookmark_obj.gif"), null));
        registry.put(ICON_SINGLE_BOOKMARK, imgDesc);

        imgDesc = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/bookmark_obj_ovl.gif"), null));
        registry.put(ICON_SINGLE_BOOKMARK_OVERLAY, imgDesc);

        imgDesc = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/read_obj.gif"), null));
        registry.put(ICON_SHOW_IN_EDITOR, imgDesc);

        imgDesc = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/clear_co.gif"), null));
        registry.put(ICON_CLOSE_ALL_OPEN_EDITORS, imgDesc);

        imgDesc = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/opentype.gif"), null));
        registry.put(ICON_OPEN_IN_SYSTEM_EXPLORER, imgDesc);

        imgDesc = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/synced.gif"), null));
        registry.put(ICON_TOGGLE_VIEW, imgDesc);

        imgDesc = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/delete_obj.gif"), null));
        registry.put(ICON_DELETE, imgDesc);

        imgDesc = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/write_obj.gif"), null));
        registry.put(ICON_RENAME_CATEGORY, imgDesc);

        imgDesc = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/tree_explorer.gif"), null));
        registry.put(ICON_SWITCH_FLAT_HIERARCHY, imgDesc);

        imgDesc = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/save_edit.gif"), null));
        registry.put(ICON_SAVE, imgDesc);

    }

}
