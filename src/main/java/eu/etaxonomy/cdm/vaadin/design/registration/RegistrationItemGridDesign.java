package eu.etaxonomy.cdm.vaadin.design.registration;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.declarative.Design;

/** 
 * !! DO NOT EDIT THIS FILE !!
 * 
 * This class is generated by Vaadin Designer and will be overwritten.
 * 
 * Please make a subclass with logic and additional interfaces as needed,
 * e.g class LoginView extends LoginDesign implements View { }
 */
@DesignRoot
@AutoGenerated
@SuppressWarnings("serial")
public class RegistrationItemGridDesign extends GridLayout {
    protected Label typeStateLabel;
    protected Link identifierLink;
    protected Button blockedByButton;
    protected Button messageButton;
    protected Button openButton;
    protected Label citationLabel;

    public RegistrationItemGridDesign() {
        Design.read(this);
    }
}
