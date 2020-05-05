/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package SYNister.model.StepModels;

/**
 * Different possible types of Steps
 *
 * @author J. Christopher Anderson
 */
public enum Operation {
    acquire,
    pca,
    pcr,
    digest,
    ligate,
    assemble,
    cleanup,
    transform,
    GelPurify
}
