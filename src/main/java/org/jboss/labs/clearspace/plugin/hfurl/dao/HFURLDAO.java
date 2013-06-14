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
package org.jboss.labs.clearspace.plugin.hfurl.dao;

import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;

import com.jivesoftware.base.database.dao.DAOException;

/**
 * Human friendly DAO interface
 * 
 * @author <a href="mailto:lkrzyzan@redhat.com">Libor Krzyzanek</a>
 */
public interface HFURLDAO {

  /**
   * Update index of HF URLs. Go through articles and add missing HF URLs
   */
  public void updateIndex();

  /**
   * Update index of HF URLs for particular Document ID.
   * 
   * @param documentID
   * @return actual HF URL
   */
  public String updateIndex(String documentID);

  /**
   * Get count of indexed URLs
   * 
   * @return
   */
  public long getIndexedURLsCount();

  /**
   * Get by HF URL Title. Check is case insensitive
   * 
   * @param hfURLTitle
   * @return set of HF URL Bean
   * @throws EmptyResultDataAccessException
   *           if document HF URL not found
   */
  public List<HFURLBean> getByHfURLTitle(String hfURLTitle) throws EmptyResultDataAccessException;

  /**
   * Get by documentId (mapping for actual version of document)
   * 
   * @param documentId
   * @return HF URL Bean
   * @throws EmptyResultDataAccessException
   *           if document HF URL not found
   */
  public HFURLBean getByDocumentId(String documentId) throws EmptyResultDataAccessException;

  /**
   * Create new entry
   * 
   * @param bean
   *          bean to delete
   * @return bean with assigned id
   * @throws DAOException
   */
  public HFURLBean createHFURL(HFURLBean bean) throws DAOException;

  /**
   * Update HF URL entry
   * 
   * @param bean
   *          bean to delete
   * @return updated entry
   * @throws DAOException
   */
  public HFURLBean updateHFURL(HFURLBean bean) throws DAOException;

  /**
   * Delete entry
   * 
   * @param id
   *          id of bean
   * @throws DAOException
   */
  public void deleteHFURL(Long id) throws DAOException;

  /**
   * Delete entry
   * 
   * @param documentId
   *          document ID
   * @throws DAOException
   */
  public void deleteHFURL(String documentId) throws DAOException;

}
