package mdas.p2.gestorreservamgr;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.StringTokenizer;

// import mdas.p2.gestorreservamgr.Incidencia;
// import mdas.p2.gestorreservamgr.Reserva;
// import mdas.p2.gestorreservamgr.Sala;
// import mdas.p2.gestorreservamgr.SalaYRecurso;
// import mdas.p2.gestorreservamgr.TipoIncidencia;


/**
 * Clase GestorReservaMgr
 * Componente de gestión de reservas del sistema
 * Es implementado por medio del patrón Singleton, con el fin de prevenir la existencia de más de un gestor
 * Implementa la interfaz IReservaMgt
 *
 * @author		Rafael Carlos Méndez Rodríguez (i82meror)
 * @date		09/06/2020
 * @version		1.8.0
 *
 * TODO: Reducir longitud
 */


public class ReservaMgr implements IReservaMgt {
	private static	ReservaMgr				_instance				= null;
	private			String					_archivoIncidencias;
	private			String					_archivoRecursos;
	private			String					_archivoReservas;
	private			String					_archivoSalas;
	private			String					_archivoSalasYRecursos;
	private			String					_archivoSanciones;
	private			ArrayList<Incidencia>	_incidencias;
	private			ArrayList<Recurso>		_recursos;
	private			ArrayList<Reserva>		_reservas;
	private			ArrayList<Sala>			_salas;
	private			ArrayList<SalaYRecurso>	_salasYRecursos;
	private			ArrayList<Sancion> 		_sanciones;


	/**
	 * Constructor de clase
	 * Privado, requisito del patrón Singleton
	 * Inicializa las listas del gestor
	 *
	 * @param		archivoIncidencias				String							Ruta del archivo donde incidencias
	 * @param		archivoRecursos					String							Ruta del archivo donde recursos
	 * @param		archivoReservas					String							Ruta del archivo donde reservas
	 * @param		archivoSalas					String							Ruta del archivo donde salas
	 * @param		archivoSalasYRecursos			String							Ruta del archivo donde salas y recursos
	 * @param		archivoSanciones				String							Ruta del archivo donde sanciones
	 */

	private ReservaMgr(String archivoIncidencias, String archivoRecursos, String archivoReservas, String archivoSalas, String archivoSalasYRecursos, String archivoSanciones) {
		this._archivoIncidencias		= archivoIncidencias;
		this._archivoRecursos			= archivoRecursos;
		this._archivoReservas			= archivoReservas;
		this._archivoSalas				= archivoSalas;
		this._archivoSalasYRecursos		= archivoSalasYRecursos;
		this._archivoSanciones			= archivoSanciones;

		this._incidencias				= new ArrayList<Incidencia>();
		this._recursos					= new ArrayList<Recurso>();
		this._reservas					= new ArrayList<Reserva>();
		this._salas						= new ArrayList<Sala>();
		this._salasYRecursos			= new ArrayList<SalaYRecurso>();
		this._sanciones					= new ArrayList<Sancion>();

		this.cargar();
	}


	/**
	 * Método estático para obtener la única instancia válida (o crearla si no existe) del gestor
	 *
	 * @param		archivoIncidencias				String							Ruta del archivo donde incidencias
	 * @param		archivoRecursos					String							Ruta del archivo donde recursos
	 * @param		archivoReservas					String							Ruta del archivo donde reservas
	 * @param		archivoSalas					String							Ruta del archivo donde salas
	 * @param		archivoSalasYRecursos			String							Ruta del archivo donde salas y recursos
	 * @param		archivoSanciones				String							Ruta del archivo donde sanciones
	 *
	 * @return										ReservaMgr						Instancia del gestor
	 */

	public static ReservaMgr getInstance(String archivoIncidencias, String archivoRecursos, String archivoReservas, String archivoSalas, String archivoSalasYRecursos, String archivoSanciones) {
		if(ReservaMgr._instance == null) {
			ReservaMgr._instance = new ReservaMgr(archivoIncidencias, archivoRecursos, archivoReservas, archivoSalas, archivoSalasYRecursos, archivoSanciones);
		}

		return ReservaMgr._instance;
	}


	/**
	 * Buscador de incidencias
	 * Busca una incidencia a través de un ID de reserva
	 *
	 * @param		idReserva						int								ID de la reserva para buscar sus incidencias
	 *
	 * @return										int[]							Vector de incidencias asociadas a la reserva (null si ninguna)
	 */

