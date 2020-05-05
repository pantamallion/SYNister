/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SYNister.model.SheetModels;

/**
 * Abstract parent class for all LabSheet types
 *
 * @author niharikadesaraju
 * @author Arnav Raha
 */
public abstract class LabSheet {
    private String title;
    private String[][] samples;
    
    public void setTitle(String t) {
        this.title = t;
    }
    
    public String getTitle() {
        return this.title;
    }

    public String[][] getSampleTable() { return null; }

    public String[][] getSourceTable() {return null; }

    public String getDestination() { return null; }

    public String getNote() { return null; }

    //change from default note
    public void setNote() {}

    public String getProgram() { return null; }

    public String getProtocol() { return null; }

    //for pcr: new diluted oligos
    public String[][] getDestTable() { return null; }

    //for Assembly, Digestion, Ligation
    public String getMasterMix() { return null; }


}
