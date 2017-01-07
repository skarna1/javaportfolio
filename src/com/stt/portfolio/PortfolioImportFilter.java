package com.stt.portfolio;

import java.io.File;
import java.io.FilenameFilter;

public class PortfolioImportFilter implements FilenameFilter {

	@Override
	public boolean accept(File dir, String name) {
		return  (name.equals("info.txt") || name.equals("tapahtumat.csv"));
	
	}

}
