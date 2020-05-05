/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SYNister.model.SheetModels;
import SYNister.model.StepModels.PCR;
import SYNister.InventoryModel.Inventory;


import java.util.*;

/**
 * A LabSheet that defines instructions for a PCR step
 *
 * @author niharikadesaraju
 * @author Arnav Raha
 */
public class PCRSheet extends LabSheet {
    private String[][] sample;
    private String[][] source;
    private String[][] destTable;
    private String destination;
    private String program;
    private String protocol;
    private String note;
    private Inventory inv;

    public PCRSheet(PCR[] pcr, Inventory inv, String title) {
        setTitle(title+" PCR");
        this.inv =inv;
        fillSampleTable(pcr);
        fillSourceTable(pcr);
        this.destination = "thermocycler__"; //default location
        switch (pcr[0].getType()) {
            case NGSMALL: this.program = "55"; this.protocol = "Phusion"; break;
            case NGMED: this.program = "main/phu1"; this.protocol = "Phusion"; break;
            case NGBIG: this.program = "Q5-8K"; this.protocol = "Q5"; break;
            case GSMALL: this.program = "2K55"; this.protocol = "Expand"; break;
            case GMED: this.program = "4K55"; this.protocol = "Expand"; break;
            case GBIG: this.program = "8K55"; this.protocol = "Expand"; break;
            default: this.program = " "; this.protocol = " "; break;
        }
        this.note = "Note:\nNever let enzymes warm up! Only take the enzyme cooler out of "+
                "the freezer when you are actively using it, and only take the tubes out of it when actively dispensing";
    }

    //if different from default
    public void setDestination(String destination) { this.destination = destination;}

    public String getDestination()  {
        return this.destination;
    }

    public String getProgram() {
        return this.program;
    }

    public String getProtocol() {
        return this.protocol;
    }
    
    public String getNote() {
        return this.note;
    }
    
    public String[][] getSampleTable() {
        return this.sample;
    }
    
    public String[][] getSourceTable() {
        return this.source;
    }
    public String[][] getDestTable() { return this.destTable; }
    
    private void fillSourceTable(PCR[] pcr) {
        Set<String> oligos = new HashSet<>();

        for (PCR p : pcr) {
            oligos.add(p.getTemplate());
            oligos.add(p.getOligo1());
            oligos.add(p.getOligo2());
        }
        source = new String[oligos.size() + 1][3];
        source[0][0] = "label";
        source[0][1] = "concentration";
        source[0][2] = "location";

        int row = 1;
        for (String o : oligos) {
            source[row][0] = o;
            source[row][1] = this.inv.findConc(o);
            source[row][2] = this.inv.findTube(o);
            if (source[row][2] == null) {
                source[row][2] = "benchtop, lyophilized stock [VERIFY]"; //if not in inventory assumes benchtop
            }
            row++;
        }

        //sorts sources by location
        for (int i = 1; i < source.length; i ++) {
            for (int j = 1; j < source.length; j++) {
                if(this.source[i][2] == null) {
                    if(this.source[j][2] == null) {
                        continue;
                    } else {
                        String[] temp = this.source[i];
                        this.source[i] = this.source[j];
                        this.source[j] = temp;
                    }
                }
                if (this.source[i][2].compareTo(this.source[j][2]) < 0 ) {
                    String[] temp = this.source[i];
                    this.source[i] = this.source[j];
                    this.source[j] = temp;

                }
            }
        }
        fillDestTable();
    }

    //Destination for newly diluted DNAs
    private void fillDestTable() {
        ArrayList<String> novelOligos = new ArrayList<>();
        boolean header = true;
        for (String[] sourceRow : this.source) {
            if (header) {
                header = false;
                continue;
            }
            if (this.inv.findTube(sourceRow[0]) == null) {
                novelOligos.add(sourceRow[0]);
            }
        }

        if (novelOligos.size() > 0) {
            this.destTable = new String[novelOligos.size()*2+1][3];
            this.destTable[0][0] = "label";
            this.destTable[0][1] = "concentration";
            this.destTable[0][2] = "location";

            int row = 1;
            for (String o : novelOligos) {
                this.destTable[row][0] = "10uM " + o;
                this.destTable[row][1] = "10uM";
                this.destTable[row][2] = " ";
                row++;
            }
            for (String o : novelOligos) {
                this.destTable[row][0] = o;
                this.destTable[row][1] = "100uM";
                this.destTable[row][2] = " ";
                row++;
            }
        }
    }

    
    private void fillSampleTable(PCR[] pcr) {
        this.sample = new String[pcr.length + 1][5];
        this.sample[0][0] = "label";
        this.sample[0][1] = "primer1";
        this.sample[0][2] = "primer2";
        this.sample[0][3] = "template";
        this.sample[0][4] = "product";
        
        for(int i = 0; i < pcr.length; i++) {
            PCR p = pcr[i];
            this.sample[i+1][0] = p.getLabel();
            this.sample[i+1][1] = p.getOligo1();
            this.sample[i+1][2] = p.getOligo2();
            this.sample[i+1][3] = p.getTemplate();
            this.sample[i+1][4] = p.getProduct();
        }
    }


    //example
    public static void main(String[] args) throws Exception {
        PCR pcr1 = new PCR("cscB1", "pTargRev", "pTargetF", "f14");
        PCR pcr2 = new PCR("cscB2", "pTargRev", "pTargetF", "f15");
        PCR[] p = {pcr1, pcr2};
        PCRSheet psheet = new PCRSheet(p, new Inventory(), " ");
        psheet.setDestination("thermocycler1A");

        System.out.println("samples: " + Arrays.deepToString(psheet.getSampleTable()));
        System.out.println("sources: " + Arrays.deepToString(psheet.getSourceTable()));
        System.out.println("destination: " + psheet.getDestination());
        System.out.println("destination: " + psheet.getDestTable());
        System.out.println("program: " + psheet.getProgram());
        System.out.println("protocol: " + psheet.getProtocol());
    }
    
    
}
