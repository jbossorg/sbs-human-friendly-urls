/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 */
package org.jboss.labs.clearspace.plugin.hfurl.struts;

import com.jivesoftware.community.action.JiveActionSupport;

/**
 * @author Libor Krzyzanek (lkrzyzan)
 */
public class RedirectToUrl extends JiveActionSupport {

	private static final long serialVersionUID = -4100763937742680024L;

	private String urlToRedirect;

	public void setUrlToRedirect(String urlToRedirect) {
		this.urlToRedirect = urlToRedirect;
	}

	public String getUrlToRedirect() {
		return urlToRedirect;
	}

}
