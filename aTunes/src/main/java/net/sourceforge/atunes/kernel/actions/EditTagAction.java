/*
 * aTunes 2.1.0-SNAPSHOT
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

import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.KeyStroke;
import javax.swing.tree.DefaultMutableTreeNode;

import net.sourceforge.atunes.kernel.modules.tags.TagHandler;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.LocalAudioObject;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Opens edit tag dialog
 * 
 * @author fleax
 * 
 */
public class EditTagAction extends AbstractActionOverSelectedObjects<LocalAudioObject> {

    private static final long serialVersionUID = -4310895355731333072L;

    public enum EditTagSources {
        PLAYLIST, NAVIGATOR
    };

    EditTagAction() {
        super(I18nUtils.getString("EDIT_TAG"), LocalAudioObject.class);
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("EDIT_TAG"));
    }

    @Override
    protected void initialize() {
        if (EditTagSources.PLAYLIST.toString().equals(getActionId())) {
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0));
        }
    }

    @Override
    protected void performAction(List<LocalAudioObject> objects) {
        // Start edit by opening edit dialog
        try {
            EditTagSources editTagSource = EditTagSources.valueOf(getActionId());
            TagHandler.getInstance().editFiles(editTagSource, objects);
        } catch (IllegalArgumentException iae) {
            Logger.error(iae);
        } catch (NullPointerException npe) {
            Logger.error(npe);
        }
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(boolean rootSelected, List<DefaultMutableTreeNode> selection) {
        if (EditTagSources.NAVIGATOR.toString().equals(getActionId())) {
            return !rootSelected && !selection.isEmpty();
        }
        return super.isEnabledForNavigationTreeSelection(rootSelected, selection);
    }

    @Override
    public boolean isEnabledForNavigationTableSelection(List<AudioObject> selection) {
        if (EditTagSources.NAVIGATOR.toString().equals(getActionId())) {
            return !selection.isEmpty();
        }
        return super.isEnabledForNavigationTableSelection(selection);
    }

    @Override
    public boolean isEnabledForPlayListSelection(List<AudioObject> selection) {
    	return super.isEnabledForPlayListSelection(selection);
    }    
}
