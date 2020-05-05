/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SYNister.model.SheetModels;

import SYNister.InventoryModel.Inventory;
import SYNister.model.StepModels.Digestion;
import SYNister.model.StepModels.Enzyme;

import java.util.*;

/**
 *A LabSheet that defines instructions for a Digestion Step
 *
 * @author niharikadesaraju
 * @author Arnav Raha
 */
public class DigestSheet extends LabSheet{
    private String[][] sample;
    private String[][] source;
    private String destination;
    private String program;
    private String note;
    private Inventory inv;
    private HashMap<Enzyme, String> buffers = new HashMap<>();
    private List<Enzyme> refEnzymes;
    
    public DigestSheet(Digestion[] digest, String program, String title, Inventory i) {
        setTitle(title + " Digest");
        this.destination = "thermocycler__"; //default destination
        this.program = program;
        this.note = "";
        this.inv = i;
        this.note ="Note:\nNever let enzymes warm up!  Only take the enzyme cooler out of the freezer when you are actively using it, and only take the tubes out of it when actively dispensing.";
        fillSampleTable(digest);
        this.refEnzymes = digest[0].getEnzymes();

        buffers.put(Enzyme.BamHI, "NEB Buffer 2 (10x)");
        buffers.put(Enzyme.BglII, "NEB Buffer 2 (10x)");
        buffers.put(Enzyme.MfeI, "NEB Buffer 2 (10x)");
        buffers.put(Enzyme.EcoRI, "NEB Buffer 2 (10x)");
        buffers.put(Enzyme.SpeI, "NEB Buffer 2 (10x)");
        buffers.put(Enzyme.XbaI, "NEB Buffer 2 (10x)");
        buffers.put(Enzyme.PstI, "NEB Buffer 2 (10x)");
        buffers.put(Enzyme.SphI, "NEB Buffer 2 (10x)");
        buffers.put(Enzyme.HindIII, "NEB Buffer 2 (10x)");
        buffers.put(Enzyme.XhoI, "NEB Buffer 2 (10x)");

        buffers.put(Enzyme.BsaI, "Cutsmart Buffer");
        buffers.put(Enzyme.BseRI, "Cutsmart Buffer");

        buffers.put(Enzyme.BsmBI, "NEB Buffer 3.1");
    }

    //if different from default
    public void setDestination(String destination) { this.destination = destination;}
    
    public String getDestination()  {
        return this.destination;
    }

    public void setProgram(String program) { this.program = program;}
    
    public String getProgram() {
        return this.program;
    }
    
    public String[][] getSampleTable() {
        return this.sample;
    }

    public String[][] getSourceTable() { return this.source; }

    public String getNote() {return this.note;}

    public void fillSourceTable(Digestion[] digest) {
        Set<String> dnas = new HashSet<>();

        for (Digestion d : digest) {
            dnas.add(d.getSubstrate());
        }
        this.source = new String[dnas.size() + 1][3];
        this.source[0][0] = "label";
        this.source[0][1] = "concentration";
        this.source[0][2] = "location";

        int row = 1;
        for (String o : dnas) {
            this.source[row][0] = o;
            this.source[row][1] = this.inv.findConc(o);
            this.source[row][2] = this.inv.findTube(o);
            row++;
        }

        for (int i = 1; i < this.source.length; i ++) {
            for (int j = 1; j < source.length; j++) {
                if(this.source[j][2] == null) {
                    continue;
                } else if (this.source[i][2] == null){
                    String[] temp = this.source[i];
                    this.source[i] = this.source[j];
                    this.source[j] = temp;
                } else if (this.source[i][2].compareTo(this.source[j][2]) < 0 ) {
                    String[] temp = this.source[i];
                    this.source[i] = this.source[j];
                    this.source[j] = temp;

                }
            }
        }
    }
    
    public void fillSampleTable(Digestion[] digest) {
        sample = new String[digest.length + 1][4];
        sample[0][0] = "label";
        sample[0][1] = "dna";
        sample[0][2] = "enzymes";
        sample[0][3] = "product";

        for(int i = 1; i < digest.length+1; i++) {
            Digestion d = digest[i-1];
            sample[i][0] = " ";

            sample[i][1] = d.getSubstrate();
            sample[i][2] = d.getEnzymes().toString().substring(1,d.getEnzymes().toString().length()-1);
            sample[i][3] = d.getProduct();
        }
        fillSourceTable(digest);
    }

    public String getMasterMix(){
        StringBuilder recipe = new StringBuilder();
        recipe.append("Reaction Mix:\n");
        recipe.append("8uL eluted eluted DNA\n");
        recipe.append("1uL ");
        recipe.append(buffers.get(this.refEnzymes.get(0)));
        recipe.append("\n");
        for (Enzyme e : this.refEnzymes) {
            recipe.append(1./this.refEnzymes.size());
            recipe.append(" ");
            recipe.append(e.toString());
            recipe.append("\n");
        }
        recipe.append("\n");


        return recipe.toString();
    }

    //example
    public static void main(String[] args) {
    Enzyme aari = Enzyme.AarI;
    Enzyme bbsi = Enzyme.BbsI;
    List<Enzyme> enzymes = new ArrayList<>();
    enzymes.add(aari);
    enzymes.add(bbsi);
    Inventory inv = new Inventory();
    Digestion d1 = new Digestion("A1p", enzymes, "pTarget-cscB1/spedig", "A1", "boxA/A1", 200);
    Digestion d2 = new Digestion("A2p", enzymes, "pTarget-cscB1/spedig", "A2", "boxA/A2", 200);
    Digestion d3 = new Digestion("A3p", enzymes, "pTarget-cscB1/spedig", "A3", "boxA/A3", 200);
    Digestion[] d = {d1, d2, d3};       
    DigestSheet dsheet = new DigestSheet(d," ", "main/SPE1",   inv);

    System.out.println("sample:  " + Arrays.deepToString(dsheet.getSampleTable()));
    System.out.println("destination: " + dsheet.getDestination());
    System.out.println("program: " + dsheet.getProgram());
    }
}
