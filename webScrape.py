# http://www.fluorophores.tugraz.at/ csv scraper


import webbrowser
import requests
import bs4
import os
# ,adPPYba,  ,adPPYba,  88       88 8b,dPPYba,   
# I8[    "" a8"     "8a 88       88 88P'    "8a  
#  `"Y8ba,  8b       d8 88       88 88       d8  
# aa    ]8I "8a,   ,a8" "8a,   ,a88 88b,   ,a8"  
# `"YbbdP"'  `"YbbdP"'   `"YbbdP'Y8 88`YbbdP"'   
#                                   88           
#                                   88      


#######
###	Beautiful Soup is amazing
#####

#Download the directory page
res = requests.get("http://www.fluorophores.tugraz.at/substance/")
res.raise_for_status()
substancesPage = bs4.BeautifulSoup(res.text)

#Saves the list of fp rows in the directory
fpDir = substancesPage.select('.title')

#Get each fluorophore row
#Connection timed out/dropped around item ~570
#Just reverse fpDir and then work your way backwards to complete the entire list.
for title in reversed(fpDir):

	#Find the link to the fluorophore page
	link = title.select('a')[0]
	append = link.get('href');
	dlFrom = "http://www.fluorophores.tugraz.at/" + append

	fluoroPage = requests.get(dlFrom)

	#now I've downloaded the page. Get the HTML
	fluoroPage.raise_for_status();
	readPage = bs4.BeautifulSoup(fluoroPage.text);

	#Find the name in the header
	fpName = readPage.select('h1')[0].getText()
	fpName = fpName.replace('/','-')
	fpName = fpName.replace(' ','')
	#Get the link to the csv
	downloadLink = readPage.select('.downloadCommand')[0].select('a')[0].get('href')

	#Download the csv
	csv = requests.get("http://www.fluorophores.tugraz.at/"+downloadLink)
	csv.raise_for_status();

	#Save to a folder on desktop
	filePath = os.path.join('/home/david/Desktop/fpCSVs',fpName + '.csv')

	csvFile = open(filePath,'wb')
	for chunk in csv.iter_content(100000):
		csvFile.write(chunk)

	csvFile.close()

	#Done
	print("Downloaded " + fpName)




