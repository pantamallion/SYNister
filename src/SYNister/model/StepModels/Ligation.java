package SYNister.model.StepModels;

import java.util.List;

/**
 * A Step that contains the information for a Ligation
 *
 * @author J. Christopher Anderson
 */
public class Ligation implements Step {
    private final List<String> fragments;
    private final String product;
    private final String label;
    private final String source;

    public Ligation(List<String> fragments, String product, String label, String source) {
        this.fragments = fragments;
        this.product = product;
        this.label = label;
        this.source = source;
    }

    public Ligation(List<String> fragments, String product) {
        this.fragments = fragments;
        this.product = product;
        this.label = null;
        this.source = null;
    }

    public List<String> getFragments() {
        return fragments;
    }
    
    public String getLabel() {
        return label;
    }
    
    public String getSource() {
        return source;
    }

    @Override
    public Operation getOperation() {
        return Operation.ligate;
    }

    @Override
    public String getProduct() {
        return product;
    }
}
