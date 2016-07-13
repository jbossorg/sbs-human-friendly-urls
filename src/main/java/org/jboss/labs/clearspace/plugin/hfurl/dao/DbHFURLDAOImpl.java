/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 */
package org.jboss.labs.clearspace.plugin.hfurl.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jboss.labs.clearspace.plugin.hfurl.DbHFURLManager;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Transactional;

import com.jivesoftware.base.database.dao.DAOException;
import com.jivesoftware.base.database.dao.JiveJdbcDaoSupport;
import com.jivesoftware.base.database.sequence.SequenceManager;

/**
 * DB Implementation of HF URL DAO
 *
 * @author <a href="mailto:lkrzyzan@redhat.com">Libor Krzyzanek</a>
 */
public class DbHFURLDAOImpl extends JiveJdbcDaoSupport implements HFURLDAO {

	private static final Logger log = LogManager.getLogger(DbHFURLDAOImpl.class);

	public static final int HFURL_MAP_SEQ = 5000;

	private static final String SELECT_FIELDS = "SELECT id, hfTitle, documentID, actualTitle ";

	/**
	 * Select all documents including archived versions
	 */
	private static final String SELECT_ALL_DOCUMENTS = "SELECT distinct d.documentID, v.title FROM jiveDocument d, jiveDocVersion v WHERE d.internalDocID = v.internalDocID";

	private static final String COUNT_ROWS = "SELECT COUNT(*) FROM humanFriendlyURLMap";

	private static final String LOAD_HFURL_BY_HFTITLE = SELECT_FIELDS
			+ " FROM humanFriendlyURLMap WHERE hfTitle = ?";

	private static final String LOAD_HFURL_BY_DOCUMENT_ID = SELECT_FIELDS
			+ " FROM humanFriendlyURLMap WHERE documentID=? and actualTitle=true";

	private static final String INSERT_HFURL_ENTRY = "INSERT INTO humanFriendlyURLMap (id, hfTitle, documentID, actualTitle) VALUES (?, ?, ?, ?)";

	private static final String UPDATE_HFURL_ENTRY = "UPDATE humanFriendlyURLMap SET hfTitle=?, documentID=?, actualTitle=? WHERE id=?";

	private static final String DELETE_HFURL_ALL = "DELETE FROM humanFriendlyURLMap";

	private static final String DELETE_HFURL_ENTRY_BY_ID = "DELETE FROM humanFriendlyURLMap WHERE id=?";

	private static final String DELETE_HFURL_ENTRY_BY_DOCUMENT_ID = "DELETE FROM humanFriendlyURLMap WHERE documentID=?";

	private static final HfURLMapMapper mapper = new HfURLMapMapper();

	@Transactional
	public void updateIndex() {
		log.info("update index called");

		log.info("Delete all HF URL entries");
		getJdbcTemplate().update(DELETE_HFURL_ALL);

		// Reset ID sequencer to 0 - unfortunately it's not possible

		log.info("Create titles for published versions");
		final List<String[]> actualTitles = getJdbcTemplate().query(
				SELECT_ALL_DOCUMENTS + " and v.state='published'", new DocIdTitleExtractor());

		List<String> addedTitles = new ArrayList<String>();
		for (String[] values : actualTitles) {
			HFURLBean bean = new HFURLBean(values[0], values[1], true);
			createHFURL(bean);
			addedTitles.add(values[1].toLowerCase());
		}

		log.info("Create titles for archived versions");
		final List<String[]> archiveTitles = getJdbcTemplate().query(
				SELECT_ALL_DOCUMENTS + " and v.state='archived'", new DocIdTitleExtractor());

		for (String[] values : archiveTitles) {
			String archivedKey = values[0];
			String archivedTitle = values[1];

			// Ignoring case because HF URL Mapper ignore case.
			if (addedTitles.contains(archivedTitle.toLowerCase())) {
				if (log.isDebugEnabled()) {
					log.debug("HF Title already exists in the index. key: " + archivedKey + ", title: " + archivedTitle);
				}
				continue;
			}
			HFURLBean bean = new HFURLBean(archivedKey, archivedTitle, false);
			createHFURL(bean);
		}
	}

