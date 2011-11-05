package org.eclipselabs.recommenders.bookmark;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipselabs.recommenders.bookmark"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
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
		registry.put("default", imgDesc);
		
		imgDesc = ImageDescriptor.createFromURL(FileLocator
				.find(bundle, new Path("icons/jcu_obj.gif"), null));
		registry.put("javafile", imgDesc);

	}
}
