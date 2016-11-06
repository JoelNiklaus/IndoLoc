package ch.joelniklaus.indoloc.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import ch.joelniklaus.indoloc.R;
import ch.joelniklaus.indoloc.services.WekaService;

public class MainActivity extends AppCompatActivity {

    // instantiate WekaService with context
    private WekaService wekaService = new WekaService(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onButtonTap(View v) {
        Toast myToast = Toast.makeText(this, wekaService.testTrain(), Toast.LENGTH_LONG);
        myToast.show();
    }

    public void goToCollectData(View v) {
        startActivity(new Intent(MainActivity.this, CollectDataActivity.class));
    }
}

