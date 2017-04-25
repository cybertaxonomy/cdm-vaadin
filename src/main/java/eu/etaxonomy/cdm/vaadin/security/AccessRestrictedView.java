/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

/**
 * @author a.kohlbecker
 * @since Apr 25, 2017
 *
 */
public interface AccessRestrictedView {

    /**
     * @return
     */
    public boolean allowAnonymousAccess();

    /**
     * The collections of  {@link GrantedAuthority} objects returned by this method are
     * evaluated by the {@link AnnotationBasedAccessControlBean} to determine if the
     * current authentication is having sufficient grants to access the view.
     * <p>
     * The collections are alternative sets of GrantedAuthorities to check.
     * The GrantedAuthorities of each of the inner collections must instead all be satisfied.
     *
     * @return
     */
    public Collection<Collection<GrantedAuthority>> allowedGrantedAuthorities();

}
