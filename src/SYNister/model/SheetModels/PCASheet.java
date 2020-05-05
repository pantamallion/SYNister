/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SYNister.model.SheetModels;

import SYNister.InventoryModel.Inventory;
import SYNister.model.StepModels.PCA;

import java.util.*;

/**
 * A LabSheet that defines instructions for a PCA step
 *
 * @author Arnav Raha
 */
public class PCASheet extends LabSheet {
    private String[][] sample;
    private String[][] source;
    private String destination;
    private String program;
    private String protocol;
    private String note;
    private Inventory inv;

    public PCASheet(PCA[] pca, String destination, Inventory inv, String title) {
        setTitle(title+" Polymerase Chain Assembly");
        this.inv =inv;
        fillSampleTable(pca);
        fillSourceTable(pca);
        this.destination = destination;
        this.program = " ";
        this.protocol = " ";

        this.note = "Note:\nNever let enzymes warm up! Only take the enzyme cooler out of "+
                "the freezer when you are actively using it, and only take the tubes out of it when actively dispensing";
    }

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

    public void fillSourceTable(PCA[] pca) {
        Set<String> oligos = new HashSet<>();
        for (PCA p : pca) {
            oligos.addAll(p.getOligoPool());

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
            row++;
        }

        //sorts source by location
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

    public void fillSampleTable(PCA[] pca) {
        this.sample = new String[pca.length + 1][3];
        this.sample[0][0] = "label";
        this.sample[0][1] = "oligos";
        this.sample[0][2] = "product";

        for(int i = 0; i < pca.length; i++) {
            PCA p = pca[i];
            this.sample[i+1][0] = " ";
            this.sample[i+1][1] = p.getOligoPool().toString().substring(1, p.getOligoPool().toString().length() - 1);
            this.sample[i+1][2] = p.getProduct();
        }

    }

    //example
    public static void main(String[] args) throws Exception {

        List<String> o1 = Arrays.asList(new String[]{"a","b","c"});
        List<String> o2 = Arrays.asList(new String[]{"X","b","z"});

        PCA pca1 = new PCA(o1, "pTargRev");
        PCA pca2 = new PCA(o2, "pTargRev");
        PCA[] p = {pca1, pca2};
        PCASheet psheet = new PCASheet(p, "thermocycler1A", new Inventory(), " ");

        System.out.println("samples: " + Arrays.deepToString(psheet.getSampleTable()));
        System.out.println("sources: " + Arrays.deepToString(psheet.getSourceTable()));
        System.out.println("destination: " + psheet.getDestination());
        System.out.println("program: " + psheet.getProgram());
        System.out.println("protocol: " + psheet.getProtocol());
    }


}
