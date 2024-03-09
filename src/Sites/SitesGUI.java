package Sites;

import static Sites.MySQLConnection.DB_URL;
import static Sites.MySQLConnection.PASS;
import static Sites.MySQLConnection.USER;

import java.awt.Toolkit;
import java.awt.print.PrinterException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * File          SitesGUI.java
 * Description   A class representing the GUI
 * Platform      jdk 19.0.2; Windows 11, PC
 * Date          10/31/2023
 * History Log
 * @author       <i>wldeh</i>
 * @version      %1% %3%
 * @see          javax.swing.JFrame
 * @see          java.awt.Toolkit
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
public class SitesGUI extends javax.swing.JFrame {

  private String sitesFileName = "src/Data/Sites_1.txt";
  private final String SITES_TEXT_FILE = "src/Data/Sites_1.txt";
  private int currentID = 1, sizeOfDB;
  private ArrayList<Site> places = new ArrayList<Site>();

  /**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   * Constructor   SitesGUI()-default constructor
   * Description   Create an instance of the GUI form, set the default button
   *               to be submitJButton, set icon image, center form, read sites
   *               and players from external files.
   * Date          10/31/2023
   * History log
   * @author       <i>wldeh</i>
   *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
  public SitesGUI() {
    try {
      initComponents();
      this.getRootPane().setDefaultButton(addJButton);
      this.setIconImage(
          Toolkit
            .getDefaultToolkit()
            .getImage("src/Images/worldFlags_small.gif")
        );

      setLocationRelativeTo(null);

      readFromTextFile(SITES_TEXT_FILE);
      populateSitesJListOnStartup();
      createDB();

      String url = DB_URL;
      String user = USER;
      String password = PASS;

      Connection con = DriverManager.getConnection(url, user, password);
      Statement stmt = con.createStatement(
        ResultSet.TYPE_SCROLL_INSENSITIVE,
        ResultSet.CONCUR_READ_ONLY
      );

      ResultSet rs = stmt.executeQuery("SELECT count(*) FROM Sites");
      rs.next();
      sizeOfDB = rs.getInt("count(*)");

      String query = "SELECT * FROM Sites";
      rs = stmt.executeQuery(query);
      rs.next();

      String siteName = rs.getString("name");
      Site tempSite = searchSite(siteName);

      display(tempSite);
    } catch (SQLException exp) {
      exp.printStackTrace();
      // Show error message
      JOptionPane.showMessageDialog(
        null,
        "Input error -- SQL error.",
        "SQL Error!",
        JOptionPane.ERROR_MESSAGE
      );
    } catch (ClassNotFoundException ex) {
      Logger.getLogger(SitesGUI.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  /**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   * Method       populateSitesJListOnStartup()
   * Description  Populate sitesJList on startup
   * Date:        10/31/2023
   * @author      <i>wldeh</i>
   * @param       sitesFile String
   *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
  private void populateSitesJListOnStartup() {
    DefaultListModel<String> listModel = new DefaultListModel<>();

    // Establishing a connection to the database
    String url = DB_URL;
    String user = USER;
    String password = PASS;

    String query;
    if (populationJRadioButtonMenuItem.isSelected()) {
      query = "SELECT name FROM Sites ORDER BY population ASC";
    } else {
      query = "SELECT name FROM Sites ORDER BY name ASC";
    }

    try (Connection conn = DriverManager.getConnection(url, user, password)) {
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(query);

      while (rs.next()) {
        listModel.addElement(rs.getString("name"));
      }

      sitesJList.setModel(listModel);
    } catch (SQLException e) {
      JOptionPane.showMessageDialog(
        null,
        "Error fetching sites from database!",
        "Error!",
        JOptionPane.ERROR_MESSAGE
      );
    }
  }

  /**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   * Method       createDB()
   * Description  Make connection, drop existing table, and create a database
   *              table for vacation sites.
   * Date:        10/31/2023
   * @author      <i>wldeh</i>
   * @param       sitesFile String
   *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
  private void createDB() throws ClassNotFoundException {
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
      String url = DB_URL;
      String user = USER; //"root";
      String password = PASS;

      //Make connection to MySQL DB
      Connection con = DriverManager.getConnection(url, user, password);

      Statement stmt = con.createStatement(
        ResultSet.TYPE_SCROLL_INSENSITIVE,
        ResultSet.CONCUR_READ_ONLY
      );
      DatabaseMetaData dbm = con.getMetaData();
      ResultSet table;

      table = dbm.getTables(null, null, "Sites", null);
      if (table.next()) {
        return;
      }

      stmt.executeUpdate(
        "CREATE TABLE Sites (siteID" +
        " SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT, name" +
        " VARCHAR(50), country VARCHAR(50), population FLOAT," +
        " capital VARCHAR(30), area FLOAT," +
        " PRIMARY KEY (siteID))"
      );

      for (int i = 0; i < places.size(); i++) {
        stmt.executeUpdate(
          "INSERT INTO Sites(name, country, population, capital, area) VALUES(" +
          "'" +
          places.get(i).getName() +
          "'," +
          "'" +
          places.get(i).getCountry() +
          "'," +
          places.get(i).getPopulation() +
          "," +
          "'" +
          places.get(i).getCapital() +
          "'," +
          places.get(i).getArea() +
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

  /**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   *<pre>
   * Method       readFromTextFile()
   * Description  Reads text file and creates an arraylist with vacation sites.
   * Date:        10/31/2023
   * @author      <i>wldeh</i>
   * @param       textFile String
   *</pre>
   *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
  private void readFromTextFile(String textFile) {
    try {
      FileReader freader = new FileReader(textFile);
      BufferedReader input = new BufferedReader(freader);
      String line = input.readLine(); // Read the first line

      while (line != null) {
        Site tempSite = new Site();
        StringTokenizer token = new StringTokenizer(line, ",");

        tempSite.setName(token.nextToken());
        tempSite.setCountry(token.nextToken());
        tempSite.setPopulation(Float.parseFloat(token.nextToken()));
        tempSite.setCapital(token.nextToken());
        tempSite.setArea(Float.parseFloat(token.nextToken()));

        places.add(tempSite);
        line = input.readLine();
      }
      input.close();
    } catch (FileNotFoundException fnfexp) {
      JOptionPane.showMessageDialog(
        null,
        "Input error -- File not found.",
        "File Not Found Error!",
        JOptionPane.ERROR_MESSAGE
      );
    } catch (IOException | NumberFormatException exp) {
      JOptionPane.showMessageDialog(
        null,
        "Input error -- File could not be read.",
        "File Read Error!",
        JOptionPane.ERROR_MESSAGE
      );
    }
  }

  /**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   *<pre>
   * Method       searchPerson()
   * Description  Search Site in DB.
   * Date:        10/31/2023
   * @author      <i>wldeh</i>
   * @param       id iny
   * @return      myPerson Person
   *</pre>
   *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
  private Site searchSite(String siteName) {
    try {
      String url = DB_URL;
      String user = USER;
      String password = PASS;
      Connection conn = DriverManager.getConnection(url, user, password);
      Site site = new Site(); // Create a new site

      // SQL query to search for site by name
      String query = "SELECT * FROM Sites WHERE name = ?";
      PreparedStatement pstmt = conn.prepareStatement(query);
      pstmt.setString(1, siteName);
      ResultSet results = pstmt.executeQuery();

      if (results.next()) { // If a result is found
        site.setName(results.getString("name"));
        site.setCountry(results.getString("country"));
        site.setPopulation(results.getFloat("population"));
        site.setCapital(results.getString("capital"));
        site.setArea(results.getFloat("area"));
      }

      results.close(); // Close the result set
      pstmt.close(); // Close the prepared statement
      conn.close(); // Close the connection

      return site;
    } catch (SQLException exp) {
      JOptionPane.showMessageDialog(
        null,
        "Error searching database for site",
        "Search Error",
        JOptionPane.ERROR_MESSAGE
      );
      return new Site(); // Return an empty site object
    }
  }

  /**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   *<pre>
   * Method       display()
   * Description  Show information about the person passed as parameter.
   * @param       myPerson Person
   * @author      <i>wldeh</i>
   * Date         10/31/2023
   * History Log
   *</pre>
   *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
  private void display(Site site) {
    nameJTextField.setText(site.getName());
    countryJTextField.setText(site.getCountry());
    populationJTextField.setText(String.valueOf(site.getPopulation()));
    capitalJTextField.setText(site.getCapital());
    areaJTextField.setText(String.valueOf(site.getArea()));
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {
    titleJLabel = new javax.swing.JLabel();
    contentJPanel = new javax.swing.JPanel();
    nameJLabel = new javax.swing.JLabel();
    nameJTextField = new javax.swing.JTextField();
    countryJLabel = new javax.swing.JLabel();
    countryJTextField = new javax.swing.JTextField();
    populationJLabel = new javax.swing.JLabel();
    populationJTextField = new javax.swing.JTextField();
    capitalJLabel = new javax.swing.JLabel();
    capitalJTextField = new javax.swing.JTextField();
    areaJLabel = new javax.swing.JLabel();
    areaJTextField = new javax.swing.JTextField();
    controlJPanel = new javax.swing.JPanel();
    addJButton = new javax.swing.JButton();
    editJButton = new javax.swing.JButton();
    deleteJButton = new javax.swing.JButton();
    exitJButton = new javax.swing.JButton();
    sitesJPanel = new javax.swing.JPanel();
    sitesJScrollPane = new javax.swing.JScrollPane();
    sitesJList = new javax.swing.JList<>();
    sitesJMenuBar = new javax.swing.JMenuBar();
    fileJMenu = new javax.swing.JMenu();
    newJMenuItem = new javax.swing.JMenuItem();
    printSpeciesJMenuItem = new javax.swing.JMenuItem();
    printJMenuItem = new javax.swing.JMenuItem();
    fileJSeparator = new javax.swing.JPopupMenu.Separator();
    exitJMenuItem = new javax.swing.JMenuItem();
    sortJMenu = new javax.swing.JMenu();
    nameJRadioButtonMenuItem = new javax.swing.JRadioButtonMenuItem();
    populationJRadioButtonMenuItem = new javax.swing.JRadioButtonMenuItem();
    actionJMenu = new javax.swing.JMenu();
    addJMenuItem = new javax.swing.JMenuItem();
    deleteJMenuItem = new javax.swing.JMenuItem();
    editJMenuItem = new javax.swing.JMenuItem();
    searchJMenuItem = new javax.swing.JMenuItem();
    detailsJMenuItem = new javax.swing.JMenuItem();
    helpJMenu = new javax.swing.JMenu();
    aboutJMenuItem = new javax.swing.JMenuItem();

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

    titleJLabel.setFont(new java.awt.Font("Tempus Sans ITC", 2, 48)); // NOI18N
    titleJLabel.setForeground(new java.awt.Color(0, 102, 102));
    titleJLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    titleJLabel.setIcon(
      new javax.swing.ImageIcon(
        getClass().getResource("/Images/worldFlags_small.GIF")
      )
    ); // NOI18N
    titleJLabel.setText("Vacation Sites ");

    contentJPanel.setLayout(new java.awt.GridLayout(5, 2, 2, 2));

    nameJLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
    nameJLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    nameJLabel.setText("Name of Site: \n");
    contentJPanel.add(nameJLabel);

    nameJTextField.setEditable(false);
    nameJTextField.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
    contentJPanel.add(nameJTextField);

    countryJLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
    countryJLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    countryJLabel.setText("Country:\n\n ");
    contentJPanel.add(countryJLabel);

    countryJTextField.setEditable(false);
    countryJTextField.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
    contentJPanel.add(countryJTextField);

    populationJLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
    populationJLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    populationJLabel.setText("Population: ");
    contentJPanel.add(populationJLabel);

    populationJTextField.setEditable(false);
    populationJTextField.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
    contentJPanel.add(populationJTextField);

    capitalJLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
    capitalJLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    capitalJLabel.setText("Capital: ");
    contentJPanel.add(capitalJLabel);

    capitalJTextField.setEditable(false);
    capitalJTextField.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
    contentJPanel.add(capitalJTextField);

    areaJLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
    areaJLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    areaJLabel.setText("Area: ");
    contentJPanel.add(areaJLabel);

    areaJTextField.setEditable(false);
    areaJTextField.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
    contentJPanel.add(areaJTextField);

    controlJPanel.setLayout(new java.awt.GridLayout(1, 3, 3, 5));

    addJButton.setBackground(new java.awt.Color(255, 255, 204));
    addJButton.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
    addJButton.setMnemonic('A');
    addJButton.setText("Add");
    addJButton.setToolTipText("Click to submit your answer");
    addJButton.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          addJButtonActionPerformed(evt);
        }
      }
    );
    controlJPanel.add(addJButton);

    editJButton.setBackground(new java.awt.Color(255, 255, 204));
    editJButton.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
    editJButton.setMnemonic('E');
    editJButton.setText("Edit");
    editJButton.setToolTipText("Click to see the next site");
    editJButton.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          editJButtonActionPerformed(evt);
        }
      }
    );
    controlJPanel.add(editJButton);

    deleteJButton.setBackground(new java.awt.Color(255, 255, 204));
    deleteJButton.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
    deleteJButton.setMnemonic('D');
    deleteJButton.setText("Delete");
    deleteJButton.setToolTipText("Play all over again!");
    deleteJButton.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          deleteJButtonActionPerformed(evt);
        }
      }
    );
    controlJPanel.add(deleteJButton);

    exitJButton.setBackground(new java.awt.Color(255, 255, 204));
    exitJButton.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
    exitJButton.setMnemonic('x');
    exitJButton.setText("Exit");
    exitJButton.setToolTipText("Click to submit your answer");
    exitJButton.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          exitJButtonActionPerformed(evt);
        }
      }
    );
    controlJPanel.add(exitJButton);

    sitesJList.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
    sitesJList.setModel(
      new javax.swing.AbstractListModel<String>() {
        String[] strings = { " " };

        public int getSize() {
          return strings.length;
        }

        public String getElementAt(int i) {
          return strings[i];
        }
      }
    );
    sitesJList.addListSelectionListener(
      new javax.swing.event.ListSelectionListener() {
        public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
          sitesJListValueChanged(evt);
        }
      }
    );
    sitesJScrollPane.setViewportView(sitesJList);

    javax.swing.GroupLayout sitesJPanelLayout = new javax.swing.GroupLayout(
      sitesJPanel
    );
    sitesJPanel.setLayout(sitesJPanelLayout);
    sitesJPanelLayout.setHorizontalGroup(
      sitesJPanelLayout
        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(
          javax.swing.GroupLayout.Alignment.TRAILING,
          sitesJPanelLayout
            .createSequentialGroup()
            .addContainerGap()
            .addComponent(
              sitesJScrollPane,
              javax.swing.GroupLayout.DEFAULT_SIZE,
              247,
              Short.MAX_VALUE
            )
            .addContainerGap()
        )
    );
    sitesJPanelLayout.setVerticalGroup(
      sitesJPanelLayout
        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(
          javax.swing.GroupLayout.Alignment.TRAILING,
          sitesJPanelLayout
            .createSequentialGroup()
            .addContainerGap()
            .addComponent(
              sitesJScrollPane,
              javax.swing.GroupLayout.DEFAULT_SIZE,
              240,
              Short.MAX_VALUE
            )
            .addContainerGap()
        )
    );

    fileJMenu.setMnemonic('F');
    fileJMenu.setText("File");

    newJMenuItem.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
    newJMenuItem.setMnemonic('N');
    newJMenuItem.setText("New");
    newJMenuItem.setToolTipText("Load new sites database");
    newJMenuItem.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          newJMenuItemActionPerformed(evt);
        }
      }
    );
    fileJMenu.add(newJMenuItem);

    printSpeciesJMenuItem.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
    printSpeciesJMenuItem.setMnemonic('t');
    printSpeciesJMenuItem.setText("Print");
    printSpeciesJMenuItem.setToolTipText("Print details about dish");
    printSpeciesJMenuItem.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          printSpeciesJMenuItemActionPerformed(evt);
        }
      }
    );
    fileJMenu.add(printSpeciesJMenuItem);

    printJMenuItem.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
    printJMenuItem.setMnemonic('P');
    printJMenuItem.setText("Print Form");
    printJMenuItem.setToolTipText("Print Form as GUI");
    printJMenuItem.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          printJMenuItemActionPerformed(evt);
        }
      }
    );
    fileJMenu.add(printJMenuItem);
    fileJMenu.add(fileJSeparator);

    exitJMenuItem.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
    exitJMenuItem.setMnemonic('x');
    exitJMenuItem.setText("Exit");
    exitJMenuItem.setToolTipText("End application");
    exitJMenuItem.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          exitJMenuItemActionPerformed(evt);
        }
      }
    );
    fileJMenu.add(exitJMenuItem);

    sitesJMenuBar.add(fileJMenu);

    sortJMenu.setMnemonic('S');
    sortJMenu.setText("Sort");

    nameJRadioButtonMenuItem.setMnemonic('n');
    nameJRadioButtonMenuItem.setSelected(true);
    nameJRadioButtonMenuItem.setText("By name ");
    nameJRadioButtonMenuItem.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          nameJRadioButtonMenuItemActionPerformed(evt);
        }
      }
    );
    sortJMenu.add(nameJRadioButtonMenuItem);

    populationJRadioButtonMenuItem.setMnemonic('B');
    populationJRadioButtonMenuItem.setSelected(true);
    populationJRadioButtonMenuItem.setText("By population");
    populationJRadioButtonMenuItem.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          populationJRadioButtonMenuItemActionPerformed(evt);
        }
      }
    );
    sortJMenu.add(populationJRadioButtonMenuItem);

    sitesJMenuBar.add(sortJMenu);

    actionJMenu.setMnemonic('t');
    actionJMenu.setText("Database Management");

    addJMenuItem.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
    addJMenuItem.setMnemonic('A');
    addJMenuItem.setText("Add");
    addJMenuItem.setToolTipText("Add new site");
    addJMenuItem.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          addJMenuItemActionPerformed(evt);
        }
      }
    );
    actionJMenu.add(addJMenuItem);

    deleteJMenuItem.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
    deleteJMenuItem.setMnemonic('D');
    deleteJMenuItem.setText("Delete");
    deleteJMenuItem.setToolTipText("Delete selected site");
    deleteJMenuItem.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          deleteJMenuItemActionPerformed(evt);
        }
      }
    );
    actionJMenu.add(deleteJMenuItem);

    editJMenuItem.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
    editJMenuItem.setMnemonic('E');
    editJMenuItem.setText("Edit");
    editJMenuItem.setToolTipText("Edit selected site\n");
    editJMenuItem.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          editJMenuItemActionPerformed(evt);
        }
      }
    );
    actionJMenu.add(editJMenuItem);

    searchJMenuItem.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
    searchJMenuItem.setMnemonic('r');
    searchJMenuItem.setText("Search");
    searchJMenuItem.setToolTipText("Search site by name");
    searchJMenuItem.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          searchJMenuItemActionPerformed(evt);
        }
      }
    );
    actionJMenu.add(searchJMenuItem);

    detailsJMenuItem.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
    detailsJMenuItem.setMnemonic('Q');
    detailsJMenuItem.setText("Details");
    detailsJMenuItem.setToolTipText(
      "Display details of selected vacation site"
    );
    detailsJMenuItem.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          detailsJMenuItemActionPerformed(evt);
        }
      }
    );
    actionJMenu.add(detailsJMenuItem);

    sitesJMenuBar.add(actionJMenu);

    helpJMenu.setMnemonic('H');
    helpJMenu.setText("Help");

    aboutJMenuItem.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
    aboutJMenuItem.setMnemonic('A');
    aboutJMenuItem.setText("About");
    aboutJMenuItem.setToolTipText("Show About form");
    aboutJMenuItem.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          aboutJMenuItemActionPerformed(evt);
        }
      }
    );
    helpJMenu.add(aboutJMenuItem);

    sitesJMenuBar.add(helpJMenu);

    setJMenuBar(sitesJMenuBar);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
      getContentPane()
    );
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout
        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(
          layout
            .createSequentialGroup()
            .addContainerGap(134, Short.MAX_VALUE)
            .addComponent(
              titleJLabel,
              javax.swing.GroupLayout.PREFERRED_SIZE,
              446,
              javax.swing.GroupLayout.PREFERRED_SIZE
            )
            .addGap(132, 132, 132)
        )
        .addGroup(
          layout
            .createSequentialGroup()
            .addGap(16, 16, 16)
            .addComponent(
              sitesJPanel,
              javax.swing.GroupLayout.PREFERRED_SIZE,
              javax.swing.GroupLayout.DEFAULT_SIZE,
              javax.swing.GroupLayout.PREFERRED_SIZE
            )
            .addContainerGap(
              javax.swing.GroupLayout.DEFAULT_SIZE,
              Short.MAX_VALUE
            )
        )
        .addGroup(
          javax.swing.GroupLayout.Alignment.TRAILING,
          layout
            .createSequentialGroup()
            .addContainerGap()
            .addComponent(
              controlJPanel,
              javax.swing.GroupLayout.DEFAULT_SIZE,
              javax.swing.GroupLayout.DEFAULT_SIZE,
              Short.MAX_VALUE
            )
            .addContainerGap()
        )
        .addGroup(
          layout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(
              javax.swing.GroupLayout.Alignment.TRAILING,
              layout
                .createSequentialGroup()
                .addContainerGap(272, Short.MAX_VALUE)
                .addComponent(
                  contentJPanel,
                  javax.swing.GroupLayout.PREFERRED_SIZE,
                  422,
                  javax.swing.GroupLayout.PREFERRED_SIZE
                )
                .addContainerGap(18, Short.MAX_VALUE)
            )
        )
    );
    layout.setVerticalGroup(
      layout
        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(
          javax.swing.GroupLayout.Alignment.TRAILING,
          layout
            .createSequentialGroup()
            .addGap(25, 25, 25)
            .addComponent(
              titleJLabel,
              javax.swing.GroupLayout.PREFERRED_SIZE,
              74,
              javax.swing.GroupLayout.PREFERRED_SIZE
            )
            .addGap(35, 35, 35)
            .addComponent(
              sitesJPanel,
              javax.swing.GroupLayout.PREFERRED_SIZE,
              javax.swing.GroupLayout.DEFAULT_SIZE,
              javax.swing.GroupLayout.PREFERRED_SIZE
            )
            .addPreferredGap(
              javax.swing.LayoutStyle.ComponentPlacement.RELATED,
              26,
              Short.MAX_VALUE
            )
            .addComponent(
              controlJPanel,
              javax.swing.GroupLayout.PREFERRED_SIZE,
              62,
              javax.swing.GroupLayout.PREFERRED_SIZE
            )
            .addContainerGap()
        )
        .addGroup(
          layout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(
              javax.swing.GroupLayout.Alignment.TRAILING,
              layout
                .createSequentialGroup()
                .addContainerGap(132, Short.MAX_VALUE)
                .addComponent(
                  contentJPanel,
                  javax.swing.GroupLayout.PREFERRED_SIZE,
                  257,
                  javax.swing.GroupLayout.PREFERRED_SIZE
                )
                .addContainerGap(91, Short.MAX_VALUE)
            )
        )
    );

    pack();
  } // </editor-fold>//GEN-END:initComponents

  /**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   * Method       exitJButtonActionPerformed()
   * Description  Close application.
   * Date         10/31/2023
   * History Log
   * @param       evt java.awt.event.ActionEvent
   * @author      <i>wldeh</i>
   *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
  private void exitJButtonActionPerformed(java.awt.event.ActionEvent evt) { //GEN-FIRST:event_exitJButtonActionPerformed
    System.exit(0);
  } //GEN-LAST:event_exitJButtonActionPerformed

  /**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   * Method       exitJMenuItemActionPerformed()
   * Description  Close application.
   * Date         10/31/2023
   * History Log
   * @param       evt java.awt.event.ActionEvent
   * @author      <i>wldeh</i>
   *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
  private void exitJMenuItemActionPerformed(java.awt.event.ActionEvent evt) { //GEN-FIRST:event_exitJMenuItemActionPerformed
    System.exit(0);
  } //GEN-LAST:event_exitJMenuItemActionPerformed

  /**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   * Method        printJMenuItemActionPerformed()
   * Description   Event handler to print the form as GUI.
   * @param        evt java.awt.event.ActionEvent
   * @see          java.awt.event.ActionEvent
   * @author       <i>wldeh</i>
   * Date          10/31/2023
   * History Log
   *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
  private void printJMenuItemActionPerformed(java.awt.event.ActionEvent evt) { //GEN-FIRST:event_printJMenuItemActionPerformed
    PrintUtilities.printComponent(this);
  } //GEN-LAST:event_printJMenuItemActionPerformed

  /**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   * Method        aboutJMenuItemActionPerformed()
   * Description   Creates and display the About form
   * @param        evt javax.swing.event.ActionEvent
   * @see          javax.swing.event.ActionEvent
   * @author       <i>wldeh</i>
   * Date          10/31/2023
   * History Log
   *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
  private void aboutJMenuItemActionPerformed(java.awt.event.ActionEvent evt) { //GEN-FIRST:event_aboutJMenuItemActionPerformed
    About aboutWindow = new About(this, true);
    aboutWindow.setVisible(true);
  } //GEN-LAST:event_aboutJMenuItemActionPerformed

  /**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   * Method        sitesJListValueChanged()
   * Description   sitesJList event handler to update site's fields.
   *               It fires everytime new sites is selected in JList.
   *               Calls showSites() method with found index.
   * @param        evt javax.swing.event.ActionEvent
   * @see          javax.swing.event.ActionEvent
   * @author       <i>wldeh</i>
   * Date          10/31/2023
   * History Log
   *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
  private void sitesJListValueChanged(
    javax.swing.event.ListSelectionEvent evt
  ) { //GEN-FIRST:event_sitesJListValueChanged
    try {
      int index = sitesJList.getSelectedIndex();

      if (index == -1) {
        return;
      }

      String name = sitesJList.getSelectedValue().toString();

      if (name.contains(",")) {
        index = name.indexOf(',');
        name = name.substring(0, index);
      }

      if (name != null && !name.isEmpty()) {
        Site selectedSite = searchSite(name);
        display(selectedSite);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  } //GEN-LAST:event_sitesJListValueChanged

  /**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   * Method        printSpeciesJMenuItemActionPerformed()
   * Description   Event handler to print.
   * @param        evt java.awt.event.ActionEvent
   * @see          java.awt.event.ActionEvent
   * @author       <i>wldeh</i>
   * Date          10/31/2023
   * History Log
   *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
  private void printSpeciesJMenuItemActionPerformed(
    java.awt.event.ActionEvent evt
  ) { //GEN-FIRST:event_printSpeciesJMenuItemActionPerformed
    int index = sitesJList.getSelectedIndex();
    JTextArea printSites = new JTextArea();
    printSites.setLineWrap(true);
    printSites.setWrapStyleWord(true);
    printSites.setEditable(false);
    if (index >= 0) {
      try {
        Site sitesToPrint = new Site();
        String output =
          "Name: " +
          sitesToPrint.getName() +
          "\n" +
          "Country: " +
          sitesToPrint.getCountry() +
          "\n" +
          "Population: " +
          sitesToPrint.getPopulation() +
          "\n" +
          "Capital: " +
          sitesToPrint.getCapital() +
          "\n" +
          "Area: " +
          sitesToPrint.getArea();

        printSites.setText(output);
        printSites.print();
      } catch (PrinterException ex) {
        JOptionPane.showMessageDialog(
          null,
          "Sites not Printed",
          "Print Error",
          JOptionPane.WARNING_MESSAGE
        );
      }
    }
  } //GEN-LAST:event_printSpeciesJMenuItemActionPerformed

  /**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   *<pre>
   * Method       addJButtonActionPerformed()
   * Description  Add a new site.
   * @param       evt java.awt.event.ActionEvent
   * @author      <i>wldeh</i>
   * Date         10/31/2023
   * History Log
   *</pre>
   *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
  private void addJButtonActionPerformed(java.awt.event.ActionEvent evt) { //GEN-FIRST:event_addJButtonActionPerformed
    String message = "Site not added.";
    try {
      Add myAddForm = new Add(this, true);
      myAddForm.setVisible(true);

      int lastIndex = 0;

      Site newSite = myAddForm.getSite();

      if (newSite != null && !exists(newSite)) {
        String url = DB_URL;
        String user = USER; // "root";
        String password = PASS;

        Connection con = DriverManager.getConnection(url, user, password);
        Statement stmt = con.createStatement(
          ResultSet.TYPE_SCROLL_INSENSITIVE,
          ResultSet.CONCUR_READ_ONLY
        );
        String query = "SELECT * FROM Sites";

        ResultSet rs = stmt.executeQuery(query);
        rs.last();

        lastIndex = sizeOfDB;

        stmt.executeUpdate(
          "INSERT INTO Sites VALUES (" +
          newSite.getName() +
          "','" +
          newSite.getCountry() +
          "'," +
          newSite.getPopulation() +
          "," +
          "'" +
          newSite.getCapital() +
          "'" +
          "," +
          newSite.getArea() +
          ")"
        );

        display(newSite);
        sizeOfDB++;
        con.close();
      } else {
        message = "Site already exists or fields are incomplete.";
        throw new IllegalArgumentException();
      }
    } catch (IllegalArgumentException exp) {
      JOptionPane.showMessageDialog(
        null,
        message,
        "Input Error",
        JOptionPane.WARNING_MESSAGE
      );
    } catch (Exception exp) {
      exp.printStackTrace();
      JOptionPane.showMessageDialog(
        null,
        "Error updating to database",
        "Error!",
        JOptionPane.ERROR_MESSAGE
      );
    }
  } //GEN-LAST:event_addJButtonActionPerformed

  /**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    * Method        detailsJMenuItemActionPerformed()
    * Description   Display details of selected site
    * @param        evt java.awt.event.ActionEvent
    * @author       <i>wldeh</i>
    * Date          10/31/2023
    * History Log   
    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
  private void detailsJMenuItemActionPerformed(java.awt.event.ActionEvent evt) { //GEN-FIRST:event_detailsJMenuItemActionPerformed
    int index = sitesJList.getSelectedIndex();
    if (index >= 0) {
      String siteName = sitesJList.getSelectedValue();

      // Searching the site in the database
      Site site = null;
      try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
        PreparedStatement stmt = con.prepareStatement(
          "SELECT * FROM Sites WHERE name = ?"
        );
        stmt.setString(1, siteName);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
          site = new Site();
          site.setName(rs.getString("name"));
          site.setCountry(rs.getString("country"));
          site.setPopulation(rs.getFloat("population"));
          site.setCapital(rs.getString("capital"));
          site.setArea(rs.getFloat("area"));
        }

        rs.close();
        stmt.close();
        con.close();
      } catch (SQLException | ClassNotFoundException ex) {
        ex.printStackTrace();
      }

      // Display the site details
      if (site != null) {
        SiteDetail siteQuote = new SiteDetail(site);
        siteQuote.setVisible(true);
      }
    }
  } //GEN-LAST:event_detailsJMenuItemActionPerformed

  /**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    * Method        searchJMenuItemActionPerformed()
    * Description   Search site by name with only partial string.
    * @param        evt java.awt.event.ActionEvent
    * @author       <i>wldeh</i>
    * Date          10/31/2023
    * History Log   
    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
  private void searchJMenuItemActionPerformed(java.awt.event.ActionEvent evt) { //GEN-FIRST:event_searchJMenuItemActionPerformed
    // TODO add your handling code here:
  } //GEN-LAST:event_searchJMenuItemActionPerformed

  /**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   *<pre>
   * Method       addJMenuItemActionPerformed()
   * Description  Delete a site.
   * @param       evt java.awt.event.ActionEvent
   * @author      <i>wldeh</i>
   * Date         10/31/2023
   * History Log
   *</pre>
   *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
  private void deleteJMenuItemActionPerformed(java.awt.event.ActionEvent evt) { //GEN-FIRST:event_deleteJMenuItemActionPerformed
    deleteJButtonActionPerformed(evt);
  } //GEN-LAST:event_deleteJMenuItemActionPerformed

  /**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   *<pre>
   * Method       newJMenuItemActionPerformed
   * Description  Event handler to chose a separate table.
   * @param       evt java.awt.event.KeyEvent
   * @author      <i>wldeh</i>
   *</pre>
   *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
  private void newJMenuItemActionPerformed(java.awt.event.ActionEvent evt) { //GEN-FIRST:event_newJMenuItemActionPerformed
    // TODO add your handling code here:
  } //GEN-LAST:event_newJMenuItemActionPerformed

  /**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   *<pre>
   * Method       addJMenuItemActionPerformed()
   * Description  Add a new site.
   * @param       evt java.awt.event.ActionEvent
   * @author      <i>wldeh</i>
   * Date         10/31/2023
   * History Log
   *</pre>
   *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
  private void addJMenuItemActionPerformed(java.awt.event.ActionEvent evt) { //GEN-FIRST:event_addJMenuItemActionPerformed
    addJButtonActionPerformed(evt);
  } //GEN-LAST:event_addJMenuItemActionPerformed

  /**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   *<pre>
   * Method       deleteJButtonActionPerformed()
   * Description  Delete a site
   * @param       evt java.awt.event.ActionEvent
   * @author      <i>wldeh</i>
   * Date         10/31/2023
   * History Log
   *</pre>
   *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
  private void deleteJButtonActionPerformed(java.awt.event.ActionEvent evt) { //GEN-FIRST:event_deleteJButtonActionPerformed
    /*try {
                String url = DB_URL;        
                String user = USER;         
                String password = PASS;     
                Connection conn = DriverManager.getConnection(url, user, password);

                // Check if the site exists
                String selectQuery = "SELECT * FROM Sites WHERE name = ?";
                PreparedStatement selectStmt = conn.prepareStatement(selectQuery);
                selectStmt.setString(1, siteName);
                ResultSet rs = selectStmt.executeQuery();

                if (rs.next()) {
                    int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete "
                        + siteName + "?", "Delete site", JOptionPane.YES_NO_OPTION);

                    if (result == JOptionPane.YES_OPTION) {
                        String deleteQuery = "DELETE FROM Sites WHERE name = ?";
                        PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery);
                        deleteStmt.setString(1, siteName);
                        deleteStmt.execute();

                        JOptionPane.showMessageDialog(null, "Site deleted successfully.", 
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Site not found.", 
                            "Error!", JOptionPane.ERROR_MESSAGE);
                }

                conn.close();
            } catch (SQLException exp) {
                exp.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error deleting.", 
                        "Error!", JOptionPane.ERROR_MESSAGE);
            }*/
  } //GEN-LAST:event_deleteJButtonActionPerformed

  /**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   *<pre>
   * Method       editJMenuItemActionPerformed()
   * Description  Edit a site
   * @param       evt java.awt.event.ActionEvent
   * @author      <i>wldeh</i>
   * Date         10/31/2023
   * History Log
   *</pre>
   *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
  private void editJMenuItemActionPerformed(java.awt.event.ActionEvent evt) { //GEN-FIRST:event_editJMenuItemActionPerformed
    editJButtonActionPerformed(evt);
  } //GEN-LAST:event_editJMenuItemActionPerformed

  /**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   *<pre>
   * Method       editJButtonActionPerformed()
   * Description  Edit a site
   * @param       evt java.awt.event.ActionEvent
   * @author      <i>wldeh</i>
   * Date         10/31/2023
   * History Log
   *</pre>
   *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
  private void editJButtonActionPerformed(java.awt.event.ActionEvent evt) { //GEN-FIRST:event_editJButtonActionPerformed
    /*String message = "Site not edited.";

        try {
            currentID = Integer.parseInt(siteIDJLabel.getText());
            mySite = searchSite(currentID);

            AddEditSiteForm myEditForm = new AddEditSiteForm(mySite);
            myEditForm.setVisible(true);

            Site editSite = myEditForm.getSite();
            editSite.setID(currentID);

            if(editSite != null) {
                // Update edited Site to DB
                mySite.setID(currentID);
                mySite.setName(editSite.getName());
                mySite.setCountry(editSite.getCountry());
                mySite.setPopulation(editSite.getPopulation());
                mySite.setCapital(editSite.getCapital());
                mySite.setArea(editSite.getArea());

                // Make connection to Sites DB
                String url = DB_URL;
                String user = USER;
                String password = PASS;

                Connection conn = DriverManager.getConnection(url, user, password);
                Statement stmt = conn.createStatement();

                // Set query to update record
                String query = "UPDATE Sites SET name = '" + mySite.getName() + 
                               "', country = '" + mySite.getCountry() +
                               "', population = " + mySite.getPopulation() + 
                               ", capital = '" + mySite.getCapital() +
                               "', area = " + mySite.getArea() + 
                               " WHERE siteID = " + mySite.getID();

                stmt.executeUpdate(query);

                display(mySite);
                stmt.close();
                conn.close();
            }
            else {
                JOptionPane.showMessageDialog(null, message, "Error!", JOptionPane.ERROR_MESSAGE);
            }
        }
        catch(SQLException exp) {
            JOptionPane.showMessageDialog(null, message, "Error!", JOptionPane.ERROR_MESSAGE);
        }
*/
  } //GEN-LAST:event_editJButtonActionPerformed

  private void nameJRadioButtonMenuItemActionPerformed(
    java.awt.event.ActionEvent evt
  ) { //GEN-FIRST:event_nameJRadioButtonMenuItemActionPerformed
    populateSitesJListOnStartup();
  } //GEN-LAST:event_nameJRadioButtonMenuItemActionPerformed

  private void populationJRadioButtonMenuItemActionPerformed(
    java.awt.event.ActionEvent evt
  ) { //GEN-FIRST:event_populationJRadioButtonMenuItemActionPerformed
    populateSitesJListOnStartup();
  } //GEN-LAST:event_populationJRadioButtonMenuItemActionPerformed

  /**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   *<pre>
   * Method       exists()
   * Description  Check if the parameter-given site exists in the DB.
   * @param       mySite Site
   * @return      boolean -- true if the site exists, false otherwise
   * @author      <i>wldeh</i>
   * Date         10/31/2023
   * History Log
   *</pre>
   *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
  private boolean exists(Site mySite) {
    boolean found = false;
    try {
      String url = DB_URL;
      String user = USER; // "root";
      String password = PASS;

      Connection con = DriverManager.getConnection(url, user, password);
      Statement stmt = con.createStatement();

      String query =
        "SELECT COUNT(*) FROM Sites WHERE name = '" + mySite.getName() + "'";

      ResultSet rs = stmt.executeQuery(query);

      if (rs.next()) {
        int count = rs.getInt(1);
        if (count > 0) {
          found = true;
        }
      }

      con.close();
    } catch (Exception exp) {
      exp.printStackTrace();
      JOptionPane.showMessageDialog(
        null,
        "Error in checking site existence.",
        "Error!",
        JOptionPane.ERROR_MESSAGE
      );
    }

    return found;
  }

  /**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-------
   * Method        main()
   * Description   Displays splash screen and the main Sites GUI form.
   * @param        args are the command line strings
   * @author       <i>wldeh</i>
   * Date          1/25/2023
   * History Log
   *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~------*/
  public static void main(String args[]) {
    Splash mySplash = new Splash(5000); // duration = 4 seconds
    mySplash.showSplash(); // show splash screen
    /* Set the Nimbus look and feel */
    //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
    /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
     * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
     */
    try {
      for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
        if ("Nimbus".equals(info.getName())) {
          javax.swing.UIManager.setLookAndFeel(info.getClassName());
          break;
        }
      }
    } catch (ClassNotFoundException ex) {
      java.util.logging.Logger
        .getLogger(SitesGUI.class.getName())
        .log(java.util.logging.Level.SEVERE, null, ex);
    } catch (InstantiationException ex) {
      java.util.logging.Logger
        .getLogger(SitesGUI.class.getName())
        .log(java.util.logging.Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
      java.util.logging.Logger
        .getLogger(SitesGUI.class.getName())
        .log(java.util.logging.Level.SEVERE, null, ex);
    } catch (javax.swing.UnsupportedLookAndFeelException ex) {
      java.util.logging.Logger
        .getLogger(SitesGUI.class.getName())
        .log(java.util.logging.Level.SEVERE, null, ex);
    }
    //</editor-fold>
    //</editor-fold>

    /* Create and display the form */
    java.awt.EventQueue.invokeLater(
      new Runnable() {
        public void run() {
          new SitesGUI().setVisible(true);
        }
      }
    );
  }

  /**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   * Inner class   MyFilter
   * Description   An inner class used to filter Players and Sites in text
   *               file display
   * @author       <i>wldeh</i>
   * Date          10/31/2023
   * History Log
   *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
  class MyFilter extends javax.swing.filechooser.FileFilter {

    private String startSequence = "";

    /**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * Constructor   MyFilter
     * Description   A constructor to decide Player or Sites filter for text
     *               files display
     * @param        filter String
     * @author       <i>wldeh</i>
     * Date          10/31/2023
     * History Log
     *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    public MyFilter(String filter) {
      startSequence = filter;
    }

    /**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * Method        accept
     * Description   Overriding the abstract boolean method
     *               files display
     * @param        file File
     * @return       true/false if pattern contained in filter
     * @author       <i>wldeh</i>
     * Date          10/31/2023
     * History Log
     *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    public boolean accept(File file) {
      String filename = file.getName();
      return filename.contains(startSequence);
    }

    /**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * Method       getDescription
     * Description   Getter method for file description
     *               files display
     * @return       *.txt String
     * @author       <i>wldeh</i>
     * Date          10/31/2023
     * History Log
     *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    public String getDescription() {
      return "*.txt";
    }
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JMenuItem aboutJMenuItem;
  private javax.swing.JMenu actionJMenu;
  private javax.swing.JButton addJButton;
  private javax.swing.JMenuItem addJMenuItem;
  private javax.swing.JLabel areaJLabel;
  private javax.swing.JTextField areaJTextField;
  private javax.swing.JLabel capitalJLabel;
  private javax.swing.JTextField capitalJTextField;
  private javax.swing.JPanel contentJPanel;
  private javax.swing.JPanel controlJPanel;
  private javax.swing.JLabel countryJLabel;
  private javax.swing.JTextField countryJTextField;
  private javax.swing.JButton deleteJButton;
  private javax.swing.JMenuItem deleteJMenuItem;
  private javax.swing.JMenuItem detailsJMenuItem;
  private javax.swing.JButton editJButton;
  private javax.swing.JMenuItem editJMenuItem;
  private javax.swing.JButton exitJButton;
  private javax.swing.JMenuItem exitJMenuItem;
  private javax.swing.JMenu fileJMenu;
  private javax.swing.JPopupMenu.Separator fileJSeparator;
  private javax.swing.JMenu helpJMenu;
  private javax.swing.JLabel nameJLabel;
  private javax.swing.JRadioButtonMenuItem nameJRadioButtonMenuItem;
  private javax.swing.JTextField nameJTextField;
  private javax.swing.JMenuItem newJMenuItem;
  private javax.swing.JLabel populationJLabel;
  private javax.swing.JRadioButtonMenuItem populationJRadioButtonMenuItem;
  private javax.swing.JTextField populationJTextField;
  private javax.swing.JMenuItem printJMenuItem;
  private javax.swing.JMenuItem printSpeciesJMenuItem;
  private javax.swing.JMenuItem searchJMenuItem;
  private javax.swing.JList<String> sitesJList;
  private javax.swing.JMenuBar sitesJMenuBar;
  private javax.swing.JPanel sitesJPanel;
  private javax.swing.JScrollPane sitesJScrollPane;
  private javax.swing.JMenu sortJMenu;
  private javax.swing.JLabel titleJLabel;
  // End of variables declaration//GEN-END:variables
}
