/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.persistence.hibernate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.event.spi.EventSource;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;

import eu.etaxonomy.cdm.hibernate.HibernateProxyHelper;
import eu.etaxonomy.cdm.model.agent.AgentBase;
import eu.etaxonomy.cdm.model.agent.Person;
import eu.etaxonomy.cdm.model.agent.Team;
import eu.etaxonomy.cdm.model.agent.TeamOrPersonBase;
import eu.etaxonomy.cdm.model.name.Registration;
import eu.etaxonomy.cdm.model.name.RegistrationStatus;
import eu.etaxonomy.cdm.model.name.SpecimenTypeDesignation;
import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.model.name.TypeDesignationBase;
import eu.etaxonomy.cdm.model.occurrence.DerivedUnit;
import eu.etaxonomy.cdm.model.occurrence.FieldUnit;
import eu.etaxonomy.cdm.model.occurrence.SpecimenOrObservationBase;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.persistence.hibernate.permission.CRUD;
import eu.etaxonomy.cdm.persistence.hibernate.permission.CdmAuthority;

/**
 * @author a.kohlbecker
 * @since Dec 18, 2017
 *
 */
public class GrantedAuthorityRevokingRegistrationUpdateLister implements PostUpdateEventListener {

    private static final long serialVersionUID = -3542204523291766866L;

    /**
     *
     * Registrations having these states must no longer be midifiable by users having only per entity permissions on the
     * Registration subgraph
     */
    private static final EnumSet<RegistrationStatus> MODIFICATION_STOP_STATES = EnumSet.of(
            RegistrationStatus.PUBLISHED,
            RegistrationStatus.READY,
            RegistrationStatus.REJECTED
            );

    private static final EnumSet<CRUD> UPDATE_DELETE = EnumSet.of(CRUD.UPDATE, CRUD.DELETE);

