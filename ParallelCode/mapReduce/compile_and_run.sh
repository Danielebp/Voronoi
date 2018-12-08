rm part*
javac -classpath ${HADOOP_HOME}/hadoop-${HADOOP_VERSION}-core.jar -d ./ ../../Shared/sharedClasses/*.java Voronoi.java
jar -cvf voronoi.jar *.class
hadoop fs -rm /user/jpfab_css534/input/*
hadoop fs -copyFromLocal points.txt /user/jpfab_css534/input/
hadoop fs -rmr /user/jpfab_css534/output
hadoop jar voronoi.jar Voronoi input output points.txt $1 $2
hadoop fs -copyToLocal /user/jpfab_css534/output/part-00000 ./
