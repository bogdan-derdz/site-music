package umcs.derdz;

import umcs.derdz.auth.Account;
import umcs.derdz.database.DatabaseConnection;

import javax.naming.AuthenticationException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        DatabaseConnection.connect("./accounts.db");
        Account.Persistence.init();

//        Account.Persistenmusicce.register("notch", "verysecurepassword");

        try {
            Account notch = Account.Persistence.authenticate("notch", "verysecurepassword");
            System.out.println(notch);
        } catch (AuthenticationException e) {
            throw new RuntimeException(e);
        }

        DatabaseConnection.disconnect();

    }
}