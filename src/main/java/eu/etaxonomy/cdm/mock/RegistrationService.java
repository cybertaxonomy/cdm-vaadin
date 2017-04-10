/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.mock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;

import eu.etaxonomy.cdm.api.application.CdmRepository;
import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.name.Rank;
import eu.etaxonomy.cdm.model.name.TaxonNameBase;
import eu.etaxonomy.cdm.vaadin.model.registration.RegistrationWorkingSet;
import eu.etaxonomy.cdm.vaadin.view.registration.RegistrationDTO;
import eu.etaxonomy.cdm.vaadin.view.registration.RegistrationValidationException;

/**
 * @author a.kohlbecker
 * @since Mar 10, 2017
 *
 */
@Service("registrationServiceMock")
@Transactional(readOnly=true)
public class RegistrationService {

    private static final int SIZE = 50; // FIXME test performance with 50 !!!!!

    @Autowired
    @Qualifier("cdmRepository")
    private CdmRepository repo;

    private Map<UUID, Registration> registrationsByUUID = new HashMap<>();
    private Map<Integer, Registration> registrationsByRegID = new HashMap<>();
    private Map<Integer, RegistrationDTO> registrationDTOsById = new HashMap<>();
    private Map<String, RegistrationDTO> registrationDTOsByIdentifier = new HashMap<>();
    private Map<Integer, List<RegistrationDTO>> registrationDTOsByCitationId = new HashMap<>();

    private Collection<CdmBase> cdmEntities = new HashSet<>();

    public RegistrationService() {
    }


    int minTypeDesignationCount = 1;

    protected void init(){
        if(registrationsByUUID.size() == 0){
            TransactionStatus tx = repo.startTransaction(true);
            int pageIndex = 0;
            while(registrationsByUUID.size() < SIZE){
                List<TaxonNameBase> names = repo.getNameService().list(TaxonNameBase.class, 100, pageIndex++, null, null);
                if(names.isEmpty()){
                    break;
                }
                for(TaxonNameBase name : names){
                    if(name != null && name.getRank() != null && name.getRank().isLower(Rank.SUBFAMILY())){
                        if(name.getTypeDesignations().size() > minTypeDesignationCount - 1) {

                            // name
                            Registration nameReg = new Registration();
                            nameReg.setName(name);
                            cdmEntities.add(name);
                            put(new RegistrationDTO(nameReg));

                            // typedesignation
                            Registration typedesignationReg = new Registration();
                            typedesignationReg.addTypeDesignations(name.getTypeDesignations());
                            cdmEntities.addAll(name.getTypeDesignations());
                            put(new RegistrationDTO(typedesignationReg));
                        }
                    }
                }
            }
            repo.commitTransaction(tx);
        }
    }

    /**
     * @param reg
     */
    private void put(RegistrationDTO dto) {
        Registration reg = dto.registration();
        registrationsByUUID.put(dto.getUuid(), reg);
        registrationsByRegID.put(reg.getId(), reg);

        registrationDTOsById.put(reg.getId(), dto);
        registrationDTOsByIdentifier.put(reg.getIdentifier(), dto);

        if(! registrationDTOsByCitationId.containsKey(dto.getCitationID())){
            registrationDTOsByCitationId.put(dto.getCitationID(), new ArrayList<RegistrationDTO>());
        }
        registrationDTOsByCitationId.get(dto.getCitationID()).add(dto);
    }

    private void mergeBack(){
        cdmEntities.forEach(e -> repo.getNameService().getSession().merge(e));
    }

    /**
     * {@inheritDoc}
     */
    public Registration load(UUID uuid) {
        init();
        return registrationsByUUID.get(uuid);
    }


    public Collection<Registration> list(){
        init();
        return registrationsByUUID.values();
    }

    public Collection<RegistrationDTO> listDTOs() {
        init();
        return registrationDTOsById.values();
    }

    public Map<Integer, List<RegistrationDTO>> listDTOsByWorkingSet() {
        init();
        return registrationDTOsByCitationId;
    }

    /**
     * @param  id the CDM Entity id
     * @return
     */
    public Registration loadByRegistrationID(Integer id) {
        init();
        return registrationsByRegID.get(id);
    }

    /**
     * @param identifier the Registration Identifier String
     * @return
     */
    public RegistrationDTO loadDtoByIdentifier(String identifier) {
        init();
        return registrationDTOsById.get(identifier);
    }

    /**
     * @param id the CDM Entity id
     * @return
     */
    public RegistrationDTO loadDtoById(Integer id) {
        init();
        return registrationDTOsById.get(id);
    }

    /**
     * @param  id the CDM Entity id
     * @return
     * @throws RegistrationValidationException
     */
    public RegistrationWorkingSet loadWorkingSetByRegistrationID(Integer id) throws RegistrationValidationException {
        init();
        RegistrationDTO dto = registrationDTOsById.get(id);

        return new RegistrationWorkingSet(registrationDTOsByCitationId.get(dto.getCitationID()));
    }


}
