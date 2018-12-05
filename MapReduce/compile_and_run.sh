javac -classpath ${HADOOP_HOME}/hadoop-${HADOOP_VERSION}-core.jar -d ./ ../Shared/*.java Voronoi.java
jar -cvf voronoi.jar *.class
hadoop fs -rmr /user/jpfab_css534/output
hadoop jar voronoi.jar Voronoi input output points.txt
hadoop fs -copyToLocal /user/jpfab_css534/output/part-00000 ./