	@Override
	public ArrayList<Integer> buscarIncidencias(int idReserva) {
		ArrayList<Integer> res = new ArrayList<Integer>();

		for(Incidencia i: this._incidencias) {
			if(i.idReserva() == idReserva) {
				res.add(i.id());
			}
		}

		if(res.size() == 0) {
			res = null;
		}

		return res;
	}


	/**
	 * Método privado para buscar recursos
	 * Busca un recurso por su ID
	 *
	 * @param		idRecurso						int								ID del recurso a buscar
	 *
	 * @return										int								Posición en la lista de recursos (-1 si no encontrado)
	 */

	private int buscarRecurso(int idRecurso) {
		int	i;
		int res			= -1;
		int	tamLista	= this._recursos.size();

		for(i = 0; i < tamLista; i++) {
			if((this._recursos.get(i).id() == idRecurso)) {
				res = i;

				break;
			}
		}

		return res;
	}


	/**
	 * Método privado para buscar reservas
	 * Busca una reserva por su ID
	 *
	 * @param		idReserva						int								ID de la reserva a buscar
	 * @param		todas							boolean							Buscar en todas las reservas o sólo las futuras
	 *
	 * @return										int								Posición en la lista de reservas (-1 si no encontrada)
	 */

	private int buscarReserva(int idReserva, boolean todas) {
		int		i;
		int		res			= -1;
		int		tamLista	= this._reservas.size();
		Reserva	reserva;

		for(i = 0; i < tamLista; i++) {
			reserva = this._reservas.get(i);

			if(reserva.id() == idReserva) {
				if(todas || reserva.fechaYHora().isAfter(LocalDateTime.now())) {
					res = i;
				}

				break;
			}
		}

		return res;
	}


	/**
	 * Buscador de reservas
	 * Busca las reservas de un alumno a través de la ID del alumno asociado a las mismas
	 *
	 * @param		idAlumno						int								ID del alumno
	 * @param		todas							boolean							Buscar todas las reservas o sólo las futuras
	 *
	 * @return										ArrayList&lt;Integer&gt;		ArrayList de IDs de reservas asociadas al alumno (null si ninguna)
	 */

	@Override
	public ArrayList<Integer> buscarReservas(int idAlumno, boolean todas) {
		ArrayList<Integer> res = new ArrayList<Integer>();

		for(Reserva r: this._reservas) {
			if((r.idAlumno() == idAlumno) && (todas || r.fechaYHora().isAfter(LocalDateTime.now()))) {
				res.add(r.id());
			}
		}

		if(res.size() == 0) {
			res = null;
		}

		return res;
	}


	/**
	 * Método privado para buscar salas
	 * Busca una sala por su ID
	 *
	 * @param		idSala							int								ID de la sala a buscar
	 *
	 * @return										int								Posición en la lista de salas (-1 si no encontrada)
	 */

	private int buscarSala(int idSala) {
		int	i;
		int res			= -1;
		int	tamLista	= this._salas.size();

		for(i = 0; i < tamLista; i++) {
			if((this._salas.get(i).id() == idSala)) {
				res = i;

				break;
			}
		}

		return res;
	}


	/**
	 * Buscador de salas
	 * Busca una sala adecuada al aforo y a los recursos proporcionados
	 *
	 * @param		aforo							int								Aforo mínimo requerido
	 * @param		idsRecursos						ArrayList&lt;Integer&gt;		IDs de los recursos mínimos requeridos
	 *
	 * @return										ArrayList&lt;Integer&gt;		ArrayList de IDs de salas que cumplen los requisitos (null si ninguno)
	 */

	@Override
	public ArrayList<Integer> buscarSala(int aforo, ArrayList<Integer> idsRecursos) {
		int					recursosQueTengo;
		ArrayList<Integer>	res					= new ArrayList<Integer>();

		for(Sala s: this._salas) {
			if((s.aforo() >= aforo)) {
				recursosQueTengo = 0;

				for(SalaYRecurso syr: this._salasYRecursos) {
					if(((s.id() == syr.idSala()) && idsRecursos.contains(syr.idRecurso()))) {
						recursosQueTengo++;
					}
				}

				if(recursosQueTengo >= idsRecursos.size()) {
					res.add(s.id());
				}
			}
		}

		if(res.size() == 0) {
			res = null;
		}

		return res;
	}


