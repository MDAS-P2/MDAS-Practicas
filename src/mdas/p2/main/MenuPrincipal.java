package mdas.p2.main;


import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.StringTokenizer;

import mdas.p2.administradoralumnos.AdministradorAlumnos;
import mdas.p2.administradorusuarios.AdministradorUsuarios;
import mdas.p2.gestorincidencias.GestorIncidencias;
import mdas.p2.gestorsalas.GestorSalas;


/**
 * Clase MenuPrincipal
 * Clase MenuPrincipal del programa
 *
 * @author		Rafael Carlos Méndez Rodríguez (i82meror)
 * @date		29/05/2020
 * @version		2.0.2
 */

public class MenuPrincipal {
	final static private	String					ARCHIVOINCIDENCIAS		= System.getProperty("user.dir") + File.separatorChar + "data" + File.separatorChar + "incidencias.csv";
	final static private	String					ARCHIVORECURSOS			= System.getProperty("user.dir") + File.separatorChar + "data" + File.separatorChar + "recursos.csv";
	final static private	String					ARCHIVORESERVAS			= System.getProperty("user.dir") + File.separatorChar + "data" + File.separatorChar + "reservas.csv";
	final static private	String					ARCHIVOSALAS			= System.getProperty("user.dir") + File.separatorChar + "data" + File.separatorChar + "salas.csv";
	final static private	String					ARCHIVOSALASYRECURSOS	= System.getProperty("user.dir") + File.separatorChar + "data" + File.separatorChar + "salasyrecursos.csv";
	final static private	String					ARCHIVOSANCIONES		= System.getProperty("user.dir") + File.separatorChar + "data" + File.separatorChar + "sanciones.csv";
	final static private	String					ARCHIVOUSUARIOS			= System.getProperty("user.dir") + File.separatorChar + "data" + File.separatorChar + "usuarios.csv";

	static private			Scanner					_entrada				= new Scanner(System.in);
	static private			AdministradorAlumnos	_aa						= new AdministradorAlumnos(MenuPrincipal.ARCHIVOINCIDENCIAS, MenuPrincipal.ARCHIVORECURSOS, MenuPrincipal.ARCHIVORESERVAS, MenuPrincipal.ARCHIVOSALAS, MenuPrincipal.ARCHIVOSALASYRECURSOS, MenuPrincipal.ARCHIVOSANCIONES);
	static private			AdministradorUsuarios	_au						= new AdministradorUsuarios(MenuPrincipal.ARCHIVOUSUARIOS);
	static private			GestorIncidencias		_gi						= new GestorIncidencias(MenuPrincipal.ARCHIVOINCIDENCIAS, MenuPrincipal.ARCHIVORECURSOS, MenuPrincipal.ARCHIVORESERVAS, MenuPrincipal.ARCHIVOSALAS, MenuPrincipal.ARCHIVOSALASYRECURSOS, MenuPrincipal.ARCHIVOSANCIONES, MenuPrincipal.ARCHIVOUSUARIOS);
	static private			GestorSalas				_gs						= new GestorSalas(MenuPrincipal.ARCHIVOINCIDENCIAS, MenuPrincipal.ARCHIVORECURSOS, MenuPrincipal.ARCHIVORESERVAS, MenuPrincipal.ARCHIVOSALAS, MenuPrincipal.ARCHIVOSALASYRECURSOS, MenuPrincipal.ARCHIVOSANCIONES);


	/**
	 * Método estático privado para dar de alta una nueva sala
	 *
	 * @return										boolean							Resultado de la operación
	 */

