package ch.joelniklaus.indoloc.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;

import ch.joelniklaus.indoloc.R;
import ch.joelniklaus.indoloc.models.DataPoint;
import ch.joelniklaus.indoloc.services.WekaService;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

public class CollectDataActivity extends AppCompatActivity {

    protected WekaService wekaService = new WekaService(this);
    protected ArrayList<DataPoint> dataPoints = new ArrayList<DataPoint>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_data);
    }

    public void addDataPoint(View v) {
        dataPoints.add(new DataPoint(getRadioButtonText(), null, 1.23));

        String output = "Data Point added:\n" +
                "Room: " + getRadioButtonText() + "\n" +
                "Barometer: ..." + "\n";

        Toast myToast = Toast.makeText(this, output, Toast.LENGTH_LONG);
        myToast.show();
    }

    public void saveToArff(View v) {
        String filePath = "localizationData.arff";
        createArffFile(filePath);
        this.dataPoints = new ArrayList<DataPoint>();
        Toast myToast = Toast.makeText(this, "Saved data points to " + filePath, Toast.LENGTH_LONG);
        myToast.show();
    }

    private void createArffFile(String filePath) {
        ArrayList<Attribute> attributes = new ArrayList<Attribute>(2);
        ArrayList<String> rooms = new ArrayList<String>(6);
        rooms.add(getString(R.string.corridor));
        rooms.add(getString(R.string.kitchen));
        rooms.add(getString(R.string.living_room));
        rooms.add(getString(R.string.joel_room));
        rooms.add(getString(R.string.tobias_room));
        rooms.add(getString(R.string.nicola_room));
        attributes.add(new Attribute("room", rooms));
        attributes.add(new Attribute("Barometer", Attribute.NUMERIC));

        Instances dataRaw = new Instances("TestInstances", attributes, dataPoints.size());
        System.out.println("Before adding any instance");
        System.out.println("--------------------------");
        System.out.println(dataRaw);
        System.out.println("--------------------------");

        double[] instanceValues = null;
        for (int i = 0; i < dataPoints.size(); i++) {
            instanceValues = new double[dataRaw.numAttributes()];
            instanceValues[0] = rooms.indexOf(dataPoints.get(i).getRoom());
            instanceValues[1] = dataPoints.get(i).getBarometer();

            dataRaw.add(new DenseInstance(1.0, instanceValues));

            System.out.println("After instance number " + i);
            System.out.println("--------------------------");
            System.out.println(dataRaw);
            System.out.println("--------------------------");
        }

        System.out.println("After adding all instances");
        System.out.println("--------------------------");
        System.out.println(dataRaw);
        System.out.println("--------------------------");

        try {
            wekaService.saveArffToInternalStorage(dataRaw, filePath);
            Instances data = wekaService.loadArffFromInternalStorage(filePath);
            System.out.println(data.toSummaryString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getRadioButtonText() {
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        int radioButtonId = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) radioGroup.findViewById(radioButtonId);
        return (String) radioButton.getText();
    }

}
