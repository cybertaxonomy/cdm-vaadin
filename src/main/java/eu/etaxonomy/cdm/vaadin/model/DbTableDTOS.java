package eu.etaxonomy.cdm.vaadin.model;

import java.util.ArrayList;
import java.util.List;

public class DbTableDTOS{

	private List<DbTableDTO> listDTO = new ArrayList<DbTableDTO>();
	
	public DbTableDTOS(){
	}
	
	public void add(DbTableDTO dto){
		listDTO.add(dto);
	}
	
	public List<DbTableDTO> getList(){
		return listDTO;
	}
}
