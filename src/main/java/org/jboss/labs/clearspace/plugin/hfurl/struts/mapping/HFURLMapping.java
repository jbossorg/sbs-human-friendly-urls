/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 */
package org.jboss.labs.clearspace.plugin.hfurl.struts.mapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.jboss.labs.clearspace.plugin.hfurl.HFURLManager;
import org.jboss.labs.clearspace.plugin.hfurl.dao.HFURLBean;

import com.jivesoftware.community.JiveGlobals;
import com.jivesoftware.community.web.struts.mapping.DocURLMapping;

/**
 * Process document Human friendly URIs into standard CS doc url.<br>
 * It extends {@link DocURLMapping} because it only construct standard DOC-XXXX
 * URL and call standard mapping implementation.
 *
 * @author <a href="mailto:lkrzyzan@redhat.com">Libor Krzyzanek</a>
 */
public class HFURLMapping extends DocURLMapping {

	private static final Logger log = LogManager.getLogger(HFURLMapping.class);

	public static final String DOC_NOT_FOUND = "NOT_FOUND";

	public static final String DOC_MORE_THAN_ONE = "MORE_THAN_ONE";

	public static final String OLD_WIKI_URL = "OLD_WIKI_URL";

	private HFURLManager hfURLManager;

	@SuppressWarnings("unchecked")
	public void process(String uri, ActionMapping mapping) {
		Map<String, Object> params = mapping.getParams();
		if (null == params) {
			params = new HashMap<>();
		}
		uri = convertHFURL2StandardURL(uri, params);
		mapping.setParams(params);
		if (DOC_NOT_FOUND.equals(uri)) {
			mapping.setName("wikiNotFound");
			mapping.setNamespace("/hfurl");
		} else if (DOC_MORE_THAN_ONE.equals(uri)) {
			mapping.setName("wikiNavigation");
			mapping.setNamespace("/hfurl");
		} else if (OLD_WIKI_URL.equals(uri)) {
			mapping.setName("oldWikiUrl");
			mapping.setNamespace("/hfurl");
		} else {
			super.process(uri, mapping);
		}
	}

	/**
	 * Converts HF URL to standard CS URL
	 *
	 * @param uri
	 * @param params action params
	 * @return /docs/DOC-XXXX or value of {@link #DOC_NOT_FOUND} or
	 *         {@link #DOC_MORE_THAN_ONE} or {@link #OLD_WIKI_URL}
	 */
	protected String convertHFURL2StandardURL(String uri, Map<String, Object> params) {
		String[] uriElements = uri.split("/");
		if (uriElements.length > 2) {
			// in this case try to switch from HF URL to standard CS URL
			// document (documentID) is always the second element
			String hfURLTitle;
			boolean isPdf = false;
			if (uriElements[2].endsWith(".pdf")) {
				hfURLTitle = uriElements[2].replace(".pdf", "");
				isPdf = true;
			} else {
				hfURLTitle = uriElements[2];
			}
			List<HFURLBean> documentIDs = hfURLManager.getDocumentID(hfURLTitle);

			if (documentIDs.isEmpty()) {
				log.debug("Document not found");
				return DOC_NOT_FOUND;
			} else {
				if (documentIDs.size() == 1) {
					HFURLBean hfurlBean = documentIDs.get(0);
					if (!documentIDs.get(0).getActualTitle()) {
						final String correctUrl = JiveGlobals.getDefaultBaseURL() + hfURLManager.getHfULRPrefix() + "/"
								+ hfURLManager.getHfURLTitle(hfurlBean.getDocumentID());
						params.put("urlToRedirect", correctUrl);
						return OLD_WIKI_URL;
					}
					final int hfURLTitleEndIndex = uri.indexOf(hfURLTitle) + hfURLTitle.length();
					StringBuilder sb = new StringBuilder("/docs/");
					sb.append(hfurlBean.getDocumentID());

					if (hfURLTitleEndIndex < uri.length()) {
						sb.append(uri.substring(hfURLTitleEndIndex, uri.length()));
					}
					return sb.toString();
				} else {
					if (log.isDebugEnabled()) {
						log.debug("More than one page is founded for title: " + hfURLTitle);
						log.debug("docIds: " + documentIDs);
					}
					for (int i = 0; i < documentIDs.size(); i++) {
						params.put("docIds[" + i + "]", documentIDs.get(i).getDocumentID());
					}
					StringBuilder suffix = new StringBuilder();
					for (int i = 3; i < uriElements.length; i++) {
						suffix.append(uriElements[i]);
						if (i + 1 < uriElements.length) {
							suffix.append('/');
						}
					}
					params.put("urlSuffix", suffix.toString());
					params.put("pdfRequired", Boolean.toString(isPdf));
					return DOC_MORE_THAN_ONE;
				}
			}
		}
		return uri;
	}

	public void setHfURLManager(HFURLManager hfURLManager) {
		this.hfURLManager = hfURLManager;
	}

}
