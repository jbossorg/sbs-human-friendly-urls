/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 */
package org.jboss.labs.clearspace.plugin.hfurl.struts;

import com.jivesoftware.community.Community;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.List;

/**
 * Value object of duplicate document
 *
 * @author <a href="mailto:lkrzyzan@redhat.com">Libor Krzyzanek</a>
 */
public class DuplicateDocument {

	private String documentID;

	private String subject;

	private List<Community> communities;

	/**
	 * Get Document ID
	 *
	 * @return
	 * @see com.jivesoftware.community.Document#getDocumentID()
	 */
	public String getDocumentID() {
		return documentID;
	}

	public void setDocumentID(String documentID) {
		this.documentID = documentID;
	}

	/**
	 * Get document subject
	 *
	 * @return
	 */
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setCommunities(List<Community> communities) {
		this.communities = communities;
	}

	public List<Community> getCommunities() {
		return communities;
	}

	public int hashCode() {
		return new HashCodeBuilder(1618983549, -1070315631).append(this.documentID)
				.toHashCode();
	}

	public boolean equals(Object object) {
		if (!(object instanceof DuplicateDocument)) {
			return false;
		}
		DuplicateDocument rhs = (DuplicateDocument) object;
		return new EqualsBuilder().append(this.documentID, rhs.documentID)
				.isEquals();
	}

	public String toString() {
		return new ToStringBuilder(this).append("docId", this.documentID).append(
				"subject", this.subject).toString();
	}

}
