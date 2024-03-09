package Sites;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;

/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *<pre>
 * Class        CreateSitesDB.java
 * Description  A class used to create the Sites DB
 * Platform     jdk 1.8.0_241; wldehPC Windows 10
 * Date         10/30/2023
 * History Log
 * @author	    <i>wldeh</i>
 * @version 	  %1% %2%
 * @see     	  javax.swing.JFrame
 * @see         java.awt.Toolkit
 *</pre>
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
public class CreateSitesDB implements MySQLConnection {

  private static final String SITES_TEXT_FILE = "src/Data/Sites.txt";
  private static final ArrayList<Site> sites = new ArrayList<Site>();
  private Site mySite = new Site();

  /**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   *<pre>
   * Method       main()
   * Description  Reads data from external text file, creates the Sites
   *              table
   * @param       args are the command line strings
   * @author      <i>wldeh</i>
   * Date         10/30/2023
   * History Log
   *</pre>
   *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
  public static void main(String[] args) {
    try {
      readFromTextFile(SITES_TEXT_FILE);
      String url = DB_URL;
      String user = USER;
      String password = PASS;
      Connection con = DriverManager.getConnection(url, user, password);
      Statement stmt = con.createStatement(
        ResultSet.TYPE_SCROLL_INSENSITIVE,
        ResultSet.CONCUR_READ_ONLY
      );
      DatabaseMetaData dbm = con.getMetaData();
      ResultSet table;

      table = dbm.getTables(null, null, "VacationSites", null);
      if (table.next()) {
        stmt.executeUpdate("DROP TABLE VacationSites");
      }

      stmt.executeUpdate(
        "CREATE TABLE VacationSites (siteID" +
        " SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT, name" +
        " VARCHAR(50), country VARCHAR(50), population FLOAT," +
        " capital VARCHAR(30), area FLOAT," +
        " PRIMARY KEY (siteID))"
      );

      for (Site site : sites) {
        stmt.executeUpdate(
          "INSERT INTO VacationSites VALUES(" +
          site.getID() +
          "," +
          "'" +
          site.getName() +
          "'," +
          "'" +
          site.getCountry() +
          "'," +
          site.getPopulation() +
          "," +
          "'" +
          site.getCapital() +
          "'" +
          "," +
          site.getArea() +
          ")"
        );
      }
      stmt.close();
    } catch (SQLException e) {
      JOptionPane.showMessageDialog(
        null,
        "SQL error",
        "SQL Error!",
        JOptionPane.ERROR_MESSAGE
      );
    }
  }

  /**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   *<pre>
   * Method       readFromTextFile()
   * Description  Reads text file and creates an arraylist with persons.
   * Date:        10/30/2023
   * @author      <i>wldeh</i>
   * @param       textFile String
   *</pre>
   *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
  private static void readFromTextFile(String textFile) {
    try {
      FileReader freader = new FileReader(textFile);
      BufferedReader input = new BufferedReader(freader);
      String line = input.readLine();

      while (line != null) {
        Site tempSite = new Site();
        StringTokenizer token = new StringTokenizer(line, ",");
        while (token.hasMoreElements()) {
          tempSite.setName(token.nextToken());
          tempSite.setCountry(token.nextToken());
          tempSite.setPopulation(Float.parseFloat(token.nextToken()));
          tempSite.setCapital(token.nextToken());
          tempSite.setArea(Float.parseFloat(token.nextToken()));
        }
        sites.add(tempSite);
        line = input.readLine();
      }
      input.close();
    } catch (Exception e) {
      JOptionPane.showMessageDialog(
        null,
        "Error reading file.",
        "File Read Error!",
        JOptionPane.ERROR_MESSAGE
      );
    }
  }
}
