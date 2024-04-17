package bennett.earthquake;

import bennett.earthquake.json.Feature;
import bennett.earthquake.json.FeatureCollection;
import hu.akarnokd.rxjava3.swing.SwingSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class EarthquakeFrame extends JFrame {

    private JList<String> jlist = new JList<>();
    private JRadioButton oneHour = new JRadioButton("One hour");
    private JRadioButton thirtyDays = new JRadioButton("30 days");
    private Disposable disposable;
    private FeatureCollection featureCollection;


    public EarthquakeFrame() {
        setTitle("EarthquakeFrame");
        setSize(300, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(oneHour);
        buttonPanel.add(thirtyDays);

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(oneHour);
        buttonGroup.add(thirtyDays);

        add(buttonPanel, BorderLayout.PAGE_START);
        add(jlist, BorderLayout.CENTER);

        EarthquakeService service = new EarthquakeServiceFactory().getService();

        oneHour.addActionListener(e -> fetchData(service.oneHour()));
        thirtyDays.addActionListener(e -> fetchData(service.thirtyDays()));

        jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jlist.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && featureCollection != null) {
                int index = jlist.getSelectedIndex();
                if (index != -1) {
                    Feature feature = featureCollection.features[index];
                    double latitude = feature.geometry.coordinates[1];
                    double longitude = feature.geometry.coordinates[0];
                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                        try {
                            Desktop.getDesktop().browse(new URI(
                                    "https://www.google.com/maps/search/?api=1&query="
                                            + latitude + "," + longitude));
                        } catch (IOException | URISyntaxException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
            }
        });

        fetchData(service.oneHour());
    }

    private void fetchData(Single<FeatureCollection> single) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        disposable = single
                // tells Rx to request the data on a background Thread
                .subscribeOn(Schedulers.io())
                // tells Rx to handle the response on Swing's main Thread
                .observeOn(SwingSchedulers.edt())
                //.observeOn(AndroidSchedulers.mainThread()) // Instead use this on Android only
                .subscribe(
                        this::handleResponse,
                        Throwable::printStackTrace
                );
    }

    private void handleResponse(FeatureCollection response) {
        featureCollection = response;
        String[] listData = new String[response.features.length];
        for (int i = 0; i < response.features.length; i++) {
            Feature feature = response.features[i];
            listData[i] = feature.properties.mag + " " + feature.properties.place;
        }
        jlist.setListData(listData);
    }

    public static void main(String[] args) {
        new EarthquakeFrame().setVisible(true);
    }
}