#Written by Evan Appleton at Harvard Medical School 3/25/19
#This script reads in files which specify which overlap matrixes to create and reads recorded data to create the experimentally measured matrixes

#Load libraries
library(flowCore)
library(dict)
library(flowStats)
nSpilloverMat <- 100
dir.create("results")

#Loop through all SNR files, determine how big the spillover matrix will be for each file and create an output folder for matrixes
SNRfiles <- list.files(pattern = "\\.csv$")
for (f in SNRfiles) {
	d <- sub("^([^.]*).*", "\\1", f)
	
	#Only do overlap matrixes for >1 fp... makes no sense for only 1FP
	if (!grepl("1fp", d, ignore.case=TRUE)) {
#	if (!grepl("1fp", d, ignore.case=TRUE) && !grepl("Fort", d, ignore.case=TRUE)) {
		
		dir.create(paste0("results/",d))
		print(f)
		
		#Read a SNR file created by Prashant by line to determine which overlap matrixes to make
		#Determine if there is a header or not - only a header for exhaustive for some reason right now
		h = FALSE
		if (grepl("EX_", f, ignore.case=TRUE)) {
			h = TRUE
		} else if (grepl("Stoch", f, ignore.case=TRUE)) {
			h = TRUE
		}
		
		t <- read.csv(file=f, header=h, sep=",")
		numFP = ncol(t)/5
		
		#Determine which data folder to upload data from
		if (grepl("sony", f, ignore.case=TRUE)) {
			dataDir <- "Sony_HMS/"
			detectorTable <- read.csv(file="inputFiles/HarvardSony_Dictionary.csv", header=TRUE, sep=",")
		} else if (grepl("fort", f, ignore.case=TRUE)) {
			dataDir <- "Fortessa_HMS/"
			detectorTable <- read.csv(file="inputFiles/HarvardFortessa_Dictionary.csv", header=TRUE, sep=",")
		} else if (grepl("macs", f, ignore.case=TRUE)) {
			dataDir <- "MACSQuant_HMS/"
			detectorTable <- read.csv(file="inputFiles/HarvardMacsquant_Dictionary.csv", header=TRUE, sep=",")
		} else if (grepl("flex", f, ignore.case=TRUE)) {
			dataDir <- "CytoFlex_HMS/"
			detectorTable <- read.csv(file="inputFiles/HarvardCytoflex_Dictionary.csv", header=TRUE, sep=",")
		}
		
		#Only read the first nSpilloverMat lines of each of these files to make matrixes
		n <- nrow(t)
		if (n > nSpilloverMat) {
			n = nSpilloverMat
		}
		setwd(dataDir)
		
		for (r in 1:n) {
			
			#Determine the appropriate files to load into a flowSet and remove unimportant channels
			row <- t[r,]
			dataFiles <- list.files()
			neg_control <- dataFiles[grepl("negative", dataFiles, ignore.case=TRUE)]
			flowSetFiles <- c(neg_control)
			detectors <- c()
			fpDetectorMap <- dict()
			for (FP in 1:numFP) {
				
				#Get the FP name and related file
				FPName <- as.character(apply(row[(FP*5 - 4)], 1, paste, collapse = ""))
				FPFile <- dataFiles[grepl(FPName, dataFiles, ignore.case=TRUE)]
				flowSetFiles <- c(flowSetFiles, FPFile)
				
				#Get the detector to be measured with this FP
				FPLaser <- as.character(apply(row[(FP*5 - 3)], 1, paste, collapse = ""))
				FPDetector <- as.character(apply(row[(FP*5 - 2)], 1, paste, collapse = ""))
				# print(FPName)
				# print(FPFile)
				# print(FPLaser)
				# print(FPDetector)
				
				newDetector <- ""
				for (s in 1:nrow(detectorTable)) {
					if (grepl(FPLaser, detectorTable[s,1], ignore.case=TRUE) && grepl(FPDetector, detectorTable[s,2], ignore.case=TRUE)) {
						newDetector <- paste0(as.character(detectorTable[s,3]),"-A")
					}
				}
				fpDetectorMap[[newDetector]] <- FPName
				detectors <- c(detectors,newDetector)
				
				# print(newDetector)
			}
	
			# print(fpDetectorMap$items())
	
			#Create flowSet and change detector names
			fs <- read.flowSet(flowSetFiles)
			detectorCols <- c()
			for (detector in detectors) {
				detectorCol <- match(detector, colnames(fs))
				detectorCols <- c(detectorCols, detectorCol)
			}
			FSCSSCcols <- match(c("FSC-A", "SSC-A"), colnames(fs))
			detectorCols <- c(FSCSSCcols, detectorCols)
			fs <- fs[,detectorCols]
			for (key in fpDetectorMap$keys()) {
				FP <- fpDetectorMap[[key]]
				colnames(fs)[which(colnames(fs) == key)] <- paste0(FP, "-A (", key, ")")
			}
			
			#Create spillover matrixes (one for mean, one for median) and save them in new csv files in folder
			spilloverMEAN <- spillover(x=fs,unstained=neg_control,fsc="FSC-A",ssc="SSC-A",method="mean", stain_match = c("ordered"))
			write.csv(file=paste0("../results/",d,"/",d,"_spMEAN_row",r,".csv"), x=spilloverMEAN)
			spilloverMEDIAN <- spillover(x=fs,unstained=neg_control,fsc="FSC-A",ssc="SSC-A",method="median", stain_match = c("ordered"))
			write.csv(file=paste0("../results/",d,"/",d,"_spMEDIAN_row",r,".csv"), x=spilloverMEDIAN)
		}
		setwd("..")
	}
}