	private static boolean darDeAltaSala() {
		boolean				okDato;
		int					aforo					= 0;
		int					idTipoDeSalaElegido		= 0;
		String				nombre;
		String				ubicacion;
		ArrayList<Integer>	idsRecursos;
		ArrayList<Integer>	idsTiposDeSala;

		System.out.println("Para realizar un alta es necesario introducir datos que se le irán solicitando uno por uno");
		System.out.print("En primer lugar, introduzca el nombre de la sala: ");

		MenuPrincipal._entrada.nextLine();

		nombre = MenuPrincipal._entrada.nextLine();

		okDato = false;

		do {
			try {
				System.out.print("Introduzca el aforo de la sala: ");

				aforo = MenuPrincipal._entrada.nextInt();

				if(aforo > 0) {
					okDato = true;
				}
				else {
					throw new InputMismatchException("El número debe ser positivo");
				}
			}
			catch (InputMismatchException e) {
				System.out.println("El número introducido es incorrecto");
				System.out.println("Por favor, introduzca un número entero (sin decimales) y positivo e inténtelo de nuevo");

				MenuPrincipal._entrada.nextLine();
			}
		} while(!okDato);

		System.out.println("A continuación se le presentarán una serie de tipos de sala");

		idsTiposDeSala = MenuPrincipal._gs.obtenerTiposDeSala();

		for(int idTipoDeSala: idsTiposDeSala) {
			System.out.println(MenuPrincipal._gs.mostrarTipoDeSala(idTipoDeSala));
		}

		okDato = false;

		do {
			System.out.print("Seleccione el más adecuado a la sala, introduciendo su número: ");

			try {
				idTipoDeSalaElegido = MenuPrincipal._entrada.nextInt();

				if(idsTiposDeSala.contains(idTipoDeSalaElegido)) {
					okDato = true;
				}
				else {
					System.out.println("El tipo de sala introducido (" + idTipoDeSalaElegido + ") es incorrecto");
					System.out.println("Por favor, introduzca un número que esté en la lista");
				}
			}
			catch (InputMismatchException e) {
				System.out.println("El tipo de sala introducido (" + e.getLocalizedMessage() + ") es incorrecto");
				System.out.println("Por favor, recuerde introducir números solamente");

				MenuPrincipal._entrada.nextLine();
			}
		} while(!okDato);

		MenuPrincipal._entrada.nextLine();

		System.out.print("Introduzca la ubicación de la sala: ");

		ubicacion = MenuPrincipal._entrada.nextLine();

		idsRecursos = MenuPrincipal.seleccionarRecursos();

		if(MenuPrincipal._gs.validarDatos(nombre, aforo, idTipoDeSalaElegido, ubicacion, idsRecursos) != -1) {
			System.out.println("La sala se ha creado");

			return true;
		}
		else {
			System.out.println("La sala no ha podido ser creada");

			return false;
		}

	}


	/**
	 * Método estático privado de inicio de sesión
	 *
	 * @return										int								ID del usuario (-1 si no ha iniciado sesión)
	 */

	static private int iniciarSesion() {
		char					confirmacion	= 'S';
		int						idUsuario		= -1;

		while((Character.toUpperCase(confirmacion) == 'S') && ((idUsuario = MenuPrincipal._au.iniciarSesion()) == -1)) {
			System.out.println("Ha introducido unas credenciales de usuario no válidas");
			System.out.print("¿Desea volverlo a intentar? [S/n]: ");

			confirmacion = Character.toUpperCase(MenuPrincipal._entrada.next().charAt(0));
		}

		return idUsuario;
	}


	/**
	 * Método estático main de la clase (y del programa)
	 *
	 * @param		args							String							Argumentos recibidos por la línea de comandos
	 */

