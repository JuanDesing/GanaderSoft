package Controlador;

import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
//SD1017099  FREDDY CHAVEZ.



public class Conexion{
	Connection conexion;
        Statement sentencia;
	String usuario="postgres", password="admin", iP="localhost", puerto="5433", nombreBD="GanaderSoft";

	/*public Conexion(String iP, String puerto, String nombreBD, String usuario, String password){
		this.usuario = usuario;
		this.password = password;
		this.iP = iP;
		this.puerto = puerto;
		this.nombreBD = nombreBD;*/
		
	public Conexion(){
		
//	this.usuario = "root";
//	this.password = "aprendizsena";
//	this.iP = "localhost";
//	this.puerto = "3306";
//	this.nombreBD = "senova";
				
		}	

    
		
		
	

	// sw es true si va a conectar con MySQL (3306) y postgresql (5432)
	// sw es false si va a conectar con access porque es por medio del odbc
	private void conectar	(String driver, String puente, boolean sw) 
					throws ClassNotFoundException,
						SQLException,
						InstantiationException, 
						IllegalAccessException{

		Class.forName(driver).newInstance();
		if (sw)
 			conexion = DriverManager.getConnection ("jdbc:"+puente+
								"://"+iP+
								":"+puerto+
								"/"+nombreBD,usuario,password);
		else
			conexion = DriverManager.getConnection ("jdbc:"+puente+
								":"+nombreBD,usuario,password);
		sentencia = conexion.createStatement (ResultSet.TYPE_SCROLL_SENSITIVE,
								    ResultSet.CONCUR_UPDATABLE);
	}

	// Con JDBC
	public void ConexionJDBC() throws 	ClassNotFoundException, 
							SQLException,
							InstantiationException,
							IllegalAccessException{
		conectar("sun.jdbc.odbc.JdbcOdbcDriver","odbc",false);
	}

	// Con MySQL
	public void ConexionMySQL() throws   ClassNotFoundException,
							SQLException,
							InstantiationException,
							IllegalAccessException{
		conectar("com.mysql.jdbc.Driver","mysql",true);
	}

	// Con PostgreSql
	public void ConexionPostgres() throws ClassNotFoundException,
							SQLException,
							InstantiationException,
							IllegalAccessException{
		conectar("org.postgresql.Driver","postgresql",true);
	}

	public void actualizar(String actualiza) throws SQLException{
		sentencia.executeUpdate(actualiza);
	}

	public ResultSet consultar(String consulta) throws SQLException{
		return (sentencia.executeQuery(consulta));
	}

	// Devuelve el numero de filas de la tabla virtual
	public int contar(ResultSet rS) throws SQLException{
		int cont = 0;
		rS.beforeFirst();
		while (rS.next()) cont++;
		return (cont);
	}

	public void cerrar() throws SQLException{
		conexion.close();
		sentencia.close();
	}

    public Statement createStatement() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
