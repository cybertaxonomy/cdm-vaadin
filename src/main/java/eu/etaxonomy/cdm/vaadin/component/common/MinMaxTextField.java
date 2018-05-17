/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.component.common;

import org.apache.commons.lang.StringUtils;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import eu.etaxonomy.cdm.vaadin.component.TextFieldNFix;

/**
 * @author a.kohlbecker
 * @since Jun 22, 2017
 *
 */
public class MinMaxTextField extends HorizontalLayout {

    /**
     *
     */
    private static final String PRIMARY_STYLE = "v-min-max-text-field";

    private static final long serialVersionUID = -6690659776664579698L;

    TextFieldNFix minField, textField;
    MaxTextField maxField;

    Converter<String, ?> numberConverter;

    String unitOfMeasure;

    public MinMaxTextField(String caption, String unitOfMeasure, Converter<String, ?> numberConverter){

        this.unitOfMeasure = unitOfMeasure;
        this.numberConverter = numberConverter;

        setCaption(caption);
        setPrimaryStyleName(PRIMARY_STYLE);

        initFields(unitOfMeasure);

    }

    public MinMaxTextField(String caption, String unitOfMeasure){
        this(caption, unitOfMeasure, null);
    }

    /**
     * @param unitOfMeasure
     */
    protected void initFields(String unitOfMeasure) {
        textField = new TextFieldNFix("free text");
        maxField = new MaxTextField(String.format("max (%s)", unitOfMeasure));
        minField = new TextFieldNFix(String.format("min (%s)", unitOfMeasure)) {

            private static final long serialVersionUID = -536012841624056585L;

            /**
             * {@inheritDoc}
             */
            @Override
            protected void setInternalValue(String newValue) {
                super.setInternalValue(newValue);
                updateMaxFieldEnablement();
            }

        };

        minField.setWidth("100px");
        maxField.setWidth("100px");
        textField.setWidth("100%");

        addComponents(minField, maxField, textField);

        setExpandRatio(textField, 1);

        minField.addValueChangeListener(e -> updateMaxFieldEnablement());
        maxField.setEnabled(false);
    }

    public void updateMaxFieldEnablement(){
        if(maxField != null && minField != null){
            boolean enabled = !StringUtils.isEmpty(minField.getValue());
            maxField.setSuperEnabled(enabled);
        }
    }

    /**
     * @return the minField
     */
    public TextField getMinField() {
        return minField;
    }

    /**
     * @return the maxField
     */
    public TextField getMaxField() {
        return maxField;
    }

    /**
     * @return the textField
     */
    public TextField getTextField() {
        return textField;
    }

    /**
     * @return the unitOfMeasure
     */
    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void addSubComponentsStyleName(String style) {
        minField.addStyleName(style);
        maxField.addStyleName(style);
        textField.addStyleName(style);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnabled(boolean enabled) {
        // TODO Auto-generated method stub
        super.setEnabled(enabled);
    }

    class MaxTextField extends TextFieldNFix {

        private static final long serialVersionUID = -536012841624056585L;



        /**
         * @param caption
         */
        public MaxTextField(String caption) {
            super(caption);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void setInternalValue(String newValue) {
            super.setInternalValue(newValue);
            updateMaxFieldEnablement();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setEnabled(boolean enabled) {
            updateMaxFieldEnablement();
        }

        public void setSuperEnabled(boolean enabled) {
            super.setEnabled(enabled);
        }

    }

}
