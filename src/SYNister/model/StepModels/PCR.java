/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package SYNister.model.StepModels;

/**
 * A Step that contains the information for a PCR
 *
 * @author J. Christopher Anderson
 * @author Arnav Raha
 */
public class PCR implements Step {
    //Loose-coupled references by name
    private final String oligo1;
    private final String oligo2;
    private final String template;
    private final String product;
    private String label;
    private final int productLength;
    private final PCRType type;

    public PCR(String oligo1, String oligo2, String template, String product, int productLength) {
        this.oligo1 = oligo1;
        this.oligo2 = oligo2;
        this.template = template;
        this.product = product;
        this.label = null;
        this.productLength = productLength;
        this.type = setType();
    }

    //if product length is not provided
    public PCR(String oligo1, String oligo2, String template, String product) {
        this.oligo1 = oligo1;
        this.oligo2 = oligo2;
        this.template = template;
        this.product = product;
        this.label = null;
        this.productLength = -1;
        this.type = PCRType.UNSPECIFIED;
    }

    public int getProductLength() { return this.productLength;}

    public String getOligo1() {
        return oligo1;
    }

    public String getOligo2() {
        return oligo2;
    }

    public String getTemplate() {
        return template;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    //sets type based on length and if genomic to determine protocol
    private PCRType setType() {
        if (this.template.length() > 4 && this.template.substring(this.template.length()-4).equals("-gen")) {
            if (this.productLength < 2000) {
                return PCRType.GSMALL;
            } else if (this.productLength <= 4000) {
                return PCRType.GMED;
            } else {
                return PCRType.GBIG;
            }
        } else {
            if (this.productLength < 1500) {
                return PCRType.NGSMALL;
            } else if (this.productLength <= 4000) {
                return PCRType.NGMED;
            } else {
                return PCRType.NGBIG;
            }
        }
    }
    public PCRType getType() { return this.type;}

    
    @Override
    public String getProduct() {
        return product;
    }
    
    @Override
    public Operation getOperation() {
        return Operation.pcr;
    }
}
