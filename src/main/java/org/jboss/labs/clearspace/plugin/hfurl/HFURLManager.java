/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 */
package org.jboss.labs.clearspace.plugin.hfurl;

import org.jboss.labs.clearspace.plugin.hfurl.dao.HFURLBean;

import java.util.List;

/**
 * Manager for handling with Human friendly URLs.
 *
 * @author @author <a href="mailto:lkrzyzan@redhat.com">Libor Krzyzanek</a>
 */
public interface HFURLManager {

	/**
	 * Get status of Human friendly links
	 *
	 * @return true if HF links are enabled, otherwise false
	 */
	public boolean isHFLinksEnabled();

	/**
	 * Set if HF links are enabled or disabled
	 *
	 * @param enabled true if HF links are enabled, false if disabled
	 */
	public void setHFLinksEnabled(boolean enabled);

	/**
	 * Get count of indexed URLs
	 *
	 * @return
	 */
	public long getIndexedURLsCount();

	/**
	 * Update index of all HF URLs. Go through articles and add missing HF URLs
	 */
	public void updateIndex();

	/**
	 * Update index of particular documentID
	 *
	 * @param documentID
	 * @return actual HF Title
	 */
	public String updateIndex(String documentID);

	/**
	 * Get document ID.
	 *
	 * @param hfURLTitle HF URL title.
	 * @return list of of HFURLBean or empty list if no document is founded
	 * @see #createHFURLTitle(String)
	 * @see com.jivesoftware.community.impl.dao.DocumentBean#getDocumentID()
	 */
	public List<HFURLBean> getDocumentID(String hfURLTitle);

	/**
	 * Shortcut for getHFURLUnique(standardURL, false)
	 *
	 * @see #getHFURLUnique(String, boolean)
	 */
	public String getHFURL(String standardURL);

	/**
	 * Get human friendly URL from standard SBS URL. If human friendly links are
	 * note enabled then standardURL is returned.
	 * <p/>
	 * For urls that contain "/delete" or "/restore" then standard URL is
	 * returned.
	 *
	 * @param standardURL     i.e. /docs/DOC-1234
	 * @param onlyUniqueHFURL if true then HF URL is returned only when there is no duplicate HF
	 *                        URLs
	 * @return i.e. /wiki/humanFriendlyURL
	 */
	public String getHFURLUnique(String standardURL, boolean onlyUniqueHFURL);

	/**
	 * Get HF URL Title for document ID
	 *
	 * @param documentID i.e. DOC-1234
	 * @return HF URL Title or null.
	 */
	public String getHfURLTitle(String documentID);

	/**
	 * Prefix for HF URLs
	 *
	 * @return prefix for HF URL i.e. /wiki
	 */
	public String getHfULRPrefix();

	/**
	 * Prefix for standard URLS
	 *
	 * @return /docs
	 */
	public String getDocIdURLPrefix();

}
