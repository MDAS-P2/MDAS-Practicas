package mdas.p2.gestorsalas;


import java.time.LocalDateTime;
import java.util.ArrayList;

import mdas.p2.gestorreservamgr.IReservaMgt;
import mdas.p2.gestorreservamgr.ReservaMgr;


/**
 * Clase GestorSalas
 *
 * @author			Javier Ortiz Aragones
 * @date			24/05/2020
 * @version			1.0.7
 */

public class GestorSalas implements IReserva, ISala{

	private ReservaMgr _reservaMgr;

	/**
	 * Constructor de clase
	 * Inicializa el objeto de la clase ReservaMgr
	 */

	public GestorSalas() {
		this._reservaMgr = ReservaMgr.getInstance();
	}


	/**
	 * Buscador de reservas por alumno
	 * Busca las reservas a través de un ID de alumno
	 *
	 * @param		idAlumno						int								ID del alumno para buscar sus reservas
	 *
	 * @return										int[]							Vector que contiene los IDs de las reservas del alumno
	 */

	@Override
	public ArrayList<Integer> buscarReservas(int idAlumno) {
		return this._reservaMgr.buscarReservas(idAlumno);
	}

	/**
	 * Confirmar la reserva realizada por el alumno
	 *
	 * @param		idRserva						int								ID de la reserva para buscar sus incidencias
	 *
	 * @return										boolean							True en caso de que se haya realizado con exito, false en el caso contrario
	 */

	@Override
	public Boolean confirmarReserva(int idReserva) {
		return this._reservaMgr.confirmarReserva(idReserva);
	}

	/**
	 * Eliminar una reserva a partir de su ID
	 *
	 * @param		idReserva						int								ID de la reserva a eliminar
	 *
	 * @return										boolean							True en caso de que se haya realizado con exito, false en el caso contrario
	 */

	@Override
	public Boolean eliminarReserva(int idReserva) {
		return this._reservaMgr.eliminarReserva(idReserva);
	}


	/**
	 * Buscador de salas posibles para los datos aportados por el alumno
	 *
	 * @param		aforo							int								Aforo minimo que solicita el alumno
	 * @param		idsRecursos						int[]							Vector con los IDs de los recursos que necesita el alumno
	 *
	 * @return int[] Vector que contiene los IDs de las posibles salas
	 */

	@Override
	public ArrayList<Integer> buscarSala(int aforo, ArrayList<Integer> idsRecursos) {
		return this._reservaMgr.buscarSala(aforo, idsRecursos);
	}


	/**
	 * Confirmar la nueva sala registrada por el empleado
	 *
	 * @param		idSala							int								ID de la nueva sala
	 *
	 * @return										boolean							True en caso de que se haya realizado con exito, false en el caso contrario
	 */

	@Override
	public Boolean confirmarRegistro(int idSala) {
		return this._reservaMgr.confirmarRegistro(idSala);
	}

	/**
	 * Buscador de la sala optima entre las posibles
	 *
	 * @param		aforo							int								Aforo minimo que solicita el alumno
	 * @param		idSalas							int[]							Vector con los IDs de las salas posibles
	 *
	 * @return										int								ID de la sala óptima
	 */

	@Override
	public int elegirSala(int aforo, int idAlumno, String asignatura, int duracion, LocalDateTime fechaYHora, ArrayList<Integer> idSalas) {
		int		maxAforo		= Integer.MAX_VALUE;
		int		nuevoAforo;
		int		idSalaElegida	= -1;

		for(int sala : idSalas) {
			if(aforo == (nuevoAforo = this._reservaMgr.obtenerAforoSala(sala))) {
				idSalaElegida = sala;

				break;
			}


			else if(maxAforo > nuevoAforo) {
				maxAforo = nuevoAforo;
				idSalaElegida = sala;
			}
		}

		if(idSalaElegida != -1) {
			return this._reservaMgr.preReservarSala(idAlumno, idSalaElegida, aforo, asignatura, duracion, fechaYHora);
		}
		else {
			return -1;
		}
	}


	/**
	 * Validador de los datos introducidos por el empleado para la nueva sala
	 *
	 * @param		nombre							String							Nombre de la nueva sala
	 * @param		aforo							int								Aforo máximo de la nueva sala
	 * @param		tipo							int								Tipo de sala(aula, laboratorio...)
	 * @param		ubicacion						String							Ubicación de la nueva sala
	 * @param		recursos						int[]							ID de los recursos disponibles en la nueva sala
	 *
	 * @return boolean True en caso de que los datos sean correctos, false en el caso contrario
	 */

	@Override
	public int validarDatos(String nombre, int aforo, int tipo, String ubicacion, ArrayList<Integer> recursos) {
		if(aforo < 1) {
			return -1;
		}
		else {
			for(int tipoSala: IReservaMgt.obtenerTiposDeSala()) {
				if(tipo == tipoSala) {
					return this._reservaMgr.preRegistrarSala(nombre, aforo, tipo, ubicacion, recursos);
				}
			}
			return -1;
		}
	}
}
