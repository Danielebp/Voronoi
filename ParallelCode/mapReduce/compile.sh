javac -classpath ${HADOOP_HOME}/hadoop-${HADOOP_VERSION}-core.jar -d ./ ../../Shared/sharedClasses/*.java Voronoi.java
jar -cvf voronoi.jar *.class