	@Override
	@Transactional
	public String updateIndex(String documentID) {
		deleteHFURL(documentID);

		log.info("Create titles for published versions");
		String actualHFTitle = null;

		final List<String[]> actualTitles = getJdbcTemplate().query(
				SELECT_ALL_DOCUMENTS + " and v.state='published' and d.documentID = ?", new Object[]{documentID},
				new DocIdTitleExtractor());

		List<String> addedTitles = new ArrayList<String>();
		for (String[] values : actualTitles) {
			HFURLBean bean = new HFURLBean(values[0], values[1], true);
			createHFURL(bean);
			addedTitles.add(values[1].toLowerCase());
			actualHFTitle = values[1];
		}

		log.info("Create titles for archived versions");
		final List<String[]> archiveTitles = getJdbcTemplate().query(
				SELECT_ALL_DOCUMENTS + " and v.state='archived' and d.documentID = ?", new Object[]{documentID},
				new DocIdTitleExtractor());

		for (String[] values : archiveTitles) {
			String archivedKey = values[0];
			String archivedTitle = values[1];

			// Ignoring case because HF URL Mapper ignore case.
			if (addedTitles.contains(archivedTitle.toLowerCase())) {
				if (log.isDebugEnabled()) {
					log.debug("HF Title already exists in the index. key: " + archivedKey + ", title: " + archivedTitle);
				}
				continue;
			}
			HFURLBean bean = new HFURLBean(archivedKey, archivedTitle, false);
			createHFURL(bean);
		}

		return actualHFTitle;
	}

	@SuppressWarnings({"rawtypes"})
	public long getIndexedURLsCount() {
		return this.getSimpleJdbcTemplate().queryForLong(COUNT_ROWS, new HashMap());
	}

	public List<HFURLBean> getByHfURLTitle(String hfURLTitle) throws EmptyResultDataAccessException {
		return this.getSimpleJdbcTemplate().query(LOAD_HFURL_BY_HFTITLE, mapper, hfURLTitle.toLowerCase());
	}

	public HFURLBean getByDocumentId(String documentId) throws EmptyResultDataAccessException {
		return this.getSimpleJdbcTemplate().queryForObject(LOAD_HFURL_BY_DOCUMENT_ID, mapper, documentId);
	}

	public HFURLBean createHFURL(HFURLBean bean) throws DAOException {
		log.debug("Create HF URL map");
		bean.setId(SequenceManager.nextID(HFURL_MAP_SEQ));

		if (log.isDebugEnabled()) {
			log.debug("bean: " + bean);
		}

		getSimpleJdbcTemplate().update(INSERT_HFURL_ENTRY, bean.getId(), bean.getHfTitle(), bean.getDocumentID(),
				bean.getActualTitle());

		return bean;
	}

	public HFURLBean updateHFURL(HFURLBean bean) throws DAOException {
		log.debug("Update HF URL map");
		getSimpleJdbcTemplate().update(UPDATE_HFURL_ENTRY, bean.getHfTitle(), bean.getDocumentID(), bean.getActualTitle(),
				bean.getId());

		return bean;
	}

	public void deleteHFURL(Long id) throws DAOException {
		log.debug("Delete HF URL map");
		getSimpleJdbcTemplate().update(DELETE_HFURL_ENTRY_BY_ID, id);
	}

	public void deleteHFURL(String documentId) throws DAOException {
		getSimpleJdbcTemplate().update(DELETE_HFURL_ENTRY_BY_DOCUMENT_ID, documentId);
	}

	static final class HfURLMapMapper implements RowMapper<HFURLBean> {
		@Override
		public HFURLBean mapRow(ResultSet rs, int rowNum) throws SQLException {
			HFURLBean uct = new HFURLBean();
			uct.setId(rs.getLong("id"));
			uct.setHfTitle(rs.getString("hfTitle"));
			uct.setDocumentID(rs.getString("documentID"));
			uct.setActualTitle(rs.getBoolean("actualTitle"));
			return uct;
		}
	}

	class DocIdTitleExtractor implements ResultSetExtractor<List<String[]>> {
		@Override
		public List<String[]> extractData(ResultSet rs) throws SQLException, DataAccessException {
			List<String[]> titles = new ArrayList<String[]>();
			while (rs.next()) {
				String hfTitle = DbHFURLManager.createHFURLTitle(rs.getString(2));

				if (StringUtils.isBlank(hfTitle)) {
					log.warn("Document's subject contains probably only special characters. It will not have HF URL. Doc ID: "
							+ rs.getString(1));
					continue;
				}

				titles.add(new String[]{rs.getString(1), hfTitle});
			}
			return titles;
		}
	}
}