	/**
	 * Buscador de sanciones
	 * Busca la sanción asociada a la incidencia dada
	 *
	 * @param		idIncidencia					int								ID de la incidencia
	 *
	 * @return										int								ID de la sanción (-1 si no la hay)
	 */

	@Override
	public int buscarSancion(int idIncidencia) {
		Sancion res = null;

		for(Sancion s: this._sanciones) {
			if(idIncidencia == s.idIncidencia()) {
				res = s;

				break;
			}
		}

		if((res != null) && (res.fecha().plusDays(res.duracion()).compareTo(LocalDate.now()) >= 0)) {
			return res.id();
		}
		else {
			return -1;
		}
	}


	/**
	 * Método privado para buscar el tipo de incidencia
	 *
	 * @param		int_TipoIncidencia				int								ID de tipo de incidencia
	 *
	 * @return										TipoIncidencia					El tipo de incidencia encontrado
	 */

	private TipoIncidencia buscarTipoIncidencia(int int_TipoIncidencia) {
		TipoIncidencia tipoIncidencia = null;

		for(TipoIncidencia ti: TipoIncidencia.values()) {
			if(int_TipoIncidencia == ti.id()) {
				tipoIncidencia = ti;

				break;
			}
		}

		return tipoIncidencia;
	}


	/**
	 * Método privado para buscar el tipo de sala
	 *
	 * @param		int_TipoSala					int								ID de tipo de sala
	 *
	 * @return										TipoSala						El tipo de sala encontrado
	 */

	private TipoSala buscarTipoSala(int int_TipoSala) {
		TipoSala tipoSala = null;

		for(TipoSala ts: TipoSala.values()) {
			if(int_TipoSala == ts.id()) {
				tipoSala = ts;

				break;
			}
		}

		return tipoSala;
	}


	/**
	 * Método de carga de archivos
	 * Carga los archivos CSV solicitados en la memoria del gestor
	 *
	 * @return										boolean							Resultado de la operación
	 *
	 * TODO: Reducir longitud
	 */

	private boolean cargar() {
		int					i;
		String				linea;
		ArrayList<String>	campos;
		BufferedReader		buffer;
		StringTokenizer		stLinea;

		for(i = 0; i <= 5; i++) {
			try {
				switch(i) {
				case 0:
					buffer = new BufferedReader(new FileReader(new File(this._archivoIncidencias)));

					break;
				case 1:
					buffer = new BufferedReader(new FileReader(new File(this._archivoRecursos)));

					break;
				case 2:
					buffer = new BufferedReader(new FileReader(new File(this._archivoReservas)));

					break;
				case 3:
					buffer = new BufferedReader(new FileReader(new File(this._archivoSalas)));

					break;
				case 4:
					buffer = new BufferedReader(new FileReader(new File(this._archivoSalasYRecursos)));

					break;
				case 5:
					buffer = new BufferedReader(new FileReader(new File(this._archivoSanciones)));

					break;
				default:
					buffer = null;

					break;
				}

				while((linea = buffer.readLine()) != null) {
					stLinea = new StringTokenizer(linea, ",");

					campos = new ArrayList<String>();

					while(stLinea.hasMoreTokens()) {
						campos.add(stLinea.nextToken());
					}

					switch(i) {
					case 0:
						this._incidencias.add(new Incidencia(
								Integer.parseInt(campos.get(0)),
								Integer.parseInt(campos.get(1)),
								campos.get(2),										// FIXME: Lidiar con las comas "extra"
								this.buscarTipoIncidencia(Integer.parseInt(campos.get(campos.size() - 1)))
								));

						break;
					case 1:
						this._recursos.add(new Recurso(
								Integer.parseInt(campos.get(0)),
								campos.get(1),										// FIXME: Lidiar con las comas "extra"
								campos.get(2)										// FIXME: Lidiar con las comas "extra"
								));

						break;
					case 2:
						this._reservas.add(new Reserva(
								Integer.parseInt(campos.get(0)),
								Integer.parseInt(campos.get(1)),
								Integer.parseInt(campos.get(2)),
								Integer.parseInt(campos.get(3)),
								campos.get(4),										// FIXME: Lidiar con las comas "extra"
								Integer.parseInt(campos.get(campos.size() - 3)),
								Boolean.parseBoolean(campos.get(campos.size() - 2)),
								LocalDateTime.parse(campos.get(campos.size() - 1), DateTimeFormatter.ISO_LOCAL_DATE_TIME)
								));

						break;
					case 3:
						this._salas.add(new Sala(Integer.parseInt(campos.get(0)),
								Integer.parseInt(campos.get(1)),
								campos.get(2),										// FIXME: Lidiar con las comas "extra"
								this.buscarTipoSala(Integer.parseInt(campos.get(3))),
								campos.get(4)										// FIXME: Lidiar con las comas "extra"
								));

						break;
					case 4:
						this._salasYRecursos.add(new SalaYRecurso(
								Integer.parseInt(campos.get(0)),
								Integer.parseInt(campos.get(1))
								));

						break;
					case 5:
						this._sanciones.add(new Sancion(
								Integer.parseInt(campos.get(0)),
								Integer.parseInt(campos.get(1)),
								Integer.parseInt(campos.get(2)),
								Integer.parseInt(campos.get(3)),
								LocalDate.parse(campos.get(4), DateTimeFormatter.ISO_LOCAL_DATE)
								));

						break;
					default:														// Nunca se llegará aquí
						break;
					}
				}

				buffer.close();
			}
			catch(FileNotFoundException e) {
				System.out.println("Error: " + e.getMessage());
			}
			catch(IOException e) {
				System.out.println("Error: " + e.getMessage());

				return false;
			}
		}

		return true;
	}


