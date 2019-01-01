package com.stt.portfolioupdater;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Calendar;

public class DBUpdater {

	private static Connection connect() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/portfolio",
					"portfolio","portfolio");
			return con;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private static List<Item> getItems(String uri)
	{
		KauppalehtiJsonQuoteFetcher fetcher = new KauppalehtiJsonQuoteFetcher(uri);
		List<Item> items = fetcher.parseHtml();
		return items;
	}
	
	public static void main(String[] args) throws SQLException {
		Connection connection = connect();
		if (connection == null)
			return;
		String query = " insert into kurssit (ticker, date, high, low, last, volume)"
				+ " values (?, ?, ?, ?, ?, ?)";

		PreparedStatement preparedStmt = connection.prepareStatement(query);
		
		String uri = "https://www.kauppalehti.fi/porssi/kurssit/XHEL";
		List<Item> items = getItems(uri);
		for (Item item : items) {
			item.print();
			
			preparedStmt.setString(1, item.getTicker());
			
			preparedStmt.setDate(2, new java.sql.Date(item.getGivenDate().getTime()));
			preparedStmt.setDouble(3, item.getHigh());
			preparedStmt.setDouble(4, item.getLow());
			preparedStmt.setDouble(5, item.getLast());
			preparedStmt.setInt(6, (int) item.getVolume());
			
			preparedStmt.execute();
		}
		connection.close();
	}

}
