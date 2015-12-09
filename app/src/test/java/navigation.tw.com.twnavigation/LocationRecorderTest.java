package navigation.tw.com.twnavigation;

import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;


public class LocationRecorderTest {

    @Test
    public void testRecording() {
        LocationDatabase locationDatabase = Mockito.mock(LocationDatabase.class);
        LocationRecorder locationRecorder = new LocationRecorder(locationDatabase);

        locationRecorder.startRecording("newAp");
        for (int i = 0; i < 26; i++){
            WifiSignals signals = new WifiSignals();
            signals.add(new WifiSignal("A", 5+i));
            signals.add(new WifiSignal("B", 6+i));
            signals.add(new WifiSignal("C", 7+i));
            signals.add(new WifiSignal("D", 8+i));
            locationRecorder.record(signals);
        }

       verify(locationDatabase).recordLocation(argThat(new ArgumentMatcher<BuildingLocation>() {
           @Override
           public boolean matches(Object argument) {
               BuildingLocation actualLocation = (BuildingLocation) argument;
               return actualLocation.getName().equalsIgnoreCase("newAp")
                       && actualLocation.getWifiSignalsAsString().equalsIgnoreCase("A--17__B--18__C--19__D--20");

           }
       }));
    }
}