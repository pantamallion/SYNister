/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SYNister.model.SheetModels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import SYNister.InventoryModel.Inventory;
import SYNister.model.StepModels.Ligation;

/**
 * A LabSheet that defines instructions for a Ligation Step
 *
 * @author niharikadesaraju
 * @author Arnav Raha
 */
public class LigateSheet extends LabSheet {
    private String[][] sample;
    private String[][] source;
    private String destination;
    private String program;
    private String note;
    private Inventory inv;
    
    public LigateSheet(Ligation[] ligates, String title, Inventory i) {
        setTitle(title+" Ligate");
        this.inv = i;
        this.destination = "thermocycler__"; //default
        this.program = "main/LIGATE"; //default
        this.note = "Note:\nNever let enzymes warm up!  Only take the enzyme cooler out of the freezer when you are actively using it, and only take the tubes out of it when actively dispensing.";

        fillSampleTable(ligates);
    }

    //if different from default
    public void setDestination(String destination) { this.destination = destination;}
    
    public String getDestination()  {
        return this.destination;
    }

    //if different from default
    public void setProgram(String program) { this.program = program;}
    
    public String getProgram() {
        return this.program;
    }

    public String[][] getSampleTable() {
        return this.sample;
    }

    public String[][] getSourceTable() {
        return this.source;
    }

    public String getNote() {return this.note;}

    public void fillSourceTable(Ligation[] ligate) {
        source = new String[ligate.length + 1][2];
        source[0][0] = "label";
        source[0][1] = "location";
        for(int i = 1; i < ligate.length+1; i++) {
            Ligation l = ligate[i-1];
            source[i][0] = " "; //l.getLabel();
            if (this.inv.findTube(l.getLabel()) != null) {
                source[i][1] = this.inv.findTube(l.getLabel());//l.getSource();
            } else {
                source[i][1] = "    ";
            }
        }
    }

    public void fillSampleTable(Ligation[] ligate) {
        this.sample = new String[ligate.length + 1][3];
        this.sample[0][0] = "label";
        this.sample[0][1] = "digest";
        this.sample[0][2] = "product";

        
        for(int i = 1; i < ligate.length+1; i++) {
            Ligation l = ligate[i-1];
            this.sample[i][0] = " ";//this.title+i;//l.getLabel();
            this.sample[i][1] = l.getFragments().toString().substring(1,l.getFragments().toString().length()-2);
            //this.sample[i+1][1] = l.getFragments().get(0);?? getFragments() gives you a list of string im not sure what that list represents though...
            this.sample[i][2] = l.getProduct();
        }

        fillSourceTable(ligate);
    }

    public String getMasterMix(){
        StringBuilder recipe = new StringBuilder();
        recipe.append("Reaction Mix:\n");
        recipe.append("<total volume 10 uL> uL ddH2O\n");
        recipe.append("1 uL T4 DNA Ligase Buffer\n");
        recipe.append("1 uL of each DNA\n");
        recipe.append("0.5 uL T4 DNA Ligase\n");
        recipe.append("\n");

        return recipe.toString();
    }
    
    public static void main(String[] args) throws Exception {
    List<String> fragments = new ArrayList<String>();
    fragments.add("idk");
    fragments.add("idk2");
    Ligation l1 = new Ligation(fragments,"pTarget-cscB1/lig", "A1", "boxA/B1");
    Ligation l2 = new Ligation(fragments,"pTarget-cscB2/lig", "A2", "boxA/B2");
    Ligation l3 = new Ligation(fragments,"pTarget-cscB3/lig", "A3", "boxA/B3");
    Ligation[] l = {l1, l2, l3};       
    LigateSheet lsheet = new LigateSheet(l, "A", new Inventory());

    System.out.println("sample:  " + Arrays.deepToString(lsheet.getSampleTable()));
    System.out.println("destination: " + lsheet.getDestination());
    System.out.println("program: " + lsheet.getProgram());
    }
    
}
