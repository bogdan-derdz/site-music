package umcs.derdz.music;

import umcs.derdz.database.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public record Song(String artist, String title, int duration) {
    public static class Persistence {
        public static Optional<Song> read(int index) {
            String sql = "SELECT * FROM song WHERE id = ?";

            try {
                PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql);
                statement.setInt(1, index);

                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    String artist = resultSet.getString(2);
                    String title = resultSet.getString(3);
                    int duration = resultSet.getInt(4);

                    return Optional.of(new Song(artist, title, duration));
                }

                return Optional.empty();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
