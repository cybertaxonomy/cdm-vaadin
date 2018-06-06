/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.component;

import com.vaadin.data.Property;
import com.vaadin.ui.TextField;

/**
 * TextField which has "" as null represnetation, fixed the ugly "null"s in the
 * default  TextField implementation in vaadin 7. TODO: this might no longer be
 * required in vaadin 8 since it is supposed to have a mechanism to configure
 * the null representations.
 * <p>
 * Additional features:
 * <ul>
 *    <li>entered text is trimmed</li>
 * </ul>
 *
 *
 * @author a.kohlbecker
 * @since Dec 21, 2017
 *
 */
public class TextFieldNFix extends TextField {

    private static final long serialVersionUID = -7582619519894748364L;

    /**
     * @param format
     */
    public TextFieldNFix(String format) {
        super(format);
    }



    /**
     *
     */
    public TextFieldNFix() {
        super();
        setNullSettingAllowed(true);
    }



    /**
     * @param dataSource
     */
    public TextFieldNFix(Property dataSource) {
        super(dataSource);
    }



    /**
     * @param caption
     * @param dataSource
     */
    public TextFieldNFix(String caption, Property dataSource) {
        super(caption, dataSource);
    }



    /**
     * @param caption
     * @param value
     */
    public TextFieldNFix(String caption, String value) {
        super(caption, value);
        // TODO Auto-generated constructor stub
    }



    /**
     * {@inheritDoc}
     */
    @Override
    public String getNullRepresentation() {
        return "";
    }



    /**
     * {@inheritDoc}
     */
    @Override
    protected void setInternalValue(String newValue) {
        if(newValue != null){
            newValue = newValue.trim();
            if(newValue.isEmpty()){
                newValue = null;
            }
        }
        super.setInternalValue(newValue);
    }





}
