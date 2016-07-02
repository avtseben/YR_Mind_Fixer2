package ru.alexandertsebenko.yr_mind_fixer.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.UUID;

import ru.alexandertsebenko.yr_mind_fixer.R;
import ru.alexandertsebenko.yr_mind_fixer.db.NoteDataSource;
import ru.alexandertsebenko.yr_mind_fixer.ui.activity.AllNotesListActivity;
import ru.alexandertsebenko.yr_mind_fixer.ui.activity.NoteActivity;


public class RecordSoundFragment extends DialogFragment  implements DialogInterface.OnClickListener {

    Uri uri = null;
    boolean recordStarted = false;
    MediaRecorder recorder = new MediaRecorder();
    NoteDataSource datasource;
    String LOG_TAG = "FragmentLog";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        startSoundRecord();
        setRetainInstance(true);
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setTitle("Идет запись. Говорите ну!")
                .setPositiveButton(R.string.record_dialog_positive, this)
                .setNegativeButton(R.string.record_dialog_negative, this)
                .setMessage("Для остановки и сохранения нажмите ОК, если нехотите сохранять, нажмите Отмена");
        return adb.create();
    }

    public void onClick(DialogInterface dialog, int which) {
        int i = 0;
        switch (which) {
            case Dialog.BUTTON_POSITIVE:
                i = R.string.record_dialog_positive;
                stopAndSaveRecord();
                break;
            case Dialog.BUTTON_NEGATIVE:
                i = R.string.record_dialog_negative;
                cancelAndDeleteRecord();
                break;
        }
        if (i > 0)
            Log.d(LOG_TAG, "Dialog 2: " + getResources().getString(i));
    }
    public void startSoundRecord() {
        if (!recordStarted) {
            recordStarted = true;
            try {
                releaseRecorder();
                String randomFileName = UUID.randomUUID().toString();
//                randomFileName = new StringBuffer(randomFileName).append(".3gpp").toString();//Заменил из за warninga
                randomFileName = randomFileName + "." + AllNotesListActivity.SOUND_FILE_FORMAT;
                uri = AllNotesListActivity.prepareFileUri(AllNotesListActivity.AUDIO_SUB_DIRECTORY, randomFileName);//получаем uri, оно нужно нам для ссылки из БД
                recorder = new MediaRecorder();
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                recorder.setOutputFile(uri.getPath());
                recorder.prepare();
                recorder.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /*
    * stopAndSaveRecord() останавливает запись в файл, сохраняет ссылку на файл в БД
    * и обновляет список в активности
    * */
    private void stopAndSaveRecord(){
      if (recorder != null) {
          try {
              recorder.stop();
              datasource = new NoteDataSource(getActivity().getApplicationContext());
              datasource.open();
              datasource.createTextNote(uri.toString(), null, AllNotesListActivity.NOTE_TYPE_AUDIO, System.currentTimeMillis());
              ((AllNotesListActivity)getActivity()).makeAdapter();
              Log.d(LOG_TAG, "record OK");
              datasource.close();
          } catch (Exception e) {
              e.printStackTrace();
          }
          recordStarted = false;
      }
    }
    /*
    * cancelAndDeleteRecord() обновляет recorder и удаляем звуковой файл
    * */
    private void cancelAndDeleteRecord(){
        releaseRecorder();
        NoteActivity.deleteFileByURI(uri);
        Log.d(LOG_TAG, "record Canceled");
    }
    private void releaseRecorder() {
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }
    }
    //Запрет диалогу уничтожаться при псмене ориентации
    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }
}
