package navigation.tw.com.twnavigation;

import org.junit.Ignore;
import org.junit.Test;

import static navigation.tw.com.twnavigation.WifiSignals.fromString;
import static org.junit.Assert.*;

public class BasicLocationMatcherTest {

    String currentSignalsString = "d8:b1:90:c9:08:5e--9__d8:b1:90:b2:ba:a1--9__d8:b1:90:c9:05:71--10__d8:b1:90:b2:ba:ae--12__d8:b1:90:c4:5c:4e--19__d8:b1:90:c4:5c:5e--20__d8:b1:90:c4:5c:41--20";
    String b2Entrance =  "d8:b1:90:c9:0a:ce--6__d8:b1:90:b2:bd:91--7__d8:b1:90:b2:ba:a1--8__d8:b1:90:c9:08:51--9__d8:b1:90:75:33:be--9__d8:b1:90:c4:5c:51--10__d8:b1:90:b1:f2:ce--11__d8:b1:90:c4:5c:41--11__d8:b1:90:b2:ba:ae--11__d8:b1:90:c9:08:5e--11__d8:b1:90:c4:5c:5e--17__d8:b1:90:c4:5c:4e--19";
    String krogerTable = "d8:b1:90:c9:08:51--7__d8:b1:90:bb:98:b1--8__d8:b1:90:b2:ba:a1--9__d8:b1:90:c9:08:5e--10__d8:b1:90:c4:5c:4e--13__d8:b1:90:b2:ba:ae--14__d8:b1:90:c4:5c:51--16__d8:b1:90:c4:5c:5e--19";
    String b2Entrace2 = "d8:b1:90:b1:f2:ce--5__d8:b1:90:c9:08:51--6__d8:b1:90:b1:f2:c1--6__d8:b1:90:bb:98:b1--8__d8:b1:90:c9:08:5e--10__d8:b1:90:b2:ba:a1--10__d8:b1:90:c4:5c:51--13__d8:b1:90:c4:5c:41--14__d8:b1:90:c4:5c:4e--17__d8:b1:90:c4:5c:5e--17__d8:b1:90:b2:ba:ae--17";
    String maritimeRoom = "d8:b1:90:c9:0a:ce--5__d8:b1:90:75:33:be--5__d8:b1:90:b1:f2:ce--8__d8:b1:90:75:33:b1--8__d8:b1:90:bb:98:b1--10__d8:b1:90:b2:ba:a1--13__d8:b1:90:c9:08:51--14__d8:b1:90:c4:5c:4e--14__d8:b1:90:c4:5c:41--14__d8:b1:90:c9:08:5e--15__d8:b1:90:c4:5c:51--18__d8:b1:90:b2:ba:ae--20__d8:b1:90:c4:5c:5e--24";
    String pharmacyTable = "d8:b1:90:b1:f2:c1--6__d8:b1:90:b1:f2:ce--7__d8:b1:90:75:33:be--7__d8:b1:90:bb:98:b1--10__d8:b1:90:c4:5c:4e--13__d8:b1:90:c4:5c:41--13__d8:b1:90:c9:08:51--14__d8:b1:90:c9:08:5e--16__d8:b1:90:b2:ba:a1--16__d8:b1:90:b2:ba:ae--20__d8:b1:90:c4:5c:5e--24__d8:b1:90:c4:5c:51--24";
    String recruitmentRoom = "d8:b1:90:c9:0a:ce--6__d8:b1:90:b1:f2:c1--9__d8:b1:90:c9:08:5e--9__d8:b1:90:bb:98:b1--9__d8:b1:90:b1:f2:ce--10__d8:b1:90:b2:ba:a1--12__d8:b1:90:c4:5c:4e--13__d8:b1:90:c4:5c:41--16__d8:b1:90:b2:ba:ae--18__d8:b1:90:c4:5c:5e--24__d8:b1:90:c4:5c:51--24";
    String waterPlace = "d8:b1:90:b1:f2:ce--7__d8:b1:90:c9:08:5e--8__d8:b1:90:b2:ba:a1--13__d8:b1:90:c4:5c:4e--14__d8:b1:90:c4:5c:41--14__d8:b1:90:c4:5c:51--14__d8:b1:90:c4:5c:5e--18__d8:b1:90:b2:ba:ae--19";
    String goTable = "d8:b1:90:b1:f2:ce--6__d8:b1:90:c9:05:71--9__d8:b1:90:c9:08:5e--10__d8:b1:90:b2:ba:a1--10__d8:b1:90:b2:ba:ae--17__d8:b1:90:c4:5c:51--18__d8:b1:90:c4:5c:4e--22__d8:b1:90:c4:5c:5e--22";
    String krogerTable2 = "d8:b1:90:c9:0a:ce--5__d8:b1:90:b1:f2:c1--8__d8:b1:90:75:33:be--8__d8:b1:90:b1:f2:ce--9__d8:b1:90:c9:08:5e--10__d8:b1:90:bb:98:b1--10__d8:b1:90:c9:08:51--12__d8:b1:90:c9:05:71--12__d8:b1:90:b2:ba:a1--13__d8:b1:90:c4:5c:41--18__d8:b1:90:c4:5c:51--19__d8:b1:90:c4:5c:4e--23__d8:b1:90:b2:ba:ae--23__d8:b1:90:c4:5c:5e--24";
    String pharmacyTable2 = "d8:b1:90:c9:0a:ce--8__d8:b1:90:c9:08:5e--9__d8:b1:90:bb:98:b1--10__d8:b1:90:b1:f2:c1--11__d8:b1:90:c4:5c:4e--12__d8:b1:90:c4:5c:41--15__d8:b1:90:b1:f2:ce--16__d8:b1:90:b2:ba:a1--19__d8:b1:90:c4:5c:51--24__d8:b1:90:b2:ba:ae--24__d8:b1:90:c4:5c:5e--24";
    String krogerTable3 = "d8:b1:90:c9:08:51--7__d8:b1:90:bb:98:b1--8__d8:b1:90:b2:ba:a1--9__d8:b1:90:c9:08:5e--10__d8:b1:90:c4:5c:4e--13__d8:b1:90:b2:ba:ae--14__d8:b1:90:c4:5c:51--16__d8:b1:90:c4:5c:5e--19";

