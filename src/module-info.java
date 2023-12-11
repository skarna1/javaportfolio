module javaportfolio {
	exports com.stt.portfolio;
	exports com.stt.portfolio.dividends;
	exports com.stt.portfolio.transactions;
	exports com.stt.portfolioupdater;
	exports in.satpathy.math;
	exports com.stt.portfolio.quotes.portfoliofiles;
	exports com.stt.portfolio.gui.treetable;
	exports com.stt.portfolio.quotes;
	exports in.satpathy.financial;
	exports com.stt.portfolio.gui;

	requires java.desktop;
	requires java.net.http;
	requires java.scripting;
	requires java.sql;
	requires java.xml;
	requires jcalendar;
	requires jcommon;
	requires org.jfree.jfreechart;
	requires jtidy;
	requires com.google.gson;
}
