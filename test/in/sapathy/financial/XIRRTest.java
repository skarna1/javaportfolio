package in.sapathy.financial;

import static org.junit.Assert.*;

import java.util.Date;

import in.satpathy.financial.XIRR;
import in.satpathy.financial.XIRRData;

import org.junit.Before;
import org.junit.Test;

public class XIRRTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testXirr() {
		
		double[] dates = new double[2];
		double[] values = new double[2];
		
		values[0] = -4400.0;
		values[1] = 4664.30;
		
		Date startDate = new Date(109,1,3); // 3.2.2009
		Date endDate = new Date(109,8,13); // 13.9.2009
		
		dates[0] = XIRRData.getExcelDateValue(startDate);
		dates[1] = XIRRData.getExcelDateValue(endDate);
		
		XIRRData data = new XIRRData(2, 0.002,
				values, dates);
		
		double xirr = XIRR.xirr(data) * 100.0;
		
		assertEquals(10.1, xirr, 0.05);
	}

}
