package SYNister.model.StepModels;

import java.util.List;

/**
 * A Step that contains the information for a Digestion
 *
 * @author J. Christopher Anderson
 */
public class Digestion implements Step {
    private final String substrate;
    private final List<Enzyme> enzymes;
    private final String product;
    private final String label;
    private final String source;
    private final int length;

    public Digestion(String substrate, List<Enzyme> enzymes, String product, String label, String source, int length) {
        this.substrate = substrate;
        this.enzymes = enzymes;
        this.product = product;
        this.label = null; //label;
        this.source = source;
        this.length = length;
    }

    public Digestion(String substrate, List<Enzyme> enzymes, String product, int length) {
        this.substrate = substrate;
        this.enzymes = enzymes;
        this.product = product;
        this.label = null;
        this.source = null;
        this.length = length;
    }
    
    public List<Enzyme> getEnzymes() {
        return enzymes;
    }

    public String getSubstrate() {
        return substrate;
    }
    
    public String getLabel() {
        return label;
    }
    
    public String getSource() {
        return source;
    }

    public int getLength() {return this.length;}

    @Override
    public Operation getOperation() {
        return Operation.digest;
    }

    @Override
    public String getProduct() {
        return product;
    }
}
