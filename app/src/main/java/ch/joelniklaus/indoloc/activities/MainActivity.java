package ch.joelniklaus.indoloc.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import ch.joelniklaus.indoloc.R;
import ch.joelniklaus.indoloc.helpers.WekaHelper;

public class MainActivity extends AppCompatActivity {

    // instantiate WekaHelper with context
    private WekaHelper wekaHelper = new WekaHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onButtonTap(View v) {
        Toast.makeText(this, wekaHelper.testTrain(), Toast.LENGTH_SHORT).show();
    }

    public void goToCollectData(View v) {
        startActivity(new Intent(MainActivity.this, CollectDataActivity.class));
    }

    public void alert(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

