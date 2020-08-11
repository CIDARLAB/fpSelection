# FPselection compensation matrix creation


This folder contains our scripts for creating compensation matrices from raw cytometry data using R Bioconductor packages.

The main running file is called ‘FPselection_overlap_analysis.R’. This file reads through other files to collect data and machine settings to create compensation matrices for fluorescence control files. To create a compensation matrix with this script, you must have at minimum the following files per machine:

1. A file in the main directory for defining which channels each fluorophore is going to be measured (i.e. ’EX_HarvFlex_3fp.csv’). Each row is read individually, so a user many input many candidate compensation settings if they like.
2. A dictionary file in a folder called ‘inputFiles’ for defining the machine filter and laser settings (i.e. ‘HarvardCytoFlex_Dictionary.csv’)
3. A data folder with a name that string matches the main input file (i.e. ‘CytoFlex_HMS’) that contains the at least the following raw data files:
	
	A. A negative control file (must have the string ‘negative’ within the file name) (i.e. ‘negative.fcs’)
	
	B. Two or more data files with single fluorophores measured as controls (files must have a string match to the names within the main file for specifying which fluorophores are to be read in which channel) (i.e. ‘Sirius.fcs’)

A folder called ‘results’ will be generated that puts all compensation matrices (for each row) into a folder with the name of the input file.

The following R packages will need to be installed as dependencies for running this software:
‘flowCore’, ‘flowStats’, ‘dict’