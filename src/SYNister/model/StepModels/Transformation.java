/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SYNister.model.StepModels;

/**
 * A Step that contains the information for a Transformation
 *
 * @author J. Christopher Anderson
 */
public class Transformation implements Step {

    private final String dna;
    private final String strain;
    private final Antibiotic antibiotic;
    private final String product;
    private final String label;

    public Transformation(String dna, String strain, Antibiotic antibiotic, String product, String label) {
        this.dna = dna;
        this.strain = strain;
        this.antibiotic = antibiotic;
        this.product = product;
        this.label = label;
    }

    public Transformation(String dna, String strain, Antibiotic antibiotic, String product) {
        this.dna = dna;
        this.strain = strain;
        this.antibiotic = antibiotic;
        this.product = product;
        this.label = null;
    }

    public String getDna() {
        return dna;
    }

    public String getStrain() {
        return strain;
    }

    public Antibiotic getAntibiotic() {
        return antibiotic;
    }
    
    public String getLabel() {
        return label;
    }

    @Override
    public Operation getOperation() {
        return Operation.transform;
    }

    @Override
    public String getProduct() {
        return product;
    }
}
