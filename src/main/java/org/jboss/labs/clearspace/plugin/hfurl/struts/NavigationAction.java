/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 */
package org.jboss.labs.clearspace.plugin.hfurl.struts;

import com.jivesoftware.base.UnauthorizedException;
import com.jivesoftware.community.*;
import com.jivesoftware.community.action.JiveActionSupport;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:lkrzyzan@redhat.com">Libor Krzyzanek</a>
 */
public class NavigationAction extends JiveActionSupport {

	private static final long serialVersionUID = 1L;

	private static final Logger log = LogManager.getLogger(NavigationAction.class);

	private List<String> docIds;

	private String urlSuffix;

	private String pdfRequired;

	private String decorator;

	private List<DuplicateDocument> documents = new ArrayList<DuplicateDocument>();

	protected DocumentManager documentManager;

	@Override
	public String execute() {
		for (String docId : docIds) {
			try {
				Document doc = documentManager.getDocument(docId);
				if (log.isTraceEnabled()) {
					log.trace("Adding doc in navigation: " + docId + ", container type: " + doc.getContainerType());
				}
				DuplicateDocument dupDoc = new DuplicateDocument();
				dupDoc.setDocumentID(doc.getDocumentID());
				dupDoc.setSubject(doc.getSubject());

				if (doc.getContainerType() == JiveConstants.COMMUNITY) {
					dupDoc.setCommunities(getCommunities(doc));
				} else {
					List<JiveContainer> containers = new ArrayList<JiveContainer>(2);
					containers.add(communityManager.getRootCommunity());
					containers.add(doc.getJiveContainer());
					dupDoc.setCommunities(containers);
				}

				documents.add(dupDoc);
			} catch (DocumentObjectNotFoundException e) {
				log.error("Document not found for duplicated HF URL.", e);
				// TODO Probably we can remove record from HF URL Map
			} catch (CommunityNotFoundException e) {
				log.warn("Community not found for doc", e);
			}
			// TODO Catch and handle UnauthorizedException
		}

		if (log.isTraceEnabled()) {
			log.trace("Duplicate Documents: " + documents);
		}

		return SUCCESS;
	}

	protected List<JiveContainer> getCommunities(Document doc)
			throws CommunityNotFoundException, UnauthorizedException {
		if (log.isTraceEnabled()) {
			log.trace("Getting Community: " + doc.getContainerID());
		}
		Community community = communityManager.getCommunity(doc.getContainerID());

		List<JiveContainer> communities = new ArrayList<JiveContainer>();

		if (community != null
				&& community.getID() != communityManager.getRootCommunity().getID()) {
			Community currentCommunity = community;
			communities.add(0, currentCommunity);
			for (int i = communityManager.getCommunityDepth(community); i > 0; i--) {
				currentCommunity = currentCommunity.getParentContainer();
				communities.add(0, currentCommunity);
			}
		}
		return communities;
	}

	public List<String> getDocIds() {
		return docIds;
	}

	public void setDocIds(List<String> docIds) {
		this.docIds = docIds;
	}

	public List<DuplicateDocument> getDocuments() {
		return documents;
	}

	public void setDocuments(List<DuplicateDocument> documents) {
		this.documents = documents;
	}

	public void setDocumentManager(DocumentManager documentManager) {
		this.documentManager = documentManager;
	}

	public String getUrlSuffix() {
		return urlSuffix;
	}

	public void setUrlSuffix(String urlSuffix) {
		this.urlSuffix = urlSuffix;
	}

	public void setPdfRequired(String pdfRequired) {
		this.pdfRequired = pdfRequired;
	}

	public String getPdfRequired() {
		return pdfRequired;
	}

	public void setDecorator(String decorator) {
		this.decorator = decorator;
	}

	public String getDecorator() {
		return decorator;
	}
}
