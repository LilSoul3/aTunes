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

package net.sourceforge.atunes.kernel.modules.repository;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.model.IErrorDialogFactory;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IProcessListener;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.utils.I18nUtils;

final class ImportFilesProcessListener implements IProcessListener {
	
	private final ImportFilesProcess process;
	private final IRepositoryHandler repositoryHandler;
	private final IFrame frame;
	private final IErrorDialogFactory errorDialogFactory;
	
	ImportFilesProcessListener(ImportFilesProcess process, IRepositoryHandler repositoryHandler, IFrame frame, IErrorDialogFactory errorDialogFactory) {
		this.process = process;
		this.repositoryHandler = repositoryHandler;
		this.frame = frame;
		this.errorDialogFactory = errorDialogFactory;
	}

	@Override
	public void processCanceled() {
		// Nothing to do, files copied will be removed before calling this method 
	}

	@Override
	public void processFinished(final boolean ok) {
		if (!ok) {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						errorDialogFactory.getDialog().showErrorDialog(frame, I18nUtils.getString("ERRORS_IN_IMPORT_PROCESS"));
					}
				});
			} catch (InterruptedException e) {
				// Do nothing
			} catch (InvocationTargetException e) {
				// Do nothing
			}
		} else {
			// If import is ok then add files to repository
			repositoryHandler.addFilesAndRefresh(process.getFilesTransferred());
		}
	}
}