/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view;

import eu.etaxonomy.cdm.vaadin.component.LoginDialog;
import eu.etaxonomy.vaadin.mvp.ApplicationView;

/**
 * @author a.kohlbecker
 * @since Apr 25, 2017
 */
public interface LoginView extends ApplicationView<LoginView,LoginPresenter>  {

    public LoginDialog getLoginDialog();

    public void clearMessage();

    public void showErrorMessage(String text);
}