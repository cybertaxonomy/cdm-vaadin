/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.component.registration;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

import eu.etaxonomy.cdm.vaadin.view.registration.RegistrationDTO;
import eu.etaxonomy.vaadin.component.CompositeStyledComponent;

/**
 * @author a.kohlbecker
 * @since May 19, 2017
 *
 */
public class RegistrationItemEditButtonGroup extends CompositeStyledComponent {


    private static final long serialVersionUID = -5059163772392864050L;

    public static final String STYLE_NAMES = "edit-button-group " + ValoTheme.LAYOUT_COMPONENT_GROUP;

    private IdButton nameButton = null;

    private List<IdButton> typeDesignationButtons = new ArrayList<>();

    private List<Label> labels = new ArrayList<>();


    public RegistrationItemEditButtonGroup(RegistrationDTO regDto){

        setWidth(100, Unit.PERCENTAGE);

        if(regDto.getName() != null){
            nameButton = new IdButton(regDto.getName().getId(), new Button(regDto.getName().getLabel()));
            addComponent(nameButton.getButton());
        } else {
            // no name in the registration! we only show the typified name as label
            addComponent(new Label(regDto.getTypifiedName().getLabel()));
        }
        regDto.getTypeDesignations().keySet().iterator().forEachRemaining(key -> {
            Label label = new Label(key   + ":");
            label.setWidthUndefined();
            addComponent(label);
            labels.add(label);

            regDto.getTypeDesignations().get(key).forEach(value -> {
            Button tdButton = new Button(value.getLabel());
            addComponent(tdButton);
            typeDesignationButtons.add(new IdButton(value.getId(), tdButton));
            }) ;
        });
        Button addTypeDesignationButton = new Button(FontAwesome.PLUS);
        addComponent(addTypeDesignationButton);

        iterator().forEachRemaining(c -> addStyledComponent(c));
        addDefaultStyles();

    }

    public IdButton getNameButton() {
        return nameButton;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addDefaultStyles() {
        addStyleName(STYLE_NAMES);
    }

    public class IdButton {
        private Integer id;
        private Button button;

        public IdButton(Integer id, Button button){
            this.id = id;
            this.button = button;
        }

        /**
         * @return the id
         */
        public Integer getId() {
            return id;
        }

        /**
         * @return the button
         */
        public Button getButton() {
            return button;
        }


    }

}