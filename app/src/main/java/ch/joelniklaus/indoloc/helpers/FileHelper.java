package ch.joelniklaus.indoloc.helpers;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import ch.joelniklaus.indoloc.models.DataPoint;
import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils;

/**
 * Reads and writes data to files on the phone.
 *
 * Created by joelniklaus on 19.11.16.
 */
public class FileHelper extends AbstractHelper {

    private static final File EXTERNAL_DIRECTORY = new File("sdcard", "Indoloc");

    /**
     * Plain Constructor used for Testing
     */
    public FileHelper() {

    }

    /**
     * Constructs FileHelper using the Super-Constructor
     *
     * @param context
     */
    public FileHelper(Context context) {
        super(context);
    }

    /**
     * Serializes data, e.g. a classifier to a file with a given name.
     *
     * @param data
     * @param fileName
     * @throws Exception
     */
    public void serializeData(Serializable data, String fileName) throws Exception {
        SerializationHelper.write(fileName, data);
    }

    /**
     * Loads serialized data from a file with a given name and casts it to a classifier.
     *
     * @param fileName
     * @return
     * @throws Exception
     */
    public Classifier loadModel(String fileName) throws Exception {
        return (Classifier) SerializationHelper.read(fileName);
    }

    /**
     * Saves instances to an arff file with a given name in the external storage of the phone (sd card).
     *
     * @param data
     * @param fileName
     * @throws IOException
     */
    public void saveArffToExternalStorage(Instances data, String fileName) throws IOException {
        if (isExternalStorageWritable()) {
            if (!EXTERNAL_DIRECTORY.exists())
                EXTERNAL_DIRECTORY.mkdirs();

            File file = new File(EXTERNAL_DIRECTORY, fileName);

            saveArff(data, file);
        } else
            alert("External Storage is not writable");
    }

    /**
     * Loads Instances from an arff file with a given name from the external storage of the phone (sd card).
     *
     * @param fileName
     * @return
     * @throws Exception
     */
    public Instances loadArffFromExternalStorage(String fileName) throws Exception {
        if (isExternalStorageReadable()) {
            File file = new File(EXTERNAL_DIRECTORY, fileName);
            return loadArff(file.getAbsolutePath());
        } else {
            alert("External Storage is not readable");
        }
        return null;
    }

    /**
     * Saves instances to an arff file with a given name in the internal storage of the phone.
     *
     * @param data
     * @param fileName
     * @throws IOException
     */
    public void saveArffToInternalStorage(Instances data, String fileName) throws IOException {
        saveArff(data, new File(context.getFilesDir() + "/" + fileName));
    }

    /**
     * Loads Instances from an arff file with a given name from the internal storage of the phone.
     *
     * @param fileName
     * @return
     * @throws Exception
     */
    public Instances loadArffFromInternalStorage(String fileName) throws Exception {
        return loadArff(context.getFilesDir() + "/" + fileName);
    }

    /**
     * Loads Instances from an arff file with a given name from the assets of the phone (readonly storage inside the application).
     *
     * @param fileName
     * @return
     * @throws Exception
     */
    public Instances loadArffFromAssets(String fileName) throws Exception {
        Instances data = new ConverterUtils.DataSource(context.getAssets().open(fileName)).getDataSet();
        data.setClassIndex(0);
        return data;
    }

    /**
     * Saves Instances to an arff file
     *
     * @param data
     * @param file
     * @throws IOException
     */
    private void saveArff(Instances data, File file) throws IOException {
        /* ArffSaver apparently does not work in new version of gradle, android studio or whatever
        ArffSaver saver = new ArffSaver();
        saver.setInstances(data);
        saver.setFile(file);
        saver.writeBatch();
        */

        BufferedWriter writer = new BufferedWriter(new FileWriter(file.getAbsolutePath()));
        writer.write(data.toString());
        writer.flush();
        writer.close();
    }

    /**
     * Loads instances from an arff file.
     *
     * @param filePath
     * @return
     * @throws Exception
     */
    public Instances loadArff(String filePath) throws Exception {
        Instances data = new ConverterUtils.DataSource(filePath).getDataSet();
        data.setClassIndex(0);
        return data;
    }

    /**
     * Saves serializable data to the external storage of the phone (sd card).
     *
     * @param data
     */
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

    /**
     * Loads the saved data points from the external storage of the phone (sd card) again.
     *
     * @return
     */
    public ArrayList<DataPoint> loadDataPoints() {
        if (isExternalStorageReadable()) {
            String filePath = EXTERNAL_DIRECTORY + "/dataPoints.tmp";
            if (new File(filePath).exists()) try {
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
        } else {
            alert("External Storage is not readable");
        }
        alert("No DataPoints collected yet");
        return new ArrayList<>();
    }

    /**
     * Checks if external storage is available for read and write
     *
     * @return
     */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * Checks if external storage is available to at least read
     *
     * @return
     */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }
}
