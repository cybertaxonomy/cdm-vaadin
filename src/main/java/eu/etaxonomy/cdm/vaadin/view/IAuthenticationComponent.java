/**
 * Copyright (C) 2015 EDIT
 * European Distributed Institute of Taxonomy
 * http://www.e-taxonomy.eu
 *
 * The contents of this file are subject to the Mozilla Public License Version 1.1
 * See LICENSE.TXT at the top of this package for the full license terms.
 */
package eu.etaxonomy.cdm.vaadin.view;

import eu.etaxonomy.cdm.common.URI;

/**
 * @author cmathew
 * @since 28.04.2015
 */
public interface IAuthenticationComponent {

	public interface AuthenticationComponentListener {

        /**
         * @param uri
         * @param context
         * @param userName
         * @param password
         * @return
         */
        boolean login(URI uri, String context, String userName, String password);
    }

    public void addListener(AuthenticationComponentListener listener);

}