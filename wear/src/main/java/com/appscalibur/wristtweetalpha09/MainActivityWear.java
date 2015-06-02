package com.appscalibur.wristtweetalpha09;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.Set;

public class MainActivityWear extends Activity {

    private TextView mTextView;

    //Retrieve the nodes with the required capabilities
    private static final String
            VOICE_TRANSCRIPTION_CAPABILITY_NAME = "voice_transcription";

    private GoogleApiClient mGoogleApiClient;

    private void setupVoiceTranscription() {
        CapabilityApi.GetCapabilityResult result = Wearable.CapabilityApi.getCapability(mGoogleApiClient,
                VOICE_TRANSCRIPTION_CAPABILITY_NAME, CapabilityApi.FILTER_REACHABLE).await();

        updateTranscriptionCapability(result.getCapability());

        //register the listener and retrieve the results of reachable nodes with the voice_transcription capability:
        CapabilityApi.CapabilityListener capabilityListener =
                new CapabilityApi.CapabilityListener() {

                    @Override
                    public void onCapabilityChanged(CapabilityInfo capabilityInfo){
                        updateTranscriptionCapability(capabilityInfo);
                    }
                };

        Wearable.CapabilityApi.addCapabilityListener(mGoogleApiClient, capabilityListener,
                VOICE_TRANSCRIPTION_CAPABILITY_NAME);
    }

    //After detecting the capable nodes, determine where to send the message.
    private String transcriptionId = null;

    private void updateTranscriptionCapability(CapabilityInfo capabilityInfo){
        Set<Node> connectedNodes = capabilityInfo.getNodes();

        transcriptionId = pickBestNodeId(connectedNodes);
    }

    private String pickBestNodeId(Set<Node> nodes){
        String bestNodeId = null;
        //Find a nearby node or pick one arbitrarily
        for (Node node : nodes){
            if(node.isNearby()) {
                return node.getId();
            }
            bestNodeId = node.getId();
        }
        return bestNodeId;
    }

    //Deliver the message




    //VOICE RECOGNITION STUFF
    private static final int SPEECH_REQUEST_CODE = 0;
    //Create an intent that can start the speech recognizer activity
    private  void displaySpeechRecognizer(){
        Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Start the activity, the intent will be populated with the speech text
        startActivityForResult(speechIntent, SPEECH_REQUEST_CODE);
    }
    // This callback is invoked when the Speech Recognizer returns.
    // This is where you process the intent and extract the speech text from the intent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            // Do something with spokenText
            //Deliver the message


        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity_wear);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });
    }

    //Opens speech recognizer
    public void openVoiceRecognizer(View view){
        displaySpeechRecognizer();
    }

}