	/**
	 * Método de confirmación del registro de una reserva
	 * Marca como reservada una reserva de sala en estado prerreservada
	 *
	 * @param		idReserva						int								ID de la reserva a confirmar
	 *
	 * @return										boolean							Resultado de la operación
	 */


	@Override
	public boolean confirmarReserva(int idReserva) {
		int posReserva = this.buscarReserva(idReserva, false);

		if(posReserva != -1) {
			this._reservas.get(posReserva).estado(true);

			this.guardar();

			return true;
		}
		else {
			return false;
		}
	}


	/**
	 * Método de eliminación de una reserva
	 * Elimina una reserva de sala de la lista
	 *
	 * @param		idReserva						int								ID de la reserva a eliminar
	 *
	 * @return										boolean							Resultado de la operación
	 */

	@Override
	public boolean eliminarReserva(int idReserva) {
		int posReserva = this.buscarReserva(idReserva, false);

		if(posReserva != -1) {
			this._reservas.remove(posReserva);

			this.guardar();

			return true;
		}
		else {
			return false;
		}
	}


	/**
	 * Método de guardado de archivos
	 * Guarda la memoria del gestor en los archivos CSV solicitados
	 *
	 * @return										boolean							Resultado de la operación
	 */


	private boolean guardar() {
		int					i;
		ArrayList<String>	lineas;
		BufferedWriter		buffer	= null;

		for(i = 0; i <= 5; i++) {
			lineas	= new ArrayList<String>();

			try {
				switch(i) {
				case 0:
					buffer = new BufferedWriter(new FileWriter(new File(this._archivoIncidencias)));

					for(Incidencia in: this._incidencias) {
						lineas.add(in.toCsv());
					}

					break;
				case 1:
					buffer = new BufferedWriter(new FileWriter(new File(this._archivoRecursos)));

					for(Recurso rec: this._recursos) {
						lineas.add(rec.toCsv());
					}

					break;
				case 2:
					buffer = new BufferedWriter(new FileWriter(new File(this._archivoReservas)));

					for(Reserva res: this._reservas) {
						lineas.add(res.toCsv());
					}

					break;
				case 3:
					buffer = new BufferedWriter(new FileWriter(new File(this._archivoSalas)));

					for(Sala sal: this._salas) {
						lineas.add(sal.toCsv());
					}

					break;
				case 4:
					buffer = new BufferedWriter(new FileWriter(new File(this._archivoSalasYRecursos)));

					for(SalaYRecurso syl: this._salasYRecursos) {
						lineas.add(syl.toString());
					}

					break;
				case 5:
					buffer = new BufferedWriter(new FileWriter(new File(this._archivoSanciones)));

					for(Sancion san: this._sanciones) {
						lineas.add(san.toString());
					}

					break;
				default:															// Nunca se llegará aquí
					break;
				}

				for(String linea: lineas) {
					buffer.write(linea + System.getProperty("line.separator"));
				}

				buffer.close();
			}
			catch(FileNotFoundException e) {
				System.out.println("Error: " + e.getMessage());

				return false;
			}
			catch(IOException e) {
				System.out.println("Error: " + e.getMessage());

				return false;
			}
		}

		return true;
	}


