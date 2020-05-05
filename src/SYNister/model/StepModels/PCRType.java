package SYNister.model.StepModels;

/**
 * Different types of PCR protocols
 *
 * @author Arnav Raha
 */
public enum PCRType {
    NGSMALL, //nongenomic, <1.5k bp
    NGMED, //nongenomic, 1.5-4k bp
    NGBIG, //nongenomic, >4k bp
    GSMALL, //genomic, <2k bp
    GMED, //genomic, 2-4k bp
    GBIG, //genomic, 4-8k bp
    UNSPECIFIED
}
