/*
 * aTunes 2.2.0-SNAPSHOT
 * Copyright (C) 2006-2011 Alex Aranda, Sylvain Gaudard and contributors
 *
 * See http://www.atunes.org/wiki/index.php?title=Contributing for information about contributors
 *
 * http://www.atunes.org
 * http://sourceforge.net/projects/atunes
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package net.sourceforge.atunes.gui.views.menus;

import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JSeparator;

import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.utils.I18nUtils;

public class EditMenu extends JMenu {

	private static final long serialVersionUID = -3624790857729577320L;

	private IOSManager osManager;
	
	private Action showEqualizerAction;
	private Action volumeDownAction;
	private Action volumeUpAction;
	private Action muteAction;
	private Action repairTrackNumbersAction;
	private Action repairGenresAction;
	private Action repairAlbumNamesAction;
	private Action editPreferencesAction;
	
	/**
	 * @param i18nKey
	 */
	public EditMenu(String i18nKey) {
		super(I18nUtils.getString(i18nKey));
	}
	
	/**
	 * @param showEqualizerAction
	 */
	public void setShowEqualizerAction(Action showEqualizerAction) {
		this.showEqualizerAction = showEqualizerAction;
	}
	
	/**
	 * @param volumeDownAction
	 */
	public void setVolumeDownAction(Action volumeDownAction) {
		this.volumeDownAction = volumeDownAction;
	}
	
	/**
	 * @param volumeUpAction
	 */
	public void setVolumeUpAction(Action volumeUpAction) {
		this.volumeUpAction = volumeUpAction;
	}
	
	/**
	 * @param muteAction
	 */
	public void setMuteAction(Action muteAction) {
		this.muteAction = muteAction;
	}
	
	/**
	 * @param repairTrackNumbersAction
	 */
	public void setRepairTrackNumbersAction(Action repairTrackNumbersAction) {
		this.repairTrackNumbersAction = repairTrackNumbersAction;
	}
	
	/**
	 * @param repairAlbumNamesAction
	 */
	public void setRepairAlbumNamesAction(Action repairAlbumNamesAction) {
		this.repairAlbumNamesAction = repairAlbumNamesAction;
	}
	
	/**
	 * @param repairGenresAction
	 */
	public void setRepairGenresAction(Action repairGenresAction) {
		this.repairGenresAction = repairGenresAction;
	}
	
	/**
	 * @param editPreferencesAction
	 */
	public void setEditPreferencesAction(Action editPreferencesAction) {
		this.editPreferencesAction = editPreferencesAction;
	}
	
	/**
	 * @param osManager
	 */
	public void setOsManager(IOSManager osManager) {
		this.osManager = osManager;
	}
	
	/**
	 * Initializes menu
	 */
	public void initialize() {
        JMenu player = new JMenu(I18nUtils.getString("VOLUME"));
        player.add(showEqualizerAction);
        player.add(volumeUpAction);
        player.add(volumeDownAction);
        player.add(new JCheckBoxMenuItem(muteAction));
        JMenu repair = new JMenu(I18nUtils.getString("REPAIR"));
        repair.add(repairTrackNumbersAction);
        repair.add(repairGenresAction);
        repair.add(repairAlbumNamesAction);
        add(player);
        if (!osManager.areMenuEntriesDelegated()) {
        	add(editPreferencesAction);
        }
        add(new JSeparator());
        add(repair);
	}
}
