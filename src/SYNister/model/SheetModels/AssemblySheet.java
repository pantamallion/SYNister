package SYNister.model.SheetModels;

import SYNister.InventoryModel.Inventory;
import SYNister.model.StepModels.Assembly;
import SYNister.model.StepModels.Enzyme;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;

/**
 * A LabSheet that defines instructions for an Assembly step.
 *
 * @author Stephen Lin
 * @author Arnav Raha
 */

public class AssemblySheet extends LabSheet {

    private String[][] source;
    private String[][] samples;
    private String destination;
    private String program;
    private String note;
    private HashMap<String, Integer> oligoCounts;
    private Enzyme enzyme;
    private Inventory inv;

    //default destination and program
    public AssemblySheet(Assembly[] assemblies, String title, Inventory inv) {

        this.enzyme = assemblies[0].getEnzyme();

        if (this.enzyme == Enzyme.gibson) {
            setTitle(title+" Gibson Assembly");
            this.program = "main/GIB2";
        } else {
            setTitle(title+" Golden Gate Assembly");
            this.program = "main/GG1";
        }

        this.inv = inv;

        fillSampleTable(assemblies);
        fillSourceTable(assemblies);

        this.destination = "thermocycler__"; //default

        //Default note
        this.note = "Note:\nNever let enzymes warm up! Only take the enzyme cooler out of "+
                "the freezer when you are actively using it, and only take the tubes out of it when actively dispensing";

        //counts number of times each oligo is used for the purpose of creating a master mix
        this.oligoCounts = new HashMap<>();
        for (Assembly assembly : assemblies) {
            for(String frag : assembly.getFragments()) {
                if (this.oligoCounts.get(frag) == null) {
                    this.oligoCounts.put(frag, 1);
                } else {
                    this.oligoCounts.put(frag, this.oligoCounts.get(frag)+1);
                }
            }
        }

    }

    //only if change from default
    public void setDestination(String destination) {
        this.destination = destination;
    }

    //only if change from default
    public void setProgram(String program) {
        this.program = program;
    }

    public String getDestination() {
        return this.destination;
    }

    public String getProgram() {
        return this.program;
    }

    public String[][] getSourceTable() {
        return this.source;
    }

    private void fillSourceTable(Assembly[] assemblies){

        Set<String> dnas = new HashSet<>();

        for (Assembly a : assemblies) {
            for (String f : a.getFragments()){
                dnas.add(f);
            }
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

    private void fillSampleTable(Assembly[] assemblies){
        this.samples = new String[assemblies.length+1][4];
        this.samples[0][0] = "label";
        this.samples[0][1] = "dna";
        this.samples[0][2] = "enzyme";
        this.samples[0][3] = "product";

        for (int i = 1; i < assemblies.length+1; i++) {
            ArrayList<String> dnas = new ArrayList<>();
            for (String frag : assemblies[i-1].getFragments()) {
                if (frag != null) {
                    dnas.add(frag);
                }
            }
            this.samples[i][1] = dnas.toString().substring(1, dnas.toString().length()-1);
            this.samples[i][2] = assemblies[i-1].getEnzyme().name();
            this.samples[i][3] = assemblies[i-1].getProduct();
        }

    }

    public String getMasterMix() {
        if (this.enzyme == Enzyme.gibson) {
            return getGibsonRecipe();
        }
        return getGGMasterMix();
    }

    //Golden Gate Assembly Master Mix
    private String getGGMasterMix() {
        ArrayList<String> masterFrags = new ArrayList<>();

        for (String oligo : this.oligoCounts.keySet()) {
            if (this.oligoCounts.get(oligo) == this.samples.length - 1 ) {
                masterFrags.add(oligo);
            }
        }

        StringBuilder recipe = new StringBuilder();
        recipe.append("[VERIFY] Master Mix:\n");
        recipe.append(Double.toString(6.* Collections.max(this.oligoCounts.values()) +6));
        recipe.append(" uL ddH2O\n");
        recipe.append(Double.toString(Collections.max(this.oligoCounts.values())+1.));
        recipe.append(" 10x T4 DNA Ligase Buffer\n");
        for (String oligo : masterFrags) {
            recipe.append(Double.toString((Collections.max(this.oligoCounts.values())+1.)/2));
            recipe.append(" uL ");
            recipe.append(oligo);recipe.append("\n");
        }
        recipe.append(Double.toString((Collections.max(this.oligoCounts.values())+1.)/2));
        recipe.append(" uL ");
        recipe.append(this.enzyme.toString());recipe.append("\n\n");

        if(Collections.max(this.oligoCounts.values()) < this.samples.length - 1 ) {
            recipe.append("Recipe [TOTAL = 10uL]:\n6.5 uL Master Mix\n1 uL each remaining DNA\n0.5 uL T4 DNA ligase\n\n");
        } else {
            recipe.append("Recipe [TOTAL = 10uL]:\n8.5 uL Master Mix\n1 uL remaining DNA\n0.5 uL T4 DNA ligase\n\n");
        }

        return recipe.toString();
    }

    //Gibson assembly recipe
    private String getGibsonRecipe(){

        StringBuilder recipe = new StringBuilder();
        recipe.append("DNA Mix:\n");
        recipe.append("5 uL of each oligo\n");

        recipe.append("\nReaction Mix:\n");
        recipe.append("4 uL ddH2O\n");
        recipe.append("1 uL DNA Mix\n");
        recipe.append("5 uL 2X Gibson Mix\n\n");


        return recipe.toString();
    }


    public String[][] getSampleTable() {
        return this.samples;
    }

    public String getNote() {
        return this.note;
    }
}
