import java.io.*;
import java.sql.*;
import java.util.Date;
import java.util.*;
import oracle.jdbc.driver.*;
import org.apache.ibatis.jdbc.ScriptRunner;

public class Student{
    static Connection con;
    static Statement stmt;
    
    public static void main(String argv[]) {
    	//1. Connect to JDBC & Execute sql script file.
    	connectToDatabase();
    	
    	try {
    		//2. Display Menu
        	while (true) {
        		String choice = userChoice();
        		switch (choice) {
        			case "1": 
            			displayTables();
        				break;
        			case "2":
            			searchMovies();
        				break;
        			case "3":
            			insertMoviesRatings();
        				break;
        			case "4":
        				System.out.println("\nExit the Program.");
        				return;
        			default:
        				System.out.println("Invalid Menu: Please choose the menu again");
        		}
        	}
    	} catch (Exception e) {
    		//will never be the case: all the invalid menu will fall under default case.
    		System.out.println("Invalid Menu: Please choose the menu again");
    		return;
    	}    	
    }

    
    /*
     * Connect to JDBC Database and Read script file. 
     */
    public static void connectToDatabase() {
    	String driverPrefixURL="jdbc:oracle:thin:@";
    	String jdbc_url="artemis.vsnet.gmu.edu:1521/vse18c.vsnet.gmu.edu";
	
        // IMPORTANT: DO NOT PUT YOUR LOGIN INFORMATION HERE. INSTEAD, PROMPT USER FOR HIS/HER LOGIN/PASSWD
    	String username, password;
    	Scanner sc = new Scanner(System.in);
    	
    	System.out.println("Enter your Oracle ID: ");
        username = sc.nextLine();
        System.out.println("Enter your Oracle Password: ");
        password = sc.nextLine();
        
       
        try{
        	//Register Oracle driver
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        } catch (Exception e) {
            System.out.println("Failed to load JDBC/ODBC driver.");
            return;
        }
        
        String script_path = "";

        try{
            System.out.println("\n"+driverPrefixURL+jdbc_url);
            con=DriverManager.getConnection(driverPrefixURL+jdbc_url, username, password);
            DatabaseMetaData dbmd=con.getMetaData();
            stmt=con.createStatement();
            System.out.println("Connected.");

            //Read script file.
            ScriptRunner sr = new ScriptRunner(con);
            
            //Ask script file path.
        	System.out.println("\nEnter the script file path: ");
        	script_path = sc.nextLine();
            Reader reader = new BufferedReader(new FileReader(script_path));
            sr.runScript(reader);

            if(dbmd==null) {
                System.out.println("No database meta data");
            } else {
                System.out.println("Database Product Name: "+dbmd.getDatabaseProductName());
                System.out.println("Database Product Version: "+dbmd.getDatabaseProductVersion());
                System.out.println("Database Driver Name: "+dbmd.getDriverName());
                System.out.println("Database Driver Version: "+dbmd.getDriverVersion());
            }
        } catch(Exception e) {
        	System.out.println("\nINVALID File Path: " + script_path + "\nPlease start over.\n");
        	connectToDatabase();
        }
    }  
    
    
    /*
     * Display the menu.
     */
    public static String userChoice() {
    	System.out.println("\n---------- MENU ---------\n");
    	System.out.println("1: View Table Contents");
    	System.out.println("2: Search by Movie ID");
    	System.out.println("3: Insert by Movie ID");
    	System.out.println("4: Exit the Program");
    
    	Scanner sc = new Scanner(System.in);
    	System.out.println("\nEnter your choice (1, 2, 3, 4): ");
    	String input = sc.nextLine();
    	
    	return input;
    }
    
