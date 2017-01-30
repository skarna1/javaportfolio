#!/bin/bash

cd /home/sami/workspace/javaportfolio

function setupClassPath() {
   CLASSPATH=bin

   JARS=$(ls jar)

   for jar in $JARS
   do
      CLASSPATH=$CLASSPATH:jar/$jar
   done

   export CLASSPATH
}


setupClassPath

java -Xmx1024m  com.stt.portfolio.PortfolioGuiApp
