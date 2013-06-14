/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 */
package org.jboss.labs.clearspace.plugin.hfurl.dao;

/**
 * HF URL entry
 *
 * @author <a href="mailto:lkrzyzan@redhat.com">Libor Krzyzanek</a>
 */
public class HFURLBean {

	/**
	 * Internal ID
	 */
	private long id = -1;

	/**
	 * HF Title
	 */
	private String hfTitle;

	/**
	 * Document ID
	 */
	private String documentID;

	/**
	 * Flag if title is actual or archive
	 */
	private Boolean actualTitle;

	public HFURLBean() {
	}

	public HFURLBean(String documentID, String hfTitle, Boolean actualTitle) {
		this.documentID = documentID;
		this.hfTitle = hfTitle;
		this.actualTitle = actualTitle;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof HFURLBean)) {
			return false;
		}
		HFURLBean other = (HFURLBean) obj;
		if (actualTitle == null) {
			if (other.actualTitle != null) {
				return false;
			}
		} else if (!actualTitle.equals(other.actualTitle)) {
			return false;
		}
		if (documentID == null) {
			if (other.documentID != null) {
				return false;
			}
		} else if (!documentID.equals(other.documentID)) {
			return false;
		}
		if (hfTitle == null) {
			if (other.hfTitle != null) {
				return false;
			}
		} else if (!hfTitle.equals(other.hfTitle)) {
			return false;
		}
		if (id != other.id) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((actualTitle == null) ? 0 : actualTitle.hashCode());
		result = prime * result + ((documentID == null) ? 0 : documentID.hashCode());
		result = prime * result + ((hfTitle == null) ? 0 : hfTitle.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public String toString() {
		return "HFURLBean [id=" + id + ", hfTitle=" + hfTitle + ", documentID=" + documentID + ", actualTitle="
				+ actualTitle + "]";
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getHfTitle() {
		return hfTitle;
	}

	public void setHfTitle(String hfTitle) {
		this.hfTitle = hfTitle;
	}

	public String getDocumentID() {
		return documentID;
	}

	public void setDocumentID(String documentID) {
		this.documentID = documentID;
	}

	public void setActualTitle(Boolean actualTitle) {
		this.actualTitle = actualTitle;
	}

	public Boolean getActualTitle() {
		return actualTitle;
	}

}