    /*
     * Display movies/ratings table.
     */
    public static void displayTables() {
    	Scanner sc = new Scanner(System.in);
    	
    	//1. Ask users which tables they would like to see.
		System.out.println("Would you like to see MOVIES Table? (Yes/No)");
		String movies = sc.nextLine();
		System.out.println("Would you like to see RATINGS Table? (Yes/No)");
		String ratings = sc.nextLine();
		
		//2. Display movies table.
		if (movies.equals("Yes")) {
			System.out.println("\nDISPLAYING MOVIES TABLE.\n");
			
			String query = "SELECT * FROM Movies";
			
		    try (Statement stmt = con.createStatement()) {
		    	System.out.println("\n---DISPLAYING MOVIES TABLE---\n");
		    	System.out.println("Movie ID, Title, Language, Production Company, Production Country, Runtime, Release Date");
		    	
		    	ResultSet rs = stmt.executeQuery(query);
		    	
		    	while (rs.next()) {
		    		String movieid = rs.getString("MOVIEID");
		    		String title = rs.getString("TITLE");
		    		String language = rs.getString("LANGUAGE");
		    		String production_company = rs.getString("PRODUCTION_COMPANY");
		    		String production_country = rs.getString("PRODUCTION_COUNTRY");
		    		int runtime = rs.getInt("RUNTIME");
		    		Date release_date = rs.getDate("RELEASE_DATE");
		        
		    		System.out.println(movieid + ", " + title + ", " + language + ", " 
		    				+ production_company + ", " + production_country + ", " 
		    				+ runtime +  ", " + release_date);
		    	}
		    } catch (SQLException e) {
		    	System.out.println("\nINVALID QUERY: PLEASE SELECT MENU AGAIN.\n");
		    	return;
		    }
		} else if (!movies.equals("No")) {
			System.out.println("\nINVALID RESPONSE: PLEASE SELECT MENU AGAIN\n");
			return;
		}
		
		
		//3. Display ratings table.
		if (ratings.equals("Yes")) {
			System.out.println("\nDISPLAYING RATINGS TABLE.\n");
			
			String query = "SELECT * FROM Ratings";
			
			try (Statement stmt = con.createStatement()) {
				System.out.println("\n---DISPLAYING RATINGS TABLE---\n");
				System.out.println("Movie ID, User ID, Rating");
				
				ResultSet rs = stmt.executeQuery(query);
				
			    while (rs.next()) {
			    	String movieid = rs.getString("MOVIEID");
			        String userid = rs.getString("USERID");
			        Float rating = rs.getFloat("RATING");
			        
			        System.out.println(movieid + ", " + userid + ", " + rating);
			      }
			    } catch (SQLException e) {
			    	System.out.println("\nINVALID QUERY: PLEASE SELECT MENU AGAIN.\n");
			    	return;
			    }
		} else if (!ratings.equals("No")) {
			System.out.println("\nINVALID RESPONSE: PLEASE SELECT MENU AGAIN.\n");
			return;
		}	
    }
    
    /*
     * Search movies by movie id.
     */
    public static void searchMovies() throws SQLException {
    	Scanner sc = new Scanner(System.in);
    	
    	//1. Ask for movie id.
		System.out.println("Enter the Movie ID: ");
		String search = sc.nextLine();
		
		//2. Search movie id.
		String query = "SELECT * FROM Movies WHERE movieid = ?";
		
    	try (PreparedStatement pstmt = con.prepareStatement(query)) {
    		pstmt.setString(1, search); 

    		try (ResultSet rs = pstmt.executeQuery()) { 
                if (!rs.isBeforeFirst()) { 
                    System.out.println("\nINVALID MOVIE ID '" + search + "': PLEASE SELECT MENU AGAIN.\n");
                    return;
                } else {
                	System.out.println("\n---DISPLAYING MOVIE ATTRIBUTES---\n");
                	while (rs.next()) {
                		String movieid = rs.getString("MOVIEID");
    		    		String title = rs.getString("TITLE");
    		    		String language = rs.getString("LANGUAGE");
    		    		String production_company = rs.getString("PRODUCTION_COMPANY");
    		    		String production_country = rs.getString("PRODUCTION_COUNTRY");
    		    		int runtime = rs.getInt("RUNTIME");
    		    		Date release_date = rs.getDate("RELEASE_DATE");
    		        
    		    		System.out.println(movieid + ", " + title + ", " + language + ", " 
    		    				+ production_company + ", " + production_country + ", " 
    		    				+ runtime +  ", " + release_date);
            		}
                }	
    		} catch (SQLException e) {
    			System.out.println("\nINVALID MOVIE ID '" + search + "': PLEASE SELECT MENU AGAIN.\n");
    			return;
    		}
    	}
    	
    	//3. Computer the average rating of the movies with movie id.
    	int num_ratings = 0;
    	float sum_ratings = 0;
    	
    	query = "SELECT * FROM Ratings WHERE movieid = ?";
    	
    	try (PreparedStatement pstmt = con.prepareStatement(query)) {
    		pstmt.setString(1, search); 

    		try (ResultSet rs = pstmt.executeQuery()) { 
                if (!rs.isBeforeFirst()) { 
                	System.out.println("\nINVALID MOVIE ID '" + search + "': PLEASE SELECT MENU AGAIN.\n");
                    return;
                } else {
                	System.out.println("\n---DISPLAYING AVERAGE RATING---\n");
                	while (rs.next()) {
                		int rating = rs.getInt("RATING");
                		num_ratings++;
                		sum_ratings+=rating;
            		}
                }
                
                System.out.println("Average Rating for Movie ID '" + search + "': " + sum_ratings/num_ratings);
    		} catch (SQLException e) {
    			System.out.println("\nINVALID MOVIE ID '" + search + "': PLEASE SELECT MENU AGAIN.\n");
    			return;
    		}
    	}
    }
    
