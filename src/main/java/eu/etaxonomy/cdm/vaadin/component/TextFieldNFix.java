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
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.TextField;

/**
 * TextField which has "" as null representation, fixed the ugly "null"s in the
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
 */
public class TextFieldNFix extends TextField {

    private static final long serialVersionUID = -7582619519894748364L;


    public TextFieldNFix(String format) {
        super(format);
        init();
    }

    public TextFieldNFix() {
        super();
        init();
    }

    public TextFieldNFix(Property dataSource) {
        super(dataSource);
        init();
    }

    public TextFieldNFix(String caption, Property dataSource) {
        super(caption, dataSource);
        init();
    }

    public TextFieldNFix(String caption, String value) {
        super(caption, value);
        init();
    }

    protected void init() {
        setNullSettingAllowed(true);
        addBlurListener(e -> {
            AbstractTextField c = ((AbstractTextField) e.getComponent());
            if (c.getValue() != null) {
                c.setValue(c.getValue().trim());
            }
        });
    }

    @Override
    public String getNullRepresentation() {
        return "";
    }

//    @Override
//    protected void setValue(String newFieldValue, boolean repaintIsNotNeeded,
//            boolean ignoreReadOnly) {
//        newFieldValue = trimValue(newFieldValue);
//        super.setValue(newFieldValue, repaintIsNotNeeded, ignoreReadOnly);
//    }
//
//    @Override
//    protected void setInternalValue(String newValue) {
//        newValue = trimValue(newValue);
//        super.setInternalValue(newValue);
//    }
//
//
//
//    /**
//     * @param newValue
//     * @return
//     */
//    protected String trimValue(String newValue) {
//        if(newValue != null){
//            newValue = newValue.trim();
//            if(newValue.isEmpty()){
//                newValue = null;
//            }
//        }
//        return newValue;
//    }



}
