package Sites;

import java.io.Serializable;
import java.util.Objects;

/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *<pre>
 * Class        Site.java
 * Description  A class representing a vacation site with five properties:
 *              name, country, population (in millions), capital, and
 *              area (in square kilometers).
 * Platform     jdk 1.8.0_241; Windows 10
 * Date         10/31/2023
 * History log
 * @author	    <i>wldeh</i>
 * @version 	  %1% %2%
 *</pre>
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
public class Site implements Serializable, Comparable {

  private int id;
  private String name;
  private String country;
  private float population;
  private String capital;
  private float area;

  /**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   * <pre>
   * Constructor   Site()-default constructor
   * Description   Create an instance of a Site class, set the values
   *               for all fields.
   * @author       <i>wldeh</i>
   * Date          10/31/2023
   * History Log
   * </pre>
   *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
  public Site() {
    this("", "", 0, "", 0);
  }

  /**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   * <pre>
   * Constructor   Site()-overloaded constructor
   * Description   Create an instance of a site, set the values for all fields
   *               with provided parameters.
   * @param        name String
   * @param        country String
   * @param        population float
   * @param        capital String
   * @param        area float
   * @author       <i>wldeh</i>
   * Date          10/31/2023
   * History Log
   * </pre>
   *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
  public Site(
    String name,
    String country,
    float population,
    String capital,
    float area
  ) {
    this.name = name;
    this.country = country;
    this.population = population;
    this.capital = capital;
    this.area = area;
  }

  /**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   * <pre>
   * Method        getName()
   * Description   Getter method to return the site's name
   * @return       name String
   * @author       <i>wldeh</i>
   * Date          10/31/2023
   * History Log
   * </pre>
   *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
  public String getName() {
    return name;
  }

  /**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   * <pre>
   * Method        setName()
   * Description   Setter method to return the site's name
   * @param        name String
   * @author       <i>wldeh</i>
   * Date          10/31/2023
   * History Log
   * </pre>
   *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
  public void setName(String name) {
    this.name = name;
  }

  /**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   * <pre>
   * Method        getCountry()
   * Description   Getter method to return the country.
   * @return       country String
   * @author       <i>wldeh</i>
   * Date          10/31/2023
   * History Log
   * </pre>
   *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
  public String getCountry() {
    return country;
  }

  /**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   * <pre>
   * Method        setCountry()
   * Description   Setter method to set the country.
   * @param        country String
   * @author       <i>wldeh</i>
   * Date          10/31/2023
   * History Log
   * </pre>
   *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
  public void setCountry(String country) {
    this.country = country;
  }

  /**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   * <pre>
   * Method        getPopulation()
   * Description   Getter method to return the country's population.
   * @return       population float
   * @author       <i>wldeh</i>
   * Date          10/31/2023
   * History Log
   * </pre>
   *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
  public float getPopulation() {
    return population;
  }

  /**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   * <pre>
   * Method        setPopulation(float population)
   * Description   Setter method to set the population.
   * @param        population float
   * @author       <i>wldeh</i>
   * Date          10/31/2023
   * History Log
   * </pre>
   *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
  public void setPopulation(float population) {
    this.population = population;
  }

  /**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   * <pre>
   * Method        getCapital()
   * Description   Getter method to return the country's capital
   * @return       country String
   * @author       <i>wldeh</i>
   * Date          10/31/2023
   * History Log
   * </pre>
   *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
  public String getCapital() {
    return capital;
  }

  /**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   * <pre>
   * Method        setCapital()
   * Description   Setter method to set the country's capital
   * @param        capital String
   * @author       <i>wldeh</i>
   * Date          10/31/2023
   * History Log
   * </pre>
   *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
  public void setCapital(String capital) {
    this.capital = capital;
  }

  /**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   * <pre>
   * Method        getArea()
   * Description   Getter method to return the country's area in square km.
   * @return       area float
   * @author       <i>wldeh</i>
   * Date          10/31/2023
   * History Log
   * </pre>
   *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
  public float getArea() {
    return area;
  }

  /**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   * <pre>
   * Method        setArea(float area)
   * Description   Setter method to set the countries area in square km.
   * @param        area float
   * @author       <i>wldeh</i>
   * Date          10/31/2023
   * History Log
   * </pre>
   *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
  public void setArea(float area) {
    this.area = area;
  }

  public int getID() {
    return id;
  }

  public void setID(int id) {
    this.id = id;
  }

  /**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   * <pre>
   * Method        hashCode()
   * Description   Overridden hash code.
   * @return       hash int
   * @author       <i>wldeh</i>
   * Date          10/31/2023
   * History Log
   * </pre>
   *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
  @Override
  public int hashCode() {
    int hash = 7;
    return hash;
  }

  /**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   * <pre>
   * Method        equals()
   * Description   Overridden equals method to compare two site objects.
   * Date          10/31/2023
   * History Log
   * @author       <i>wldeh</i>
   * @return       true or false boolean
   * </pre>
   *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Site other = (Site) obj;
    if (
      Float.floatToIntBits(this.population) !=
      Float.floatToIntBits(other.population)
    ) {
      return false;
    }
    if (Float.floatToIntBits(this.area) != Float.floatToIntBits(other.area)) {
      return false;
    }
    if (!Objects.equals(this.name, other.name)) {
      return false;
    }
    if (!Objects.equals(this.country, other.country)) {
      return false;
    }
    if (!Objects.equals(this.capital, other.capital)) {
      return false;
    }
    return true;
  }

  /**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   * <pre>
   * Method        compareTo()
   * Description   Overriden compareTo method to compare two site objects.
   * @author       <i>wldeh</i>
   * Date          10/31/2023
   * History log
   * @param        obj Object
   * @return       1, -1, 0 int
   * </pre>
   *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
  @Override
  public int compareTo(Object obj) {
    return this.getName().compareToIgnoreCase(((Site) obj).getName());
  }
}
