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

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IChangeTagsProcess;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IProcessFactory;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Calls process to set lyrics automatically
 * 
 * @author fleax
 * 
 */
public class AutoSetLyricsAction extends AbstractActionOverSelectedObjects<ILocalAudioObject> {

    private static final long serialVersionUID = 4778415252933283915L;

    private IProcessFactory processFactory;

    /**
     * @param processFactory
     */
    public void setProcessFactory(IProcessFactory processFactory) {
		this.processFactory = processFactory;
	}
    
    public AutoSetLyricsAction() {
        super(I18nUtils.getString("AUTO_SET_LYRICS"));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("AUTO_SET_LYRICS"));
    }

    @Override
    protected void executeAction(List<ILocalAudioObject> objects) {
    	IChangeTagsProcess process = (IChangeTagsProcess) processFactory.getProcessByName("setLyricsProcess");
    	process.setFilesToChange(objects);
        process.execute();
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(boolean rootSelected, List<DefaultMutableTreeNode> selection) {
        return !rootSelected && !selection.isEmpty();
    }

    @Override
    public boolean isEnabledForNavigationTableSelection(List<IAudioObject> selection) {
        return !selection.isEmpty();
    }

}
