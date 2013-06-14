/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 */
package org.jboss.labs.clearspace.plugin.hfurl;

import com.jivesoftware.base.event.v2.EventListener;
import com.jivesoftware.cache.Cache;
import com.jivesoftware.community.Document;
import com.jivesoftware.community.JiveContentObject.Status;
import com.jivesoftware.community.JiveGlobals;
import com.jivesoftware.community.event.DocumentEvent;
import com.jivesoftware.community.event.listener.BaseDocumentEventListener;
import com.jivesoftware.community.lifecycle.JiveApplication;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jboss.labs.clearspace.plugin.hfurl.dao.HFURLBean;
import org.jboss.labs.clearspace.plugin.hfurl.dao.HFURLDAO;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.ArrayList;
import java.util.List;

/**
 * DB Implementation of HF URL Manager
 *
 * @author <a href="mailto:lkrzyzan@redhat.com">Libor Krzyzanek</a>
 */
public class DbHFURLManager extends BaseDocumentEventListener implements HFURLManager, EventListener<DocumentEvent> {

	private static final Logger log = LogManager.getLogger(DbHFURLManager.class);

	/**
	 * A cache for HF URL titles. It contains only actual (published) documents<br>
	 * Key is DocumentID<br>
	 * Value is HF Title (capitalized)
	 */
	private Cache<String, String> hfURLIDsCache;

	/**
	 * A cache for HF URL titles.<br>
	 * Key is HF Title in lower case!<br>
	 * Value is list of HFURLBean beans
	 */
	private Cache<String, List<HFURLBean>> hfURLTitlesCache;

	private HFURLDAO hfURLDAO;

	private String hfULRPrefix;

	private String docIdURLPrefix;

	public long getIndexedURLsCount() {
		return hfURLDAO.getIndexedURLsCount();
	}

	public boolean isHFLinksEnabled() {
		return JiveGlobals.getJiveBooleanProperty(HFURLPlugin.HFURL_ENABLED_KEY);
	}

	public void setHFLinksEnabled(boolean enabled) {
		JiveGlobals.setJiveProperty(HFURLPlugin.HFURL_ENABLED_KEY, Boolean.toString(enabled));

	}

	public static String createHFURLTitle(String documentTitle) {
		if (log.isDebugEnabled()) {
			log.debug("createHFURLTitle from doc title: " + documentTitle);
		}
		// Capitalize (but not fully because we cannot break words like J2EE or JSF
		String hfURLTitle = WordUtils.capitalize(documentTitle);

		// remove white spaces
		hfURLTitle = hfURLTitle.replaceAll("[\\s]+", "");

		// Remove accents
		// Java 6
		hfURLTitle = java.text.Normalizer.normalize(hfURLTitle, java.text.Normalizer.Form.NFD);

		// remove special characters
		hfURLTitle = hfURLTitle.replaceAll("[^a-zA-Z0-9-]+", "");

		// URL Encode - not needed because all special characters are removed
		// hfURLTitle = URLEncoder.encode(hfURLTitle, "UTF-8");

		if (log.isDebugEnabled()) {
			log.debug("created HF URL title " + hfURLTitle);
		}

		return hfURLTitle;
	}

	public void updateIndex() {
		hfURLDAO.updateIndex();
		hfURLIDsCache.clear();
		hfURLTitlesCache.clear();
	}

	@Override
	public String updateIndex(String documentID) {
		if (log.isDebugEnabled()) {
			log.debug("Update HF URL Index for document: " + documentID);
		}
		String hfTitle = hfURLDAO.updateIndex(documentID);
		hfURLIDsCache.remove(documentID);
		hfURLTitlesCache.clear();

		// refresh item in Titles' cache.
		if (hfTitle != null) {
			hfURLTitlesCache.remove(hfTitle.toLowerCase());
			getDocumentID(hfTitle);
		} else {
			hfURLTitlesCache.clear();
		}
		return hfTitle;
	}