	static public void main(String[] args) {
		boolean					salir		= false;
		char					operacion;
		int						idUsuario	= -1;
		String					operaciones;

		System.out.println("Bienvenido al Gestor de salas");

		if((idUsuario = MenuPrincipal.iniciarSesion()) == -1) {
			salir = true;
		}

		if(!salir) {
			System.out.println("¡Bienvenido, " + MenuPrincipal._au.nombre(idUsuario) + "!");

			while(!salir) {
				operaciones = MenuPrincipal.menu(idUsuario);

				salir = ("".equals(operaciones));

				if(!salir) {
					System.out.print("Por favor, seleccione una operación para continuar [" + operaciones + "]: ");

					operacion = Character.toUpperCase(MenuPrincipal._entrada.next().charAt(0));

					if(operaciones.toUpperCase().indexOf(operacion) != -1) {
						switch(operacion) {
						case 'A':
							MenuPrincipal.darDeAltaSala();

							break;
						case 'B':
							MenuPrincipal.modificarReserva(idUsuario, false);

							break;
						case 'I':
							MenuPrincipal.registrarIncidencia();

							break;
						case 'M':
							MenuPrincipal.modificarReserva(idUsuario, true);

							break;
						case 'R':
							MenuPrincipal.reservar(idUsuario);

							break;

						case 'S':
							salir = true;

							break;

						default:													// Nunca se llegará aquí
							break;
						}
					}
					else {
						System.out.println("La operación seleccionada (" + operacion + ") es incorrecta o no tiene permisos para realizarla");
						System.out.println("Por favor, seleccione una válida del menú");
					}
				}
			}
		}

		System.out.println("Gracias por utilizar nuestro sistema");
		System.out.println("Saliendo...");
	}


	/**
	 * Método estático privado para mostrar el menú del programa
	 *
	 * @param		idUsuario						int								ID del usuario que utiliza el programa
	 *
	 * @return										String							Operaciones disponibles para ese usuario ("" si ninguna)
	 */

	static private String menu(int idUsuario) {
		int		idSancion;
		String	operaciones	= "";

		if(MenuPrincipal._au.alumno(idUsuario)) {
			if((idSancion = MenuPrincipal._aa.comprobarSancion(idUsuario)) == -1) {
				System.out.println("El menú de operaciones es el siguiente:");
				System.out.println("Introduzca 'r' para realizar una reserva");

				operaciones += 'r';

				if(MenuPrincipal._gs.buscarReservas(idUsuario, false) != null) {
					System.out.println("Introduzca 'b' para borrar una reserva ya existente");
					System.out.println("Introduzca 'm' para modificar una reserva ya existente");

					operaciones += "bm";
				}

				System.out.println("Introduzca 's' para salir");

				operaciones += 's';
			}
			else {
				System.out.println(MenuPrincipal._aa.notificarAlumnoSancionado(idSancion));
			}
		}
		else if(MenuPrincipal._au.empleado(idUsuario)) {
			System.out.println("El menú de operaciones es el siguiente:");
			System.out.println("Introduzca 'a' para dar de alta una nueva sala de estudio");
			System.out.println("Introduzca 'i' para registrar una nueva incidencia");
			System.out.println("Introduzca 's' para salir");

			operaciones += "ais";
		}
		else {
			System.out.println("Lo sentimos, su perfil de usuario no le permite utilizar este servicio");
		}

		return operaciones;
	}


	/**
	 * Método estático privado para modificar o eliminar una reserva
	 *
	 * @param		idAlumno						int								ID del alumno que solicita modificar la reserva
	 * @param		modificar						boolean							<em>true</em> para modificaciones, <em>false</em> para borrados
	 */

	static private void modificarReserva(int idAlumno, boolean modificar) {
		int idReserva = MenuPrincipal.seleccionarReserva(MenuPrincipal._gs.buscarReservas(idAlumno, false));

		if(idReserva >= 0) {
			switch(MenuPrincipal._gs.suspenderReserva(idAlumno, idReserva)) {
			default:
				if(modificar) {
					if(MenuPrincipal.reservar(idAlumno)) {
						MenuPrincipal._gs.eliminarReserva(idReserva);

						System.out.println("La reserva ha sido modificada satisfactoriamente");
					}
					else {
						MenuPrincipal._gs.reanudarReserva(idAlumno, idReserva);

						System.out.println("La reserva no ha sido modificada");
					}
				}
				else {
					MenuPrincipal._gs.eliminarReserva(idReserva);

					System.out.println("La reserva ha sido eliminada satisfactoriamente");
				}

				break;
			case -1:
				System.out.println("La reserva seleccionada (" + idReserva + ") no existe");

				break;
			case -2:
				System.out.println("La reserva seleccionada (" + idReserva + ") no es de su propiedad");

				break;
			}
		}
		else /* if(idReserva == -1) */ {
			System.out.println("La operación ha sido cancelada");
		}
	}


