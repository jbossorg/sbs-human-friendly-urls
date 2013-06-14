/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 */
package org.jboss.labs.clearspace.plugin.hfurl;

import com.jivesoftware.base.database.dao.DAOException;
import com.jivesoftware.community.cache.CacheBean;
import org.jboss.labs.clearspace.plugin.hfurl.dao.HFURLBean;
import org.jboss.labs.clearspace.plugin.hfurl.dao.HFURLDAO;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test of DbHFURLManager
 *
 * @author <a href="mailto:lkrzyzan@redhat.com">Libor Krzyzanek</a>
 */
public class DbHFURLManagerTest {

	private DbHFURLManager hfURLManager;

	@SuppressWarnings("unchecked")
	@Before
	public void setupHFURLManager() {
		hfURLManager = new DummyHFURLManager();
		hfURLManager.setDocIdURLPrefix("/docs");
		hfURLManager.setHfULRPrefix("/wiki");

		CacheBean<String, String> hfURLIDsCache = new CacheBean<String, String>("hfURLIDsCache");
		hfURLIDsCache.init();

		@SuppressWarnings("rawtypes")
		CacheBean hfURLTitlesCache = new CacheBean<String, ArrayList<HFURLBean>>("hfURLTitlesCache");
		hfURLTitlesCache.init();

		hfURLManager.setHfURLIDsCache(hfURLIDsCache);
		hfURLManager.setHfURLTitlesCache(hfURLTitlesCache);

		hfURLManager.setHfURLDAO(new DummyHFURLDAO());
	}

	class DummyHFURLManager extends DbHFURLManager {
		@Override
		public boolean isHFLinksEnabled() {
			return true;
		}
	}

	class DummyHFURLDAO implements HFURLDAO {

		HashMap<String, String> data = new HashMap<String, String>();

		public DummyHFURLDAO() {

			data.put("DOC-1234", "DocumentWithFriendlyUrl");
			data.put("DOC-2345", "DocumentWithFriendlyUrl2");

			data.put("DOC-3456", "DuplicateHfurl");
			data.put("DOC-5678", "DuplicateHfurl");
		}

		public void updateIndex() {
		}

		public long getIndexedURLsCount() {
			return data.size();
		}

		public HFURLBean createHFURL(HFURLBean bean) throws DAOException {
			return null;
		}

		public void deleteHFURL(Long id) throws DAOException {
		}

		public void deleteHFURL(String documentId) throws DAOException {
		}

		public HFURLBean getByDocumentId(String documentId) throws EmptyResultDataAccessException {
			HFURLBean title = new HFURLBean();
			title.setHfTitle(data.get(documentId));
			title.setDocumentID(data.get(documentId));
			return title;
		}

		public List<HFURLBean> getByHfURLTitle(String hfURLTitle) throws EmptyResultDataAccessException {
			if ("not-existing".equals(hfURLTitle)) {
				throw new EmptyResultDataAccessException("non existing document", 1);
			}
			List<HFURLBean> titles = new ArrayList<HFURLBean>();
			for (String key : data.keySet()) {
				String value = data.get(key).toLowerCase();
				if (value.equals(hfURLTitle.toLowerCase())) {
					HFURLBean title = new HFURLBean();
					title.setHfTitle(hfURLTitle);
					title.setDocumentID(key);
					titles.add(title);
				}
			}
			return titles;
		}

		public HFURLBean updateHFURL(HFURLBean bean) throws DAOException {
			return null;
		}

		@Override
		public String updateIndex(String documentID) {
			return null;
		}

	}

