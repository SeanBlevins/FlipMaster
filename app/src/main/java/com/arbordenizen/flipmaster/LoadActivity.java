package com.arbordenizen.flipmaster;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FilenameFilter;

public class LoadActivity extends AppCompatActivity {

    public ViewGroup flipBtnContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        File flipMasterDir = FileOperations.flipMasterDir;

        flipBtnContainer = (ViewGroup) findViewById(R.id.linearLayout);
        TextView topText = (TextView) findViewById(R.id.topText);

        if (FileOperations.flipMasterFolderExists()) {
            //Get a list of files ending with .flip in the FlipMaster folder
            File[] flipFiles = flipMasterDir.listFiles(new FilenameFilter() {
                public boolean accept(File flipMasterDir, String name) {
                    return name.toLowerCase().endsWith(".flip");
                }
            });

            //There are some valid files in the directory, create a button for each
            if (flipFiles.length > 0) {
                topText.append("Select .flip File\n");
                for (File flipFile: flipFiles) {
                    createPackButton(flipFile);
                }
            } else {
                topText.append("No .flip files in " + flipMasterDir.getAbsolutePath() + "\n");
            }
        } else {
            topText.append("Failed to read FlipMaster directory.\n");
        }
    }

    //Create button for .flip file and add onclick listener to record which file is selected by user
    private void createPackButton(File flipFile) {
        final Button bt = new Button(this);
        String fileName = flipFile.getName();
        bt.setText(fileName);

        bt.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set selected file
                SharedPreferences settings = getSharedPreferences(FileOperations.PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("selectedFile", bt.getText().toString());

                // Commit shared preference changes
                editor.commit();

                //Return intent with RESULT_OK
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        //Add created button to the scroll view container
        flipBtnContainer.addView(bt);
    }

}
