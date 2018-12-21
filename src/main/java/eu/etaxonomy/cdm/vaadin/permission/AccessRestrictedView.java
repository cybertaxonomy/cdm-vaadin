/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.permission;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

/**
 * @author a.kohlbecker
 * @since Apr 25, 2017
 *
 */
public interface AccessRestrictedView extends ReleasableResourcesView {

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

    public String getAccessDeniedMessage();

    /**
     * The <code>accessDeniedMessage</code> can be set my the presenter, e.g. in response
     * to an Exception or during the processing of the bean to be loaded into the view.
     * <p>
     * In case an <code>accessDeniedMessage</code> is present access to the view is considered
     * to be denied.
     *
     * @param accessDeniedMessage
     */
    public void setAccessDeniedMessage(String accessDeniedMessage);

    /**
     * Evaluated by the {@link AccessRestrictedViewControlBean}
     *
     * @return true if the {@link #getAccessDeniedMessage()} is not <code>null</code>.
     */
    default public boolean isAccessDenied(){
        return getAccessDeniedMessage() != null;
    }

}