	/**
	 * Test method for
	 * {@link org.jboss.labs.clearspace.plugin.hfurl.DbHFURLManager#createHFURLTitle(java.lang.String)}
	 */
	@Test
	public void testCreateHFURLTitle() {
		assertEquals("DocumentWithFriendlyUrl", DbHFURLManager.createHFURLTitle("document with friendly url"));
		assertEquals("MoreThanOneSpace", DbHFURLManager.createHFURLTitle("more    than   one  space"));
		assertEquals("UPPERCASEDOCUMENTTITLE", DbHFURLManager.createHFURLTitle("UPPER CASE DOCUMENT TITLE"));

		assertEquals("SpecialCharacters",
				DbHFURLManager.createHFURLTitle("special characters :!@#$%^&*()\"\"\u00a7()[]?><~\u00b1_+`"));
		assertEquals("TitleWith-Hyphen", DbHFURLManager.createHFURLTitle("title with - hyphen"));
		assertEquals("TitleWithNumbers0123456789", DbHFURLManager.createHFURLTitle("title with numbers 0123456789"));
		assertEquals(
				"NationalCharactersEscrzyaiedtnESCRZYAIEDTN",
				DbHFURLManager
						.createHFURLTitle("National characters: \u011b\u0161\u010d\u0159\u017e\u00fd\u00e1\u00ed\u00e9\u010f\u0165\u0148\u011a\u0160\u010c\u0158\u017d\u00dd\u00c1\u00cd\u00c9\u010e\u0164\u0147"));
	}

	/**
	 * Test method for
	 * {@link org.jboss.labs.clearspace.plugin.hfurl.DbHFURLManager#getDocumentID(java.lang.String)}
	 */
	@Test
	public void testGetDocumentID() {
		assertEquals("DOC-1234", hfURLManager.getDocumentID("documentwithfriendlyurl").get(0).getDocumentID());
		assertEquals("DOC-1234", hfURLManager.getDocumentID("DOCUMENTWITHFRIENDLYURL").get(0).getDocumentID());
		assertEquals("DOC-1234", hfURLManager.getDocumentID("DocumentWithFriendlyUrl").get(0).getDocumentID());
		assertEquals("DOC-2345", hfURLManager.getDocumentID("documentwithfriendlyurl2").get(0).getDocumentID());
		assertEquals("DOC-2345", hfURLManager.getDocumentID("DOCUMENTWITHFRIENDLYURL2").get(0).getDocumentID());
		assertEquals("DOC-2345", hfURLManager.getDocumentID("DocumentWithFriendlyUrl2").get(0).getDocumentID());

		assertEquals(0, hfURLManager.getDocumentID("not-existing").size());
	}

	@Test
	public void testGetHFURL() {
		assertEquals("/wiki/DocumentWithFriendlyUrl", hfURLManager.getHFURL("/docs/DOC-1234"));
		assertEquals("/wiki/DocumentWithFriendlyUrl.pdf", hfURLManager.getHFURL("/docs/DOC-1234.pdf"));
		assertEquals("/wiki/DocumentWithFriendlyUrl/edit", hfURLManager.getHFURL("/docs/DOC-1234/edit"));
		assertEquals("/wiki/DocumentWithFriendlyUrl?decorator=print",
				hfURLManager.getHFURL("/docs/DOC-1234?decorator=print"));

		assertEquals("/docs/DOC-1234/delete", hfURLManager.getHFURL("/docs/DOC-1234/delete"));
		assertEquals("/docs/DOC-1234/restore", hfURLManager.getHFURL("/docs/DOC-1234/restore"));

		assertEquals("/docs/DOC-3456", hfURLManager.getHFURLUnique("/docs/DOC-3456", true));

	}

	@Test
	public void testGetDocumentId() {
		assertEquals("DOC-1234", hfURLManager.getDocumentId("/community/docs/DOC-1234"));
		assertEquals("DOC-1234", hfURLManager.getDocumentId("/community/docs/DOC-1234.pdf"));
		assertEquals("DOC-10350", hfURLManager.getDocumentId("/community/docs/DOC-10350"));
		assertEquals("DOC-1234", hfURLManager.getDocumentId("/community/docs/DOC-1234/edit"));

		assertEquals("DOC-1234", hfURLManager.getDocumentId("/community/docs/DOC-1234?decorator=print"));
		assertEquals("DOC-1234", hfURLManager.getDocumentId("/community/docs/DOC-1234/edit?decorator=print"));

		assertEquals("DOC-1234",
				hfURLManager.getDocumentId("/community/docs/DOC-1234;jsessionid=4666CC84E4D942F30DA3A5C3142D1DA4"));

	}

}
