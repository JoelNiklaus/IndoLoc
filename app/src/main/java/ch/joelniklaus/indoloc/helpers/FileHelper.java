package ch.joelniklaus.indoloc.helpers;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

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

    public void saveModel(Classifier classifier, String fileName) throws Exception {
        SerializationHelper.write(fileName, classifier);
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
