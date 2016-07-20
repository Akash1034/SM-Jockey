package com.app.smjockey.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.app.smjockey.Models.Streams;
import com.app.smjockey.R;
import com.app.smjockey.Utils.Constants;
import com.firebase.client.Firebase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Akash Srivastava on 20-07-2016.
 */
public class AnnouncementFragment extends Fragment {


    private String TAG=com.app.smjockey.Fragments.AnnouncementFragment.class.getSimpleName();
    private EditText textEditText;
    private EditText imageEditText;
    private EditText durationEditText;
    private Button pushButton;

    String text;
    String image;
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
        View view= inflater.inflate(R.layout.announcement_fragment, container, false);

         textEditText=(EditText)view.findViewById(R.id.message_editText);
        imageEditText=(EditText)view.findViewById(R.id.URL_editText);
        durationEditText = (EditText) view.findViewById( R.id.time_editText );
        pushButton = (Button) view.findViewById(R.id.push_button);




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

}
