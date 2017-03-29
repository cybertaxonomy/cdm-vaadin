/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.mock;

public enum RegistrationStatus {

    PREPARATION,// A new record which is being edited by the Author
    CURATION, //A record ready for the curator to be validated.
    READY, //The record has passed the validation by the curator and is ready for publication.
    PUBLISHED, //The name or typification has finally been published.
    REJECTED //The registration has been rejected, the process is aborted and the record is preserved.

}