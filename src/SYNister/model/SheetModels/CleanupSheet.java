package SYNister.model.SheetModels;

import SYNister.model.StepModels.Cleanup;

/**
 * A LabSheet that defines instructions for a Zymo cleanup step
 *
 * @author niharikadesaraju
 * @author Arnav Raha
 */

public class CleanupSheet extends LabSheet {
    private String[][] sample;
    private String source;


    public CleanupSheet(Cleanup[] cleanup, String source, String title) {
        setTitle(title+" Zymo");
        this.source = source;
        fillSampleTable(cleanup);

    }

    public String getSource() {
        return this.source;
    }

    private void fillSampleTable(Cleanup[] cleanup) {
        sample = new String[cleanup.length + 1][5];
        sample[0][0] = "reaction";
        sample[0][1] = "label";
        sample[0][2] = "elution_vol";
        sample[0][3] = "destination";
        sample[0][4] = "product";

        for (int i = 1; i < cleanup.length+1;i ++) {
            sample[i][0] = " ";
            sample[i][1] = cleanup[i-1].getSubstrate();
            sample[i][2] = "50 uL";
            sample[i][3] = "    ";
            sample[i][4] = cleanup[i-1].getProduct();
        }

    }

    public String[][] getSampleTable(){
        return this.sample;
    }

    public String[][] getSourceTable(){
        String[][] table = new String[1][1];
        table[0][0] = this.source;
        return table;
    }
}
