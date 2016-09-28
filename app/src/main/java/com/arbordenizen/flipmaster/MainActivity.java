package com.arbordenizen.flipmaster;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private String selectedFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView infoText = (TextView) findViewById(R.id.infoTxt);

        //Check if this is the first run of the app
        SharedPreferences firstRunPref = getPreferences(0);
        boolean isFirstRun = firstRunPref.getBoolean("FIRSTRUN", true);
        if (isFirstRun)
        {
            //Write some .flip files from the raw resources to the SD card
            try {
                if (!FileOperations.writeResRawFilesToSD(this.getApplication().getApplicationContext())) {
                    infoText.setText("Failed to create flip files on SD card.");
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            //Store FIRSTRUN boolean in shared preferences
            SharedPreferences.Editor editor = firstRunPref.edit();
            editor.putBoolean("FIRSTRUN", false);
            editor.commit();
        }

        //Start load activity for result in order to ensure a file was selected
        Button loadBtn = (Button) findViewById(R.id.loadBtn);
        loadBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, LoadActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        checkPreferences();
    }

    //Check if a file is already selected from previous run or if file has been selected after returning from LoadActivity
    public void checkPreferences() {
        TextView infoText = (TextView) findViewById(R.id.infoTxt);
        Button playBtn = (Button) findViewById(R.id.playBtn);

        if (!FileOperations.isExternalStorageReadable() && !FileOperations.flipMasterFolderExists()) {
            infoText.setText("Couldn't access FlipMaster directory on SD card.");
            return;
        }

        SharedPreferences settings = getSharedPreferences(FileOperations.PREFS_NAME, 0);

        //Get selected file from shared preferences
        selectedFile = settings.getString("selectedFile", null);
        String fullFilePath = FileOperations.flipMasterDir + "/" + selectedFile;

        if (selectedFile != null && !selectedFile.isEmpty()) {
            File selFile = new File(fullFilePath);
            if (selFile.exists()) {
                //Selected file is a valid file on the SD card so we can enable the play button
                infoText.setText("Selected File : " + selectedFile);
                playBtn.setEnabled(true);
            } else {
                //Selected file not on SD card
                infoText.setText("Selected File : " + selectedFile + " not on SD card.");
            }
        } else {
            //Selected file is not set onshared preferences, user must select file using the load activity
            infoText.setText("Select file from SD card.");
        }
    }

    public void startGame(View view) {
        Intent intent = new Intent(this, MainGame.class);
        startActivity(intent);
    }

    //If returning from the LoadActivity with RESULT_OK we know a file has been selected and should now be in the shared preferences
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                checkPreferences();
            }
        }
    }
}
