package Sites;

/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * File          MySQLConnection.java
 * Description   An interface representing the connections constants needed for
 *               the Address Book Application.
 * Platform      jdk 19.0.2; Windows 11, PC
 
 * Hours         3 hours
 * Date          10/31/2023
 * History Log
 * @author       <i>wldeh</i>
 * @version      %1% %3%
 * @see          javax.swing.JFrame
 * @see          java.awt.Toolkit
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
public interface MySQLConnection {
  public static final String DB_URL = "jdbc:mysql://localhost:3306/sites";
  public static final String USER = "root";
  public static final String PASS = "REDACTED";
  public static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";
}
