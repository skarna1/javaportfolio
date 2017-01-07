package in.sapathy.financial;

import static org.junit.Assert.*;

import in.satpathy.financial.XIRRData;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

public class XIRRDataTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGetDaysBetween() {
		
		Date startDate = new Date(109,1,3); // 3.2.2009
		Date endDate = new Date(109,8,13); // 13.9.2009
		
		Calendar c1 = Calendar.getInstance();
		c1.setTime(startDate);
		
		Calendar c2 = Calendar.getInstance();
		c2.setTime(endDate);
		
		
		int days = XIRRData.getDaysBetween(c1, c2);
		assertEquals(221, days);
	}

}
