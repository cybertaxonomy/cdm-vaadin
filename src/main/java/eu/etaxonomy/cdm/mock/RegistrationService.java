/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.mock;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;

import eu.etaxonomy.cdm.api.application.CdmRepository;
import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.name.Rank;
import eu.etaxonomy.cdm.model.name.TaxonNameBase;
import eu.etaxonomy.cdm.vaadin.presenter.phycobank.RegistrationDTO;

/**
 * @author a.kohlbecker
 * @since Mar 10, 2017
 *
 */
@Service("registrationServiceMock")
@Transactional
public class RegistrationService {

    @Autowired
    @Qualifier("cdmRepository")
    private CdmRepository repo;

    private Map<UUID, Registration> registrationsByUUID = new HashMap<>();
    private Map<String, Registration> registrationsByRegID = new HashMap<>();
    private Map<String, RegistrationDTO> registrationDTOsByRegID = new HashMap<>();

    private Collection<CdmBase> cdmEntities = new HashSet<>();

    public RegistrationService() {
    }


    int minTypeDesignationCount = 1;

    @PostConstruct
    protected void init(){
        TransactionStatus tx = repo.startTransaction(true);
        while(registrationsByUUID.size() < 20){
            List<TaxonNameBase> names = repo.getNameService().list(TaxonNameBase.class, 100, 0, null, null);
            for(TaxonNameBase name : names){
                if(name.getRank() != null && name.getRank().isLower(Rank.SUBFAMILY())){
                    if(name.getTypeDesignations().size() > minTypeDesignationCount - 1) {

                        // name
                        Registration nameReg = new Registration();
                        nameReg.setName(name);
                        cdmEntities.add(name);
                        put(nameReg, new RegistrationDTO(nameReg, null));

                        // typedesignation
                        Registration typedesignationReg = new Registration();
                        typedesignationReg.addTypeDesignations(name.getTypeDesignations());
                        cdmEntities.addAll(name.getTypeDesignations());
                        put(typedesignationReg,  new RegistrationDTO(typedesignationReg, name));
                    }
                }
            }
        }
        repo.commitTransaction(tx);
    }

    /**
     * @param reg
     */
    private void put(Registration reg, RegistrationDTO dto) {
        registrationsByUUID.put(reg.getUuid(), reg);
        registrationsByRegID.put(reg.getSpecificIdentifier(), reg);
        registrationDTOsByRegID.put(reg.getSpecificIdentifier(), dto);
    }

    private void mergeBack(){
        cdmEntities.forEach(e -> repo.getNameService().getSession().merge(e));
    }

    /**
     * {@inheritDoc}
     */
    public Registration load(UUID uuid) {
        return registrationsByUUID.get(uuid);
    }

    public Collection<Registration> list(){
        return registrationsByUUID.values();
    }

    public Collection<RegistrationDTO> listDTOs() {
        return registrationDTOsByRegID.values();
    }

    /**
     * @param registrationID
     * @return
     */
    public Registration loadByRegistrationID(Integer registrationID) {
        return registrationsByRegID.get(registrationID.toString());
    }


}
