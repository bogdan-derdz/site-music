package music;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import umcs.derdz.auth.Account;
import umcs.derdz.database.DatabaseConnection;
import umcs.derdz.music.ListenerAccount;
import umcs.derdz.music.NotEnoughCreditsException;
import umcs.derdz.music.Playlist;
import umcs.derdz.music.Song;

import javax.naming.AuthenticationException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ListenerAccountTest {
    @BeforeAll
    public static void connectWithDatabase() throws SQLException {
        DatabaseConnection.connect("./songs.db");
        ListenerAccount.init();
    }

    @AfterAll
    public static void disconnectWithDatabase() {
        DatabaseConnection.disconnect();
    }

    @Test
    public void registerAccount() {
        int id = Account.Persistence.register(
                "Lord universe", "5d98gvb9d");
        Account account = new ListenerAccount(id, "Lord universe");
        assertEquals(account.getId(), id);
    }

    @Test
    public void authenticateAccount() throws AuthenticationException {
        Account listenerAccount = new ListenerAccount(1, "Wild beast");
        Account account = Account.Persistence.authenticate(
                "Wild beast", "nj430sp4");
        assertEquals(listenerAccount.getId(), account.getId());
    }

    @Test
    public void methodGetCreditEmpty() throws SQLException {
        ListenerAccount listenerAccount = new ListenerAccount(
                3, "Lord universe");
        int credit = listenerAccount.getCredits();
        assertEquals(0, credit);
    }

    @Test
    public void methodAddCredit() throws SQLException {
        ListenerAccount listenerAccount = new ListenerAccount(
                2, "Classical spice");
        listenerAccount.addCredits(6);
        assertEquals(6, listenerAccount.getCredits());
    }

    @Test
    public void methodBuySongWhenSongIsInAccount() throws NotEnoughCreditsException, SQLException {
        int id = 9;//Account.Persistence.register("Super Frank", "jks56sbk23l");
        ListenerAccount listenerAccount = new ListenerAccount(id, "Super Frank");
        listenerAccount.addCredits(4);
        listenerAccount.addSong(4);
        listenerAccount.buySong(9);
        assertEquals(4, listenerAccount.getCredits());
    }

    @Test
    public void methodBuySongWhenNOSongInAccount() throws NotEnoughCreditsException, SQLException {
        int id = Account.Persistence.register("King octopus", "jks56sbk23l");
        ListenerAccount listenerAccount = new ListenerAccount(id, "King octopus");
        listenerAccount.addCredits(4);
        listenerAccount.buySong(9);
        assertEquals(3, listenerAccount.getCredits());
    }

    @Test
    public void methodBuySongWhenNOSongInAccountAndCreditNull() {
        int id = Account.Persistence.register("Octavian", "sk23ni492");
        ListenerAccount listenerAccount = new ListenerAccount(id, "Octavian");
        assertThrows(NotEnoughCreditsException.class, () -> listenerAccount.buySong(9));
    }

    @Test
    public void createPlaylist() throws NotEnoughCreditsException, SQLException, NotEnoughCreditsException {
        Song song1 = new Song("James Brown", "I Got You (I Feel Good)", 167);
        Song song2 = new Song("The Who", "My Generation", 198);
        Song song3 = new Song("The Rolling Stones", "Paint It Black", 224);
        Song song4 = new Song("The Beach Boys", "Good Vibrations", 215);
        Playlist manuallyPlaylist = new Playlist();
        manuallyPlaylist.add(song1);
        manuallyPlaylist.add(song2);
        manuallyPlaylist.add(song3);
        manuallyPlaylist.add(song4);

        int id = Account.Persistence.register("Darling", "love42");
        ListenerAccount listenerAccount = new ListenerAccount(id, "darling");
        listenerAccount.addCredits(10);

        List<Integer> listIdSongs = new ArrayList<>();
        listIdSongs.add(39);
        listIdSongs.add(10);
        listIdSongs.add(24);
        listIdSongs.add(8);

        Playlist playlist = listenerAccount.createPlaylist(listIdSongs);
        assertEquals(manuallyPlaylist, playlist);
    }
}