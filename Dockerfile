FROM openjdk:8 

RUN apt-get update  
RUN apt-get install -y maven

#gnuplot dependencies
RUN apt-get install -y g++ gcc make libtool libgd-dev
# Unzip + compile gnuplot
RUN wget https://sourceforge.net/projects/gnuplot/files/gnuplot/5.2.8/gnuplot-5.2.8.tar.gz/download -O gnuplot-5.2.8.tar.gz
RUN tar -xvf gnuplot-5.2.8.tar.gz
RUN cd gnuplot-5.2.8 && ./configure --prefix=/usr && make && make install
# Cleanup
RUN rm -rf gnuplot-*

COPY . /fp

# Verify the package, and install it to a local repo
WORKDIR /fp/fpSelection
RUN ["mvn", "dependency:resolve"]  
RUN ["mvn", "verify"]
RUN ["mvn", "install"]

# Verify the webapp, including the fpSel lib we just installed to the local sys repo.
WORKDIR /fp/fpSelectionWebApp
RUN ["mvn", "dependency:resolve"]  
RUN ["mvn", "verify"]

# Expose the 8080 port on the container and run the webapp.
EXPOSE 8080
CMD ["mvn", "jetty:run"]
