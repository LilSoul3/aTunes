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

package net.sourceforge.atunes.kernel.modules.playlist;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IListOfPlayLists;
import net.sourceforge.atunes.model.IPlayList;
import net.sourceforge.atunes.model.IStatePlayer;

/**
 * This class is used to contain all playlists when storing and reading from
 * disk.
 * 
 * @author alex
 */
public class ListOfPlayLists implements IListOfPlayLists {

    private static final long serialVersionUID = -9098493526495642598L;

    /** The play lists. */
    List<IPlayList> playLists;

    /** The selected play list. */
    int selectedPlayListIndex;
    
    ListOfPlayLists() {
	}
    
    /**
     * Returns a list of playlists with an empty playlist.
     * @param statePlayer
     * @return
     */
    static ListOfPlayLists getEmptyPlayList(IStatePlayer statePlayer) {
        ListOfPlayLists l = new ListOfPlayLists();
        List<IPlayList> playLists = new ArrayList<IPlayList>();
        playLists.add(new PlayList(statePlayer));
        l.setPlayLists(playLists);
        l.setSelectedPlayList(0);
        return l;
    }

    /**
     * Gets the play lists.
     * 
     * @return the playLists
     */
    @Override
    public List<IPlayList> getPlayLists() {
        return playLists;
    }

    /**
     * Gets the selected play list.
     * 
     * @return the selectedPlayList
     */
    @Override
    public int getSelectedPlayList() {
        return selectedPlayListIndex;
    }

    /**
     * Sets the play lists.
     * 
     * @param playLists
     *            the playLists to set
     */
    void setPlayLists(List<IPlayList> playLists) {
        this.playLists = playLists;
    }

    /**
     * Sets the selected play list.
     * 
     * @param selectedPlayList
     *            the selectedPlayList to set
     */
    @Override
	public void setSelectedPlayList(int selectedPlayList) {
        this.selectedPlayListIndex = selectedPlayList;
    }

    /**
     * Fills contents of a list of playlists
     * 
     * @param contents
     * @param state
     */
    @Override
	public void setContents(List<List<IAudioObject>> contents, IStatePlayer statePlayer) {
    	if (playLists.size() != contents.size()) {
    		throw new IllegalArgumentException("Invalid play list contents");
    	}
        for (int i = 0; i < playLists.size(); i++) {
        	PlayList pl = (PlayList) getPlayLists().get(i);
			pl.setStatePlayer(statePlayer);
            pl.setContent(contents.get(i));
            pl.setMode(PlayListMode.getPlayListMode(pl, statePlayer));
        }
    }
}