	/**
	 * Observador en texto de un recurso
	 * Recoge los datos de un recurso y los convierte en un String en texto apto para mostrárselo al usuario
	 *
	 * @param		idRecurso						int								ID del recurso a mostrar
	 *
	 * @return										String							Texto con los datos del recurso ("" si no encontrada)
	 */

	@Override
	public String mostrarRecurso(int idRecurso) {
		int		posRecurso = this.buscarRecurso(idRecurso);
		String	res;
		Recurso	recurso;

		if(posRecurso != -1) {
			recurso = this._recursos.get(posRecurso);

			res = recurso.id() + ": " + recurso.nombre() + " (" + recurso.descripcion() + ")";

			return res;
		}
		else {
			return "";
		}
	}


	/**
	 * Observador en texto de una reserva
	 * Recoge los datos de una reversa y los convierte en un String en texto apto para mostrárselo al usuario
	 *
	 * @param		idReserva						int								ID de la reserva a mostrar
	 *
	 * @return										String							Texto con los datos de la reserva ("" si no encontrada)
	 */

	@Override
	public String mostrarReserva(int idReserva) {
		int		posReserva = this.buscarReserva(idReserva, true);
		String	res;
		Recurso	recurso;
		Reserva	reserva;
		Sala	sala;

		if(posReserva != -1) {
			reserva = this._reservas.get(posReserva);

			sala = this._salas.get(this.buscarSala(reserva.idSala()));

			res =
					"Nombre de la sala: " + sala.nombre() + System.getProperty("line.separator") +
					"Aforo de la sala: " + sala.aforo() + " personas" + System.getProperty("line.separator") +
					"Ubicación de la sala: " + sala.ubicacion() + System.getProperty("line.separator") +
					"Fecha de la reserva: " + reserva.fechaYHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + System.getProperty("line.separator") +
					"Hora de entrada: " + reserva.fechaYHora().format(DateTimeFormatter.ofPattern("HH:mm")) + System.getProperty("line.separator") +
					"Hora de salida: " + reserva.fechaYHora().plusHours(reserva.duracion()).format(DateTimeFormatter.ofPattern("HH:mm")) + System.getProperty("line.separator") +
					"Tiempo total de ocupación: " + reserva.duracion() + " hora" + ((reserva.duracion() > 1) ? ("s") : ("")) + System.getProperty("line.separator") +
					"Recursos disponibles: " + System.getProperty("line.separator")
					;

			for(SalaYRecurso syr: this._salasYRecursos) {
				if(reserva.idSala() == syr.idSala()) {
					recurso = this._recursos.get(this.buscarRecurso(syr.idRecurso()));

					res += recurso.nombre() + " (" + recurso.descripcion() + ")" + System.getProperty("line.separator");
				}
			}

			return res;
		}
		else {
			return "";
		}
	}




	@Override
	public String notificarAlumnoSancionado(int idSancion) {
		Sancion sancion = this.obtenerSancion(idSancion);

		return "Lo sentimos, usted está sancionado hasta el día " + sancion.fecha().plusDays(sancion.duracion()).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " por el siguiente motivo: \"" + IReservaMgt.describirSancion(sancion.codigo()) + "\"";
	}

	/**
	 * Observador del aforo de una sala
	 * Busca una sala por su ID y devuelve su aforo
	 *
	 * @param		idSala							int								ID de la sala
	 *
	 * @return										int								Aforo de la sala (-1 si no encontrada)
	 */

	@Override
	public int obtenerAforoSala(int idSala) {
		int posSala = this.buscarSala(idSala);

		if(posSala != -1) {
			return this._salas.get(posSala).aforo();
		}
		else {
			return -1;
		}
	}


	/**
	 * Observador de la ID de un alumno asociado a una reserva
	 * Devuelve la ID del alumno asociado a la reserva proporcionada
	 *
	 * @param		idReserva						int								ID de la reserva
	 *
	 * @return										int								ID del alumno asociado a la reserva (-1 si no encontrado)
	 */

	@Override
	public int obtenerAlumno(int idReserva) {
		int posReserva = this.buscarReserva(idReserva, true);

		if(posReserva != -1) {
			return this._reservas.get(posReserva).idAlumno();
		}
		else {
			return -1;
		}
	}


