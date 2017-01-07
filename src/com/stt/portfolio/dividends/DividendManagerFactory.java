package com.stt.portfolio.dividends;

import com.stt.portfolio.dividends.DividendManager;

public class DividendManagerFactory {

	public static DividendManager create() {
		return new DividendFileManager();
	}
}
