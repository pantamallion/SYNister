![SYNister Logo](/READMEphotos/SYNister.png)
# SYNister: Labsheet Generator
Description
## How to use
1. Download SYNister.jar file
2. Click to run, the following window will appear:  
![interface1](/READMEphotos/interface.png)    
3. Fill in full paths to inventory folder, folder of relevant construction files, and desired output location (as shown above)
4. Click "Generate Labsheets"
5. Repeat, editting relevant fields, as necessary
  
Example inventory folder, input folders, and outputs can be seen in /src/SYNister/Example  
### Inputs
#### Inventory
Folder of TSV files
#### Construction Files
ConstructionFile formatting   
The following steps are supported:  
 * pcr _primer1_,_primer2_ on _template_		(_productLength_bp, _product_)
   * All unique pcr products must have unique names
   * If template is genomic, should end in “-gen”
   * If product already exists in the inventory, SYNister will not repeat the pcr
 * assemble oligo1,oligo2,...				(enzyme, product)
  * Assumes Golden Gate Assembly
  * If Gibson Assembly “enzyme” = “gibson”
 * digest oligo with enzyme1/enzyme2/....	(product)
 * ligate digest							(product)
 * transform plasmid						(strain, antibiotic)
 * pca oligo1,oligo2,... 					(product)
### Outputs
## Troubleshooting
Error messages displayed under button  
![error](/READMEphotos/interface_error.png)  
Else check contruction file  
## Future plans
### ConstructionFileBuilder
insert location in ProcessConstructionFile where this connects
### Drag and drop GUI