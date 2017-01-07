package com.stt.portfolio.dividends;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class DividendFileManager implements DividendManager {
	
	Map<String, Double> dividends = new HashMap<String, Double>();
	
	public void init() {
		String filename = "etc/osingot.txt";
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					new FileInputStream(filename), "ISO8859_1"));

			String str;
			while ((str = in.readLine()) != null) {
				if (str.length() > 0 && !str.startsWith("#")) {
					processLine(str);
				}
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void processLine(String line) {
		String[] items = line.split("\\s+");
		String ticker = items[0];
		String dividend = items[4].trim().replace(',', '.');
		
		double div = Double.parseDouble(dividend);
		if (dividends.containsKey(ticker))
			div = div + dividends.get(ticker);
		
		dividends.put(ticker, div);
	}
	
	public double getDividend(String ticker) {
		if (dividends.containsKey(ticker))
			return dividends.get(ticker);
		else
			return 0.0;
	}
	
	public static void main(String[] args) {
		DividendFileManager d = new DividendFileManager();
		d.init();
		System.out.println(d.getDividend("KCR1V"));
		
	}
	
}