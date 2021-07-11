#!/bin/bash

cd /home/sami/workspace/javaportfolio

java -Xmx1024m -cp "bin:target/lib/*"  com.stt.portfolio.PortfolioGuiApp
