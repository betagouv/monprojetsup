#!/bin/bash
cd /home/gimbert/code/monprojetsup/back/java/tmp

#java -jar -Xms16G -Xmx16G /home/gimbert/code/monprojetsup/back/java/target/monprojetsup-1.4.0-exec.jar
java -jar -Xms16G -Xmx16G -XX:+ExitOnOutOfMemoryError /home/gimbert/code/monprojetsup/back/java/target/monprojetsup-1.4.0-exec.jar 
