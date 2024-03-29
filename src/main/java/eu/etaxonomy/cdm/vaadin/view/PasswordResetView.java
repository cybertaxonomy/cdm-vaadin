/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view;

import com.vaadin.ui.PasswordField;

import eu.etaxonomy.vaadin.mvp.ApplicationView;


public interface PasswordResetView extends ApplicationView<PasswordResetView,PasswordResetPresenter>  {

    public void showSuccessMessage(String text);

    public void showErrorMessage(String text);

    public void setUserName(String userName);

    public PasswordField getPassword1Field();
}