/*
 * JBoss.org http://jboss.org/
 *
 * Copyright (c) 2009  Red Hat Middleware, LLC. All rights reserved.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT A WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License, v.2.1 along with this distribution; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * Red Hat Author(s): Libor Krzyzanek
 */
package org.jboss.labs.clearspace.plugin.hfurl.struts;

import org.jboss.labs.clearspace.plugin.hfurl.HFURLManager;

import com.jivesoftware.community.action.JiveActionSupport;

/**
 * Action redirects to HF URL
 * 
 * @author <a href="mailto:lkrzyzan@redhat.com">Libor Krzyzanek</a>
 * 
 */
public class RedirectToHFURLAction extends JiveActionSupport {

  private static final long serialVersionUID = 6117400340423042335L;

  /**
   * URL parameter
   */
  private String url = null;

  /**
   * URL to redirect
   */
  private String urlToRedirect = null;

  /**
   * Parameters from URLs
   */
  private String params = null;

  private HFURLManager hfURLManager;

  public static final String URL_NOT_DEFINED_RESULT = "urlNotDefined";

  public static final String BAD_REQUEST_RESULT = "badRequest";

  public String execute() {
    if (url == null || url.trim().equals("")) {
      urlToRedirect = "/";
      return URL_NOT_DEFINED_RESULT;
    }
    if (!url.startsWith("/docs/")) {
      // prevent Open Redirect vulnerability
      return BAD_REQUEST_RESULT;
    }

    if (params != null && params.length() > 0) {
      // add params to URL
      url = url + "?" + params;
    }

    urlToRedirect = hfURLManager.getHFURLUnique(url, true);

    if (url.equals(urlToRedirect)) {
      urlToRedirect = addParameter(urlToRedirect, "uniqueTitle=false");
      return "input";
    } else {
      return "success";
    }
  }

  protected String addParameter(String url, String parameter) {
    StringBuilder sb = new StringBuilder(url);
    if (url.indexOf('?') == -1) {
      sb.append('?');
    } else {
      sb.append('&');
    }
    sb.append(parameter);
    return sb.toString();
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public void setHfURLManager(HFURLManager hfURLManager) {
    this.hfURLManager = hfURLManager;
  }

  public void setParams(String params) {
    this.params = params;
  }

  public String getParams() {
    return params;
  }

  public String getUrlToRedirect() {
    return urlToRedirect;
  }

}
