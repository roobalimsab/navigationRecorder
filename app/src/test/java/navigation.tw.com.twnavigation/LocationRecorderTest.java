//package navigation.tw.com.twnavigation;
//
//import org.junit.Test;
//import org.mockito.ArgumentMatcher;
//
//import static org.junit.Assert.assertEquals;
//import static org.mockito.Matchers.argThat;
//import static org.mockito.Matchers.eq;
//import static org.mockito.Mockito.verify;
//
//
//public class LocationRecorderTest {
//
//    @Test
//    public void testRecording() {
//        LocationRecorder locationRecorder = new LocationRecorder();
//        for (int i = 0; i < 3; i++){
//            WifiSignals signals = new WifiSignals();
//            signals.add(new WifiSignal("A", 5+i));
//            signals.add(new WifiSignal("B", 6+i));
//            signals.add(new WifiSignal("C", 7+i));
//            signals.add(new WifiSignal("D", 8 + i));
//            System.out.println("snapshot-" + i + " : " + signals.toString());
//            locationRecorder.record(signals);
//        }
//        WifiSignals signals = locationRecorder.stop();
//        assertEquals(signals.toString(), "A--6__B--7__C--8__D--9");
//    }
//}