import bennett.earthquake.EarthquakeService;
import bennett.earthquake.EarthquakeServiceFactory;
import org.junit.jupiter.api.Test;
import bennett.earthquake.json.FeatureCollection;
import bennett.earthquake.json.Properties;

import static org.junit.jupiter.api.Assertions.*;

class EarthquakeServiceTest {

    @Test
    void oneHour() {
        // given
        EarthquakeService service = new EarthquakeServiceFactory().getService();

        // when
        FeatureCollection collection = service.oneHour().blockingGet();

        // then
        Properties properties = collection.features[0].properties;
        assertNotNull(properties.place);
        assertNotEquals(0, properties.mag);
        assertNotEquals(0, properties.time);
    }

    @Test
    void thirtyDays() {
        // given
        EarthquakeService service = new EarthquakeServiceFactory().getService();

        // when
        FeatureCollection collection = service.thirtyDays().blockingGet();

        // then
        Properties properties = collection.features[0].properties;
        assertNotNull(properties.place);
        assertNotEquals(0, properties.mag);
        assertNotEquals(0, properties.time);
    }
}
