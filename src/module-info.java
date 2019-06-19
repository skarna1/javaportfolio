module javaportfolio {
	exports com.stt.portfolio;
	exports com.stt.portfolio.dividends;
	exports com.stt.portfolio.transactions;
	exports com.stt.portfolioupdater;
	exports in.sapathy.financial;
	exports in.satpathy.math;
	exports com.stt.portfolio.quotes.portfoliofiles;
	exports com.stt.portfolio.gui.treetable;
	exports com.stt.portfolio.quotes;
	exports in.satpathy.financial;
	exports com.stt.portfolio.gui;

	requires Tidy;
	requires httpclient;
	requires httpcore;
	requires java.desktop;
	requires java.scripting;
	requires java.sql;
	requires java.xml;
	requires jcalendar;
	requires jcommon;
	requires jfreechart;
	requires junit;
}