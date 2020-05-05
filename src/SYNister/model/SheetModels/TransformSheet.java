/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SYNister.model.SheetModels;
import SYNister.InventoryModel.Inventory;
import SYNister.model.StepModels.Transformation;
import SYNister.model.StepModels.Antibiotic;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *A LabSheet that defines instructions for a Transfromation step
 *
 * @author niharikadesaraju
 * @author Arnav Raha
 */
public class TransformSheet extends LabSheet {
    private String[][] sample;
    private String[][] source;
    private String note;
    private final Inventory inv;

    public TransformSheet(Transformation[] transform, String title, Inventory inv) {
        setTitle(title+" Transform");

        this.inv = inv;

        fillSampleTable(transform);
        fillSourceTable(transform); //default thermocycler

        this.note = "rescue_required?: no";
        //if any of the antibiotics are NOT Amp, rescue is required
        for (Transformation t : transform) {
            if (t.getAntibiotic() != Antibiotic.Amp) {
                this.note = "rescue_required?: yes";
                break;
            }
        }
    }
    
    public String[][] getSampleTable() {
        return this.sample;
    }

    public String[][] getSourceTable() {
        return this.source;
    }
    
    public void fillSampleTable(Transformation[] transform) {
        sample = new String[transform.length + 1][5];
        sample[0][0] = "label";
        sample[0][1] = "strain";
        sample[0][2] = "antibiotic";
        sample[0][3] = "product";
        sample[0][4] = "incubate";
        
        for(int i = 0; i < transform.length; i++) {
            Transformation t = transform[i];

            sample[i + 1][0] = " ";

            sample[i+1][1] = t.getStrain();
            sample[i+1][2] = t.getAntibiotic().toString();
            sample[i+1][3] = t.getProduct();
            sample[i+1][4] = " ";
        }
    }

    public void fillSourceTable(Transformation[] transform) {
        Set<String> plasmids = new HashSet<>();

        for (Transformation t : transform) {
            plasmids.add(t.getProduct());
        }
        source = new String[plasmids.size() + 1][3];
        source[0][0] = "label";
        source[0][1] = "concentration";
        source[0][2] = "location";

        int row = 1;
        for (String p : plasmids) {
            source[row][0] = p;
            source[row][1] = this.inv.findConc(p);
            source[row][2] = this.inv.findTube(p);
            if (source[row][2] == null) {
                source[row][2] = "thermocylcer__"; //if not in inventory assumes thermocycler
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
    }

    public String getNote() {
        return this.note;
    }

    public Inventory getInventorty() {return this.inv;}

    //example
    public static void main(String[] args) throws Exception {
        Antibiotic spec = Antibiotic.Spec;
        Transformation t1 = new Transformation("dna", "Mach1", spec, "pTarget-cscB1", "A1");
        Transformation t2 = new Transformation("dna", "Mach1", spec, "pTarget-cscB2", "A2");
        Transformation t3 = new Transformation("dna", "Mach1", spec, "pTarget-cscB3", "A3");
        Transformation[] t = {t1, t2, t3};       
        TransformSheet tsheet = new TransformSheet(t, "thermocycler1A", new Inventory());
        
        System.out.println("sample:  " + Arrays.deepToString(tsheet.getSampleTable()));
        System.out.println("source: " + tsheet.getSourceTable());

    }
}
