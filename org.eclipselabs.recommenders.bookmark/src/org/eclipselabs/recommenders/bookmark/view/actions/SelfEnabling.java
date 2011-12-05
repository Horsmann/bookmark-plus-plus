package org.eclipselabs.recommenders.bookmark.view.actions;

public interface SelfEnabling {

	/**
	 * If called this method shall perform the necessary operation
	 * and set itself to enabled or disabled */
	public void updateEnabledStatus();
	
}
