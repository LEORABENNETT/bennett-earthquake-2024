package bennett.earthquake;

import bennett.earthquake.json.FeatureCollection;
import io.reactivex.rxjava3.core.Flowable;
import retrofit2.http.GET;

public interface EarthquakeService {

    @GET("/earthquakes/feed/v1.0/summary/1.0_hour.geojson")
    Flowable<FeatureCollection> oneHour();

    @GET("earthquakes/feed/v1.0/summary/significant_month.geojson")
    Flowable<FeatureCollection> thirtyDays();

}
