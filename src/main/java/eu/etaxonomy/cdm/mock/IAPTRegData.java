package eu.etaxonomy.cdm.mock;

/**
 * Created by andreas on 9/16/16.
 */
public class IAPTRegData {

    private String date = null;

    private Integer regId = null;

    private String office = null;

    private Integer formNumber = null;

    public IAPTRegData(){

    }

    public IAPTRegData(String date, String office, Integer regID, Integer formNumber) {
        this.date = date;
        this.office = office;
        this.regId = regID;
        this.formNumber = formNumber;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getRegId() {
        return regId;
    }

    public void setRegId(Integer regId) {
        this.regId = regId;
    }

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public Integer getFormNumber() {
        return formNumber;
    }

    public void setFormNumber(Integer formNumber) {
        this.formNumber = formNumber;
    }
}
