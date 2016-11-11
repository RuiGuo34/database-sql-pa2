import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PA2 {
	public static void main(String[] args) throws ClassNotFoundException {

		// load the sqlite-JDBC driver using the current class loader
		Class.forName("org.sqlite.JDBC");

		Connection connection = null;
		try {
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:pa2.db");
			Statement statement = connection.createStatement();
    		statement.setQueryTimeout(30);  // set timeout to 30 sec.
    		statement.executeUpdate("drop table if exists Connected;");
    		statement.executeUpdate("create table Connected (Airline char(32), Origin char(32), Destination char(32), Stops integer);");
    		statement.executeUpdate("drop table if exists medium;");
    		statement.executeUpdate("create table medium (Airline char(32), Origin char(32), Destination char(32), Stops integer);");
    		statement.executeUpdate("insert into medium (Airline, Origin, Destination, Stops) select Airline, Origin, Destination, 0 from Flight;");
    		
    		ResultSet res = statement.executeQuery("select * from medium;");
    		
    		while (res.next()) {
    			statement.executeUpdate("insert into Connected (Airline, Origin, Destination, Stops) select * from medium;");
	    		statement.executeUpdate("drop table if exists temp;");
	    		statement.executeUpdate("create table temp(Airline char(32), Origin char(32), Destination char(32), Stops integer);");
    			statement.executeUpdate("insert into temp (Airline, Origin, Destination, Stops) select * from medium;");
    			statement.executeUpdate("delete from medium;");
    			statement.executeUpdate("insert into medium (Airline, Origin, Destination, Stops) select distinct a.Airline, a.Origin, b.Destination, a.Stops + 1 from temp a, Flight b where a.Destination = b.Origin and a.Airline = b.Airline and a.Origin <> b.Destination and not exists (select * from Connected c where c.Airline = a.Airline and a.Origin = c.Origin and b.Destination = c.Destination);");

    			res = statement.executeQuery("select * from medium;");

    		}

    		statement.executeUpdate("drop table if exists temp;");
    		statement.executeUpdate("drop table if exists medium;");
		}
		catch (SQLException e) {
		    // if the error message is "out of memory", 
		    // it probably means no database file is found
		    System.err.println(e.getMessage());
		}
	}
}