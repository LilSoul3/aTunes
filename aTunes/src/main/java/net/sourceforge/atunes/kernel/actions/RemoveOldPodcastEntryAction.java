/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationHandler;
import net.sourceforge.atunes.kernel.modules.navigator.PodcastNavigationView;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.I18nUtils;

public class RemoveOldPodcastEntryAction extends AbstractActionOverSelectedObjects<PodcastFeedEntry> {

    private static final long serialVersionUID = -1499729879534990802L;

    RemoveOldPodcastEntryAction() {
        super(I18nUtils.getString("REMOVE_OLD_PODCAST_ENTRY"), Images.getImage(Images.REMOVE), PodcastFeedEntry.class);
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("REMOVE_OLD_PODCAST_ENTRY"));
    }

    @Override
    protected void performAction(List<PodcastFeedEntry> objects) {
        for (PodcastFeedEntry pfe : objects) {
            pfe.getPodcastFeed().removeEntry(pfe);
        }
        NavigationHandler.getInstance().refreshView(PodcastNavigationView.class);
    }

    @Override
    public boolean isEnabledForNavigationTableSelection(List<AudioObject> selection) {
        for (AudioObject ao : selection) {
            if (!(ao instanceof PodcastFeedEntry) || !((PodcastFeedEntry) ao).isOld()) {
                return false;
            }
        }
        return true;
    }

}
