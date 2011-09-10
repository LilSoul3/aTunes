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

package net.sourceforge.atunes.kernel.modules.navigator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import net.sourceforge.atunes.kernel.modules.repository.data.Genre;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.ITreeGenerator;
import net.sourceforge.atunes.model.TreeObject;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Builds a Genre ViewMode for a view. Several views can use this code (Repository and Device)
 * @author fleax
 *
 */
public class GenreTreeGenerator implements ITreeGenerator {

	/**
	 * Builds tree
	 * @param state
	 * @param rootTextKey
	 * @param view
	 * @param structure
	 * @param currentFilter
	 * @param root
	 * @param treeModel
	 * @param objectsSelected
	 * @param objectsExpanded
	 */
    public void buildTree(IState state, String rootTextKey, AbstractNavigationView view, Map<String, ?> structure, String currentFilter, DefaultMutableTreeNode root, DefaultTreeModel treeModel, List<TreeObject<? extends AudioObject>> objectsSelected, List<TreeObject<? extends AudioObject>> objectsExpanded) {
        // Nodes to be selected after refresh
        List<DefaultMutableTreeNode> nodesToSelect = new ArrayList<DefaultMutableTreeNode>();
        // Nodes to be expanded after refresh
        List<DefaultMutableTreeNode> nodesToExpand = new ArrayList<DefaultMutableTreeNode>();
        
        // Refresh nodes
        root.setUserObject(I18nUtils.getString(rootTextKey));
        root.removeAllChildren();
        List<String> genreNamesList = new ArrayList<String>(structure.keySet());
        Collections.sort(genreNamesList, view.getDefaultComparator());

        for (int i = 0; i < genreNamesList.size(); i++) {
            Genre genre = (Genre) structure.get(genreNamesList.get(i));
            if (currentFilter == null || genre.getName().toUpperCase().contains(currentFilter.toUpperCase())) {
                DefaultMutableTreeNode genreNode = new DefaultMutableTreeNode(genre);
                // If node was selected before refreshing...
                if (objectsSelected.contains(genreNode.getUserObject())) {
                    nodesToSelect.add(genreNode);
                }
                // If node was expanded before refreshing...
                if (objectsExpanded.contains(genreNode.getUserObject())) {
                    nodesToExpand.add(genreNode);
                }
                List<String> artistNamesList = new ArrayList<String>(genre.getArtistSet());
                if (state.isUseSmartTagViewSorting() && !state.isUsePersonNamesArtistTagViewSorting()) {
                    Collections.sort(artistNamesList, view.getSmartComparator());
                } else if (state.isUsePersonNamesArtistTagViewSorting()) {
                    Collections.sort(artistNamesList, view.getArtistNamesComparator());
                } else {
                    Collections.sort(artistNamesList, view.getDefaultComparator());
                }
                Map<String, Artist> genreArtists = genre.getArtistObjects();
                for (int j = 0; j < artistNamesList.size(); j++) {
                    Artist artist = genreArtists.get(artistNamesList.get(j));
                    DefaultMutableTreeNode artistNode = new DefaultMutableTreeNode(artist);
                    List<String> albumNamesList = new ArrayList<String>(artist.getAlbums().keySet());
                    if (state.isUseSmartTagViewSorting()) {
                        Collections.sort(albumNamesList, view.getSmartComparator());
                    } else {
                        Collections.sort(albumNamesList, view.getDefaultComparator());
                    }
                    for (int k = 0; k < albumNamesList.size(); k++) {
                        Album album = artist.getAlbum(albumNamesList.get(k));
                        DefaultMutableTreeNode albumNode = new DefaultMutableTreeNode(album);
                        artistNode.add(albumNode);
                        genreNode.add(artistNode);
                        // If node was selected before refreshing...
                        if (objectsSelected.contains(artistNode.getUserObject())) {
                            nodesToSelect.add(artistNode);
                        }
                        // If node was selected before refreshing...
                        if (objectsSelected.contains(albumNode.getUserObject())) {
                            nodesToSelect.add(albumNode);
                        }
                        // If node was expanded before refreshing...
                        if (objectsExpanded.contains(artistNode.getUserObject()) && objectsExpanded.contains(((DefaultMutableTreeNode) artistNode.getParent()).getUserObject())) {
                            nodesToExpand.add(artistNode);
                        }
                    }
                }
                root.add(genreNode);
            }
        }

        // Reload the tree to refresh content
        treeModel.reload();

        // Expand nodes
        AbstractNavigationView.expandNodes(view.getTree(), nodesToExpand);

        // Once tree has been refreshed, select previously selected nodes
        AbstractNavigationView.selectNodes(view.getTree(), nodesToSelect);
    }
    
    @Override
    public void selectAudioObject(JTree tree, AudioObject audioObject) {

    	DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) tree.getModel().getRoot();
    	TreePath treePath = null;

    	@SuppressWarnings("unchecked")
    	Enumeration<DefaultMutableTreeNode> genres = rootNode.children();

    	while (genres.hasMoreElements()){
    		DefaultMutableTreeNode genreNode = genres.nextElement();
    		Genre genre = (Genre) genreNode.getUserObject();
    		if (genre.getName().equals(audioObject.getGenre())){
    			@SuppressWarnings("unchecked")
    			Enumeration<DefaultMutableTreeNode> artists = genreNode.children();

    			while (artists.hasMoreElements()){
    				DefaultMutableTreeNode artistNode = artists.nextElement();
    				Artist artist = (Artist) artistNode.getUserObject();
    				if (artist.getName().equals(audioObject.getArtist())){
    					@SuppressWarnings("unchecked")
    					Enumeration<DefaultMutableTreeNode> albums = artistNode.children();
    					while (albums.hasMoreElements()){
    						DefaultMutableTreeNode albumNode = albums.nextElement();
    						Album album = (Album) albumNode.getUserObject();
    						if (album.getName().equals(audioObject.getAlbum())){
    							treePath = new TreePath(albumNode.getPath());
    							break;
    						}
    					}
    					break;
    				}
    			}
    			break;
    		}
    	}

    	tree.setSelectionPath(treePath);
    	tree.scrollPathToVisible(treePath);
    }

    @Override
    public void selectArtist(JTree tree, String artistName) {
    	DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) tree.getModel().getRoot();
    	List<TreePath> treePathList = new ArrayList<TreePath>();
    	TreePath[] treePaths = null;

    	@SuppressWarnings("unchecked")
    	Enumeration<DefaultMutableTreeNode> genres = rootNode.children();

    	while (genres.hasMoreElements()){

    		DefaultMutableTreeNode genreNode = genres.nextElement();

    		@SuppressWarnings("unchecked")
    		Enumeration<DefaultMutableTreeNode> artists = genreNode.children();

    		while (artists.hasMoreElements()){
    			DefaultMutableTreeNode artistNode = artists.nextElement();
    			Artist artist = (Artist) artistNode.getUserObject();
    			if (artist.getName().equals(artistName)){
    				TreePath treePath = new TreePath(artistNode.getPath()); 
    				treePathList.add(treePath);
    				tree.expandPath(treePath);
    				break;
    			}
    		}
    	}

    	treePaths = new TreePath[treePathList.size()];
    	treePaths = (TreePath[]) treePathList.toArray(treePaths);

    	tree.setSelectionPaths(treePaths);
    	tree.scrollPathToVisible(treePaths[0]);


    }


}