	/**
	 * Observador de la lista de recursos
	 *
	 * @return										ArrayList&lt;Integer&gt;		Lista de IDs de los recursos cargada en el gestor
	 */

	@Override
	public ArrayList<Integer> obtenerRecursos() {
		ArrayList<Integer> res = new ArrayList<Integer>();

		for(Recurso recurso: this._recursos) {
			res.add(recurso.id());
		}

		return res;
	}


	/**
	 * Observador de la lista de reservas
	 * Devuelve los IDs de todas o sólo de las ya pasadas reservas de la lista
	 *
	 * @param		todas							boolean							Devolver todas las reservas o sólo las pasadas
	 *
	 * @return										int								Lista de reservas
	 */

	@Override
	public ArrayList<Integer> obtenerReservas(boolean todas) {
		ArrayList<Integer>	res		= new ArrayList<Integer>();
		LocalDateTime		ahora	= LocalDateTime.now();

		for(Reserva reserva: this._reservas) {
			if(todas || ahora.isAfter(reserva.fechaYHora())) {
				res.add(reserva.id());
			}
		}

		if(res.size() > 0) {
			return res;
		}
		else {
			return null;
		}
	}


	/**
	 * Observador de una sanción
	 * Busca una sanción por su ID y la devuelve
	 *
	 * @param		idSancion						int								ID de la sanción
	 *
	 * @return										int								Sanción (null si no encontrada)
	 */

	private Sancion obtenerSancion(int idSancion) {
		int	i;
		int posSancion	= -1;
		int	tamLista	= this._sanciones.size();

		for(i = 0; i < tamLista; i++) {
			if((this._sanciones.get(i).id() == idSancion)) {
				posSancion = i;
			}
		}

		if(posSancion != -1) {
			return this._sanciones.get(posSancion);
		}
		else {
			return null;
		}
	}


	/**
	 * Pre-reservador de salas
	 * Crea una nueva reserva en estado de espera de confirmación
	 *
	 * @param		idAlumno						int								ID del alumno asociado a la reserva
	 * @param		idSala							int								ID de la sala asociada a la reserva
	 * @param		alumnos							int								Número de alumnos que disfrutarán la reserva
	 * @param		asignatura						String							Asignatura para la que se ha realizado la reserva
	 * @param		duracion						int								Duración (en horas) de la reserva
	 * @param		fechaYHora						LocalDateTime					Fecha y hora de la reserva
	 *
	 * @return										int								ID de la reserva
	 */

	@Override
	public int preReservarSala(int idAlumno, int idSala, int alumnos, String asignatura, int duracion, LocalDateTime fechaYHora) {
		Reserva nueva = new Reserva(this._reservas.get(this._reservas.size() - 1).id() + 1, idAlumno, idSala, alumnos, asignatura, duracion, fechaYHora);

		this._reservas.add(nueva);

		this.guardar();

		return nueva.id();
	}


	/**
	 * Registrador de incidencias
	 * Crea una nueva incidencia
	 *
	 * @param		idReserva						int								ID de la reserva asociada a la incidencia
	 * @param		descripcion						String							Descripción de la incidencia
	 * @param		tipo							int								Tipo de incidencia
	 *
	 * @return										int								ID de la incidencia
	 */

	@Override
	public int registrarIncidencia(int idReserva, String descripcion, int tipo) {
		TipoIncidencia	tipoIncidencia = null;
		Incidencia		nueva;

		for(TipoIncidencia ti: TipoIncidencia.values()) {
			if(tipo == ti.id()) {
				tipoIncidencia = ti;

				break;
			}
		}

		nueva = new Incidencia(this._incidencias.get(this._incidencias.size() - 1).id() + 1, idReserva, descripcion, tipoIncidencia);

		this._incidencias.add(nueva);

		this.guardar();

		return nueva.id();
	}


	/**
	 * Registrador de salas
	 * Crea una nueva sala en estado de espera de confirmación y sus recursos asociados
	 *
	 * @param		aforo							int								Aforo de la sala
	 * @param		nombre							String							Nombre de la sala
	 * @param		tipo							TipoSala						Tipo de la sala
	 * @param		ubicacion						String							Ubicación de la sala
	 * @param		recursos						ArrayList&lt;Integer&gt;		Recursos de la sala
	 *
	 * @return										int								ID de la sala
	 */