    /*
     * Insert movies into table.
     */
    public static void insertMoviesRatings() {
    	Scanner sc = new Scanner(System.in);
    	
    	//1. Ask paths to movies.csv and ratings.csv
		System.out.println("Enter the path to movies.csv: ");
		String movies_path = sc.nextLine();
		
		System.out.println("Enter the path to ratings.csv: ");
		String ratings_path = sc.nextLine();
		
		//2. Ask for movie id.
		System.out.println("Enter the Movie ID: ");
		String search = sc.nextLine();
		
		//3. Insert found movie into table.
		if (insertMovies(search, movies_path)) {
			//4. Compute the number of ratings of the movie.
			insertRatings(search, ratings_path);
		}	
    }
    
    /*
     * Insert movie with the given movie id and path to movies.csv
     */
    public static boolean insertMovies(String movieid, String path_movies) {
        String query = "INSERT INTO Movies "
        		+ "(title, language, production_company, production_country, release_date, runtime, movieId) "
        		+ "VALUES (?, ?, ?, ?, TO_DATE(?, 'MM/dd/yy'), ?, ?)";
        
        boolean added = false;
        
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            BufferedReader reader = new BufferedReader(new FileReader(path_movies));

        	String row;
        	boolean headerSkipped = false;
        	
        	while ((row = reader.readLine()) != null) {
        		if (!headerSkipped) {
        			headerSkipped = true;
        			continue;
        		}
        		
        		String[] values = row.split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)");        		
        		if (values[6].equals(movieid)) {
        			pstmt.setString(1, values[0]);
        			pstmt.setString(2, values[1]);
        			pstmt.setString(3, values[2]);
        			pstmt.setString(4, values[3]);
        			pstmt.setString(5, values[4]);
        			pstmt.setInt(6, Integer.parseInt(values[5]));
        			pstmt.setString(7, values[6]);
        			pstmt.executeUpdate();
        			added = true;
        		}
        	}
        	
        	if (added) return true;
        	else {
        		System.out.println("\nINVALID MOVIE ID '" + movieid + "': PLEASE SELECT MENU AGAIN.\n");
        		return false;
        	}
        } catch (Exception e) {
        	System.out.println("\nINVALID FILE PATH '" + movieid + "': PLEASE SELECT MENU AGAIN.\n");
        	return false;
        }
       
    }
    
    /*
     * Insert ratings with the given movie id and path to ratings.csv
     */
    public static void insertRatings(String movieid, String path_ratings) {
        String query = "INSERT INTO Ratings (userId, MovieId, rating) VALUES (?, ?, ?)";
        int num_ratings = 0;
        
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            BufferedReader reader = new BufferedReader(new FileReader(path_ratings));

        	String row;
        	boolean headerSkipped = false;
        	
        	while ((row = reader.readLine()) != null) {
        		if (!headerSkipped) {
        			headerSkipped = true;
        			continue;
        		}
        		
        		String[] values = row.split(",");
        		if (values[1].equals(movieid)) {
        			pstmt.setString(1, values[0]);
        			pstmt.setString(2, values[1]);
        			pstmt.setFloat(3, Float.parseFloat(values[2]));
        			pstmt.executeUpdate();
        			num_ratings++;
        		}
        	}
        	
        	System.out.println("\n---DISPLAYING NEWLY ADDED MOVIE---\n");
        	System.out.println("Movie ID: " + movieid);
			System.out.println("Total Number of Ratings: " + num_ratings);
        } catch (Exception e) {
        	System.out.println("\nINVALID FILE PATH '" + movieid + "': PLEASE SELECT MENU AGAIN.\n");
        	return;
        }
    }

}// End of class
