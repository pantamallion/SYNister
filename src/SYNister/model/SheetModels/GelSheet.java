package SYNister.model.SheetModels;


import SYNister.model.StepModels.Gel;


/**
 * A LabSheet that defines instructions for a Gel step.
 *
 * @author Arnav Raha
 */

public class GelSheet extends LabSheet {
    private String[][] source = new String[1][1];
    private String[][] samples;
    private String destination;
    private String note;

    public GelSheet(Gel[] gels, String source, String title) {
        setTitle(title + " Gel");
        this.source[0][0] = source;
        this.samples = new String[gels.length+1][3];
        this.samples[0][0] = "reaction";
        this.samples[0][1] = "size";
        this.samples[0][2] = "product";

        for (int i = 1; i < gels.length+1; i++) {
            this.samples[i][0] = "  ";
            this.samples[i][1] = Integer.toString(gels[i-1].getLength());
            this.samples[i][2] = gels[i-1].getProduct();
        }

    }

    //public String getDestination() { return this.destination; }

    public String[][] getSourceTable() {
        return this.source;
    }

    public String[][] getSampleTable() {
        return this.samples;
    }

    //public String getNote() {return note;}
}