	public String getHfURLTitle(String documentID) {
		String hfURLTitle = hfURLIDsCache.get(documentID);

		if (hfURLTitle == null) {
			try {
				HFURLBean hfBean = hfURLDAO.getByDocumentId(documentID);

				hfURLTitle = hfBean.getHfTitle();
				hfURLIDsCache.put(hfBean.getDocumentID(), hfURLTitle);

				// refresh item in Titles' cache.
				hfURLTitlesCache.remove(hfURLTitle.toLowerCase());
				getDocumentID(hfURLTitle);
			} catch (EmptyResultDataAccessException e) {
				// nothing found - this case is used also by "documentAdded".
				try {
					// let's update only published documents
					Document doc = getDocumentManager().getDocument(documentID);
					if (doc.getStatus().equals(Status.PUBLISHED)) {
						hfURLTitle = updateIndex(documentID);
					}
				} catch (Exception e1) {
					log.warn("Cannot get document. probably invalid document ID: " + documentID, e1);
				}
			}
		}

		//
		return hfURLTitle;
	}

	/**
	 * Get Path of Document
	 *
	 * @param documentID DOC-1234
	 * @return "/wiki/Infinispan" or "/docs/DOC-1234" if Human friendly URL
	 *         doesn't exists
	 */
	public static String getHfURLPath(String documentID) {
		DbHFURLManager thisBean = JiveApplication.getContext().getSpringBean("hfURLManager");
		String title = thisBean.getHfURLTitle(documentID);
		if (StringUtils.isBlank(title)) {
			return thisBean.getDocIdURLPrefix() + "/" + documentID;
		}
		return thisBean.getHfULRPrefix() + "/" + title;
	}

	public String getHFURL(String standardURL) {
		return getHFURLUnique(standardURL, false);
	}

	public String getHFURLUnique(String standardURL, boolean onlyUniqueHFURL) {
		if (!isHFLinksEnabled()) {
			return standardURL;
		}

		String hfURL = standardURL;
		try {
			// fixed bug ORG-174 and ORG-175 for deleting and restoring the article -
			// for these actions are standard URL returned.
			if (standardURL != null && standardURL.startsWith(docIdURLPrefix + "/") && !standardURL.contains("/delete")
					&& !standardURL.contains("/restore")) {

				final String documentId = getDocumentId(standardURL);
				final String hfTitle = getHfURLTitle(documentId);
				if (hfTitle == null) {
					return standardURL;
				}

				if (onlyUniqueHFURL) {
					List<HFURLBean> docIds = getDocumentID(hfTitle);
					if (docIds.size() > 1) {
						return standardURL;
					}
				}

				hfURL = standardURL.replaceFirst(docIdURLPrefix + "/" + documentId, getHfULRPrefix() + "/" + hfTitle);
				if (log.isDebugEnabled()) {
					log.debug("Founded title: " + hfTitle + ", for documentID: " + documentId);
					log.debug("Transformed URL: " + hfURL);
				}
			}
		} catch (Exception e) {
			log.error("Error occur while changing URL to Human friendly URL." + " Returning original URL", e);
			// something wrong - catch it and use original URL
		}
		return hfURL;
	}

	/**
	 * Get Document ID from URL
	 *
	 * @param url URL in standard CS format i.e. /docs/DOC-1234
	 * @return document ID i.e. DOC-1234
	 */
	protected String getDocumentId(String url) {
		log.trace("Test of question mark");
		final int questionMark = url.indexOf('?');
		if (questionMark != -1) {
			url = url.substring(0, questionMark);
		}
		log.trace("Test of ;jsessionid");
		final int jsessionid = url.indexOf(";jsessionid");
		if (jsessionid != -1) {
			url = url.substring(0, jsessionid);
		}
		log.trace("Test of .pdf");
		if (url.endsWith(".pdf")) {
			url = url.replace(".pdf", "");
		}

		final int docIDStart = url.indexOf(docIdURLPrefix + "/") + docIdURLPrefix.length() + 1;
		int docIDEnd = url.indexOf("/", docIDStart);
		if (docIDEnd == -1) {
			docIDEnd = url.length();
		}

		return url.substring(docIDStart, docIDEnd);
	}

