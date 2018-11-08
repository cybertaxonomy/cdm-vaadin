/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.event;

import java.util.Collection;
import java.util.Stack;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.ui.Component;

import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.ref.TypedEntityReference;
import eu.etaxonomy.cdm.vaadin.event.EditorActionContextFormat.TargetInfoType;
import eu.etaxonomy.cdm.vaadin.model.CdmEntityAdapterDTO;
import eu.etaxonomy.vaadin.mvp.AbstractPopupEditor;
import eu.etaxonomy.vaadin.util.PropertyIdPath;

/**
 * @author a.kohlbecker
 * @since Oct 29, 2018
 *
 */
public class EditorActionContextFormatter {

    private static final String NEW = "New";

    private static final String EDIT = "Edit";

    public String format(EditorActionContext cntxt, EditorActionContextFormat format) {

        String className = null;
        String targetInfo = null;
        String targetEntityStr = null;
        String createOrNew = null;

        Object parentEntity = cntxt.getParentEntity();

        if (parentEntity != null) {
            if (parentEntity instanceof TypedEntityReference) {
                className = ((TypedEntityReference) cntxt.getParentEntity()).getType().getSimpleName();
                createOrNew = EDIT;
            } else if (CdmEntityAdapterDTO.class.isAssignableFrom(parentEntity.getClass())) {
                CdmBase cdmEntity = ((CdmEntityAdapterDTO) parentEntity).cdmEntity();
                className = cdmEntity.getClass().getSimpleName();
                createOrNew = cdmEntity.isPersited() ? EDIT : NEW;
            } else if (CdmBase.class.isAssignableFrom(parentEntity.getClass())) {
                CdmBase cdmEntity = ((CdmBase) parentEntity);
                className = cdmEntity.getClass().getSimpleName();
                createOrNew = cdmEntity.isPersited() ? EDIT : NEW;
            } else {
                className = parentEntity.getClass().getSimpleName();
                // can not decide for createOrNew in this case
            }
        } else {
            className += "[NULL_CLASS]";
        }

        if ((format.doTargetInfo || format.doTargetEntity) && cntxt.getParentView() != null && AbstractPopupEditor.class.isAssignableFrom(cntxt.getParentView().getClass())) {
            // the top element is the cntxt istself!! we need to dig one step deeper to get the previous popup editor
            // TODO chaining the EditorActionContext would ease find the contexts of parent editors
            Stack<EditorActionContext> ctxtStack = ((AbstractPopupEditor)cntxt.getParentView()).getEditorActionContext();
            int parentPopupPos = ctxtStack.size() - 2;
            if(parentPopupPos > -1){
                EditorActionContext parentCtx = ctxtStack.get(parentPopupPos);
                if(format.targetInfoType.equals(TargetInfoType.PROPERTIES)){
                    PropertyIdPath propertyIdPath = parentCtx.getTargetPropertyIdPath();
                    if (propertyIdPath != null) {
                        targetInfo = propertyIdPath.toString();
                    }
                } else {
                    // TargetInfoType.FIELD_CAPTION
                    if(parentCtx.getTargetField() != null){
                        Component captionComponent = parentCtx.getTargetField();
                        while(captionComponent != null){
                            targetInfo = captionComponent.getCaption();
                            if(targetInfo != null){
                                break;
                            }
                            captionComponent = captionComponent.getParent();
                        }
                    }
                }
            }
        }
        if(format.doTargetEntity){
            targetEntityStr = formatTargetEntityString(cntxt.parentEntity, format);
        }

        // create output
        String outStr = "";

        if (format.doCreateOrNew && createOrNew != null) {
            if(format.tagName != null){
                outStr += "<" + format.tagName + " class=\"operation " + format.classAttributes + "\">" + createOrNew + "</" + format.tagName + ">";
            } else {
                outStr += createOrNew;
            }
        }

        if (format.doTargetInfo) {
            if(targetInfo == null && format.classNameForMissingTargetData && className != null){
                targetInfo = className;
            }
            if(targetInfo != null){
                if(!outStr.isEmpty()){
                    outStr += " ";
                    targetInfo = normalizeTargetInfo(targetInfo);
                }
                if(format.tagName != null){
                    outStr += "<" + format.tagName + " class=\"target " + format.classAttributes + "\">" + targetInfo + "</" + format.tagName + ">";
                } else {
                    outStr += targetInfo;
                }
            }
        }
        if(format.doTargetEntity && targetEntityStr != null){
            if(format.tagName != null){
                outStr += "<" + format.tagName + " class=\"target-entity" + format.classAttributes + "\">" + targetEntityStr + "</" + format.tagName + ">";
            } else {
                outStr += " (" + targetEntityStr + ")";
            }
        }
        return outStr;
    }

    public String format(Collection<EditorActionContext> cntxts, EditorActionContextFormat format) {
        String outStr = "";
        for(EditorActionContext ctx : cntxts){
            if(!outStr.isEmpty()){
                outStr += " > "; // FIXME allow configuring the separator
            }
            outStr += format(ctx, format) ;
        }
        return outStr;
    }

    /**
     * @param value
     * @param format
     * @return
     */
    private String formatTargetEntityString(Object value, EditorActionContextFormat format) {

        if(value == null){
            return "NULL";
        }
        String outStr;
        if(value instanceof CdmBase){
            outStr = ((CdmBase)value).instanceToString();
        } else if(value instanceof CdmEntityAdapterDTO) {
            outStr = value.getClass().getSimpleName() + ": ";
            outStr += ((CdmEntityAdapterDTO)value).cdmEntity().toString();
        } else {
            outStr = value.getClass().getSimpleName() + ": " + value.toString();
        }
        return outStr;
    }

    /**
     * @param targetInfo
     * @return
     */
    public String normalizeTargetInfo(String targetInfo) {
        targetInfo = StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(targetInfo), " ");
        targetInfo = targetInfo.toLowerCase();
        return targetInfo;
    }

}
