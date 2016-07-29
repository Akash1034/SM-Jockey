package com.app.smjockey.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.app.smjockey.Models.Streams;
import com.app.smjockey.R;
import com.app.smjockey.Utils.Constants;
import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.filepicker.Filepicker;
import io.filepicker.FilepickerCallback;
import io.filepicker.models.FPFile;

/**
 * Created by Akash Srivastava on 20-07-2016.
 */
public class AnnouncementFragment extends Fragment {


    private String TAG=com.app.smjockey.Fragments.AnnouncementFragment.class.getSimpleName();
    private EditText textEditText;
    private EditText imageEditText;
    private EditText durationEditText;
    private Button pushButton;
    private ImageButton uploadButton;

    String text;
    static String image;
    String duration;
    String created_at;



    static Bundle bundles;
    Streams streamItem;

    public AnnouncementFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Firebase.setAndroidContext(getContext());
        Filepicker.setKey("AJ7CmscxWS6O0G73HDC2Cz");
        View view= inflater.inflate(R.layout.announcement_fragment, container, false);

         textEditText=(EditText)view.findViewById(R.id.message_editText);
        imageEditText=(EditText)view.findViewById(R.id.URL_editText);
        durationEditText = (EditText) view.findViewById( R.id.time_editText );
        pushButton = (Button) view.findViewById(R.id.push_button);
        uploadButton=(ImageButton)view.findViewById(R.id.upload_button);



        pushButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text=textEditText.getText().toString();
                image=imageEditText.getText().toString();
                duration=durationEditText.getText().toString();
                Long created=System.currentTimeMillis();
                created_at=String.valueOf(created/1000);
                streamItem= (Streams) bundles.getSerializable("Stream");
                Log.d(TAG,created_at);
                Map<String,String> noticeValues=new HashMap<String, String>();
                noticeValues.put("created_at",created_at);
                noticeValues.put("duration",duration);
                noticeValues.put("image",image);
                noticeValues.put("text",text);
                if(validate())
                {
                    Firebase firebase = new Firebase(Constants.livewall_url + streamItem.getUuid()).child("notice");
                    firebase.setValue(noticeValues);


                }

            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Filepicker.class);
                String[] services = {"FACEBOOK", "CAMERA", "GALLERY"};
                intent.putExtra("services", services);
                intent.putExtra("showErrorToast", false);
                startActivityForResult(intent, Filepicker.REQUEST_CODE_GETFILE);


            }
        });

        return view;
    }

    public boolean validate()
    {
        if(duration!=null&&TextUtils.isDigitsOnly(duration))
        {
            return true;
        }
        return false;
    }

    public static void setArgument(Bundle bundle)
    {
        bundles=bundle;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Filepicker.REQUEST_CODE_GETFILE) {
            if(resultCode == Activity.RESULT_OK) {

                // Filepicker always returns array of FPFile objects
                ArrayList<FPFile> fpFiles = data.getParcelableArrayListExtra(Filepicker.FPFILES_EXTRA);

                // Option multiple was not set so only 1 object is expected
                FPFile file = fpFiles.get(0);
                Log.d(TAG,"image:"+file.getFilename());

                final String path =file.getLocalPath();
                Log.d(TAG,path);
                final String url = path;

                Filepicker.uploadLocalFile(Uri.parse(url), getActivity(), new FilepickerCallback() {
                    @Override
                    public void onFileUploadSuccess(FPFile fpFile) {
                        // Do something on success
                        Log.d(TAG,"success"+fpFile.getUrl());
                        image=fpFile.getUrl();
                        imageEditText.setText(image);
                        Map<String,String> noticeValues=new HashMap<String, String>();
                        noticeValues.put("created_at",created_at);
                        noticeValues.put("duration",duration);
                        noticeValues.put("image",image);
                        noticeValues.put("text",text);
                        if(validate())
                        {
                            Firebase firebase = new Firebase(Constants.livewall_url + streamItem.getUuid()).child("notice");
                            firebase.setValue(noticeValues);


                        }
                    }

                    @Override
                    public void onFileUploadError(Throwable error) {
                        // Do something on error
                        Log.d(TAG,"error:"+error.toString());
                    }

                    @Override
                    public void onFileUploadProgress(Uri uri, float progress) {
                        // Do something on progress
                    }
                });

                // Do something cool with the result
            } else {
                // Handle errors here
            }

        }
    }
}


