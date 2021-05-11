package ejercicios;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class Queries {

    //Attriubutes
    private static final String URL = "jdbc:mysql://194.224.79.42:43306/addressbook";
    private static final String USERNAME = "alumne";
    private static final String PASSWORD = "tofol";
    private Connection connection; // manages connection
    private PreparedStatement selectAllPeople;
    private PreparedStatement selectPeopleByLastName;
    private PreparedStatement insertNewPerson;

    //Nuevos atributos añadidos
    private PreparedStatement updatePerson;
    private PreparedStatement deletePerson;
    private PreparedStatement getID;

    //Builder
    public Queries(){
        try{
            connection =
                    DriverManager.getConnection(URL, USERNAME, PASSWORD);
            // create query that selects all entries in the AddressBook
            selectAllPeople =
                    connection.prepareStatement("SELECT * FROM ADDRESSES");
            // create query that selects entries with a specific last name
            selectPeopleByLastName = connection.prepareStatement(
                    "SELECT * FROM ADDRESSES WHERE LastName = ?");
            // create insert that adds a new entry into the database
            insertNewPerson = connection.prepareStatement(
                    "INSERT INTO ADDRESSES " +
                            "(AddressId,FirstName, LastName, Email, PhoneNumber) " +
                            "VALUES (?,?, ?, ?, ?)");
            //Nuevos sql añadidos para actualizar una persona , eliminar una persona y añadir un ID
            updatePerson= connection.prepareStatement(
                    "UPDATE ADDRESSES SET FIRSTNAME=?, LASTNAME=?, EMAIL=?, PHONENUMBER=? WHERE ADDRESSID=?");
            deletePerson=connection.prepareStatement(
                    "DELETE FROM ADDRESSES WHERE ADDRESSID=?");
            getID= connection.prepareStatement("SELECT * FROM ADDRESSES WHERE ADDRESSID = ?");
        }
        catch (SQLException sqlException){
            sqlException.printStackTrace();
            System.exit(1);
        }
    }

    //Other Methods

    // select all of the addresses in the database
    public List<Persona> getAllPeople(){

        List<Persona> results = null;
        ResultSet resultSet = null;
        try{
            // executeQuery returns ResultSet containing matching entries
            resultSet = selectAllPeople.executeQuery();
            results = new ArrayList<Persona>();
            while (resultSet.next()){
                results.add(new Persona(
                        resultSet.getInt("addressID"),
                        resultSet.getString("FirstName"),
                        resultSet.getString("LastName"),
                        resultSet.getString("Email"),
                        resultSet.getString("PhoneNumber")));
            }
        }
        catch (SQLException sqlException){
            sqlException.printStackTrace();
        }
        finally{
            try{
                resultSet.close();
            }
            catch (SQLException sqlException){
                sqlException.printStackTrace();
                close();
            }
        }
        return results;
    }

    // select person by last name
    public List<Persona> getPeopleByLastName(String name){
        List<Persona> results = null;
        ResultSet resultSet = null;
        try{
            selectPeopleByLastName.setString(1, name); // specify last name

            // executeQuery returns ResultSet containing matching entries
            resultSet = selectPeopleByLastName.executeQuery();
            results = new ArrayList<Persona>();
            while (resultSet.next()){
                results.add(new Persona(resultSet.getInt("addressID"),
                        resultSet.getString("FirstName"),
                        resultSet.getString("LastName"),
                        resultSet.getString("Email"),
                        resultSet.getString("PhoneNumber")));
            }
        }
        catch (SQLException sqlException){
            sqlException.printStackTrace();
        }
        finally{
            try{
                resultSet.close();
            }
            catch (SQLException sqlException){
                sqlException.printStackTrace();
                close();
            }
        }
        return results;
    }

    // add an entry
    public int addPerson(String fname, String lname, String email, String num){
        int result = 0;
        // set parameters, then execute
        try{
            insertNewPerson.setInt(1,generateNewID());
            insertNewPerson.setString(2,fname);
            insertNewPerson.setString(3,lname);
            insertNewPerson.setString(4,email);
            insertNewPerson.setString(5,num);

            // insert the new entry; returns # of rows updated
            result = insertNewPerson.executeUpdate();
        }
        catch (SQLException sqlException){
            sqlException.printStackTrace();
            close();
        }
        return result;
    }

    //update an entry
    public int updateEntry(String fname,String lname,String email, String num,String addressId){
        int result=0;
        int id=Integer.parseInt(addressId);
        try{
            updatePerson.setString(1,fname);
            updatePerson.setString(2,lname);
            updatePerson.setString(3,email);
            updatePerson.setString(4,num);
            updatePerson.setInt(5,id);
            result=updatePerson.executeUpdate();

        }catch (SQLException sqlException){
            sqlException.printStackTrace();
            close();
        }
        return result;
    }

    //delete an entry
    public int deleteEntry(String addressId){
        int result=0;
        int id=Integer.parseInt(addressId);
        try{
            deletePerson.setInt(1,id);
            result=deletePerson.executeUpdate();

        }catch (SQLException sqlException){
            sqlException.printStackTrace();
            close();
        }
        return result;
    }

    //Mira si existe el ID

    private boolean getidExist(int id) throws SQLException{
        boolean result = false;
        try {
            getID.setInt(1,id);
            ResultSet rs = getID.executeQuery();
            if(rs.next())
                result = true;
        } catch (SQLException sqlException){
            sqlException.printStackTrace();
            close();
        }
        return result;
    }

    //Genera un ID


    private int generateNewID() throws SQLException {
        int id=generarNumRandom();
        if(!getidExist(id)){
            return id;
        }else {
            return generateNewID();
        }
    }

    //Genera un ID en numero random

    private int generarNumRandom(){
        return (int) (Math.random()*9999)+1;
    }

    
    // close the database connection
    public void close(){
        try{
            connection.close();
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
}