javac -classpath ${HADOOP_HOME}/hadoop-${HADOOP_VERSION}-core.jar -d ./ ../Shared/*.java Voronoi.java
jar -cvf voronoi.jar *.class
