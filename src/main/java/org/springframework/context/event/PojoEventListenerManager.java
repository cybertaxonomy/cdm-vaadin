/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package org.springframework.context.event;

/**
 * @author a.kohlbecker
 * @since May 29, 2017
 */
public interface PojoEventListenerManager {

    public void removeEventListeners(Object o);

    public void addEventListeners(Object o);
}