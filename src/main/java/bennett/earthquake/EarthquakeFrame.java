package bennett.earthquake;

import bennett.earthquake.json.Feature;
import bennett.earthquake.json.FeatureCollection;
import hu.akarnokd.rxjava3.swing.SwingSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EarthquakeFrame extends JFrame {

        private JList<String> jlist = new JList<>();
        private JRadioButton oneHour = new JRadioButton("One hour");
        private JRadioButton thirtyDays = new JRadioButton("thirty days");
        private ButtonGroup timeGroup = new ButtonGroup();
        private Disposable disposable;

        public EarthquakeFrame() {

            setTitle("EarthquakeFrame");
            setSize(300, 600);
            setDefaultCloseOperation(EXIT_ON_CLOSE);

            setLayout(new BorderLayout());

            add(jlist, BorderLayout.CENTER);

            JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            radioPanel.add(oneHour);
            radioPanel.add(thirtyDays);

            add(radioPanel, BorderLayout.NORTH);
            add(new JScrollPane(jlist), BorderLayout.CENTER);

            timeGroup.add(oneHour);
            timeGroup.add(thirtyDays);

            oneHour.setSelected(true);

            oneHour.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Handle one hour selection
                    // Call the appropriate service method
                }
            });

            thirtyDays.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Handle thirty days selection
                    // Call the appropriate service method
                }
            });

            EarthquakeService service = new EarthquakeServiceFactory().getService();

            Disposable disposable = service.oneHour()
                    .subscribeOn(Schedulers.io())
                    .observeOn(SwingSchedulers.edt())
                    .subscribe(
                            (response) -> handleResponse(response),
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
            new EarthquakeFrame().setVisible(true);
        }
}
