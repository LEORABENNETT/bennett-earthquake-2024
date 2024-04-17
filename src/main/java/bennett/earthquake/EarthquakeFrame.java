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

public class EarthquakeFrame extends JFrame {

    private JList<String> jlist = new JList<>();
    private ButtonGroup buttonGroup = new ButtonGroup();
    private JRadioButton oneHourButton = new JRadioButton("One Hour");
    private JRadioButton thirtyDaysButton = new JRadioButton("Thirty Days");

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

        oneHourButton.setSelected(true); // Default selection

        // Add action listener to the radio buttons
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

        // Fetch One Hour data by default
        fetchData(new EarthquakeServiceFactory().getService().oneHour());
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
