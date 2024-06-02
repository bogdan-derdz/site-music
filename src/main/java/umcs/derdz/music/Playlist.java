package umcs.derdz.music;

import java.util.ArrayList;

public class Playlist extends ArrayList<Song> {
    public Song atSecond(int seconds) {
        if (seconds < 0) throw new IndexOutOfBoundsException("Negative time were given");

        for (Song song : this) {
            seconds -= song.duration();
            
            if (seconds <= 0) return song;
        }

        throw new IndexOutOfBoundsException("Time out of playlist");
    }
}
