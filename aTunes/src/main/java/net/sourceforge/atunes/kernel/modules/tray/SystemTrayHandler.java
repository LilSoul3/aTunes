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

package net.sourceforge.atunes.kernel.modules.tray;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.images.NextTrayImageIcon;
import net.sourceforge.atunes.gui.images.PauseTrayImageIcon;
import net.sourceforge.atunes.gui.images.PlayTrayImageIcon;
import net.sourceforge.atunes.gui.images.PreviousTrayImageIcon;
import net.sourceforge.atunes.gui.images.StopTrayImageIcon;
import net.sourceforge.atunes.gui.views.controls.ActionTrayIcon;
import net.sourceforge.atunes.gui.views.controls.JTrayIcon;
import net.sourceforge.atunes.gui.views.controls.JTrayIconPopupMenu;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.actions.ExitAction;
import net.sourceforge.atunes.kernel.actions.MuteAction;
import net.sourceforge.atunes.kernel.actions.PlayAction;
import net.sourceforge.atunes.kernel.actions.PlayNextAudioObjectAction;
import net.sourceforge.atunes.kernel.actions.PlayPreviousAudioObjectAction;
import net.sourceforge.atunes.kernel.actions.RepeatModeAction;
import net.sourceforge.atunes.kernel.actions.ShowAboutAction;
import net.sourceforge.atunes.kernel.actions.ShuffleModeAction;
import net.sourceforge.atunes.kernel.actions.StopCurrentAudioObjectAction;
import net.sourceforge.atunes.kernel.actions.ToggleOSDSettingAction;
import net.sourceforge.atunes.kernel.actions.ToggleWindowVisibilityAction;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.ISystemTrayHandler;
import net.sourceforge.atunes.model.PlaybackState;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.ImageUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The system tray handler.
 */
public final class SystemTrayHandler extends AbstractHandler implements ISystemTrayHandler {

    private boolean trayInitialized;
    private boolean trayIconVisible;
    private boolean trayPlayerVisible;
    private SystemTray tray;
    private JTrayIcon trayIcon;
    private TrayIcon previousIcon;
    private TrayIcon playIcon;
    private TrayIcon stopIcon;
    private TrayIcon nextIcon;
    private JMenuItem playMenuItem;
    
    private boolean playing;

    private NextTrayImageIcon nextTrayIcon;
    private PauseTrayImageIcon pauseTrayIcon;
    private PlayTrayImageIcon playTrayIcon;
    private PreviousTrayImageIcon previousTrayIcon;
    private StopTrayImageIcon stopTrayIcon;
    
    /**
     * @param stopTrayIcon
     */
    public void setStopTrayIcon(StopTrayImageIcon stopTrayIcon) {
		this.stopTrayIcon = stopTrayIcon;
	}
    
    /**
     * @param previousTrayIcon
     */
    public void setPreviousTrayIcon(PreviousTrayImageIcon previousTrayIcon) {
		this.previousTrayIcon = previousTrayIcon;
	}
    
    /**
     * @param playTrayIcon
     */
    public void setPlayTrayIcon(PlayTrayImageIcon playTrayIcon) {
		this.playTrayIcon = playTrayIcon;
	}
    
    /**
     * @param nextTrayIcon
     */
    public void setNextTrayIcon(NextTrayImageIcon nextTrayIcon) {
		this.nextTrayIcon = nextTrayIcon;
	}
    
    /**
     * @param pauseTrayIcon
     */
    public void setPauseTrayIcon(PauseTrayImageIcon pauseTrayIcon) {
		this.pauseTrayIcon = pauseTrayIcon;
	}
    
    @Override
    public void allHandlersInitialized() {
    	if (getOsManager().areTrayIconsSupported()) {
    		// System tray player
    		if (getState().isShowTrayPlayer()) {
    			initTrayPlayerIcons();
    		}

    		// System tray
    		if (getState().isShowSystemTray()) {
    			initTrayIcon();
    		}
    	}
    }

