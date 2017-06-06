/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.vaadin.component;

import org.vaadin.teemu.switchui.Switch;

/**
 * @author a.kohlbecker
 * @since May 11, 2017
 *
 */
public class SwitchButton extends Switch {

    private static final long serialVersionUID = 2557108593729214773L;

    private ValueChangeListener valueSetListener = null;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setInternalValue(Boolean newValue) {
        super.setInternalValue(newValue);
        if(valueSetListener != null){
            valueSetListener.valueChange(new ValueChangeEvent(this));
        }
    }

    public void setValueSetLister(ValueChangeListener valueSetListener){
        this.valueSetListener = valueSetListener;
    }

}