/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 */
package org.jboss.labs.clearspace.plugin.hfurl.struts.components;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.StrutsException;
import org.apache.struts2.components.URL;
import org.jboss.labs.clearspace.plugin.hfurl.HFURLManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * @author <a href="mailto:lkrzyzan@redhat.com">Libor Krzyzanek</a>
 */
public class HFURLComponent extends URL {

	private static final Log log = LogFactory.getLog(HFURLComponent.class);

	private HFURLManager hfURLManager;

	public HFURLComponent(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
		super(stack, req, res);
	}

	@Override
	public boolean end(Writer writer, String body) {
		if (!hfURLManager.isHFLinksEnabled()) {
			return super.end(writer, body);
		}

		// write result of parent to String writer and then do modification
		Writer tempWriter = new StringWriter();
		boolean result = super.end(tempWriter, body);

		String url = tempWriter.toString();
		if (log.isTraceEnabled()) {
			log.trace("original URL: " + url);
		}

		url = hfURLManager.getHFURLUnique(url, true);

		try {
			writer.write(url);
		} catch (IOException e) {
			throw new StrutsException("IOError: " + e.getMessage(), e);
		}
		return result;
	}

	public void setHfURLManager(HFURLManager hfURLManager) {
		this.hfURLManager = hfURLManager;
	}

}