    /**
     * Fill menu.
     * 
     * @param menu
     *            the menu
     * 
     * @return the j tray icon popup menu
     */
    private JPopupMenu fillMenu(JPopupMenu menu) {
        menu.add(getPlayMenuItem());
        menu.add(getStopMenuItem());
        menu.add(getPreviousMenuItem());
        menu.add(getNextMenuItem());
        menu.add(new JSeparator());
        menu.add(getMuteCheckBoxMenuItem());
        menu.add(new JSeparator());
        menu.add(getShuffleCheckBoxMenuItem());
        menu.add(getRepeatCheckBoxMenuItem());
        menu.add(new JSeparator());
        menu.add(getShowOSDCheckBoxMenuItem());
        menu.add(new JSeparator());
        menu.add(getAboutMenuItem());
        menu.add(new JSeparator());
        menu.add(getExitMenuItem());

        GuiUtils.applyComponentOrientation(menu);

        return menu;
    }

    /**
     * Finish.
     */
    public void applicationFinish() {
        setTrayIconVisible(false);
        setTrayPlayerVisible(false);
    }

    /**
     * Inits the system tray.
     */
    private void initSystemTray() {
        if (!trayInitialized && SystemTray.isSupported()) {
            tray = SystemTray.getSystemTray();
            trayInitialized = true;
        }
    }

    /**
     * Inits the tray icon.
     */
    private void initTrayIcon() {
        initSystemTray();
        if (isTrayInitialized()) {
            trayIconVisible = true;
            addTrayIcon(getTrayIcon());
            getFrame().setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        } else {
            Logger.error("No system tray supported");
        }
    }

    /**
     * Inits the tray player icons.
     */
    private void initTrayPlayerIcons() {
        initSystemTray();
        if (isTrayInitialized()) {
            trayPlayerVisible = true;
            // Icons must be added in reverse order
            addTrayIcon(getNextTrayIcon());
            addTrayIcon(getPlayTrayIcon());
            addTrayIcon(getStopTrayIcon());
            addTrayIcon(getPreviousTrayIcon());
        } else {
            Logger.error("No system tray supported");
        }
    }

    /**
     * Adds given tray icon
     * 
     * @param icon
     */
    private void addTrayIcon(TrayIcon icon) {
        try {
       		tray.add(icon);
        } catch (AWTException e) {
            Logger.error(e);
        }
    }

    @Override
	public void setPlaying(boolean playing) {
    	this.playing = playing;
    	if (isTrayInitialized()) {
        	Color color = getState().getTrayPlayerIconsColor().getColor();
        	Image icon = null;
            if (playing) {
                getPlayMenuItem().setText(I18nUtils.getString("PAUSE"));
                pauseTrayIcon.setSize(tray.getTrayIconSize());
            	icon = pauseTrayIcon.getIcon(color).getImage();
            } else {
                getPlayMenuItem().setText(I18nUtils.getString("PLAY"));
                playTrayIcon.setSize(tray.getTrayIconSize());
            	icon = playTrayIcon.getIcon(color).getImage();
            }
            getPlayTrayIcon().setImage(icon);
    	}
    }

    /**
     * Sets the tray icon visible.
     * 
     * @param visible
     *            the new tray icon visible
     */
    private void setTrayIconVisible(boolean visible) {
        if (visible && !trayIconVisible) {
            initTrayIcon();
            if (isTrayInitialized()) {
                trayIconAdvice();
            }
        } else {
            if (!visible && trayIconVisible && isTrayInitialized()) {
                tray.remove(getTrayIcon());
                getFrame().setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                trayIconVisible = false;
            }
        }
    }

    /**
     * Sets the tray player visible.
     * 
     * @param visible
     *            the new tray player visible
     */
    private void setTrayPlayerVisible(boolean visible) {
        if (visible && !trayPlayerVisible) {
            initTrayPlayerIcons();
        } else {
            if (!visible && trayPlayerVisible && isTrayInitialized()) {
                tray.remove(getPreviousTrayIcon());
                tray.remove(getPlayTrayIcon());
                tray.remove(getStopTrayIcon());
                tray.remove(getNextTrayIcon());
                trayPlayerVisible = false;
            }
        }
    }