    private static final EnumSet<CRUD> UPDATE = EnumSet.of(CRUD.UPDATE);

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        if( event.getEntity() instanceof Registration){
            Registration reg = (Registration)event.getEntity();
            if(reg.getStatus() != null && MODIFICATION_STOP_STATES.contains(reg.getStatus())){
                Set<CdmAuthority> deleteCandidates = collectDeleteCandidates(reg);
                deleteAuthorities(event.getSession(), deleteCandidates);
            }
        }
    }

    /**
     * Walks the entity graph of the Registration instance and collects all authorities which
     * could have been granted to users. Code parts in which this could have happened can be
     * found by searching for usage of the methods {@link eu.etaxonomy.cdm.vaadin.security.UserHelper#createAuthorityForCurrentUser(eu.etaxonomy.cdm.model.common.CdmBase, EnumSet, String)
     * UserHelper.createAuthorityForCurrentUser(eu.etaxonomy.cdm.model.common.CdmBase, EnumSet, String)} and {@link eu.etaxonomy.cdm.vaadin.security.UserHelper#createAuthorityForCurrentUser(Class, Integer, EnumSet, String)
     * UserHelper.createAuthorityForCurrentUser(Class, Integer, EnumSet, String)}
     * <p>
     * At the time of implementing this function these places are:
     * <ul>
     *  <li><code>RegistrationEditorPresenter.guaranteePerEntityCRUDPermissions(...)</code></li>
     *  <li><code>RegistrationWorkingsetPresenter.createNewRegistrationForName(Integer taxonNameId)</code></li>
     *  <li><code>TaxonNameEditorPresenter.guaranteePerEntityCRUDPermissions(...)</code></li>
     *  <li><code>ReferenceEditorPresenter.guaranteePerEntityCRUDPermissions(...)</code></li>
     *  <li><code>PersonField.commit()</code></li>
     *  <li><code>TeamOrPersonField.commit()</code></li>
     *  <li><code>SpecimenTypeDesignationWorkingsetEditorPresenter.saveBean(SpecimenTypeDesignationWorkingSetDTO dto)</code></li>
     * </ul>
     *
     * @param reg the Registration
     * @return the set of all possible CdmAuthorities that could have been granted to
     * individual users.
     */
    private Set<CdmAuthority> collectDeleteCandidates(Registration reg){
        Set<CdmAuthority> deleteCandidates = new HashSet<CdmAuthority>();
        // add authority for Registration
        deleteCandidates.add(new CdmAuthority(reg,  RegistrationStatus.PREPARATION.name(), UPDATE));
        if(reg.getName() != null){
            addDeleteCandidates(deleteCandidates, reg.getName());
        }
        for(TypeDesignationBase td : reg.getTypeDesignations()){
            addDeleteCandidates(deleteCandidates, td);
        }

        return deleteCandidates;

    }

    /**
     * @param deleteCandidates
     * @param name
     */
    private void addDeleteCandidates(Set<CdmAuthority> deleteCandidates, TaxonName name) {
        if(name == null){
            return;
        }
        name = HibernateProxyHelper.deproxy(name);
        deleteCandidates.add(new CdmAuthority(name, UPDATE_DELETE));
        addDeleteCandidates(deleteCandidates, (Reference)name.getNomenclaturalReference());
        addDeleteCandidates(deleteCandidates, name.getCombinationAuthorship());
        addDeleteCandidates(deleteCandidates, name.getExCombinationAuthorship());
        addDeleteCandidates(deleteCandidates, name.getBasionymAuthorship());
        addDeleteCandidates(deleteCandidates, name.getExBasionymAuthorship());
    }


    /**
     * @param deleteCandidates
     * @param td
     */
    private void addDeleteCandidates(Set<CdmAuthority> deleteCandidates, TypeDesignationBase td) {
        if(td == null){
            return;
        }
        td = HibernateProxyHelper.deproxy(td);
        deleteCandidates.add(new CdmAuthority(td, UPDATE_DELETE));
        addDeleteCandidates(deleteCandidates, td.getCitation());
        if(td instanceof SpecimenTypeDesignation){
            SpecimenTypeDesignation std = (SpecimenTypeDesignation)td;
            addDeleteCandidates(deleteCandidates, std.getTypeSpecimen());
        }
    }

    /**
     * @param deleteCandidates
     * @param typeSpecimen
     */
    private void addDeleteCandidates(Set<CdmAuthority> deleteCandidates, DerivedUnit deriveUnit) {
        if(deriveUnit == null){
            return;
        }

        deriveUnit = HibernateProxyHelper.deproxy(deriveUnit);
        if(deriveUnit.getCollection() != null){
            deleteCandidates.add(new CdmAuthority(deriveUnit.getCollection(), UPDATE_DELETE));
        }
        for(SpecimenOrObservationBase sob : deriveUnit.getOriginals()){
            if(sob == null){
                continue;
            }
            deleteCandidates.add(new CdmAuthority(sob, UPDATE_DELETE));
            if(sob instanceof FieldUnit){
                addDeleteCandidates(deleteCandidates, (FieldUnit)sob);
            } else {
                addDeleteCandidates(deleteCandidates, (DerivedUnit)sob);
            }
        }
    }

    private void addDeleteCandidates(Set<CdmAuthority> deleteCandidates, FieldUnit fieldUnit) {
        if(fieldUnit == null){
            return;
        }
        fieldUnit = HibernateProxyHelper.deproxy(fieldUnit);
        if(fieldUnit.getGatheringEvent() != null){
            addDeleteCandidates(deleteCandidates, fieldUnit.getGatheringEvent().getActor());
        }
    }

    /**
     * @param deleteCandidates
     * @param nomenclaturalReference
     */
    private void addDeleteCandidates(Set<CdmAuthority> deleteCandidates, Reference reference) {
        if(reference == null){
            return;
        }
        reference = HibernateProxyHelper.deproxy(reference);
        deleteCandidates.add(new CdmAuthority(reference, UPDATE_DELETE));
        addDeleteCandidates(deleteCandidates, reference.getAuthorship());
        addDeleteCandidates(deleteCandidates, reference.getInReference());
    }

    private void addDeleteCandidates(Set<CdmAuthority> deleteCandidates, AgentBase<?> agent) {
        if(agent == null){
            return;
        }
        agent = HibernateProxyHelper.deproxy(agent);
        deleteCandidates.add(new CdmAuthority(agent, UPDATE_DELETE));
        if(agent instanceof TeamOrPersonBase){
            if(agent instanceof Team){
                List<Person> members = ((Team)agent).getTeamMembers();
                if(members != null){
                    for(Person p : members){
                        if(p != null){
                            deleteCandidates.add(new CdmAuthority(p, UPDATE_DELETE));
                        }
                    }
                }
            }
        }

    }




    /**
     * @param deleteCandidates
     */
    private void deleteAuthorities(EventSource session, Set<CdmAuthority> deleteCandidates) {

        Collection<String> authorityStrings = new ArrayList<String>(deleteCandidates.size());
        deleteCandidates.forEach( dc -> authorityStrings.add(dc.toString()));

        String hql = "delete from GrantedAuthorityImpl as ga where ga.authority in (:authorities)";
        Query query = session.createQuery(hql);
        query.setParameterList("authorities", authorityStrings);
        query.setFlushMode(FlushMode.MANUAL); // workaround for  HHH-11822 (https://hibernate.atlassian.net/browse/HHH-11822)
        query.executeUpdate();

    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        return false;
    }

}