	/**
	 * Método estático privado para seleccionar una reserva
	 *
	 * @param		idsReservas						ArrayList&lt;Integer&gt;		Lista de IDs de reservas asociadas al alumno
	 *
	 * @return										int								ID de la reserva seleccionada (-1 si no, -2 si el usuario introduce un valor no válido)
	 */

	static private int seleccionarReserva(ArrayList<Integer> idsReservas) {
		String opcion;

		System.out.println("A continuación se le presentarán todas las reservas pendientes que ha realizado");

		for(int idReserva: idsReservas) {
			System.out.println("Reserva " + idReserva + ":");
			System.out.println(MenuPrincipal._gs.mostrarReserva(idReserva));
		}

		System.out.print("Seleccione la reserva que desee (enter para cancelar): ");

		MenuPrincipal._entrada.nextLine();

		opcion = MenuPrincipal._entrada.nextLine();

		if(!"".equals(opcion)) {
			try {
				return Integer.parseInt(opcion);
			} catch (NumberFormatException e) {
				return -2;
			}
		}
		else {
			return -1;
		}
	}


	/**
	 * Método estático privado para mostrar la lista de IDs de reservas dada en formato paginado y así seleccionar una
	 *
	 * @param		idsReservas						ArrayList&lt;Integer&gt;		Lista de IDs de reservas
	 *
	 * @return										int								ID de reserva seleccionado (-1 si no)
	 *
	 * TODO: Reducir la complejidad N-Path
	 */

	private static int paginadorReservas(ArrayList<Integer> idsReservas) {
		final	int							PAGINADO	= 5;
		final	HashMap<Integer, String>	intAString	= new HashMap<Integer, String>();

		int									i			= 1;
		int									idReserva	= -1;
		int									offset		= 0;
		int									tamLista;
		String								opcion;
		String								plural;

		intAString.put(i++, ""			);
		intAString.put(i++, "dos"		);
		intAString.put(i++, "tres"		);
		intAString.put(i++, "cuatro"	);
		intAString.put(i++, "cinco"		);

		tamLista = idsReservas.size();

		plural = ((tamLista == 1) ? ("") : ("s"));

		System.out.println("A continuación se mostrarán la" + plural + (("s".equals(plural)) ? (" ") : (""))
				+ intAString.get((tamLista > PAGINADO) ? (PAGINADO) : (tamLista)) + " última" + plural + " reserva" + plural + " del sistema");

		MenuPrincipal._entrada.nextLine();

		do {
			for (i = (tamLista + offset) - 1; (i >= 0) && (i > ((tamLista + offset) - PAGINADO - 1)); i--) {
				System.out.println("Reserva " + idsReservas.get(i) + ":");
				System.out.println(MenuPrincipal._gs.mostrarReserva(idsReservas.get(i)));
			}

			System.out.print("Seleccione una reserva");

			if(tamLista > PAGINADO) {
				System.out.print(" o introduzca ");

				if(i >= 0) {
					System.out.print("< ");
				}

				if((i >= 0) && (offset < 0)) {
					System.out.print("o ");
				}

				if(offset < 0) {
					System.out.print("> ");
				}

				System.out.print("para desplazarse por los registros (enter para cancelar)");
			}

			System.out.print(": ");

			opcion = MenuPrincipal._entrada.nextLine();

			try {
				idReserva = Integer.parseInt(opcion);

				if(!(idsReservas.contains(idReserva))) {
					idReserva = -1;

					System.out.println("Identificador de reserva no válido");
					System.out.println("Por favor, inténtelo de nuevo");
				}
			}
			catch (NumberFormatException e) {
				if("<".equals(opcion) && (i >= 0)) {
					offset -= PAGINADO;
				}
				else if(">".equals(opcion) && (offset < 0)) {
					offset += PAGINADO;
				}
				else if("".equals(opcion)) {
					System.out.println("Operación cancelada");

					break;
				}
				else {
					System.out.println("Entrada no reconocida");
					System.out.println("Por favor, inténtelo de nuevo");
				}
			}
		} while (idReserva == -1);

		return idReserva;
	}


