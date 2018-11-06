/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.event;

public class EditorActionContextFormat {

    public enum TargetInfoType {
        PROPERTIES, FIELD_CAPTION;
    }

    /**
     *
     */
    public boolean doClassName;
    /**
     *
     */
    public boolean doTargetInfo;
    /**
     *
     */
    public boolean classNameForMissingTargetData;
    /**
     *
     */
    public boolean doCreateOrNew;

    /**
     * The name of the html tag to be used
     */
    public String tagName = "span";

    /**
     * additional class attributes
     */
    public String classAttributes = "";

    public TargetInfoType targetInfoType = TargetInfoType.PROPERTIES;


    /**
     *
     */
    public EditorActionContextFormat(boolean doClassName, boolean doProperties, boolean classNameForMissingPropertyPath,
            boolean doCreateOrNew, TargetInfoType targetInfoType, String classAttributes) {
        this.doClassName = doClassName;
        this.doTargetInfo = doProperties;
        this.classNameForMissingTargetData = classNameForMissingPropertyPath;
        this.doCreateOrNew = doCreateOrNew;
        this.targetInfoType = targetInfoType;
        this.classAttributes = classAttributes;
    }
}