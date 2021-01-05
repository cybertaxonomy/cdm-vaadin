/**
* Copyright (C) 2015 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.presenter;

import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.security.authentication.BadCredentialsException;

import com.vaadin.server.VaadinSession;

import eu.etaxonomy.cdm.common.URI;
import eu.etaxonomy.cdm.vaadin.CdmVaadinBaseTest;
import eu.etaxonomy.cdm.vaadin.util.CdmVaadinAuthentication;
import eu.etaxonomy.cdm.vaadin.view.AuthenticationPresenter;

/**
 * @author cmathew
 * @since 28 Apr 2015
 */
public class AuthenticationPresenterTest extends CdmVaadinBaseTest {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(AuthenticationPresenterTest.class);

    private static AuthenticationPresenter ap;

    @BeforeClass
    public static void init() {
        ap = new AuthenticationPresenter();
    }

    @Test
    public void testLogin() throws URISyntaxException {
        URI uri = new URI("http://localhost:8080/cdm-vaadin/app/authtest");
        String context = "/cdm-vaadin";
        boolean isAuthenticated = false;
        try {
            isAuthenticated = ap.login(uri, context, "admin", "000");
            Assert.fail("BadCredentialsException should be thrown here");
        } catch(BadCredentialsException e){

        }

        isAuthenticated = ap.login(uri, context, "admin", "00000");
        Assert.assertTrue(isAuthenticated);

        CdmVaadinAuthentication authentication = (CdmVaadinAuthentication) VaadinSession.getCurrent().getAttribute(CdmVaadinAuthentication.KEY);
        Assert.assertTrue(authentication.isAuthenticated(uri, context));

        URI anotherUri = new URI("http://localhost:8081/cdm-edit/app/authtest");
        Assert.assertFalse(authentication.isAuthenticated(anotherUri, context));

        String anotherContext = "/cdm-edit";
        Assert.assertFalse(authentication.isAuthenticated(uri, anotherContext));

        isAuthenticated = ap.login(anotherUri, anotherContext, "admin", "00000");
        Assert.assertTrue(isAuthenticated);

        Assert.assertTrue(authentication.isAuthenticated(anotherUri, anotherContext));

    }

}