	/**
	 * Método estátito privado para registrar una incidencia
	 */

	private static void registrarIncidencia() {
		boolean				okDato;
		int					idIncidencia;
		int					idReserva;
		int					idTipoDeIncidenciaElegido	= -1;
		String				descripcion;
		ArrayList<Integer>	idsTiposDeIncidencia;

		if((idReserva = MenuPrincipal.paginadorReservas(MenuPrincipal._gs.obtenerReservas(false))) != -1) {
			System.out.print("Describa lo mejor posible lo sucedido: ");

			descripcion = MenuPrincipal._entrada.nextLine();

			System.out.println("A continuación se le presentarán una serie de tipos de incidencias");

			idsTiposDeIncidencia = MenuPrincipal._gi.obtenerTiposDeIncidencia();

			for(int idTipoDeIncidencia: idsTiposDeIncidencia) {
				System.out.println(MenuPrincipal._gi.mostrarTipoDeIncidencia(idTipoDeIncidencia));
			}

			okDato = false;

			do {
				System.out.print("Seleccione el más adecuado a la incidencia, introduciendo su número: ");

				try {
					idTipoDeIncidenciaElegido = MenuPrincipal._entrada.nextInt();

					if(idsTiposDeIncidencia.contains(idTipoDeIncidenciaElegido)) {
						okDato = true;
					}
					else {
						System.out.println("El tipo de incidencia introducido (" + idTipoDeIncidenciaElegido + ") es incorrecto");
						System.out.println("Por favor, introduzca un número que esté en la lista");
					}
				}
				catch (InputMismatchException e) {
					System.out.println("El tipo de incidencia introducido (" + e.getLocalizedMessage() + ") es incorrecto");
					System.out.println("Por favor, recuerde introducir números solamente");

					MenuPrincipal._entrada.nextLine();
				}
			} while(!okDato);

			if((idIncidencia = MenuPrincipal._gi.registrarIncidencia(idReserva, descripcion, idTipoDeIncidenciaElegido)) != -1) {
				System.out.println("La incidencia ha sido registrada");
				MenuPrincipal.sancionarAlumno(MenuPrincipal._gs.obtenerAlumno(idReserva), idIncidencia, descripcion);
			}
			else {
				System.out.println("La incidencia no ha podido ser registrada");
			}
		}
	}


	/**
	 * Método estático privado para sancionar a un alumno
	 *
	 * @param		idAlumno						int								ID del alumno a sancionar
	 * @param		idIncidencia					int								ID de la incidencia asociada a la sanción
	 * @param		descripcion						String							Descripción de la incidencia
	 */

