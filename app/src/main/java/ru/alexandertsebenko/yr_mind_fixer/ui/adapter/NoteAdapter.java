package ru.alexandertsebenko.yr_mind_fixer.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ru.alexandertsebenko.yr_mind_fixer.R;
import ru.alexandertsebenko.yr_mind_fixer.datamodel.Note;
import ru.alexandertsebenko.yr_mind_fixer.ui.activity.AllNotesListActivity;

public class NoteAdapter extends BaseAdapter {

    Context context;
    LayoutInflater layoutInflater;
    List<Note> notes;

    public NoteAdapter(Context context, List<Note> notes) {
        this.context = context;
        this.notes = notes;
        layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return notes.size();
    }
    @Override
    public Object getItem(int position) {
        return notes.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.note_raw, parent, false);
        }
        Note tn = getNote(position);
        //Если текстовая записка содержит заголовок
        TextView tv = (TextView) view.findViewById(R.id.label);
        tv.setText(tn.getTextNote());
        //Type
        switch (tn.getNoteType()) {
            case (AllNotesListActivity.NOTE_TYPE_TEXT):
                ((ImageView) view.findViewById(R.id.icon)).setImageResource(R.drawable.stickynotes);
                //Если у заметки есть заголовок то печатаем его
                if(tn.getNoteTitle() != null) {
                    tv.setText(tn.getNoteTitle());
                }
                break;
            case (AllNotesListActivity.NOTE_TYPE_AUDIO):
                ((ImageView) view.findViewById(R.id.icon)).setImageResource(R.drawable.microphone);
                break;
            case (AllNotesListActivity.NOTE_TYPE_FOTO):
                ((ImageView) view.findViewById(R.id.icon)).setImageResource(R.drawable.camera);
                break;
        }
        return view;
    }
    Note getNote(int position) {
        return ((Note) getItem(position));
    }




}
