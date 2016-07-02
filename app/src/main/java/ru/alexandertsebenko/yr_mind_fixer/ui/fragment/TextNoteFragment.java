package ru.alexandertsebenko.yr_mind_fixer.ui.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.alexandertsebenko.yr_mind_fixer.R;

public class TextNoteFragment extends Fragment{

    private String textOfNote;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.text_note_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        if (view != null) {
           TextView textNoteView = (TextView) view.findViewById(R.id.note_text_in_note_fragment);
            textNoteView.setText(textOfNote);
        }
    }
    public void setTextOfNote(String textOfNote) {
        this.textOfNote = textOfNote;
    }


}