    @Test
    public void testKrogerTable() throws Exception {
        System.out.println("KrogerTable - true");
        BasicLocationMatcher matcher = new BasicLocationMatcher();
        assertTrue(matcher.isMatch(fromString(krogerTable), fromString(currentSignalsString)));
    }

    @Test
    public void testKrogerTable2() throws Exception {
        System.out.println("KrogerTable2 - true");
        BasicLocationMatcher matcher = new BasicLocationMatcher();
        assertTrue(matcher.isMatch(fromString(krogerTable2), fromString(currentSignalsString)));
    }

    @Test
    public void testKrogerTable3() throws Exception {
        System.out.println("KrogerTable3 - true");
        BasicLocationMatcher matcher = new BasicLocationMatcher();
        assertTrue(matcher.isMatch(fromString(krogerTable3), fromString(currentSignalsString)));
    }

    @Test
    public void testB2Entrance() throws Exception {
        System.out.println("B2Entrance - true");
        BasicLocationMatcher matcher = new BasicLocationMatcher();
        assertTrue(matcher.isMatch(fromString(b2Entrance), fromString(currentSignalsString)));
    }

    @Test
    public void testB2Entrance2() throws Exception {
        System.out.println("B2Entrance2 - true");
        BasicLocationMatcher matcher = new BasicLocationMatcher();
        assertTrue(matcher.isMatch(fromString(b2Entrace2), fromString(currentSignalsString)));
    }

    @Test
    public void testPharmacyTable() throws Exception {
        System.out.println("pharmacyTable");
        BasicLocationMatcher matcher = new BasicLocationMatcher();
        assertFalse(matcher.isMatch(fromString(pharmacyTable), fromString(currentSignalsString)));
    }

    @Test
    public void testPharmacyTable2() throws Exception {
        System.out.println("pharmacyTable2");
        BasicLocationMatcher matcher = new BasicLocationMatcher();
        assertFalse(matcher.isMatch(fromString(pharmacyTable2), fromString(currentSignalsString)));
    }

    @Test
    public void testMaritimeRoom() throws Exception {
        System.out.println("MaritimeRoom");
        BasicLocationMatcher matcher = new BasicLocationMatcher();
        assertFalse(matcher.isMatch(fromString(maritimeRoom), fromString(currentSignalsString)));
    }

    @Test
    public void testRecruitmentRoom() throws Exception {
        System.out.println("recruitementRoom");
        BasicLocationMatcher matcher = new BasicLocationMatcher();
        assertFalse(matcher.isMatch(fromString(recruitmentRoom), fromString(currentSignalsString)));
    }

    @Test
    public void testWaterPlace() throws Exception {
        System.out.println("waterPlace");
        BasicLocationMatcher matcher = new BasicLocationMatcher();
        assertFalse(matcher.isMatch(fromString(waterPlace), fromString(currentSignalsString)));
    }

    @Test
    public void testGoTable() throws Exception {
        System.out.println("GoTable-  true");
        BasicLocationMatcher matcher = new BasicLocationMatcher();
        assertTrue(matcher.isMatch(fromString(goTable), fromString(currentSignalsString)));
    }
}