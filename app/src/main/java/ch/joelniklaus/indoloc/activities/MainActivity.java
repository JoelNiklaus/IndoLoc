package ch.joelniklaus.indoloc.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import ch.joelniklaus.indoloc.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goToCollectData(View v) {
        startActivity(new Intent(MainActivity.this, CollectDataActivity.class));
    }

    public void alert(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

