package ru.alexandertsebenko.yr_mind_fixer.ui.fragment;

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

public class ImageFragment extends Fragment {
    private File imageFile;
    ImageView imageView;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_foto_note, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
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
    public void setFileByStringUri(String uri) {
        imageFile = new File(URI.create(uri));
    }
}
