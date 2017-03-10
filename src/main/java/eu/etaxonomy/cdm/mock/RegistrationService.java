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
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import eu.etaxonomy.cdm.api.application.CdmRepository;
import eu.etaxonomy.cdm.model.name.TaxonNameBase;
import eu.etaxonomy.cdm.model.name.TypeDesignationBase;

/**
 * @author a.kohlbecker
 * @since Mar 10, 2017
 *
 */
@Component("registrationServiceMock")
public class RegistrationService {

    @Autowired
    @Qualifier("cdmRepository")
    private CdmRepository repo;

    private Map<UUID, Registration> registrationsByUUID = new HashMap<>();
    private Map<String, Registration> registrationsByRegID = new HashMap<>();

    public RegistrationService() {
    }

    @PostConstruct
    protected void init(){
        List<TaxonNameBase> names = repo.getNameService().list(TaxonNameBase.class, 20, 0, null, null);
        names.forEach(
                name -> {
                    Registration reg = new Registration();
                    reg.setName(name);
                    registrationsByUUID.put(reg.getUuid(), reg);
                    registrationsByRegID.put(reg.getSpecificIdentifier(), reg);
                }
               );
        List<TypeDesignationBase> tds = repo.getNameService().getAllTypeDesignations(20, 0);
        tds.forEach(
                type -> {
                    Registration reg = new Registration();
                    reg.addTypeDesignationBase(type);
                    registrationsByUUID.put(reg.getUuid(), reg);
                    registrationsByRegID.put(reg.getSpecificIdentifier(), reg);
                }
               );

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


    /**
     * @param registrationID
     * @return
     */
    public Registration loadByRegistrationID(Integer registrationID) {
        return registrationsByRegID.get(registrationID.toString());
    }


}