	private static void sancionarAlumno(int idAlumno, int idIncidencia, String descripcion) {
		boolean		okDato;
		int			codigoSancion	= -1;
		int			duracion		= 0;
		LocalDate	ahora;
		LocalDate	fecha			= null;

		System.out.print("¿Es necesario aplicar una sanción al alumno? [s/N]: ");

		okDato = false;

		if(Character.toUpperCase(MenuPrincipal._entrada.next().charAt(0)) == 'S') {
			do {
				try {
					System.out.print("En primer lugar, introduzca el código de sanción: ");

					codigoSancion = MenuPrincipal._entrada.nextInt();

					if(codigoSancion >= 0) {
						okDato = true;
					}
					else {
						throw new InputMismatchException("El número no debe ser negativo");
					}
				}
				catch (InputMismatchException e) {
					System.out.println("El número introducido es incorrecto");
					System.out.println("Por favor, introduzca un número entero (sin decimales) y no negativo e inténtelo de nuevo");

					MenuPrincipal._entrada.nextLine();
				}
			} while(!okDato);

			okDato = false;

			MenuPrincipal._entrada.nextLine();

			do {
				try {
					System.out.print("Introduzca la fecha de comienzo de la sanción en formato \"dd/MM/yyyy\": ");

					fecha = LocalDate.parse(MenuPrincipal._entrada.nextLine(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));

					okDato = true;
				}
				catch (DateTimeParseException e) {
					System.out.println("La fecha introducida es incorrecta");
					System.out.println("Por favor, inténtelo de nuevo");
				}

				ahora = LocalDate.now();
				if(okDato && !(fecha.isAfter(ahora))) {
					System.out.println("La fecha introducida es del pasado");
					System.out.println("Por favor, inténtelo de nuevo");
					System.out.println("Hoy es " + ahora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

					okDato = false;
				}
			} while(!okDato);

			okDato = false;

			do {
				try {
					System.out.print("Introduzca la duración (en días) de sanción: ");

					duracion = MenuPrincipal._entrada.nextInt();

					if(duracion > 0) {
						okDato = true;
					}
					else {
						throw new InputMismatchException("El número debe ser positivo");
					}
				}
				catch (InputMismatchException e) {
					System.out.println("El número introducido es incorrecto");
					System.out.println("Por favor, introduzca un número entero (sin decimales) y positivo e inténtelo de nuevo");

					MenuPrincipal._entrada.nextLine();
				}
			} while(!okDato);

			MenuPrincipal._gi.sancionarAlumno(idIncidencia, codigoSancion, fecha, duracion);

			MenuPrincipal._gi.enviarNotificacion(idAlumno, descripcion, codigoSancion, fecha, duracion);
		}
	}


	/**
	 * Método estático privado para reservar una sala
	 *
	 * @param		idAlumno						int								ID del alumno que reserva la sala
	 *
	 * @return										boolean							Resultado de la operación
	 *
	 * TODO: Reducir la complejidad N-Path
	 */

	static private boolean reservar(int idAlumno) {
		boolean				okDato;
		int					alumnos					= 0;
		int					duracion				= 0;
		int					idReserva;
		ArrayList<Integer>	idsRecursos;
		ArrayList<Integer>	idsSalasCandidatas;
		String				asignatura;
		LocalDateTime		ahora;
		LocalDateTime		fechaYHora				= null;


		System.out.println("Para realizar una reserva es necesario introducir datos que se le irán solicitando uno por uno");

		okDato = false;

		do {
			try {
				System.out.print("En primer lugar, introduzca las personas que ocuparán la sala reservada: ");

				alumnos = MenuPrincipal._entrada.nextInt();

				if(alumnos > 0) {
					okDato = true;
				}
				else {
					throw new InputMismatchException("El número debe ser positivo");
				}
			}
			catch (InputMismatchException e) {
				System.out.println("El número introducido es incorrecto");
				System.out.println("Por favor, introduzca un número entero (sin decimales) y positivo e inténtelo de nuevo");

				MenuPrincipal._entrada.nextLine();
			}
		} while(!okDato);

		MenuPrincipal._entrada.nextLine();

		idsRecursos = MenuPrincipal.seleccionarRecursos();

		idsSalasCandidatas = MenuPrincipal._gs.buscarSala(alumnos, idsRecursos);

		if(idsSalasCandidatas != null) {
			System.out.print("Introduzca la asignatura para la que será reservada la sala: ");

			asignatura = MenuPrincipal._entrada.nextLine();

			okDato = false;

			do {
				try {
					System.out.print("Introduzca la fecha y la hora de la reserva en formato \"HH:mm dd/MM/yyyy\": ");

					fechaYHora = LocalDateTime.parse(MenuPrincipal._entrada.nextLine(), DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"));

					okDato = true;
				}
				catch (DateTimeParseException e) {
					System.out.println("La fecha introducida es incorrecta");
					System.out.println("Por favor, inténtelo de nuevo");
				}

				ahora = LocalDateTime.now();
				if(okDato && !(fechaYHora.isAfter(ahora))) {
					System.out.println("La fecha introducida es del pasado");
					System.out.println("Por favor, inténtelo de nuevo");
					System.out.println("Ahora mismo son las " + ahora.format(DateTimeFormatter.ofPattern("HH:mm")) + " del " + ahora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

					okDato = false;
				}
			} while(!okDato);

			okDato = false;

			do {
				try {
					System.out.print("Para finalizar, introduzca la duración (en horas) de la reserva: ");

					duracion = MenuPrincipal._entrada.nextInt();

					if(duracion > 0) {
						okDato = true;
					}
					else {
						throw new InputMismatchException("El número debe ser positivo");
					}
				}
				catch (InputMismatchException e) {
					System.out.println("El número introducido es incorrecto");
					System.out.println("Por favor, introduzca un número entero (sin decimales) y positivo e inténtelo de nuevo");

					MenuPrincipal._entrada.nextLine();
				}
			} while(!okDato);

			if((idReserva = MenuPrincipal._gs.elegirSala(alumnos, idAlumno, asignatura, duracion, fechaYHora, idsSalasCandidatas)) != -1) {
				System.out.println("La sala más adecuada para sus requisitos es:" + System.getProperty("line.separator") + MenuPrincipal._gs.mostrarReserva(idReserva));
				System.out.print("¿Está de acuerdo? [S/n]: ");

				if(Character.toUpperCase(MenuPrincipal._entrada.next().charAt(0)) != 'N') {
					MenuPrincipal._gs.confirmarReserva(idReserva);

					System.out.println("La reserva se ha confirmado");

					return true;
				}
				else {
					MenuPrincipal._gs.eliminarReserva(idReserva);

					System.out.println("La reserva se ha cancelado");

					return false;
				}
			}
			else {
				System.out.println("No ha sido posible encontrar una sala acorde a los requisitos solicitados");

				return false;
			}
		}
		else {
			System.out.println("No ha sido posible encontrar una sala acorde a los requisitos solicitados");

			return false;
		}
	}


	/**
	 * Método estático privado para seleccionar recursos
	 *
	 * @return										ArrayList&lt;Integer&gt;		Lista de IDs de recursos seleccionados
	 */

	private static ArrayList<Integer> seleccionarRecursos() {
		String				linea;
		ArrayList<Integer>	idsRecursos;
		ArrayList<Integer>	idsRecursosIncorrectos	= new ArrayList<Integer>();
		ArrayList<Integer>	idsRecursosPedidos		= new ArrayList<Integer>();
		StringTokenizer		stLinea;

		System.out.println("A continuación se le presentarán una serie de recursos disponibles");

		idsRecursos = MenuPrincipal._gs.obtenerRecursos();

		for(int idRecurso: idsRecursos) {
			System.out.println(MenuPrincipal._gs.mostrarRecurso(idRecurso));
		}

		System.out.print("Seleccione tantos como desee, introduciendo sus números, separados por espacios: ");
		linea = MenuPrincipal._entrada.nextLine();

		stLinea = new StringTokenizer(linea, " ");

		while(stLinea.hasMoreTokens()) {
			try {
				idsRecursosPedidos.add(Integer.parseInt(stLinea.nextToken()));
			} catch (NumberFormatException e) {
				System.out.println("Uno de los recursos introducidos (" + e.getLocalizedMessage() + ") es incorrecto y no será tomado en cuenta");
				System.out.println("Por favor, recuerde introducir números solamente");
			}
		}

		for(Integer idRecursoPedido: idsRecursosPedidos) {
			if(!idsRecursos.contains(idRecursoPedido)) {
				System.out.println("El número de recurso " + idRecursoPedido + " es incorrecto y no será tomado en cuenta");

				idsRecursosIncorrectos.add(idRecursoPedido);
			}
		}

		for(Integer idRecursoIncorrecto: idsRecursosIncorrectos) {
			idsRecursosPedidos.remove(idRecursoIncorrecto);
		}

		return idsRecursosPedidos;
	}
}
