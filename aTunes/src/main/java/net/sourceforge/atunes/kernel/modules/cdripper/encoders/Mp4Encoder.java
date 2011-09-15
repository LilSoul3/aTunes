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

package net.sourceforge.atunes.kernel.modules.cdripper.encoders;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.kernel.modules.cdripper.ProgressListener;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.kernel.modules.tags.AbstractTag;
import net.sourceforge.atunes.kernel.modules.tags.DefaultTag;
import net.sourceforge.atunes.kernel.modules.tags.TagModifier;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The Class Mp4Encoder.
 */
public class Mp4Encoder implements Encoder {

    /** The format name of this encoder */
    public static final String FORMAT_NAME = "FAAC_MP4";
    public static final String OGGENC = "faac";
    public static final String OUTPUT = "-o";
    public static final String WRAP = "-w";
    public static final String TITLE = "--title";
    public static final String ARTIST = "--artist";
    public static final String ALBUM = "--album";
    public static final String QUALITY = "-q";
    public static final String VERSION = "--help";
    static final String[] MP4_QUALITY = { "50", "100", "150", "200", "250", "300", "350", "400", "450", "500" };

    static final String DEFAULT_MP4_QUALITY = "200";

    private Process p;
    private ProgressListener listener;
    private String albumArtist;
    private String album;
    private int year;
    private String genre;
    private String quality;
    
    private IOSManager osManager;

    /**
     * Test the presence of the ogg encoder oggenc.
     * @param osManager
     * @return Returns true if oggenc was found, false otherwise.
     */
    public static boolean testTool(IOSManager osManager) {
        // Test for faac
        BufferedReader stdInput = null;
        try {
            Process p = new ProcessBuilder(StringUtils.getString(osManager.getExternalToolsPath(), OGGENC)).start();
            stdInput = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            String line = null;
            while ((line = stdInput.readLine()) != null) {
            	Logger.debug(line);
            }

            int code = p.waitFor();
            if (code != 1) {
                return false;
            }
            return true;
        } catch (IOException e) {
            return false;
        } catch (InterruptedException e) {
        	return false;
		} finally {
            ClosingUtils.close(stdInput);
        }
    }

    public Mp4Encoder(IOSManager osManager) {
    	this.osManager = osManager;
	}
    
    /**
     * Encode the wav file and tags it using entagged.
     * 
     * @param wavFile
     *            The filename and path of the wav file that should be encoded
     * @param mp4File
     *            The name of the new file to be created
     * @param title
     *            The title of the song (only title, not artist and album)
     * @param trackNumber
     *            The track number of the song
     * @param artist
     *            the artist
     * @param composer
     *            the composer
     * 
     * @return Returns true if encoding was successfull, false otherwise.
     */
    @Override
    public boolean encode(File wavFile, File mp4File, String title, int trackNumber, String artist, String composer) {
        Logger.info(StringUtils.getString("Mp4 encoding process started... ", wavFile.getName(), " -> ", mp4File.getName()));
        BufferedReader stdInput = null;
        try {
            List<String> command = new ArrayList<String>();
            command.add(StringUtils.getString(osManager.getExternalToolsPath(), OGGENC));
            command.add(OUTPUT);
            command.add(mp4File.getAbsolutePath());
            command.add(QUALITY);
            command.add(quality);
            command.add(WRAP);
            command.add(wavFile.getAbsolutePath());
            p = new ProcessBuilder(command).start();
            stdInput = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String s = null;
            int percent = -1;

            // Read progress
            while ((s = stdInput.readLine()) != null) {
                if (listener != null) {
                    if (s.matches(".*(...%).*")) {
                        // Percent values can be for example 0.3% or 0,3%, so be careful with "." and ","
                        int decimalPointPosition = s.indexOf('%');
                        int aux = Integer.parseInt((s.substring(s.indexOf('(') + 1, decimalPointPosition).trim()));
                        if (aux != percent) {
                            percent = aux;
                            final int percentHelp = percent;
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    listener.notifyProgress(percentHelp);
                                }
                            });
                        }
                    } else if (s.startsWith("Done")) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                listener.notifyProgress(100);
                            }
                        });
                    }
                }
            }

            int code = p.waitFor();
            if (code != 0) {
                Logger.error(StringUtils.getString("Process returned code ", code));
                return false;
            }

            // Gather the info and write the tag
            try {
            	ILocalAudioObject audiofile = new AudioFile(mp4File);
                AbstractTag tag = new DefaultTag();

                tag.setAlbum(album);
                tag.setAlbumArtist(albumArtist);
                tag.setArtist(artist);
                tag.setComposer(composer);
                tag.setYear(year);
                tag.setGenre(genre);
                tag.setTitle(title);
                tag.setTrackNumber(trackNumber);

                TagModifier.setInfo(audiofile, tag);

            } catch (Exception e) {
                Logger.error(StringUtils.getString("Jaudiotagger: Process execution caused exception ", e));
                return false;
            }

            Logger.info("Encoded ok!!");
            return true;

        } catch (InterruptedException e) {
            Logger.error(StringUtils.getString("Process execution caused exception ", e));
            return false;
        } catch (IOException e) {
            Logger.error(StringUtils.getString("Process execution caused exception ", e));
            return false;
        } finally {
            ClosingUtils.close(stdInput);
        }
    }

    /**
     * Gets the album.
     * 
     * @return the album
     */
    public String getAlbum() {
        return album;
    }

    /**
     * Gets the album artist.
     * 
     * @return the albumArtist
     */
    public String getAlbumArtist() {
        return albumArtist;
    }

    /**
     * Gets the extension of encoded files.
     * 
     * @return Returns the extension of the encoded file
     */
    @Override
    public String getExtensionOfEncodedFiles() {
        return "m4a";
    }

    /**
     * Gets the genre.
     * 
     * @return the genre
     */
    public String getGenre() {
        return genre;
    }

    /**
     * Gets the year.
     * 
     * @return the year
     */
    public int getYear() {
        return year;
    }

    @Override
    public void setAlbum(String album) {
        this.album = album;
    }

    /**
     * Sets the album artist.
     * 
     * @param albumArtist
     *            the albumArtist to set
     */
    public void setAlbumArtist(String albumArtist) {
        this.albumArtist = albumArtist;
    }

    @Override
    public void setArtist(String artist) {
        this.albumArtist = artist;
    }

    @Override
    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Override
    public void setListener(ProgressListener listener) {
        this.listener = listener;
    }

    @Override
    public void setQuality(String quality) {
        this.quality = quality;
    }

    @Override
    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public void stop() {
        if (p != null) {
            p.destroy();
        }
    }

    @Override
    public String[] getAvailableQualities() {
        return MP4_QUALITY;
    }

    @Override
    public String getDefaultQuality() {
        return DEFAULT_MP4_QUALITY;
    }

    @Override
    public String getFormatName() {
        return FORMAT_NAME;
    }
}
