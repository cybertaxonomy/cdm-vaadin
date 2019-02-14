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
     * add information on the target class and field if possible
     */
    public boolean doTargetInfo;

    /**
     * add on the target entity like including titlecache and uuid
     */
    public boolean doTargetEntity;
    /**
     *
     */
    public boolean classNameForMissingTargetData;
    /**
     *
     */
    public boolean doCreateOrNew;

    /**
     * The name of the html tag to be used, may be <code>null</code> to format as plain text
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