package ru.alexandertsebenko.yr_mind_fixer.util;

import android.support.v7.app.AppCompatActivity;

import java.text.DateFormat;
import java.util.Date;
import ru.alexandertsebenko.yr_mind_fixer.R;

public class DateBuilder extends AppCompatActivity{

    private final long MINUTE_IN_SECS = 60;
    private final long HOUR_IN_SECS = 3600;

    public String timeTitleBuilder(long noteCreationTime) {
        long currentTime = System.currentTimeMillis();
        if(currentTime >= noteCreationTime) {
            long timeDeltaInSecs = Math.round((currentTime - noteCreationTime) / 1000);
            if (timeDeltaInSecs >= HOUR_IN_SECS * 24) {
                Date simpleDate = new Date(noteCreationTime);
                String dateFormat = DateFormat.getInstance().format(simpleDate);
                return dateFormat;
            } else if (timeDeltaInSecs >= HOUR_IN_SECS) {
                return Math.round(timeDeltaInSecs / HOUR_IN_SECS) + getString(R.string.hour_ago);
            } else if (timeDeltaInSecs >= MINUTE_IN_SECS) {
                return Math.round(timeDeltaInSecs / MINUTE_IN_SECS) + getString(R.string.minutes_ago);
            }
            return timeDeltaInSecs + getString(R.string.seconds_ago);
        }
        return getString(R.string.note_made_in_future_humor);
    }
}