	@Override
	public int registrarSala(String nombre, int aforo, int tipo, String ubicacion, ArrayList<Integer> recursos) {
		int			idSala;
		TipoSala	tipoSala = null;
		Sala		nueva;

		for(TipoSala ts: TipoSala.values()) {
			if(tipo == ts.id()) {
				tipoSala = ts;

				break;
			}
		}

		nueva = new Sala(this._salas.get(this._salas.size() - 1).id() + 1, aforo, nombre, tipoSala, ubicacion);

		this._salas.add(nueva);

		idSala = nueva.id();

		for(int recurso: recursos) {
			this._salasYRecursos.add(new SalaYRecurso(idSala, recurso));
		}

		this.guardar();

		return idSala;
	}


	/**
	 * Método de comprobación de solapamiento de reservas
	 *
	 * @param		idSala							int								ID de la sala a comprobar
	 * @param		fechaYHora						LocalDateTime					Fecha y hora para la que debe estar libre
	 * @param		duracion						int								Duración mínima (en horas) que debe estar libre
	 *
	 * @return										boolean							Si la sala está libre o no
	 */

	@Override
	public boolean salaLibre(int idAlumno, int idSala, LocalDateTime fechaYHora, int duracion) {
		ArrayList<Reserva>	reservas = new ArrayList<Reserva>();

		for(Reserva r: this._reservas) {
			if(
					(idSala == r.idSala()) &&
					LocalDateTime.now().isBefore(r.fechaYHora().plusHours(r.duracion())) &&
					!(fechaYHora.isAfter(r.fechaYHora().plusHours(r.duracion()))) &&
					!(fechaYHora.plusHours(duracion).isBefore(r.fechaYHora())) &&
					((idAlumno != r.idAlumno()) || !(r.suspendida()))
					) {
				reservas.add(r);

				break;
			}
		}

		return (reservas.size() == 0);
	}


	/**
	 * Método para reanudar una reserva en suspensión
	 * Si la modificación de una reserva ha sido cancelada, es necesario revertirla a su estado normal
	 *
	 * @param		idUsuario						int								ID del usuario que lo solicita
	 * @param		idReserva						int								ID de la reserva a reanudar
	 *
	 * @return										int								ID de la reserva a reanudada (-1 si no encontrada, -2 si la reserva no pertenece al usuario solicitado)
	 */

	@Override
	public int reanudarReserva(int idUsuario, int idReserva) {
		int		posReserva = this.buscarReserva(idReserva, false);
		Reserva	reserva;

		if(posReserva != -1) {
			reserva = this._reservas.get(posReserva);

			if(reserva.idAlumno() == idUsuario) {
				reserva.suspendida(false);

				return idReserva;
			}
			else {
				return -2;
			}
		}
		else {
			return -1;
		}
	}


	/**
	 * Sancionador de alumnos
	 * Sanciona a un alumno
	 *
	 * @param		idIncidencia					int								ID de la incidencia asociada a la sanción
	 * @param		codigoSancion					int								Código de la sanción
	 * @param		fecha							LocalDate						Fecha de la sanción
	 * @param		duracion						int								Duración (en días) de la sanción
	 *
	 * @return										int								ID de la sanción
	 */

	@Override
	public int sancionarAlumno(int idIncidencia, int codigoSancion, LocalDate fecha, int duracion) {
		Sancion	nueva;

		nueva = new Sancion(this._sanciones.get(this._sanciones.size() - 1).id() + 1, idIncidencia, codigoSancion, duracion, fecha);

		this._sanciones.add(nueva);

		this.guardar();

		return nueva.id();
	}


	/**
	 * Método para poner una reserva en suspensión
	 * Cuando una reserva está siendo modificada, es necesario dejar el "hueco" de la misma libre, para poder crear otra en su lugar
	 *
	 * @param		idUsuario						int								ID del usuario que lo solicita
	 * @param		idReserva						int								ID de la reserva a suspender
	 *
	 * @return										int								ID de la reserva a suspendida (-1 si no encontrada, -2 si la reserva no pertenece al usuario solicitado)
	 */

	@Override
	public int suspenderReserva(int idUsuario, int idReserva) {
		int		posReserva = this.buscarReserva(idReserva, false);
		Reserva	reserva;

		if(posReserva != -1) {
			reserva = this._reservas.get(posReserva);

			if(reserva.idAlumno() == idUsuario) {
				reserva.suspendida(true);

				return idReserva;
			}
			else {
				return -2;
			}
		}
		else {
			return -1;
		}
	}
}
