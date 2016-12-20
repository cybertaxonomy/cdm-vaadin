package eu.etaxonomy.cdm.vaadin.model;

import java.io.Serializable;

public class StatusDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	private String status;

	public StatusDTO(String status){
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
