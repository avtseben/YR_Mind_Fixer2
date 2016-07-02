package ru.alexandertsebenko.yr_mind_fixer.ui.fragment;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import ru.alexandertsebenko.yr_mind_fixer.R;
import ru.alexandertsebenko.yr_mind_fixer.util.Log_YR;

public class PlaySoundFragment extends Fragment implements View.OnClickListener{

    private File imageFile;
    private MediaPlayer mediaPlayer;
    private AudioManager am;
    private Button mPauseButton;
    private Button mPlayButton;
    private Button mStopButton;
    private Button mBackButton;
    private Button mForwarButton;
    private Log_YR log;
    private View view;
    private boolean mCanWePlay;


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        log = new Log_YR("Play_Sound_Fragment");
        log.v("PlaySoundFragment createView called");
        setRetainInstance(true);//На воспроизведение звука не будет влиять изменение ориентации и уход в фон
        return inflater.inflate(R.layout.fragment_sound_play,container,false);
    }

    @Override
    public void onStart() {
        super.onStart();
        view = getView();
        if(view != null) {
            mPauseButton = (Button)view.findViewById(R.id.btn_pause);
            mPlayButton = (Button)view.findViewById(R.id.btn_play);
            mStopButton = (Button)view.findViewById(R.id.btn_stop);
            mBackButton = (Button)view.findViewById(R.id.btn_back);
            mForwarButton = (Button)view.findViewById(R.id.btn_forward);
            mPauseButton.setOnClickListener(this);
            mPlayButton.setOnClickListener(this);
            mStopButton.setOnClickListener(this);
            mBackButton.setOnClickListener(this);
            mForwarButton.setOnClickListener(this);

            am = (AudioManager)view.getContext().getSystemService(Context.AUDIO_SERVICE);
        }
    }
    public void setMediaPlayerSource(String uri){
        releaseMP();
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(Uri.parse(uri).getPath());
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.prepare();
                log.v("MediaPlayer prepared to play file from Uri: " + uri);
            } catch (Exception e) {
                System.out.println("Can not play, maybe file don't exist");
            }
    }
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_pause:
                log.v("Pause button pressed");
                if (mediaPlayer.isPlaying())
                    mediaPlayer.pause();
                break;
            case R.id.btn_play:
                log.v("Play button pressed");
                mediaPlayer.start();
                break;
            case R.id.btn_stop:
                log.v("Stop button pressed");
                mediaPlayer.stop();
                break;
            case R.id.btn_back:
                log.v("Back button pressed");
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 3000);
                break;
            case R.id.btn_forward:
                log.v("Forward button pressed");
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 3000);
                break;
        }
    }
    private void releaseMP() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
                mediaPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onDestroy() {
        log.v("PlaySoundFragment destroyed");
        super.onDestroy();
        releaseMP();
    }
}
