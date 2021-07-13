#!/bin/bash

cd /home/sami/workspace/javaportfolio

java -Xmx1024m -cp "target/classes:target/lib/*"  com.stt.portfolio.PortfolioGuiApp
