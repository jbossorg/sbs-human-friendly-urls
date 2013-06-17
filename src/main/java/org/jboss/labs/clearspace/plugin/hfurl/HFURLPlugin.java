/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 */
package org.jboss.labs.clearspace.plugin.hfurl;

import com.jivesoftware.base.event.v2.EventListenerRegistry;
import com.jivesoftware.base.plugin.Plugin;
import com.jivesoftware.community.JiveGlobals;
import com.jivesoftware.community.web.struts.JiveRestfulActionMapper;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jboss.labs.clearspace.plugin.hfurl.struts.mapping.HFURLMapping;

/**
 * Plugin lifecycle definition<br>
 *
 * @author <a href="mailto:lkrzyzan@redhat.com">Libor Krzyzanek</a>
 */
public class HFURLPlugin implements Plugin {

	/**
	 * Logger
	 */
	protected static final Logger log = LogManager.getLogger(HFURLPlugin.class);

	/**
	 * HF URL Manager
	 */
	private DbHFURLManager dbHFURLManager;

	private HFURLMapping hfURLMapping;

	private EventListenerRegistry eventListenerRegistry;

	private JiveRestfulActionMapper actionMapper;

	/**
	 * Plugin name
	 */
	private final String PLUGIN_NAME = "Human friendly URLs";

	public static final String HFURL_ENABLED_KEY = "hfurl.links.enabled";

	@Override
	public void initPlugin() {
		log.debug("Init " + PLUGIN_NAME);

		// cannot use urlmapping in plugin.xml - mapping class is not managed by
		// spring
		actionMapper.addURLMapping(dbHFURLManager.getHfULRPrefix(), hfURLMapping);

		// Not needed - registered by AutoEventListenerRegistrar
		//eventListenerRegistry.register(dbHFURLManager);

		String enabledKey = JiveGlobals.getJiveProperty(HFURL_ENABLED_KEY);
		if (enabledKey == null) {
			JiveGlobals.setJiveProperty(HFURL_ENABLED_KEY, "false");
		}

		log.debug("Initialize completed of plugin " + PLUGIN_NAME);
	}

	@Override
	public void destroy() {
		eventListenerRegistry.unregister(dbHFURLManager);

		// Not needed - unregistered by AutoEventListenerRegistrar
		// actionMapper.removeURLMapping(dbHFURLManager.getHfULRPrefix());

		log.debug(PLUGIN_NAME + " destroyed");
	}

	public void setDbHFURLManager(DbHFURLManager dbHFURLManager) {
		this.dbHFURLManager = dbHFURLManager;
	}

	public void setHfURLMapping(HFURLMapping hfURLMapping) {
		this.hfURLMapping = hfURLMapping;
	}

	public void setEventListenerRegistry(
			EventListenerRegistry eventListenerRegistry) {
		this.eventListenerRegistry = eventListenerRegistry;
	}

	public void setActionMapper(JiveRestfulActionMapper actionMapper) {
		this.actionMapper = actionMapper;
	}

}
