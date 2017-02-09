package ch.joelniklaus.indoloc.helpers;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import ch.joelniklaus.indoloc.models.DataPoint;
import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils;

/**
 * Created by joelniklaus on 19.11.16.
 */

public class FileHelper {

    private static final File EXTERNAL_DIRECTORY = new File("sdcard", "Indoloc");

    private Context context;

    public FileHelper() {

    }

    public FileHelper(Context context) {
        this.context = context;
    }

    public void serializeData(Serializable data, String fileName) throws Exception {
        SerializationHelper.write(fileName, data);
    }

    public Classifier loadModel(String fileName) throws Exception {
        return (Classifier) SerializationHelper.read(fileName);
    }

    public void saveArffToExternalStorage(Instances data, String fileName) throws IOException {
        if (isExternalStorageWritable()) {
            if (!EXTERNAL_DIRECTORY.exists())
                EXTERNAL_DIRECTORY.mkdirs();

            File file = new File(EXTERNAL_DIRECTORY, fileName);

            saveArff(data, file);
        } else
            alert("External Storage is not writable");
    }

    public Instances loadArffFromExternalStorage(String fileName) throws Exception {
        if (isExternalStorageReadable()) {
            File file = new File(EXTERNAL_DIRECTORY, fileName);
            return loadArff(file.getAbsolutePath());
        } else {
            alert("External Storage is not readable");
        }
        return null;
    }

    public void saveArffToInternalStorage(Instances data, String fileName) throws IOException {
        saveArff(data, new File(context.getFilesDir() + "/" + fileName));
    }


    public Instances loadArffFromInternalStorage(String fileName) throws Exception {
        return loadArff(context.getFilesDir() + "/" + fileName);
    }

    public Instances loadArffFromAssets(String fileName) throws Exception {
        Instances data = new ConverterUtils.DataSource(context.getAssets().open(fileName)).getDataSet();
        data.setClassIndex(0);
        return data;
    }

    private void saveArff(Instances data, File file) throws IOException {
        ArffSaver saver = new ArffSaver();
        saver.setInstances(data);
        saver.setFile(file);
        saver.writeBatch();
    }

    public Instances loadArff(String filePath) throws Exception {
        Instances data = new ConverterUtils.DataSource(filePath).getDataSet();
        data.setClassIndex(0);
        return data;
    }

    public void saveDataPoints(Serializable data) {
        if (isExternalStorageWritable()) {
            if (!EXTERNAL_DIRECTORY.exists())
                EXTERNAL_DIRECTORY.mkdirs();
            String filePath = EXTERNAL_DIRECTORY + "/dataPoints.tmp";
            try {
                SerializationHelper.write(filePath, data);
                alert("Saved collected DataPoints");
            } catch (Exception e) {
                alert("Could not save DataPoints");
                e.printStackTrace();
            }
        } else
            alert("External Storage is not writable");
    }

    public ArrayList<DataPoint> loadDataPoints() {
        if (isExternalStorageReadable()) {
            String filePath = EXTERNAL_DIRECTORY + "/dataPoints.tmp";
            if (new File(filePath).exists()) {
                try {
                    ArrayList<DataPoint> dataPoints = (ArrayList<DataPoint>) SerializationHelper.read(filePath);
                    if (dataPoints.isEmpty())
                        alert("No DataPoints collected yet");
                    else
                        alert("Loaded collected DataPoints");
                    new File(filePath).delete();
                    return dataPoints;
                } catch (Exception e) {
                    alert("Could not load DataPoints");
                    e.printStackTrace();
                }
            }
        } else {
            alert("External Storage is not readable");
        }
        alert("No DataPoints collected yet");
        return new ArrayList<>();
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public void alert(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


}
