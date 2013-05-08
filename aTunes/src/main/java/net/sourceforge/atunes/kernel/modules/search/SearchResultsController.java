/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.kernel.modules.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;

import net.sourceforge.atunes.gui.ColumnRenderers;
import net.sourceforge.atunes.gui.SearchResultColumnModel;
import net.sourceforge.atunes.gui.views.controls.ColumnSetRowSorter;
import net.sourceforge.atunes.gui.views.dialogs.SearchResultsDialog;
import net.sourceforge.atunes.kernel.AbstractSimpleController;
import net.sourceforge.atunes.kernel.modules.columns.SearchResultsColumnSet;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IAudioObjectComparator;
import net.sourceforge.atunes.model.IAudioObjectPropertiesDialogFactory;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IColumn;
import net.sourceforge.atunes.model.IColumnSet;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.ISearchableObject;

/**
 * Controller for the search result dialog.
 */
final class SearchResultsController extends
		AbstractSimpleController<SearchResultsDialog> {

	private List<IAudioObject> results;

	private final IColumnSet columnSet;

	private final IPlayListHandler playListHandler;

	private final ILookAndFeelManager lookAndFeelManager;

	private final IBeanFactory beanFactory;

	/**
	 * @param beanFactory
	 * @param dialog
	 * @param playListHandler
	 * @param lookAndFeelManager
	 */
	SearchResultsController(final IBeanFactory beanFactory,
			final SearchResultsDialog dialog,
			final IPlayListHandler playListHandler,
			final ILookAndFeelManager lookAndFeelManager) {
		super(dialog);
		this.beanFactory = beanFactory;
		this.columnSet = beanFactory.getBean("searchResultsColumnSet",
				IColumnSet.class);
		this.playListHandler = playListHandler;
		this.lookAndFeelManager = lookAndFeelManager;
		addBindings();
	}

	/**
	 * Shows dialog with search results.
	 * 
	 * @param searchableObject
	 *            the searchable object
	 * @param resultsList
	 *            the results
	 */
	public void showSearchResults(final ISearchableObject searchableObject,
			final List<IAudioObject> resultsList) {
		this.results = resultsList;

		SearchResultTableModel tableModel = (SearchResultTableModel) getComponentControlled()
				.getSearchResultsTable().getModel();

		IColumn<?> sortedColumn = this.columnSet.getSortedColumn();
		if (sortedColumn != null) {
			Collections.sort(resultsList, sortedColumn.getComparator());
		} else {
			beanFactory.getBean(IAudioObjectComparator.class).sort(resultsList);
		}

		tableModel.setResults(resultsList);
		tableModel.refresh(TableModelEvent.UPDATE);
		getComponentControlled().setVisible(true);
	}

	@Override
	public void addBindings() {
		JTable table = getComponentControlled().getSearchResultsTable();
		SearchResultTableModel tableModel = new SearchResultTableModel();
		tableModel.setColumnSet(this.columnSet);
		table.setModel(tableModel);

		// TODO: Created manually
		SearchResultColumnModel columnModel = new SearchResultColumnModel();
		columnModel.setTable(table);
		columnModel.setModel(tableModel);
		columnModel.setBeanFactory(this.beanFactory);
		columnModel.setColumnSet(this.beanFactory
				.getBean(SearchResultsColumnSet.class));
		columnModel.initialize();
		table.setColumnModel(columnModel);

		// Set sorter
		new ColumnSetRowSorter(table, tableModel, columnModel);

		// Bind column set popup menu
		this.beanFactory.getBean(IControlsBuilder.class)
				.createColumnSetPopupMenu(
						getComponentControlled().getSearchResultsTable(),
						columnModel, tableModel);

		// Set renderers

		ColumnRenderers.addRenderers(getComponentControlled()
				.getSearchResultsTable(), columnModel, this.lookAndFeelManager
				.getCurrentLookAndFeel());

		SearchResultsListener listener = new SearchResultsListener(this,
				getComponentControlled());
		getComponentControlled().getShowElementInfo().addActionListener(
				listener);
		getComponentControlled().getAddToCurrentPlayList().addActionListener(
				listener);
		getComponentControlled().getAddToNewPlayList().addActionListener(
				listener);

	}

	/**
	 * Displays info of first selected item.
	 */
	protected void showInfo() {
		List<IAudioObject> selectedResults = getSelectedResults();
		if (selectedResults == null) {
			return;
		}
		this.beanFactory.getBean(IAudioObjectPropertiesDialogFactory.class)
				.newInstance(selectedResults.get(0)).showDialog();
	}

	/**
	 * Adds selected results to current play list.
	 */
	protected void addToPlayList() {
		List<IAudioObject> selectedResults = getSelectedResults();
		if (selectedResults == null) {
			return;
		}
		this.playListHandler.addToVisiblePlayList(selectedResults);
	}

	/**
	 * Adds selected results to a new play list.
	 */
	protected void addToNewPlayList() {
		List<IAudioObject> selectedResults = getSelectedResults();
		if (selectedResults == null) {
			return;
		}
		this.playListHandler.newPlayList(selectedResults);
	}

	/**
	 * Returns selected results as audio objects.
	 * 
	 * @return the selected results
	 */
	private List<IAudioObject> getSelectedResults() {
		int[] selectedRows = getComponentControlled().getSearchResultsTable()
				.getSelectedRows();
		if (selectedRows.length == 0) {
			return null;
		}
		List<IAudioObject> selectedResults = new ArrayList<IAudioObject>();
		JTable table = getComponentControlled().getSearchResultsTable();
		for (int row : selectedRows) {
			selectedResults.add(this.results.get(table
					.convertRowIndexToModel(row)));
		}
		return selectedResults;
	}
}
