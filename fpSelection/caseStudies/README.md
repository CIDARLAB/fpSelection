# FPselection Case Studies

This folder contains the input files used in the FPselection Case studies. 


## Cytometers

The following 3 measurement instruments were used for the case studies. 

| Instrument    | Number of Lasers | Number of Detectors  |
| ------------- |-------------| -----|
| [CytoFlex LX](HarvardCytoFlex.csv) | 5 | 19 |
| [BD LSRFortessa](HarvardFortessa.csv) | 5 | 16 |
| [Miltenyi MACSquant VYB](HarvardMacsquant.csv) | 3 | 7 |


## Fluorophore Spectra

The FPselection manuscript uses 3 sets of fluorophore libraries in the case studies.

The first library has the following 12 fluorophores. 

| Fluorophore | Theoretical Brightness | Peak Emission (nm) | Peak  Excitation (nm) |
|---|---|---|---|
| mCherry | 15.84 | 587 | 610 |
| mOrange | 48.99 | 548 | 562 |
| DsRed2 | 28.6 | 578.5 | 593.5 |
| iRFP713 | 6.3 | 689 | 713 |
| Sirius | 3.6 | 354 | 425 |
| Cerulean | 26.66 | 435 | 477 |
| KO | 49.39 | 549 | 561 |
| mPlum | 4.1 | 587 | 650 |
| TagRFP | 48 | 554 | 584 |
| tdTomato | 95.22 | 556 | 581.5 |
| iRFP720 | 5.76 | 701 | 720 |
| mScarlet | 70 | 570 | 594 |

The second library is a subset of the first library containing 8 fluorophores, which exclude tdTomato, mScarlet, mCherry, and KO. 

The third library has 188 fluorophores obtained from [FPbase](https://www.fpbase.org/).

The following table contains links to the spectra and brightness files for each library. 

| Library    | Emission & Excitation Spectra | Brightness  |
| ------------- |-------------| -----|
| Library 1 - 12 Fluorophores | [spectra](main/spectra.csv) | [brightness](main/brightness.csv) |
| Library 2 - 8 Fluorophores | [spectra](subset/spectra.csv) | [brightness](subset/brightness.csv) |
| Library 3 - 188 Fluorophores | [spectra](fpbase/spectra.csv) | N/A |


## Designing 10-color panels.

The [fpbase](fpbase) folder contains the results as well as a [README](fpbase/README.md) to help recreate the case study of designing a 10-color fluorophore panel from a library of 188 fluorophores (Library 3) for the CytoFlex LX cytometer. 

## Ranking results of the Heuristic Algorithms

The [performance](performance) folder contains the results as well as a [README](performance/README.md) that highlights the performance of the heuristic algorithms (Simulated Annealing and Hill Climbing) against Exhaustive Search.