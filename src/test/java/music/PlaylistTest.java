package music;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import umcs.derdz.music.Playlist;
import umcs.derdz.music.Song;

public class PlaylistTest {
    @Test
    public void playlistEmpty() {
        Playlist playlist = new Playlist();

        Assertions.assertTrue(playlist.isEmpty());
    }

    @Test
    public void playlistSize() {
        Playlist playlist = new Playlist();
        playlist.add(new Song("test", "test", 0));
        int size = playlist.size();

        Assertions.assertEquals(1, size);
    }

    @Test
    public void sameSongAdded() {
        Playlist playlist = new Playlist();
        Song song = new Song("test", "test", 0);
        playlist.add(song);

        Assertions.assertEquals(song, playlist.get(0));
    }

    @Test
    public void atSecondTest() {
        Playlist playlist = new Playlist();

        Song song1 = new Song("Pink Floyd", "Wish You Were Here", 334);
        Song song2 = new Song("John Lennon", "Imagine", 183);
        Song song3 = new Song("Pink Floyd", "Comfortably Numb", 382);

        playlist.add(song1);
        playlist.add(song2);
        playlist.add(song3);

        Assertions.assertEquals(song3, playlist.atSecond(550));
    }

    public void atSecondNegativeValueException() {
        Playlist playlist = new Playlist();

        Song song1 = new Song("Pink Floyd", "Wish You Were Here", 334);
        Song song2 = new Song("John Lennon", "Imagine", 183);
        Song song3 = new Song("Pink Floyd", "Comfortably Numb", 382);

        playlist.add(song1);
        playlist.add(song2);
        playlist.add(song3);

        IndexOutOfBoundsException exception = Assertions.assertThrows(IndexOutOfBoundsException.class, () -> playlist.atSecond(-1));

        Assertions.assertEquals(exception.getMessage(), "Negative time were given");
    }

    public void atSecondTimeOutOfPlaylistException() {
        Playlist playlist = new Playlist();

        Song song1 = new Song("Pink Floyd", "Wish You Were Here", 334);
        Song song2 = new Song("John Lennon", "Imagine", 183);
        Song song3 = new Song("Pink Floyd", "Comfortably Numb", 382);

        playlist.add(song1);
        playlist.add(song2);
        playlist.add(song3);

        IndexOutOfBoundsException exception = Assertions.assertThrows(IndexOutOfBoundsException.class, () -> playlist.atSecond(1000));

        Assertions.assertEquals(exception.getMessage(), "Time out of playlist");
    }
}
