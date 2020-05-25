package mdas.p2.main;


import java.io.File;
import java.util.Scanner;

import mdas.p2.administradoralumnos.AdministradorAlumnos;
import mdas.p2.administradorusuarios.AdministradorUsuarios;
// import mdas.p2.gestorsalas.GestorSalas;


/**
 * Clase MenuPrincipal
 * Clase MenuPrincipal del programa
 *
 * @author			Rafael Carlos Méndez Rodríguez (i82meror)
 * @date			25/05/2020
 * @version			0.3.0
 */

public class MenuPrincipal {
	final static private	String		ARCHIVOUSUARIOS			= System.getProperty("user.dir") + File.separatorChar + "data" + File.separatorChar + "usuarios.csv";
	final static private	String		ARCHIVOINCIDENCIAS		= System.getProperty("user.dir") + File.separatorChar + "data" + File.separatorChar + "incidencias.csv";
	final static private	String		ARCHIVORECURSOS			= System.getProperty("user.dir") + File.separatorChar + "data" + File.separatorChar + "recursos.csv";
	final static private	String		ARCHIVORESERVAS			= System.getProperty("user.dir") + File.separatorChar + "data" + File.separatorChar + "reservas.csv";
	final static private	String		ARCHIVOSALAS			= System.getProperty("user.dir") + File.separatorChar + "data" + File.separatorChar + "salas.csv";
	final static private	String		ARCHIVOSALASYRECURSOS	= System.getProperty("user.dir") + File.separatorChar + "data" + File.separatorChar + "salasyrecursos.csv";
	final static private	String		ARCHIVOSANCIONES		= System.getProperty("user.dir") + File.separatorChar + "data" + File.separatorChar + "sanciones.csv";


	private static			Scanner		_entrada		= new Scanner(System.in);


	/**
	 * Método main de la clase (y del programa)
	 *
	 * @param		args							String							Argumentos recibidos por la línea de comandos
	 */

	public static void main(String[] args) {
		boolean					salir		= false;
		char					operacion;
		int						idUsuario	= -1;
		int						idSancion;
		AdministradorAlumnos	aa			= new AdministradorAlumnos(MenuPrincipal.ARCHIVOINCIDENCIAS, MenuPrincipal.ARCHIVORECURSOS, MenuPrincipal.ARCHIVORESERVAS, MenuPrincipal.ARCHIVOSALAS, MenuPrincipal.ARCHIVOSALASYRECURSOS, MenuPrincipal.ARCHIVOSANCIONES, MenuPrincipal.ARCHIVOUSUARIOS);
		AdministradorUsuarios	au			= new AdministradorUsuarios(MenuPrincipal.ARCHIVOUSUARIOS);
		// GestorSalas				gs			= new GestorSalas(MenuPrincipal.ARCHIVOINCIDENCIAS, MenuPrincipal.ARCHIVORECURSOS, MenuPrincipal.ARCHIVORESERVAS, MenuPrincipal.ARCHIVOSALAS, MenuPrincipal.ARCHIVOSALASYRECURSOS, MenuPrincipal.ARCHIVOSANCIONES);

		System.out.println("Bienvenido al Gestor de salas");

		while(!salir && ((idUsuario = au.iniciarSesion()) == -1)) {
			System.out.println("Ha introducido unas credenciales de usuario no válidas");
			System.out.print("¿Desea volverlo a intentar? [S/n]: ");

			operacion = Character.toUpperCase(MenuPrincipal._entrada.next().charAt(0));

			if(Character.toUpperCase(operacion) == 'N') {
				salir = true;
			}
		}

		while(!salir) {
			System.out.println("¡Bienvenido, " + au.nombre(idUsuario) + "!");

			if(au.alumno(idUsuario)) {
				if((idSancion = aa.comprobarSancion(idUsuario)) == -1) {
					System.out.println("El menú de operaciones es el siguiente:");

					// FIXME: Evitando, por ahora, un bucle infinito
					salir = true;
				}
				else {
					System.out.println(aa.NotificarAlumnoSancionado(idUsuario, idSancion));

					salir = true;
				}
			}
			else if(au.empleado(idUsuario)) {
				System.out.println("El menú de operaciones es el siguiente:");

				// FIXME: Evitando, por ahora, un bucle infinito
				salir = true;
			}
			else {
				System.out.println("Lo sentimos, su perfil de usuario no le permite utilizar este servicio");

				salir = true;
			}
		}

		System.out.println("Gracias por utilizar nuestro sistema");
		System.out.println("Saliendo...");
	}
}
