package com.stt.portfolioupdater;


public class CcyFactory {

	public static CcyFetcher createCcyFetcher() {
		return new ECBCurrencyQuoteFetcher();
	}
}
