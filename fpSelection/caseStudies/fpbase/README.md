# Creating 10-color panels from FPbase Fluorophores


This folder contains the results produced by running 5000 seeded iterations of Simulated Annealing to design a 10-color fluorophore panel from 188 fluorophores for the CytoFlex LX cytomter. The emission and excitation spectra can be found in [spectra.csv](spectra.csv).

To recreate these solutions, please run the following command in the following location: `/repo/fpSelection/`

```bash
mvn -Dtest=FPbaseTest#testFPbase test
```