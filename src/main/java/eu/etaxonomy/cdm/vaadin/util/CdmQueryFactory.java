// $Id$
/**
* Copyright (C) 2015 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.util;

import java.sql.SQLException;
import java.util.List;

import com.vaadin.data.util.sqlcontainer.query.FreeformQuery;
import com.vaadin.data.util.sqlcontainer.query.QueryDelegate;

import eu.etaxonomy.cdm.vaadin.statement.CdmStatementDelegate;

/**
 * @author cmathew
 * @date 1 Apr 2015
 *
 */
public class CdmQueryFactory {

    public static QueryDelegate generateTaxonBaseQuery(String id,
            String uuid_id,
            String name_id,
            String pb_id,
            String unp_id,
            String rank_id,
            String has_syn_id) throws SQLException {
        String FROM_QUERY = " FROM TaxonBase tb " +
                "INNER JOIN TaxonNode tn on tn.taxon_id=tb.id " +
                "INNER JOIN TaxonNameBase tnb on tb.name_id=tnb.id " +
                "INNER JOIN DefinedTermBase dtb on tnb.rank_id=dtb.id  ";
        String SELECT_QUERY="SELECT tb.id as " + id +
                ", tb.uuid as " + uuid_id +
                ", tnb.titleCache as " + name_id +
                ", tb.publish as " + pb_id +
                ", tb.unplaced as " + unp_id +
                ", dtb.titleCache as " + rank_id +
                ", (SELECT COUNT(*) FROM  SynonymRelationship sr WHERE tb.id = sr.relatedto_id) as " + has_syn_id +
                FROM_QUERY;
        String COUNT_QUERY = "SELECT count(*) " + FROM_QUERY;
        String CONTAINS_QUERY = "SELECT * FROM TaxonBase tb WHERE tb.id = ?";

        return generateQueryDelegate(SELECT_QUERY, COUNT_QUERY, CONTAINS_QUERY, id);
    }

    public static QueryDelegate generateTaxonDistributionQuery(String id,
            List<String> termList, int classificationID) throws SQLException {
        String FROM_QUERY = 
        		" FROM TaxonNode tn " +
        		"INNER JOIN TaxonBase tb on tn.taxon_id = tb.id " +
        		"INNER JOIN Classification cl ON tn.classification_id = cl.id " +
        		"LEFT OUTER JOIN TaxonNameBase tnb ON tnb.id=tb.id " + 
        		"LEFT OUTER JOIN DescriptionBase db ON db.taxon_id=tb.id " +
        		"LEFT OUTER JOIN (SELECT indescription_id, area_id, status_id, DTYPE, id FROM DescriptionElementBase deb WHERE deb.DTYPE LIKE 'Distribution') AS deb ON deb.indescription_id=db.id " +
        		"LEFT OUTER JOIN DefinedTermBase dtb on deb.status_id=dtb.id " +
        		"LEFT OUTER JOIN DefinedTermBase dtb1 on deb.area_id=dtb1.id " +
        		"LEFT OUTER JOIN DefinedTermBase dtb2 on tnb.rank_id = dtb2.id " +
        		"WHERE tn.classification_id = "+ classificationID +" AND tb.DTYPE = 'Taxon'" ;

        String GROUP_BY = " GROUP BY tb.id ";
       
        String SELECT_QUERY=
        		"SELECT tb.DTYPE," +
        		"tb.id, " +
        		"tn.classification_id, " +
        		"tb.titleCache AS Taxon, " +
        		"dtb2.titleCache AS Rank, " +
        		"deb.DTYPE," +
        		"deb.id, " +
        		"deb.area_id, "+
        		"dtb.vocabulary_id, " +
        		"dtb1.vocabulary_id, "; 
        int count = termList.size();
        for(String term : termList){
        	if(count == 1){
        		SELECT_QUERY= SELECT_QUERY + 
            			"MAX( IF(dtb1.titleCache = '"+ term +"', dtb.titleCache, NULL) ) as '"+ term +"' " ;
        	}else{
        		SELECT_QUERY= SELECT_QUERY + 
        				"MAX( IF(dtb1.titleCache = '"+ term +"', dtb.titleCache, NULL) ) as '"+ term +"'," ;
        	}
        	count--;
        }
        SELECT_QUERY= SELECT_QUERY + FROM_QUERY + GROUP_BY; 
        String COUNT_QUERY = "SELECT count(DISTINCT tb.id)" + FROM_QUERY;
        String CONTAINS_QUERY = "SELECT * FROM TaxonNode tn WHERE tn.id = ?";

        return generateQueryDelegate(SELECT_QUERY, COUNT_QUERY, CONTAINS_QUERY, id);
    }

    public static QueryDelegate generateSynonymofTaxonQuery(String id,
    		String name_id) throws SQLException {
    	String FROM_QUERY = " FROM TaxonBase tb " +
    			"INNER JOIN TaxonNameBase tnb on tb.name_id=tnb.id " +
    			"INNER JOIN SynonymRelationship sr on tb.id=sr.relatedfrom_id ";
    	String SELECT_QUERY="SELECT tb.id as " + id +
    			", tnb.titleCache as " + name_id +
    			FROM_QUERY;
    	String COUNT_QUERY = "SELECT count(*) " + FROM_QUERY;
    	String CONTAINS_QUERY = "SELECT * FROM SynonymRelationship sr WHERE sr.relatedfrom_id = ?";
    	
    	return generateQueryDelegate(SELECT_QUERY, COUNT_QUERY, CONTAINS_QUERY, id);
    }

    public static QueryDelegate generateQueryDelegate(String SELECT_QUERY, String COUNT_QUERY, String CONTAINS_QUERY, String id) throws SQLException {
        FreeformQuery query = new FreeformQuery("This query is not used", CdmSpringContextHelper.getConnectionPool(), id);
        CdmStatementDelegate delegate = new CdmStatementDelegate(SELECT_QUERY, COUNT_QUERY, CONTAINS_QUERY);
        query.setDelegate(delegate);
        return query;
    }

}
