package org.eclipselabs.recommenders.bookmark.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import org.eclipselabs.recommenders.bookmark.Activator;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

    public void initializeDefaultPreferences() {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        store.setDefault(PreferenceConstants.CLOSE_ALL_OPEN_EDITOR_WINDOWS_IF_BOOKMARK_CATEGORY_IS_OPENED, false);
    }

}