	public List<HFURLBean> getDocumentID(String hfURLTitle) {
		final String hfURLTitleLowerCase = hfURLTitle.toLowerCase();
		List<HFURLBean> docIds = hfURLTitlesCache.get(hfURLTitleLowerCase);

		if (docIds == null) {
			try {
				docIds = hfURLDAO.getByHfURLTitle(hfURLTitle);
				hfURLTitlesCache.put(hfURLTitleLowerCase, docIds);

			} catch (EmptyResultDataAccessException e) {
				// nothing founded - should not occur
				docIds = new ArrayList<HFURLBean>();
			}
		}
		return docIds;
	}

	// DOCUMENT LISTENER METHODS

	@Override
	public void handle(DocumentEvent e) {
		switch (e.getType()) {
			case DELETED:
				documentDeleted(e);
				break;
			case ADDED:
				documentAdded(e);
				break;
			case VERSION_ADDED:
				documentModified(e);
				break;
			case VERSION_DELETING:
				if (log.isDebugEnabled()) {
					log.debug("versionDeleting: " + getDocumentID(e));
				}
				Document doc = getDocument(e);
				getHfURLTitle(doc.getDocumentID());
				updateIndex(doc.getDocumentID());
				break;
			case UNDELETED:
				documentUndeleted(e);
		}
	}

	private String getDocumentID(DocumentEvent event) {
		return "DOC-" + event.getDocID();
	}

	public void documentAdded(DocumentEvent event) {
		if (log.isDebugEnabled()) {
			log.debug("documentAdded: " + getDocumentID(event));
		}
		Document doc = getDocument(event);
		getHfURLTitle(doc.getDocumentID());
	}

	public void documentDeleted(DocumentEvent event) {
		String documentID = getDocumentID(event);
		if (log.isDebugEnabled()) {
			log.debug("documentDeleted: " + documentID);
		}
		// remove whole cache - there can be entries to same document.
		hfURLDAO.deleteHFURL(documentID);
		hfURLTitlesCache.clear();
		hfURLIDsCache.remove(documentID);
	}

	public void documentModified(DocumentEvent event) {
		Document doc = getDocument(event);
		if (log.isDebugEnabled()) {
			log.debug("documentModified: " + getDocumentID(event));
			log.debug("event params: " + event.getParams());
			log.debug("Document state: " + doc.getDocumentState());
		}

		final String documentID = getDocumentID(event);
		String actualHFUrlTitle = getHfURLTitle(getDocumentID(event));
		String newHFURLTitle = createHFURLTitle(doc.getSubject());

		// Title modify
		if (!newHFURLTitle.equals(actualHFUrlTitle)) {
			log.info("Title is modified - go to refresh HF URL Map for specified document");
			updateIndex(documentID);
		}
	}

	public void documentUndeleted(DocumentEvent event) {
		if (log.isDebugEnabled()) {
			log.debug("documentUndeleted: " + getDocumentID(event));
		}
		Document doc = getDocument(event);
		getHfURLTitle(doc.getDocumentID());
	}

	public void setHfURLDAO(HFURLDAO hfURLDAO) {
		this.hfURLDAO = hfURLDAO;
	}

	public void setHfULRPrefix(String hfULRPrefix) {
		this.hfULRPrefix = hfULRPrefix;
	}

	public String getHfULRPrefix() {
		return hfULRPrefix;
	}

	public void setDocIdURLPrefix(String docIdURLPrefix) {
		this.docIdURLPrefix = docIdURLPrefix;
	}

	@Override
	public String getDocIdURLPrefix() {
		return docIdURLPrefix;
	}

	public void setHfURLIDsCache(Cache<String, String> hfURLIDsCache) {
		this.hfURLIDsCache = hfURLIDsCache;
	}

	public void setHfURLTitlesCache(Cache<String, List<HFURLBean>> hfURLTitlesCache) {
		this.hfURLTitlesCache = hfURLTitlesCache;
	}

}
