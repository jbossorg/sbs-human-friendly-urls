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
package org.jboss.labs.clearspace.plugin.hfurl.struts.mapping;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jboss.labs.clearspace.plugin.hfurl.HFURLManager;
import org.jboss.labs.clearspace.plugin.hfurl.dao.HFURLBean;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.EmptyResultDataAccessException;

import static org.junit.Assert.assertEquals;

/**
 * Tests of HFURLMapping
 * 
 * @author <a href="mailto:lkrzyzan@redhat.com">Libor Krzyzanek</a>
 * 
 */
public class HFURLMappingTest {

  private HFURLMapping actionMapping;

  private final HFURLBean DOC1 = new HFURLBean("DOC-1234", "JDKs", true);
  private final HFURLBean DOC2 = new HFURLBean("DOC-2345", "doc2", true);
  private final HFURLBean DOC3 = new HFURLBean("DOC-1234", "doc3", false);

  @Before
  public void setupHFURLMapping() {
    actionMapping = new HFURLMapping();
    actionMapping.setHfURLManager(new HFURLManager() {

      public String getHFURL(String standardURL) {
        return null;
      }

      public String getHFURLUnique(String standardURL, boolean onlyUniqueHFURL) {
        return null;
      }

      public boolean isHFLinksEnabled() {
        return false;
      }

      public void setHFLinksEnabled(boolean enabled) {
      }

      public String getHfULRPrefix() {
        return "/wiki";
      }

      public String getHfURLTitle(String documentID) throws EmptyResultDataAccessException {
        if (documentID.equals(DOC1.getDocumentID())) {
          return DOC1.getHfTitle();
        }
        if (documentID.equals(DOC2.getDocumentID())) {
          return DOC2.getHfTitle();
        }
        if (documentID.equals(DOC3.getDocumentID())) {
          return DOC3.getHfTitle();
        }
        return null;
      }

      public long getIndexedURLsCount() {
        return 0;
      }

      public void updateIndex() {
      }

      public List<HFURLBean> getDocumentID(String hfURLTitle) {
        List<HFURLBean> docIds = new LinkedList<HFURLBean>();
        if (hfURLTitle.equals("nonExisting")) {
        } else if (hfURLTitle.equals("duplicate")) {
          docIds.add(DOC1);
          docIds.add(DOC2);
          docIds.add(DOC3);
        } else if (hfURLTitle.equals("oldtitle1")) {
          docIds.add(DOC3);
        } else {
          docIds.add(DOC1);
        }
        return docIds;
      }

      @Override
      public String getDocIdURLPrefix() {
        return "/docs";
      }

      @Override
      public String updateIndex(String documentID) {
        return null;
      }

    });
  }

  /**
   * Test method for
   * {@link org.jboss.labs.clearspace.plugin.hfurl.struts.mapping.HFURLMapping#convertHFURL2StandardURL(String, Map)}
   * .
   */
  @Test
  public void testConvertHFURL2StandardURL() {
    Map<String, Object> params = new HashMap<>();

    assertEquals("/docs/DOC-1234", actionMapping.convertHFURL2StandardURL("/wiki/JDKs", params));
    assertEquals("/docs/DOC-1234.pdf", actionMapping.convertHFURL2StandardURL("/wiki/JDKs.pdf", params));

    assertEquals("/docs/DOC-1234/collaborate", actionMapping.convertHFURL2StandardURL("/wiki/JDKs/collaborate", params));
    assertEquals("/docs/DOC-1234/edit", actionMapping.convertHFURL2StandardURL("/wiki/JDKs/edit", params));
    assertEquals("/docs/DOC-1234/upload", actionMapping.convertHFURL2StandardURL("/wiki/JDKs/upload", params));
    assertEquals("/docs/DOC-1234/diff", actionMapping.convertHFURL2StandardURL("/wiki/JDKs/diff", params));
    assertEquals("/docs/DOC-1234/restore", actionMapping.convertHFURL2StandardURL("/wiki/JDKs/restore", params));
    assertEquals("/docs/DOC-1234/deleteVersion",
        actionMapping.convertHFURL2StandardURL("/wiki/JDKs/deleteVersion", params));
    assertEquals("/docs/DOC-1234/authors", actionMapping.convertHFURL2StandardURL("/wiki/JDKs/authors", params));
    assertEquals("/docs/DOC-1234/version", actionMapping.convertHFURL2StandardURL("/wiki/JDKs/version", params));
    assertEquals("/docs/DOC-1234/version/5", actionMapping.convertHFURL2StandardURL("/wiki/JDKs/version/5", params));
    assertEquals("/docs/DOC-1234/diff/", actionMapping.convertHFURL2StandardURL("/wiki/JDKs/diff/", params));
    assertEquals("/docs/DOC-1234/delete", actionMapping.convertHFURL2StandardURL("/wiki/JDKs/delete", params));

    assertEquals(HFURLMapping.DOC_NOT_FOUND, actionMapping.convertHFURL2StandardURL("/wiki/nonExisting", params));

    Map<String, Object> redirectParams = new HashMap<>();
    assertEquals(HFURLMapping.OLD_WIKI_URL, actionMapping.convertHFURL2StandardURL("/wiki/oldtitle1", redirectParams));

    Map<String, Object> duplicateParams = new HashMap<>();
    assertEquals(HFURLMapping.DOC_MORE_THAN_ONE,
        actionMapping.convertHFURL2StandardURL("/wiki/duplicate", duplicateParams));
    assertEquals(DOC1.getDocumentID(), duplicateParams.get("docIds[0]"));
    assertEquals(DOC2.getDocumentID(), duplicateParams.get("docIds[1]"));

    duplicateParams = new HashMap<>();
    assertEquals(HFURLMapping.DOC_MORE_THAN_ONE,
        actionMapping.convertHFURL2StandardURL("/wiki/duplicate/edit", duplicateParams));
    assertEquals("edit", duplicateParams.get("urlSuffix"));

    duplicateParams = new HashMap<>();
    assertEquals(HFURLMapping.DOC_MORE_THAN_ONE,
        actionMapping.convertHFURL2StandardURL("/wiki/duplicate/version/1", duplicateParams));
    assertEquals("version/1", duplicateParams.get("urlSuffix"));
  }

}
