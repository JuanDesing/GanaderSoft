package Controlador;

import Controlador.Conexion;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class Hilo_Lee implements Runnable {

    Thread hilo;
    Conexion con, con2, con3, con4;

    public Hilo_Lee() {

    }

    public void inicio() throws Exception {
        //   String url="jdbc:odbc:inventarios";
        // Cargarbase.conexion(url);

        con = new Conexion();
        con.ConexionPostgres();
        con2 = new Conexion();
        con2.ConexionPostgres();
        con3 = new Conexion();
        con3.ConexionPostgres();
        con4 = new Conexion();
        con4.ConexionPostgres();

        if (hilo == null) {
            hilo = new Thread(this);//crea el hilo
            //	hilo=new Thread();//crea el hilo
            hilo.start();// lanzar el hilo
        }

    }

    public void stop() {
        hilo = null;
    }

    public int obteneredadenmeses(Date fechanacimiento) {
        int cantmeses = 0;
        java.util.Date fechaactuall = new java.util.Date();
        //pasamos a String la fecha de nacimiento y la actual
        String fa = new SimpleDateFormat("dd/MM/yyyy").format(fechaactuall);
        String fn = new SimpleDateFormat("dd/MM/yyyy").format(fechanacimiento);
        try {
            Calendar inicio = new GregorianCalendar();
            Calendar fin = new GregorianCalendar();
            inicio.setTime(new SimpleDateFormat("dd/MM/yyyy").parse(fn));
            fin.setTime(new SimpleDateFormat("dd/MM/yyyy").parse(fa));
            int difA = fin.get(Calendar.YEAR) - inicio.get(Calendar.YEAR);
            //System.out.println(difA);
            cantmeses = difA * 12 + fin.get(Calendar.MONTH) - inicio.get(Calendar.MONTH);
            //System.out.println(" difA * 12: " + difA * 12 + "\nfin.get(Calendar.MONTH): " + fin.get(Calendar.MONTH) + "\ninicio.get(Calendar.MONTH): " + inicio.get(Calendar.MONTH) + "\nfin.get(Calendar.MONTH) - inicio.get(Calendar.MONTH): " + (fin.get(Calendar.MONTH) - inicio.get(Calendar.MONTH)));
            //System.out.println("\n\ndifA * 12 + fin.get(Calendar.MONTH) - inicio.get(Calendar.MONTH): " + difM);
        } catch (ParseException ex) {
        }
        return cantmeses;
    }

    public String actualizacionBDseguncategoriadelacria(int edadenmeses, ResultSet temp) throws SQLException {
        String categoria = "";

        if (edadenmeses <= 13 && edadenmeses >= 0) {
            categoria = "Hemb-Lac";
        } else if (edadenmeses == 14) {
            categoria = "Hemb-Crecim 1";
        } else if (edadenmeses == 15) {
            categoria = "Hemb-Crecim 2";
        } else if (edadenmeses >= 16) {
            categoria = "Vaca";
            //JOptionPane.showMessageDialog(null, "Haremos la inserción de esta cria en la tabla vaca");
            //hacemos la inserción de esta ternera en la tabla vaca
            //JOptionPane.showMessageDialog(null, "Se va a insertar la cria " + temp.getString(7));

            con.actualizar("insert into vaca values ('" + temp.getString("id_cria") + "', "
                    + " " + temp.getInt("potrero_idpotrero") + ", '" + temp.getString("raza_nacido") + "', "
                    + " '" + temp.getString("no_siniga_nacido") + "', '" + temp.getString("nombre_nacido") + "', "
                    + " '" + temp.getString("tatuaje_nacido") + "', '" + temp.getString("no_registro_nacido") + "', '" + temp.getDate("fechanac_nacido") + "', '" + temp.getString("motivo_ingreso_nacido") + "', '" + temp.getDate("ingreso_granja_nacido") + "', '" + temp.getDate("ingreso_sist_nacido") + "', '" + temp.getString("color_nacido") + "', '" + temp.getString("tipoconcepcion_nacido") + "' ) ");

        }

        return categoria;
    }


    public void run() {
        String querynacido = "  ", queryvaca = "";
        ResultSet nacido, cat, animal;
        Thread hiloActual = Thread.currentThread();
//        Imprimir b = new Imprimir();
//        b.show();       
        while (hilo == hiloActual) {
            try {
                querynacido = "select * from nacido";
                nacido = con.consultar(querynacido);
                java.util.Date fechanacimiento = null;
                while (nacido.next()) {
                    //JOptionPane.showMessageDialog(null, "Cria " + temp.getString(7));
                    fechanacimiento = nacido.getDate("fechanac_nacido");
                    int edadenmeses = obteneredadenmeses((Date) fechanacimiento);
                    cat = con2.consultar("select nombre_categoria from categoria where " + edadenmeses + " <= mesfinal_categoria and " + edadenmeses + " >= mesinical_categoria");
                    String categoria = "";
                    if (cat.next()) {
                        categoria = cat.getString(1);
                    }
                    animal = con3.consultar("select * from vaca where idvaca = '" + nacido.getString("id_cria") + "' ");
                    boolean registrado = false;
                    if (animal.next()) {
                        registrado = true;
                    }
                    switch (categoria) {
                        case "Hemb-Lac":
                            con4.actualizar("update animal set categoria = '" + categoria + "' where idanimal = '" + nacido.getString("id_cria") + "' ");
                            break;
                        case "Hemb-Crecim 1":
                            con4.actualizar("update animal set categoria = '" + categoria + "' where idanimal = '" + nacido.getString("id_cria") + "' ");
                            break;
                        case "Hemb-Crecim 2":
                            con4.actualizar("update animal set categoria = '" + categoria + "' where idanimal = '" + nacido.getString("id_cria") + "' ");
                            break;
                        case "Vaca":
                            con4.actualizar("update animal set categoria = '" + categoria + "' where idanimal = '" + nacido.getString("id_cria") + "' ");
                            if (!registrado) {
                                con4.actualizar("insert into vaca values ('" + nacido.getString("id_cria") + "', "
                                        + " " + nacido.getString("potrero_idpotrero") + ", '" + nacido.getString("raza_nacido") + "', "
                                        + " '" + nacido.getString("no_siniga_nacido") + "', '" + nacido.getString("nombre_nacido") + "', "
                                        + " '" + nacido.getString("tatuaje_nacido") + "', '" + nacido.getString("no_registro_nacido") + "', '" + nacido.getDate("fechanac_nacido") + "', '" + nacido.getString("motivo_ingreso_nacido") + "', '" + nacido.getString("ingreso_granja_nacido") + "', '" + nacido.getString("ingreso_sist_nacido") + "', '" + nacido.getString("color_nacido") + "', '" + nacido.getString("tipoconcepcion_nacido") + "' ) ");
                            }
                            break;
                    }

                }
                long tiempo = 3 * 60 * 60 * 1000;
                pausa(5000);

            } catch (SQLException ex) {
                Logger.getLogger(Hilo_Lee.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void pausa(int tiempo) {
        try {
            Thread.sleep(tiempo);
        } catch (InterruptedException ignorada) {
        }
    }
}