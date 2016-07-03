package ru.alexandertsebenko.yr_mind_fixer.ui.fragment;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.net.URI;

import ru.alexandertsebenko.yr_mind_fixer.R;
import ru.alexandertsebenko.yr_mind_fixer.util.Log_YR;

public class ImageFragment extends Fragment {
    public File imageFile;
    ImageView imageView;
    Log_YR log = new Log_YR("Image Fragment log");

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        log.v("onAttach()");
        log.v("File object " + imageFile);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log.v("onCreate()");
        log.v("File object " + imageFile);
    }
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        log.v("onCreateView()");
        log.v("File object " + imageFile);
        return inflater.inflate(R.layout.fragment_foto_note, container, false);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        log.v("onActivityCreated()");
        log.v("File object " + imageFile);
    }


    @Override
    public void onStart() {
        super.onStart();
        log.v("onStart()");
        log.v("File object " + imageFile);
        View view = getView();
            if (view != null && imageView == null) {
            imageView = (ImageView)view.findViewById(R.id.image_view_in_foto_note_fragment);
            //Загрузить из файла картинку
            if(imageFile.exists()){
                Bitmap bm = BitmapFactory.decodeFile(imageFile.getAbsolutePath());//TODO проблема с поворотом когда рисунок при повороте на 90 градусов должен перерисоваться
                imageView.setImageBitmap(bm);
            } else {
                Toast.makeText(view.getContext(), R.string.no_file_to_show_toast, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        log.v("Image Fragment destroyed");
    }
    public void setFileByStringUri(String uri) {
        imageFile = new File(URI.create(uri));
        log.v("setFileByUri: " + uri + " and File object is: " + imageFile);
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
