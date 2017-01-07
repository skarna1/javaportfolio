package com.stt.portfolio;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.stt.portfolio.transactions.ChangeTransaction;
import com.stt.portfolio.transactions.Demerge;
import com.stt.portfolio.transactions.Demergee;
import com.stt.portfolio.transactions.Split;
import com.stt.portfolio.transactions.SymbolChange;


public class ChangesParser implements I_Parser {

	DateParser dateParser = new DateParser();
	String filename;

	public ChangesParser(String filename) {
		this.filename = filename;
	}

	@Override
	public void parse(Portfolio portfolio) {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					new FileInputStream(filename), "ISO8859_1"));

			String str;
		
			List<ChangeTransaction> cts = new ArrayList<ChangeTransaction>();
			while ((str = in.readLine()) != null) {
				try {
					process(str, cts);
				} catch (ParseException e) {
					
					e.printStackTrace();
				}

				for (ChangeTransaction t : cts) {
					portfolio.addChangeTransaction(t);
				}
				cts.clear();
				
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	

	public void process(String line, List<ChangeTransaction> cts
			) throws ParseException {

		String[] items = line.split(";");
		String dateStr = items[0].trim();
		String symbol = items[1].trim();
		String eventtype = items[2].trim();

		if (eventtype.equalsIgnoreCase("split")) {
			
			double a = Util.convertToDouble(items[3]);
			double b = Util.convertToDouble(items[4]);

			Split t = new Split(a, b);
			t.setSymbol(symbol);
			t.setDate(dateParser.parseDate(dateStr));
			cts.add(t);

		} else if (eventtype.equalsIgnoreCase("tunnus")
				|| (eventtype.equalsIgnoreCase("tunnusnimi"))) {
			String newSymbol = items[3].trim();
			SymbolChange t = new SymbolChange(newSymbol);
			t.setSymbol(symbol);
			t.setDate(dateParser.parseDate(dateStr));
			cts.add(t);

		} else if (eventtype.equalsIgnoreCase("jakautuminen")) {
			Demerge t = new Demerge();
			t.setDate(dateParser.parseDate(dateStr));
			t.setSymbol(symbol);
			cts.add(t);

			int demergees = Integer.parseInt(items[3].trim());
			int fieldsindemergee = 3;
			if (items.length > 4 + 3*demergees)
				fieldsindemergee = 4;
			for (int i = 0; i < demergees; ++i) {
				Demergee d = new Demergee();
				d.setSymbol(items[4 + i * fieldsindemergee].trim());
				d.setRatio(Util.convertToDouble(items[6 + i * fieldsindemergee]));
				if (fieldsindemergee == 4){
					d.setStockratio(Util.convertToDouble(items[7 + i * fieldsindemergee]));
					//System.out.println(Util.convertToDouble(items[7 + i * fieldsindemergee]));
				}
				t.addDemergee(d);
			}

		} else {
		}
		// System.out.println(eventtype);

	}

}
