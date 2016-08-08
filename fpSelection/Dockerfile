FROM java:8 

RUN apt-get update  
RUN apt-get install -y maven

WORKDIR /fpSelectionWebApp

ADD pom.xml /fpSelectionWebApp/pom.xml  
RUN ["mvn", "dependency:resolve"]  
RUN ["mvn", "verify"]

ADD src /fpSelection/src  
RUN ["mvn", "package"]

EXPOSE 4567  
CMD ["/usr/lib/jvm/java-8-openjdk-amd64/bin/java", "-jar", "target/fpSelectionWebApp-1.0.war"] 

