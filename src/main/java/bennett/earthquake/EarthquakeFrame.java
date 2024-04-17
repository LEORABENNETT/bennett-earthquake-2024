package bennett.earthquake;

import bennett.earthquake.json.Feature;
import bennett.earthquake.json.FeatureCollection;
import hu.akarnokd.rxjava3.swing.SwingSchedulers;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class EarthquakeFrame extends JFrame {

    private JList<String> jlist = new JList<>();
    private ButtonGroup buttonGroup = new ButtonGroup();
    private JRadioButton oneHourButton = new JRadioButton("One Hour");
    private JRadioButton thirtyDaysButton = new JRadioButton("Thirty Days");

    private FeatureCollection featureCollection;

    public EarthquakeFrame() {

        setTitle("EarthquakeFrame");
        setSize(300, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(oneHourButton);
        topPanel.add(thirtyDaysButton);

        buttonGroup.add(oneHourButton);
        buttonGroup.add(thirtyDaysButton);

        add(topPanel, BorderLayout.NORTH);
        add(jlist, BorderLayout.CENTER);

        oneHourButton.setSelected(true);

        ActionListener radioButtonListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (oneHourButton.isSelected()) {
                    fetchData(new EarthquakeServiceFactory().getService().oneHour());
                } else if (thirtyDaysButton.isSelected()) {
                    fetchData(new EarthquakeServiceFactory().getService().thirtyDays());
                }
            }
        };

        oneHourButton.addActionListener(radioButtonListener);
        thirtyDaysButton.addActionListener(radioButtonListener);

        jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jlist.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && featureCollection != null) {
                int selectedIndex = jlist.getSelectedIndex();
                if (selectedIndex != -1) {
                    openGoogleMaps(selectedIndex);
                }
            }
        });


        fetchData(new EarthquakeServiceFactory().getService().oneHour());
    }

    private void openGoogleMaps(int index) {
        Feature feature = featureCollection.features[index];
        double latitude = feature.geometry.coordinates[1];
        double longitude = feature.geometry.coordinates[0];
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                String googleMapsUrl = "https://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude;
                Desktop.getDesktop().browse(new URI(googleMapsUrl));
            } catch (IOException | URISyntaxException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private void fetchData(Flowable<FeatureCollection> flowable) {
        Disposable disposable = flowable
                .subscribeOn(Schedulers.io())
                .observeOn(SwingSchedulers.edt())
                .subscribe(
                        this::handleResponse,
                        Throwable::printStackTrace);
    }


    private void handleResponse(FeatureCollection response) {
        String[] listData = new String[response.features.length];
        for (int i = 0; i < response.features.length; i++) {
            Feature feature = response.features[i];
            listData[i] = feature.properties.mag + " " + feature.properties.place;
        }
        jlist.setListData(listData);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EarthquakeFrame().setVisible(true));
    }
}