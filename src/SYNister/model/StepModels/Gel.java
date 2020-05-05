package SYNister.model.StepModels;

/**
 * A Step that contains the information for a Gel Purification
 *
 * @author Arnav Raha
 */

public class Gel implements Step{

    private final String dnaName;
    private final String product;
    private final int length;


    public Gel(String dnaName, String product, int length) {
        this.length =length;
        this.dnaName = dnaName;
        this.product = product;
    }

    public String getDnaName() {
        return this.dnaName;
    }

    public int getLength(){return this.length;}

    @Override
    public Operation getOperation() {
        return Operation.GelPurify;
    }

    @Override
    public String getProduct() {
        return product;
    }
}
