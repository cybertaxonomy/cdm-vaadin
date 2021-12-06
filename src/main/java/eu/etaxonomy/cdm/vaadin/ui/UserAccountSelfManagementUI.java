/**
* Copyright (C) 2021 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.ui;

import java.io.IOException;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Viewport;
import com.vaadin.annotations.Widgetset;
import com.vaadin.navigator.Navigator.SingleComponentContainerViewDisplay;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.RequestHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringUI;

import eu.etaxonomy.cdm.vaadin.view.PasswordResetViewBean;

/**
 * @author a.kohlbecker
 * @since Nov 11, 2021
 */
@Theme("edit-valo")
@Title("User Account Self Management")
@SpringUI(path=UserAccountSelfManagementUI.NAME)
@Viewport("width=device-width, initial-scale=1")
@Widgetset("eu.etaxonomy.cdm.vaadin.AppWidgetSet")
public class UserAccountSelfManagementUI extends AbstractUI {

    private static final long serialVersionUID = 8553850288038649061L;

    public static final String NAME = "account";

    private ViewDisplay viewDisplay = null;

    @Override
    protected ViewDisplay getViewDisplay() {
        if(viewDisplay == null) {
            viewDisplay = new SingleComponentContainerViewDisplay(this);
        }
        return viewDisplay;
    }

    @Override
    protected String getInitialViewName() {
        return PasswordResetViewBean.NAME;
    }

    @Override
    protected void initAdditionalContent() {
        // no additional content
    }

    @Override
    protected void init(VaadinRequest request) {
        super.init(request);
        VaadinSession.getCurrent().addRequestHandler(new RequestHandler() {

            @Override
            public boolean handleRequest(VaadinSession session, VaadinRequest request, VaadinResponse response)
                    throws IOException {
                // protect from Cross-domain Referer leakage in this UI
                // see https://portswigger.net/kb/issues/00500400_cross-domain-referer-leakage
                // and https://cheatsheetseries.owasp.org/cheatsheets/Forgot_Password_Cheat_Sheet.html
                response.setHeader("Referrer-Policy", "no-referrer");
                return false;
            }
        });
    }



}
