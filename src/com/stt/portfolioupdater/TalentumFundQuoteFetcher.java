package com.stt.portfolioupdater;



public class TalentumFundQuoteFetcher extends TalentumQuoteFetcher {

	// xpath="//table/tr[@class='talentumAltBgColor' or @class='talentumBgColor']"

	public TalentumFundQuoteFetcher()
	{
		super();
		
		readTickers("etc/TalentumFundQuoteFetcher.txt");
	}
	
	protected int getNameColumn()
	{
		return 0;
	}
	protected int getLastValueColumn()
	{
		return 3;
	}
	protected int getHighColumn()
	{
		return 3;
	}
	protected int getLowColumn()
	{
		return 3;
	}
	protected int getVolumeColumn()
	{
		return -1;
	}
	protected int getDateColumn()
	{
		return 5;
	}
	
	protected String getTicker2(String name) {
		
		if (name.equalsIgnoreCase("Nordea Japani K"))
			return "NJAPANIK";
		else if (name.equalsIgnoreCase("Nordea Venäjä K"))
			return "NVENAJAK";
		else if (name.equalsIgnoreCase("Nordea Kiina K"))
			return "NKIINAK";
		else if (name.equalsIgnoreCase("Nordea 1 - US Corporate Bond HB"))
			return "N1USCOBHB";
		else if (name.equalsIgnoreCase("Nordea Eurooppa Indeksirahasto B K"))
			return "NEURINDBK";
		else if (name.equalsIgnoreCase("Nordea Kehittyvät Korkomarkkinat K"))
			return "NKEHITKORK";
		else if (name.equalsIgnoreCase("Nordea Euro Yrityslaina Plus K"))
			return "NEUROYLPLK";
		else if (name.equalsIgnoreCase("Nordea Maailma Indeksirahasto B K"))
			return "FI4000046669";
		else if (name.equalsIgnoreCase("Nordea Maailma K"))
			return "NMAAILMAK";
		else if (name.equalsIgnoreCase("Nordea Suomi Indeksirahasto B K"))
			return "NSUOMIINDK";
		else if (name.equalsIgnoreCase("Danske Invest Korko K"))
			return "SAMPOKORKOK";
		else
			return null;
			/*
		ticker: Nordea Corporate Bond A
ticker: Nordea Corporate Bond I K
ticker: Nordea III Takuuturva 100
ticker: Nordea V Takuuturva 100
ticker: Nordea Multi Manager - Hedge Select BP
ticker: Nordea II Takuuturva 100
ticker: Nordea European Cross Credit Fund BI
ticker: Nordea European Cross Credit Fund BP
ticker: Nordea Euro Obligaatio K
ticker: Nordea Euro Obligaatio T
ticker: Nordea European New Frontiers T
ticker: Nordea Euro Obligaatio I
ticker: Nordea Euro Midi Korko T
ticker: Nordea European New Frontiers K
ticker: Nordea Corporate Bond I T
ticker: Nordea Euro Midi Korko K
ticker: Nordea Lyhyt Korko S T
ticker: Nordea Lyhyt Korko S K
ticker: Nordea Lyhyt Korko I T
ticker: Nordea Lyhyt Korko I K
ticker: Nordea Lyhyt Korko B T
ticker: Nordea Lyhyt Korko B K
ticker: Nordea Lyhyt Korko A T
ticker: Nordea Lyhyt Korko A K
ticker: Nordea Pro Suomi T
ticker: Nordea Pro Suomi K
ticker: Nordea Pro Suomi C
ticker: Nordea Pro Euro Obligaatio T
ticker: Nordea Pro Euro Obligaatio K
ticker: Nordea Pro Euro Obligaatio C
ticker: Nordea Pro Eurooppa T
ticker: Nordea Pro Eurooppa K
ticker: Nordea Corporate Bond C
ticker: Nordea Pro Eurooppa C
ticker: Nordea Pohjoismaat T
ticker: Nordea Pro Stable Return C
ticker: Nordea Pohjois-Amerikka Tuotto
ticker: Nordea Pohjoismaat K
ticker: Nordea Focus Suomi Private Banking I K
ticker: Nordea Pohjois-Amerikka Kasvu
ticker: Nordea Focus Suomi Private Banking A K
ticker: Nordea Focus Suomi Private Banking C
ticker: Nordea Focus Nordic Private Banking C
ticker: Nordea Focus Nordic Private Banking I K
ticker: Nordea Focus Korko Private Banking I K
ticker: Nordea Focus Nordic Private Banking A K
ticker: Nordea Focus Korko Private Banking A K
ticker: Nordea Focus Korko Private Banking C
ticker: Nordea Teknologia K
ticker: Nordea Takuuturva 100 K
ticker: Nordea Premium Varainhoito Maltti K
ticker: Nordea Teknologia T
ticker: Nordea SVE Instituutio Korko T
ticker: Nordea SVE Instituutio Korko K
ticker: Nordea SVE Reaalikorko T
ticker: Nordea SVE Reaalikorko K
ticker: Nordea Suomi Small Cap K
ticker: Nordea Suomi K
ticker: Nordea Suomi T
ticker: Nordea Suomi Small Cap T
ticker: Nordea Suomi 130/30
ticker: Nordea Suomi Indeksirahasto C
ticker: Nordea Suomi Indeksirahasto I T
ticker: Nordea Suomi Indeksirahasto I K
ticker: Nordea Nordic Small Cap T
ticker: Nordea Stratega Korko
ticker: Nordea Suomi Indeksirahasto B K
ticker: Nordea Suomi Indeksirahasto B T
ticker: Nordea Säästö 50 T
ticker: Nordea Säästö 75 K
ticker: Nordea Säästö 75 T
ticker: Nordea Nordic Small Cap K
ticker: Nordea Säästö 10 T
ticker: Nordea Säästö 25 K
ticker: Nordea Säästö 25 T
ticker: Nordea Säästö 50 K
ticker: Nordea Lyhyt Korko C
ticker: Nordea Säästö Korko K
ticker: Nordea Säästö Korko T
ticker: Nordea Säästö 10 K
ticker: Nordea Kiina K
ticker: Nordea Kehittyvät Osakemarkkinat T
ticker: Nordea Kehittyvät Osakemarkkinat K
ticker: Nordea Kehittyvät Korkomarkkinat T
ticker: Nordea Korko A T
ticker: Nordea Korko A K
ticker: Nordea Korko C
ticker: Nordea Kiina T
ticker: Nordea Premium Varainhoito Kasvu K
ticker: Nordea Japani T
ticker: Nordea Japani K
ticker: Nordea Itä-Eurooppa T
ticker: Nordea Kehittyvät Korkomarkkinat K
ticker: Nordea Kaukoitä T
ticker: Nordea Kaukoitä K
ticker: Nordea Premium Varainhoito Kasvu T
ticker: Nordea Euro Yrityslaina Plus T
ticker: Nordea Foresta K
ticker: Nordea Eurooppa T
ticker: Nordea Euro Yrityslaina Plus K
ticker: Nordea Intia T
ticker: Nordea Itä-Eurooppa K
ticker: Nordea Foresta T
ticker: Nordea Intia K
ticker: Nordea Eurooppa Indeksirahasto C
ticker: Nordea Eurooppa Indeksirahasto I K
ticker: Nordea Eurooppa Indeksirahasto B K
ticker: Nordea Eurooppa Indeksirahasto B T
ticker: Nordea Eurooppa Plus T
ticker: Nordea Eurooppa K
ticker: Nordea Eurooppa Indeksirahasto I T
ticker: Nordea Eurooppa Plus K
ticker: Nordea Maailma Indeksirahasto I T
ticker: Nordea Maailma Indeksirahasto I K
ticker: Nordea Osakesalkku T
ticker: Nordea Osakesalkku K
ticker: Nordea Focus Europe Private Banking C
ticker: Nordea Focus Europe Private Banking A K
ticker: Nordea Focus Korko Private Banking ID
ticker: Nordea Focus Europe Private Banking I K
ticker: Nordea Maailma K
ticker: Nordea Korkosalkku Plus T
ticker: Nordea Medica K
ticker: Nordea Maailma T
ticker: Nordea Euro Obligaatio C
ticker: Nordea Medica T
ticker: Nordea Maailma Indeksirahasto B T
ticker: Nordea Maailma Indeksirahasto B K
ticker: Nordea Korkotuotto B K
ticker: Nordea Korkotuotto B T
ticker: Nordea Korkotuotto I K
ticker: Nordea Korkotuotto I T
ticker: Nordea Korkotuotto C
ticker: Nordea Korkotuotto S K
ticker: Nordea Korkotuotto S T
ticker: Nordea Korkosalkku Plus K
ticker: Nordea Korko B K
ticker: Nordea Korko B T
ticker: Nordea Korko I K
ticker: Nordea Korko I T
ticker: Nordea Korko S K
ticker: Nordea Korko S T
ticker: Nordea Korkotuotto A K
ticker: Nordea Korkotuotto A T
ticker: Nordea Yrityslaina Plus T
ticker: Nordea Pro Stable Return K
ticker: Nordea Premium Varainhoito Tasapaino T
ticker: Nordea Yrityslaina Plus K
ticker: Nordea Pro Stable Return T
ticker: Nordea 1 - North American All Cap
ticker: Nordea 1 - North American All Cap HB EUR
ticker: Nordea Vakaa Tuotto A T
ticker: Nordea Vakaa Tuotto A K
ticker: Nordea Turva 75
ticker: Nordea Premium Varainhoito Maltti T
ticker: Nordea Valtionlaina AAA A T
ticker: Nordea Valtionlaina AAA A K
ticker: Nordea Vakaa Tuotto I T
ticker: Nordea Vakaa Tuotto I K
ticker: Nordea Valtionlaina AAA I K
ticker: Nordea Valtionlaina AAA C
ticker: Nordea Valtionlaina AAA B T
ticker: Nordea Valtionlaina AAA B K
ticker: Nordea Premium Varainhoito Tasapaino K
ticker: Nordea Venäjä T
ticker: Nordea Venäjä K
ticker: Nordea Valtionlaina AAA I T
ticker: Nordea 1 - Ilmasto ja Ympäristö
ticker: Nordea 1 - Afrikka
ticker: Nordea 1 Emerging Consumer
ticker: Nordea 1 - Kehittyvät Tähdet BI
ticker: Nordea 1 - EM Corporate Bond HB-EUR
ticker: Nordea 1 - EM Corporate Bond HBI-EUR
ticker: Nordea 1 - European Covered Bond Fund BP
ticker: Nordea 1 - European Value Fund
ticker: Nordea 1 - Kehittyvät Tähdet BP
ticker: Nordea 1 - European Alpha
ticker: Nordea 1 - Global Equity
ticker: Nordea 1 - Global Portfolio BP
ticker: Nordea 1 - European Small & Mid Cap Equi
ticker: Nordea 1 - Far Eastern Equity (EUR)
ticker: Nordea 1 - Reaalikorko BP
ticker: Nordea 1 - Reaalikorko BI
ticker: Nordea 1 - US Corporate Bond HBI
ticker: Nordea 1 - Vakaat Osakkeet
ticker: Nordea 1 - Global Value
ticker: Nordea 1 - Latinalainen Amerikka
ticker: Nordea 1 - Japanese Value (EUR)
ticker: Nordea 1 - USA Lyhyt Yrityslaina Plus HB
ticker: Nordea 1 - USA Lyhyt Yrityslaina Plus BP
ticker: Nordea 1 - Multi-Asset BI
ticker: Nordea 1 - USA Lyhyt Yrityslai. Plus HBI
ticker: Nordea 1 - North American Growth HB
ticker: Nordea 1 - Multi-Asset BP
ticker: Nordea 1 - North American Value BP (EUR)
ticker: Nordea 1 - North American Growth BP
ticker: Nordea 1 - Nordic Equity
ticker: Nordea 1 - North American Value Fund HB
ticker: Nordea 1 - US Corporate Bond HB
ticker: Nordea 1 - US Corporate Bond BP

		*/
	}

}