	private void setTrayToolTip(String msg) {
    	if (isTrayInitialized()) {
    		getTrayIcon().setToolTip(msg);
    	}
    }

    /**
     * Tray icon advice.
     */
    private void trayIconAdvice() {
        // For some reason, in Linux systems display message causes Swing freeze
        if (!getOsManager().isLinux() && isTrayInitialized()) {
            getTrayIcon().displayMessage(Constants.APP_NAME, I18nUtils.getString("TRAY_ICON_MESSAGE"), TrayIcon.MessageType.INFO);
        }
    }

    @Override
    public void applicationStateChanged(IState newState) {
        setTrayIconVisible(newState.isShowSystemTray());
        setTrayPlayerVisible(newState.isShowTrayPlayer());
        setTrayPlayerIconsColor(newState.getTrayPlayerIconsColor().getColor());
    }

    /**
     * Changes color of tray player icons
     * @param color
     */
    private void setTrayPlayerIconsColor(Color color) {
    	if (trayPlayerVisible) {
    		stopTrayIcon.setSize(tray.getTrayIconSize());
    		getStopTrayIcon().setImage(stopTrayIcon.getIcon(color).getImage());
    		if (playing) {
    			pauseTrayIcon.setSize(tray.getTrayIconSize());
    			getPlayTrayIcon().setImage(pauseTrayIcon.getIcon(color).getImage());
    		} else {
    			playTrayIcon.setSize(tray.getTrayIconSize());
    			getPlayTrayIcon().setImage(playTrayIcon.getIcon(color).getImage());
    		}
    		nextTrayIcon.setSize(tray.getTrayIconSize());
    		getNextTrayIcon().setImage(nextTrayIcon.getIcon(color).getImage());
    		previousTrayIcon.setSize(tray.getTrayIconSize());
    		getPreviousTrayIcon().setImage(previousTrayIcon.getIcon(color).getImage());
    	}
	}

	/**
     * Getter of play menu item
     * 
     * @return
     */
    private JMenuItem getPlayMenuItem() {
        if (playMenuItem == null) {
            playMenuItem = new JMenuItem(getBean(PlayAction.class));
        }
        return playMenuItem;
    }

    /**
     * Getter of stop menu item
     * 
     * @return
     */
    private JMenuItem getStopMenuItem() {
        return new JMenuItem(getBean(StopCurrentAudioObjectAction.class));
    }

    /**
     * Getter of previous menu item
     * 
     * @return
     */
    private JMenuItem getPreviousMenuItem() {
        return new JMenuItem(getBean(PlayPreviousAudioObjectAction.class));
    }

    /**
     * Getter for next menu item
     * 
     * @return
     */
    private JMenuItem getNextMenuItem() {
        return new JMenuItem(getBean(PlayNextAudioObjectAction.class));
    }

    /**
     * Getter for mute menu item
     * 
     * @return
     */
    private JCheckBoxMenuItem getMuteCheckBoxMenuItem() {
        JCheckBoxMenuItem mute = new JCheckBoxMenuItem(getBean(MuteAction.class));
        mute.setIcon(null);
        return mute;
    }

    /**
     * Getter for shuffle menu item
     * 
     * @return
     */
    private JCheckBoxMenuItem getShuffleCheckBoxMenuItem() {
        return new JCheckBoxMenuItem(getBean(ShuffleModeAction.class));
    }

    /**
     * Getter for repeat menu item
     */
    private JCheckBoxMenuItem getRepeatCheckBoxMenuItem() {
        return new JCheckBoxMenuItem(getBean(RepeatModeAction.class));
    }

    /**
     * Getter for showOSD menu item
     * 
     * @return
     */
    private JCheckBoxMenuItem getShowOSDCheckBoxMenuItem() {
        return new JCheckBoxMenuItem(getBean(ToggleOSDSettingAction.class));
    }

    /**
     * Getter for about menu item
     * 
     * @return
     */
    private JMenuItem getAboutMenuItem() {
        return new JMenuItem(getBean(ShowAboutAction.class));
    }

