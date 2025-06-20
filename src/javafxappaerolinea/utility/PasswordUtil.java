package javafxappaerolinea.utility;

import org.mindrot.jbcrypt.BCrypt;


public class PasswordUtil {
    
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean verifyPassword(String password, String storedHash) {
        return BCrypt.checkpw(password, storedHash);
    }

}
