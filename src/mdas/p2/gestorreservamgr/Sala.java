package mdas.p2.gestorreservamgr;


// import mdas.p2.gestorreservamgr.TipoSala;


/**
 * Clase Sala
 * Almacena los datos de una sala
 *
 * @author		Rafael Carlos Méndez Rodríguez (i82meror)
 * @date		29/05/2020
 * @version		1.4.0
 */

public class Sala {
	private int			_id;
	private int			_aforo;
	private String		_nombre;
	private String		_ubicacion;
	private TipoSala	_tipo;


	/**
	 * Constructor de clase
	 * Crea una sala a partir de su ID, aforo, nombre, tipo y ubicacion
	 *
	 * @param		id								int								ID de la sala
	 * @param		aforo							int								Aforo de la sala
	 * @param		nombre							String							Nombre de la sala
	 * @param		tipo							TipoSala						Tipo de la sala
	 * @param		ubicacion						String							Ubicación de la sala
	 */

	public Sala(int id, int aforo, String nombre, TipoSala tipo, String ubicacion) {
		this._id			= id;
		this._aforo			= aforo;
		this._nombre		= nombre;
		this._tipo			= tipo;
		this._ubicacion		= ubicacion;
	}


	/**
	 * Observador de la variable privada _aforo
	 *
	 * @return										int								Aforo de la sala
	 */

	public int aforo() {
		return this._aforo;
	}


	/**
	 * Observador de la variable privada _id
	 *
	 * @return										int								ID de la sala
	 */

	public int id() {
		return this._id;
	}


	/**
	 * Observador de la variable privada _nombre
	 *
	 * @return										String							Nombre de la sala
	 */

	public String nombre() {
		return this._nombre;
	}


	/**
	 * Observador de la variable privada _tipo
	 *
	 * @return										TipoSala						Tipo de la sala
	 */



	public TipoSala tipo() {
		return this._tipo;
	}


	/**
	 * Observador de la variable privada _ubicacion
	 *
	 * @return										String							Ubicación de la sala
	 */

	public String ubicacion() {
		return this._ubicacion;
	}


	/**
	 * Método para exportar los datos de la clase en formato CSV
	 *
	 * @return										String							Representación en texto CSV de los datos de la sala
	 */

	public String toCsv() {
		return this._id + "," + this._aforo + "," + this._nombre.replace(",", "") + "," + this._tipo.id() + "," + this._ubicacion.replace(",", "");
	}


	/**
	 * Método "mágico" cuando una clase es usada como String
	 *
	 * @return										String							Representación en texto de los datos de la sala
	 */

	@Override
	public String toString() {
		return this._id + "," + this._aforo + "," + this._nombre + "," + this._tipo.id() + "," + this._ubicacion;
	}
}