    /**
     * Getter for exit menu item
     * 
     * @return
     */
    private JMenuItem getExitMenuItem() {
        return new JMenuItem(getBean(ExitAction.class));
    }

    /**
     * Getter for trayIcon
     * 
     * @return
     */
    private JTrayIcon getTrayIcon() {
        if (trayIcon == null) {
        	Dimension iconSize = tray.getTrayIconSize();
        	Image icon = ImageUtils.scaleImageBicubic(Images.getImage(Images.APP_LOGO_32).getImage(), iconSize.width, iconSize.height).getImage();
            trayIcon = new JTrayIcon(icon, getOsManager().isLinux(), getBean(ToggleWindowVisibilityAction.class));
            trayIcon.setToolTip(StringUtils.getString(Constants.APP_NAME, " ", Constants.VERSION.toShortString()));
            JPopupMenu popupmenu = fillMenu(new JTrayIconPopupMenu(trayIcon));
            trayIcon.setJTrayIconJPopupMenu(popupmenu);
        }
        return trayIcon;
    }

    /**
     * Getter for nextIcon
     * 
     * @return
     */
    private TrayIcon getNextTrayIcon() {
        if (nextIcon == null) {
        	Color color = getState().getTrayPlayerIconsColor().getColor();
        	nextTrayIcon.setSize(tray.getTrayIconSize());
        	Image icon = nextTrayIcon.getIcon(color).getImage();
            nextIcon = new ActionTrayIcon(icon, getBean(PlayNextAudioObjectAction.class));
        }
        return nextIcon;
    }

    /**
     * Getter for stopIcon
     * 
     * @return
     */
    private TrayIcon getStopTrayIcon() {
        if (stopIcon == null) {
        	Color color = getState().getTrayPlayerIconsColor().getColor();
        	stopTrayIcon.setSize(tray.getTrayIconSize());
        	Image icon = stopTrayIcon.getIcon(color).getImage();
            stopIcon = new ActionTrayIcon(icon, getBean(StopCurrentAudioObjectAction.class));
        }
        return stopIcon;
    }

    /**
     * Getter for playIcon
     * 
     * @return
     */
    private TrayIcon getPlayTrayIcon() {
        if (playIcon == null) {
        	Color color = getState().getTrayPlayerIconsColor().getColor();
        	playTrayIcon.setSize(tray.getTrayIconSize());
        	Image icon = playTrayIcon.getIcon(color).getImage();
            playIcon = new ActionTrayIcon(icon, getBean(PlayAction.class));
        }
        return playIcon;
    }
    
    /**
     * Getter for previousIcon
     * 
     * @return
     */
    private TrayIcon getPreviousTrayIcon() {
    	if (previousIcon == null) {
    		Color color = getState().getTrayPlayerIconsColor().getColor();
    		previousTrayIcon.setSize(tray.getTrayIconSize());
    		Image icon = previousTrayIcon.getIcon(color).getImage();
    		previousIcon = new ActionTrayIcon(icon, getBean(PlayPreviousAudioObjectAction.class));
    	}
        return previousIcon;
    }

	/**
	 * @return the trayInitialized
	 */
	protected boolean isTrayInitialized() {
		return trayInitialized;
	}
	
	@Override
	public void playbackStateChanged(final PlaybackState newState, final IAudioObject currentAudioObject) {
        if (!EventQueue.isDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    playbackStateChangedEDT(newState, currentAudioObject);
                }
            });
        } else {
            playbackStateChangedEDT(newState, currentAudioObject);
        }
	}
	
    private void playbackStateChangedEDT(PlaybackState newState, IAudioObject currentAudioObject) {
    	String text = currentAudioObject != null ? currentAudioObject.getAudioObjectDescription() : "";
        StringBuilder strBuilder = new StringBuilder();
        if (!text.equals("")) {
            strBuilder.append(text);
            strBuilder.append(" - ");
        }
        strBuilder.append(Constants.APP_NAME);
        strBuilder.append(" ");
        strBuilder.append(Constants.VERSION.toShortString());
        setTrayToolTip(strBuilder.toString());
    }
}
