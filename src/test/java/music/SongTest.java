package music;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.MethodSource;
import umcs.derdz.database.DatabaseConnection;
import umcs.derdz.music.Song;

import java.util.Optional;
import java.util.stream.Stream;

public class SongTest {
    @BeforeAll
    public static void connectDatabase() {
        DatabaseConnection.connect("./songs.db");
    }

    @AfterAll
    public static void disconnectDatabase() {
        DatabaseConnection.disconnect();
    }

    @Test
    public void persistenceReadCorrectIndex() {
        int index = 4;
        Optional<Song> song = Song.Persistence.read(index);

        Song dbSong = new Song("Bob Dylan", "Like a Rolling Stone", 373);

        Assertions.assertEquals(song.get(), dbSong);
    }

    @Test
    public void persistenceReadIncorrectIndex() {
        int index = 100;
        Optional<Song> song = Song.Persistence.read(index);

        Assertions.assertTrue(song.isEmpty());
    }

    private static Stream<Arguments> streamSongs() {
        return Stream.of(
                Arguments.arguments(3, "Led Zeppelin", "Stairway to Heaven", 482),
                Arguments.arguments(10, "The Who", "My Generation", 198),
                Arguments.arguments(13, "The Supremes", "Stop! In the Name of Love", 174),
                Arguments.arguments(16, "The Jackson 5", "I Want You Back", 180)
        );
    }

    @ParameterizedTest
    @MethodSource("streamSongs")
    public void parameterizedReadSongs(int id, String artist, String title, int duration) {
        Song song = new Song(artist, title, duration);

        Optional<Song> sqlSong = Song.Persistence.read(id);

        Assertions.assertEquals(sqlSong.get(), song);
    }

    @ParameterizedTest
    @CsvFileSource(files = "./songs.csv", numLinesToSkip = 1)
    public void parameterizedCSVReadSongs(int id, String artist, String title, int duration) {
        Song song = new Song(artist, title, duration);

        Optional<Song> sqlSong = Song.Persistence.read(id);

        Assertions.assertEquals(sqlSong.get(), song);
    }
}
