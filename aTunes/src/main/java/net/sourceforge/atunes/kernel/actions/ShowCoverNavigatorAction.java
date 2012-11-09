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

package net.sourceforge.atunes.kernel.actions;

import net.sourceforge.atunes.gui.views.dialogs.CoverNavigatorDialog;
import net.sourceforge.atunes.kernel.modules.covernavigator.CoverNavigatorController;
import net.sourceforge.atunes.model.IAudioObjectImageLocator;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IProcessFactory;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * This action shows cover navigator
 * 
 * @author fleax
 * 
 */
public class ShowCoverNavigatorAction extends CustomAbstractAction {

    private static final long serialVersionUID = 4927892497869144235L;

    private IRepositoryHandler repositoryHandler;

    private IAudioObjectImageLocator audioObjectImageLocator;

    private IProcessFactory processFactory;

    private IDialogFactory dialogFactory;

    /**
     * @param dialogFactory
     */
    public void setDialogFactory(final IDialogFactory dialogFactory) {
	this.dialogFactory = dialogFactory;
    }

    /**
     * @param processFactory
     */
    public void setProcessFactory(final IProcessFactory processFactory) {
	this.processFactory = processFactory;
    }

    /**
     * @param audioObjectImageLocator
     */
    public void setAudioObjectImageLocator(
	    final IAudioObjectImageLocator audioObjectImageLocator) {
	this.audioObjectImageLocator = audioObjectImageLocator;
    }

    /**
     * @param repositoryHandler
     */
    public void setRepositoryHandler(final IRepositoryHandler repositoryHandler) {
	this.repositoryHandler = repositoryHandler;
    }

    /**
     * Default constructor
     */
    public ShowCoverNavigatorAction() {
	super(I18nUtils.getString("COVER_NAVIGATOR"));
    }

    @Override
    protected void executeAction() {
	CoverNavigatorDialog coverNavigator = dialogFactory
		.newDialog(CoverNavigatorDialog.class);
	coverNavigator.setArtists(repositoryHandler.getArtists());
	new CoverNavigatorController(coverNavigator, audioObjectImageLocator,
		processFactory);
	coverNavigator.setVisible(true);
    }
}
