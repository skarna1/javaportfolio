package com.stt.portfolioupdater;


public class TalentumStockQuoteFetcher extends TalentumQuoteFetcher {

		
	public TalentumStockQuoteFetcher() {
		super();
		
		readTickers("etc/TalentumStockQuoteFetcher.txt");
	}
	// xpath="//table/tr[@class='talentumAltBgColor' or @class='talentumBgColor']"

	protected int getNameColumn()
	{
		return 3;
	}
	protected int getLastValueColumn()
	{
		return 4;
	}
	protected int getHighColumn()
	{
		return 7;
	}
	protected int getLowColumn()
	{
		return 8;
	}
	protected int getVolumeColumn()
	{
		return 10;
	}
	protected int getDateColumn(){
		return -1;
	}
}
