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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

import eu.etaxonomy.cdm.model.occurrence.SpecimenOrObservationBase;
import eu.etaxonomy.cdm.remote.dto.tdwg.voc.TaxonName;
import eu.etaxonomy.cdm.vaadin.model.TypedEntityReference;
import eu.etaxonomy.cdm.vaadin.util.converter.TypeDesignationConverter.TypeDesignationWorkingSet;
import eu.etaxonomy.cdm.vaadin.view.registration.RegistrationDTO;
import eu.etaxonomy.vaadin.component.CompositeStyledComponent;

/**
 * @author a.kohlbecker
 * @since May 19, 2017
 *
 */
public class RegistrationItemEditButtonGroup extends CompositeStyledComponent {


    /**
     *
     */
    private static final String DEFAULT_BUTTON_STYLES = ""; // ValoTheme.BUTTON_LINK;

    private static final long serialVersionUID = -5059163772392864050L;

    public static final String STYLE_NAMES = "edit-button-group  " + ValoTheme.LAYOUT_COMPONENT_GROUP;

    private IdButton<TaxonName> nameIdButton = null;

    private List<IdSetButton<TypeDesignationWorkingSet>> typeDesignationButtons = new ArrayList<>();

    private List<Label> labels = new ArrayList<>();

    private Button addTypeDesignationButton;


    public RegistrationItemEditButtonGroup(RegistrationDTO regDto){

        setWidth(100, Unit.PERCENTAGE);

        if(regDto.getName() != null){
            Button nameButton = new Button("Name:");
            nameIdButton = new IdButton<TaxonName>(TaxonName.class, regDto.getName().getId(), nameButton);
            Label nameLabel = new Label(regDto.getName().getLabel());
            nameLabel.setWidthUndefined();
            addComponents(nameIdButton.getButton(), nameLabel);
        } else {
            // no name in the registration! we only show the typified name as label
            addComponent(new Label(regDto.getTypifiedName().getLabel()));
        }
        if(regDto.getOrderdTypeDesignationWorkingSets() != null){
            for(TypedEntityReference baseEntityRef : regDto.getOrderdTypeDesignationWorkingSets().keySet()) {
                TypeDesignationWorkingSet typeDesignationWorkingSet = regDto.getOrderdTypeDesignationWorkingSets().get(baseEntityRef);
                String buttonLabel = SpecimenOrObservationBase.class.isAssignableFrom(baseEntityRef.getType()) ? "Type": "NameType";
                Button tdButton = new Button(buttonLabel + ":");
                addComponent(tdButton);
                Set<Integer> idSet = new HashSet<>();
                typeDesignationWorkingSet.getTypeDesignations().forEach(td -> idSet.add(td.getId()));
                typeDesignationButtons.add(new IdSetButton<TypeDesignationWorkingSet>(TypeDesignationWorkingSet.class, idSet, tdButton));
                String labelText = typeDesignationWorkingSet.getRepresentation();
                labelText = labelText.replaceAll("^[^:]+:", ""); // remove Type:, NameType: from the beginning
                Label label = new Label(labelText);

                label.setWidthUndefined();
                addComponent(label);
                labels.add(label);
            }
        }
        addTypeDesignationButton = new Button(FontAwesome.PLUS);
        addComponent(addTypeDesignationButton);


        iterator().forEachRemaining(c -> addStyledComponent(c));
        addDefaultStyles();

    }

    public IdButton<TaxonName> getNameButton() {
        return nameIdButton;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addDefaultStyles() {
        addStyleName(STYLE_NAMES);
        nameIdButton.getButton().addStyleName(DEFAULT_BUTTON_STYLES);
        typeDesignationButtons.forEach(idb -> idb.getButton().addStyleName(DEFAULT_BUTTON_STYLES));
        addTypeDesignationButton.addStyleName(DEFAULT_BUTTON_STYLES);
    }

    public class IdButton<T> {
        private Integer id;
        private Class<T> type;
        private Button button;

        public IdButton(Class<T> type, Integer id, Button button){
            this.type = type;
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

        /**
         * @return the type
         */
        public Class<T> getType() {
            return type;
        }

    }

    public class IdSetButton<T> {
        private Set<Integer> ids;
        private Class<T> type;
        private Button button;

        public IdSetButton(Class<T> type, Set<Integer> ids, Button button){
            this.type = type;
            this.ids = ids;
            this.button = button;
        }

        /**
         * @return the id
         */
        public Set<Integer> getIds() {
            return ids;
        }

        /**
         * @return the button
         */
        public Button getButton() {
            return button;
        }

        /**
         * @return the type
         */
        public Class<T> getType() {
            return type;
        }

    }

}
