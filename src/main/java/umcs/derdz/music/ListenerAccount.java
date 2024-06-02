package umcs.derdz.music;

import umcs.derdz.auth.Account;
import umcs.derdz.database.DatabaseConnection;

import javax.naming.AuthenticationException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

public class ListenerAccount extends Account {
    public ListenerAccount(int id, String name) {
        super(id, name);
    }


    public static void init() throws SQLException {
        Account.Persistence.init();
        {
            String sql = "CREATE TABLE IF NOT EXISTS listener_account( " +
                    "id_account INTEGER NOT NULL PRIMARY KEY," +
                    "credits INTEGER NOT NULL)";
            PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql);
            statement.executeUpdate();
        }
        {
            String sql = "CREATE TABLE IF NOT EXISTS owned_songs( " +
                    "id_account INTEGER NOT NULL," +
                    "id_song INTEGER NOT NULL," +
                    "PRIMARY KEY (id_account, id_song))";
            PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql);
            statement.executeUpdate();
        }
    }

    public static int register(String username, String password) throws SQLException {
        try {
            int id = Account.Persistence.register(username, password);
            String sql = "INSERT INTO listener_account(id_account, credits) VALUES (?, 0)";
            PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql);
            statement.setInt(1, id);
            statement.executeUpdate();
            return id;
        } catch (SQLException | RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public int getCredits() throws SQLException {
        String sql = "SELECT credits FROM listener_account WHERE id_account = ?";
        PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql);
        statement.setInt(1, this.id);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt("credits");
        } else throw new SQLException();
    }

    public void addCredits(int amount) throws SQLException {
        int currentCredits = getCredits();
        String sql = "UPDATE listener_account SET credits = ? WHERE id_account = ?";
        PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql);
        statement.setInt(1, currentCredits + amount);
        statement.setInt(2, this.id);
        statement.executeUpdate();
    }

    public void addSong(int songId) throws SQLException {
        String sql = "INSERT INTO owned_songs VALUES(?, ?)";
        PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql);
        statement.setInt(1, this.id);
        statement.setInt(2, songId);
        statement.executeUpdate();
    }

    public boolean hasSong(int songId) throws SQLException {
        String sql = "SELECT * FROM owned_songs WHERE id_account = ? AND id_song = ?";
        PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql);
        statement.setInt(1, this.id);
        statement.setInt(2, songId);
        return statement.executeQuery().next();
    }

    ListenerAccount authenticate(String username, String password) throws AuthenticationException {
        Account account = Account.Persistence.authenticate(username, password);
        return new ListenerAccount(account.getId(), account.getUsername());
    }

    public Playlist createPlaylist(List<Integer> songIds) throws SQLException, NotEnoughCreditsException {
        Playlist playlist = new Playlist();
        for (var id : songIds) {
            if (!hasSong(id)) {
                buySong(id);
            }
            var optionalSong = Song.Persistence.read(id);
            if (optionalSong.isPresent())
                playlist.add(optionalSong.get());
            else
                throw new SQLException();
        }
        return playlist;
    }

    public void buySong(int idSong) throws NotEnoughCreditsException {
        try {
            Song song = Song.Persistence.read(idSong).get();
            String sqlAccount = "SELECT * FROM accounts WHERE id=? AND playlist=?";
            PreparedStatement statementAccount = DatabaseConnection.getConnection()
                    .prepareStatement(sqlAccount);
            statementAccount.setInt(1, this.id);
            statementAccount.setString(2, String.format(Locale.ENGLISH,
                    "{%s,%s,%s} ", song.artist(), song.title(), song.duration()));

            ResultSet resultSet2 = statementAccount.executeQuery();
            if (!resultSet2.next()) {
                if (this.getCredits() <= 0)
                    throw new NotEnoughCreditsException("Not enough credits");
                String sql = "UPDATE accounts SET playlist=? ,credit = credit-1 WHERE id=?";

                PreparedStatement statement = DatabaseConnection.getConnection()
                        .prepareStatement(sql);
                statement.setString(1, String.format(Locale.ENGLISH,
                        "%s,%s,%s", song.artist(), song.title(), song.duration()));
                statement.setInt(2, this.id);
                statement.executeUpdate();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
