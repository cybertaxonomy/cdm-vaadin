/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.presenter.phycobank;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.joda.time.DateTime;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;

import eu.etaxonomy.cdm.model.name.TaxonNameBase;
import eu.etaxonomy.cdm.vaadin.view.phycobank.ListView;
import eu.etaxonomy.vaadin.mvp.AbstractPresenter;

/**
 * @author a.kohlbecker
 * @since Mar 3, 2017
 *
 */
@SpringComponent
@ViewScope
public class ListPresenter extends AbstractPresenter<ListView> {

    @Override
    public void onViewEnter() {
        super.onViewEnter();
        getView().populateTable(listRegistrations());
    }

    /**
     * @return
     */
    private Collection<RegistrationDTO> listRegistrations() {
        List<TaxonNameBase> names = getRepo().getNameService().list(TaxonNameBase.class, 500, 0, null, null);
        Collection<RegistrationDTO> dtos = new ArrayList<>(names.size());
        names.forEach(name -> { dtos.add(new RegistrationDTO(name)); });
        return dtos;
    }

    private static int idAutoincrement = 100000;

    public class RegistrationDTO{

        private String summary;
        private UUID registeredEntityUuid;

        private RegistrationType registrationType;
        private RegistrationStatus status;
        private String registrationId;
        private String internalRegId;
        private DateTime registrationDate = null;
        private DateTime created = null;



        /**
         * @param name
         */
        public RegistrationDTO(TaxonNameBase name) {
            summary = name.getTitleCache();
            registeredEntityUuid = name.getUuid();

            registrationType = RegistrationType.name;
            status = RegistrationStatus.values()[(int) (Math.random() * RegistrationStatus.values().length)];
            internalRegId = Integer.toString(ListPresenter.idAutoincrement++);
            registrationId = "http://pyhcobank.org/" + internalRegId;
            created = DateTime.now();

        }

        /**
         * @return the summary
         */
        public String getSummary() {
            return summary;
        }


        /**
         * @param summary the summary to set
         */
        public void setSummary(String summary) {
            this.summary = summary;
        }


        /**
         * @return the registrationType
         */
        public RegistrationType getRegistrationType() {
            return registrationType;
        }


        /**
         * @param registrationType the registrationType to set
         */
        public void setRegistrationType(RegistrationType registrationType) {
            this.registrationType = registrationType;
        }


        /**
         * @return the status
         */
        public RegistrationStatus getStatus() {
            return status;
        }


        /**
         * @param status the status to set
         */
        public void setStatus(RegistrationStatus status) {
            this.status = status;
        }


        /**
         * @return the registrationId
         */
        public String getRegistrationId() {
            return registrationId;
        }


        /**
         * @param registrationId the registrationId to set
         */
        public void setRegistrationId(String registrationId) {
            this.registrationId = registrationId;
        }


        /**
         * @return the internalRegId
         */
        public String getInternalRegId() {
            return internalRegId;
        }


        /**
         * @param internalRegId the internalRegId to set
         */
        public void setInternalRegId(String internalRegId) {
            this.internalRegId = internalRegId;
        }


        /**
         * @return the registeredEntityUuid
         */
        public UUID getRegisteredEntityUuid() {
            return registeredEntityUuid;
        }


        /**
         * @param registeredEntityUuid the registeredEntityUuid to set
         */
        public void setRegisteredEntityUuid(UUID registeredEntityUuid) {
            this.registeredEntityUuid = registeredEntityUuid;
        }

        /**
         * @return the registrationDate
         */
        public DateTime getRegistrationDate() {
            return registrationDate;
        }

        /**
         * @return the created
         */
        public DateTime getCreated() {
            return created;
        }

    }

    public enum RegistrationStatus {

        preparation,// A new record which is being edited by the Author
        curation, //A record ready for the curator to be validated.
        ready, //The record has passed the validation by the curator and is ready for publication.
        published, //The name or typification has finally been published.
        rejected //The registration has been rejected, the process is aborted and the record is preserved.
    }

}
