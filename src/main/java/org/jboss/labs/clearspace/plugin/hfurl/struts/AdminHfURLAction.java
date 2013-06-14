/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 */
package org.jboss.labs.clearspace.plugin.hfurl.struts;

import com.jivesoftware.community.action.JiveActionSupport;
import com.opensymphony.xwork2.Preparable;
import org.jboss.labs.clearspace.plugin.hfurl.HFURLManager;

/**
 * Action for manipulating with index of HF URLs
 *
 * @author <a href="mailto:lkrzyzan@redhat.com">Libor Krzyzanek</a>
 */
public class AdminHfURLAction extends JiveActionSupport implements Preparable {

	private static final long serialVersionUID = -8335873830065250279L;

	private long indexedURLs = 0;

	private HFURLManager hfURLManager;

	private boolean enabled;

	public void prepare() {
		indexedURLs = hfURLManager.getIndexedURLsCount();
		enabled = hfURLManager.isHFLinksEnabled();
	}

	@Override
	public String execute() {
		hfURLManager.updateIndex();

		addActionMessage(getText("plugin.hfurl.admin.hf-urls.reindex.text.sucess"));

		prepare();
		return SUCCESS;
	}

	public String enable() {
		changeEnabled(true);
		addActionMessage(getText("plugin.hfurl.admin.hf-urls.enabled.text.enabled.sucess"));
		return SUCCESS;
	}

	public String disable() {
		changeEnabled(false);
		addActionMessage(getText("plugin.hfurl.admin.hf-urls.enabled.text.disabled.sucess"));
		return SUCCESS;
	}

	protected void changeEnabled(boolean status) {
		hfURLManager.setHFLinksEnabled(status);
		prepare();
	}

	public void setHfURLManager(HFURLManager hfURLManager) {
		this.hfURLManager = hfURLManager;
	}

	public void setIndexedURLs(long indexedURLs) {
		this.indexedURLs = indexedURLs;
	}

	public long getIndexedURLs() {
		return indexedURLs;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return enabled;
	}

}
