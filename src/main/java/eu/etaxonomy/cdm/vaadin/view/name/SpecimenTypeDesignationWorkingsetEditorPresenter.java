/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.name;

import java.util.Set;

import org.hibernate.Session;

import eu.etaxonomy.cdm.model.common.VersionableEntity;
import eu.etaxonomy.cdm.model.name.Registration;
import eu.etaxonomy.cdm.model.name.SpecimenTypeDesignation;
import eu.etaxonomy.cdm.model.name.TypeDesignationBase;
import eu.etaxonomy.cdm.model.occurrence.DerivationEvent;
import eu.etaxonomy.cdm.model.occurrence.DerivationEventType;
import eu.etaxonomy.cdm.model.occurrence.FieldUnit;
import eu.etaxonomy.cdm.model.occurrence.GatheringEvent;
import eu.etaxonomy.cdm.vaadin.model.TypedEntityReference;
import eu.etaxonomy.cdm.vaadin.model.registration.SpecimenTypeDesignationWorkingSetDTO;
import eu.etaxonomy.cdm.vaadin.view.registration.RegistrationDTO;
import eu.etaxonomy.vaadin.mvp.AbstractEditorPresenter;

/**
 * SpecimenTypeDesignationWorkingsetPopupEditorView implementation must override the showInEditor() method,
 * see {@link #prepareAsFieldGroupDataSource()} for details.
 *
 * @author a.kohlbecker
 * @since Jun 13, 2017
 *
 */
public class SpecimenTypeDesignationWorkingsetEditorPresenter
    extends AbstractEditorPresenter<SpecimenTypeDesignationWorkingSetDTO , SpecimenTypeDesignationWorkingsetPopupEditorView> {

    private static final long serialVersionUID = 4255636253714476918L;
    private Registration registration;




    /**
     * {@inheritDoc}
     *
     * see also {@link SpecimenTypeDesignationWorkingsetPopupEditor#showInEditor()}
     */
    @Override
    protected SpecimenTypeDesignationWorkingSetDTO prepareAsFieldGroupDataSource(SpecimenTypeDesignationWorkingSetDTO bean) {
        if(bean.getFieldUnit() == null){
            // in case the base unit of the working set is not a FieldUnit all contained TypeDesignations must be modified
            // so that they are based on an empty FieldUnit with an associated Gathering Event
            if(Registration.class.isAssignableFrom(bean.getOwner().getClass())){
                // FIXME open Dialog to warn user about adding an empty fieldUnit to the typeDesignations
                logger.info("Basing all typeDesignations on a new fieldUnit");
                Session session = getSession();
                Registration reg = getRepo().getRegistrationService().find(bean.getOwner().getId());
                RegistrationDTO regDTO = new RegistrationDTO(reg);

                FieldUnit fieldUnit = FieldUnit.NewInstance();
                GatheringEvent gatheringEvent = GatheringEvent.NewInstance();
                fieldUnit.setGatheringEvent(gatheringEvent);
                getRepo().getOccurrenceService().save(fieldUnit);

                VersionableEntity baseEntity = bean.getBaseEntity();
                Set<TypeDesignationBase> typeDesignations = regDTO.getTypeDesignationsInWorkingSet(
                        new TypedEntityReference(baseEntity.getClass(), baseEntity.getId(), baseEntity.toString())
                        );
                for(TypeDesignationBase td : typeDesignations){
                    DerivationEvent de = DerivationEvent.NewInstance();//
                    de.addOriginal(fieldUnit);
                    de.addDerivative(((SpecimenTypeDesignation)td).getTypeSpecimen());
                    de.setType(DerivationEventType.GATHERING_IN_SITU());
                }

                getRepo().getRegistrationService().saveOrUpdate(reg);
                session.flush();
                session.close();
            } else {
                throw new RuntimeException("Usupported owner type " + bean.getOwner().getClass() + ", needs to be implemented.");
            }
        }
        return super.prepareAsFieldGroupDataSource(bean);
    }



    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveBean(SpecimenTypeDesignationWorkingSetDTO bean) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void deleteBean(SpecimenTypeDesignationWorkingSetDTO bean) {
        // TODO Auto-generated method stub

    }


}
