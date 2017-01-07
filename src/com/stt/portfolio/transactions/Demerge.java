package com.stt.portfolio.transactions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Demerge extends ChangeTransaction {

	
	List<Demergee> demergees = new ArrayList<Demergee>();

	public void addDemergee(Demergee demergee) {
		demergees.add(demergee);
	}
	
		
	public void process(I_BookEntryManagement bookEntryManager) {
	
		boolean isParentPreserved =  isParentPreserved();
	
		Iterator<Demergee> iter = demergees.iterator();
		while (iter.hasNext()) {
			Demergee d = iter.next();
		    
		    	bookEntryManager.spinoffBookEntry(symbol, d.getSymbol(), d.getRatio(), d.getStockratio(), 
		    			isParentPreserved);
		    	
//		    System.out.println(symbol + d.getSymbol()+ d.getRatio()+ d.getStockratio()+
//	    			isParentPreserved);
		}
		
		
		if (!isParentPreserved) {
			bookEntryManager.removeBookEntry(symbol);
		}
	}
	
	private boolean isParentPreserved() {
		boolean result = false;
		Iterator<Demergee> iter = demergees.iterator();
		while (iter.hasNext()) {
			Demergee d = iter.next();
			if (d.getSymbol().equals(symbol)) {
				result = true;
				break;
			}
		}
		return result;
	}
}
