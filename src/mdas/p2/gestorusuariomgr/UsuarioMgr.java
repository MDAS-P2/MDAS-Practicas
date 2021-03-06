package mdas.p2.gestorusuariomgr;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;


/**
 * Clase UsuarioMgr
 * Componente de gestión de usuarios del sistema
 * Implementa la interfaz IUsuarioMgt
 *
 * @author		Unai Friscia Pérez (unaif)
 * @date		08/06/2020
 * @version		2.3.1
 *
 */

public class UsuarioMgr implements IUsuarioMgt {
	final private	boolean				_DEBUG				= false;

	static private	Scanner				_entrada			= new Scanner(System.in);
	static private	UsuarioMgr			_instance			= null;
	private			String				_archivoUsuarios;
	private			ArrayList<Usuario>	_usuarios;
	private			EnviarMensaje		_mailer;


	/**
	 * Constructor de clase
	 * Privado, requisito del patrón Singleton
	 * Inicializa la lista del gestor
	 *
	 * @param		archivoUsuarios					String							Ruta del archivo donde se encuentran los usuarios
	 */

	private UsuarioMgr(String archivoUsuarios) {
		this._archivoUsuarios			= archivoUsuarios;
		this._usuarios					= new ArrayList<Usuario>();

		if(!this._DEBUG) {
			try {
				this._mailer				= new EnviarMensajeEmail();
			}
			catch (IOException e) {
				System.out.println("Error: " + e.getMessage());

				this._mailer				= new EnviarMensajeConsola();
			}
		}
		else {
			this._mailer				= new EnviarMensajeConsola();
		}

		this.cargar();
	}


	/**
	 * Método estático para obtener la única instancia válida (o crearla si no existe) del gestor
	 *
	 * @param		archivoUsuarios					String							Ruta del archivo donde se encuentran los usuarios
	 *
	 * @return										UsuarioMgr						Instancia del gestor
	 */

	public static UsuarioMgr getInstance(String archivoUsuarios) {
		if(UsuarioMgr._instance == null) {
			UsuarioMgr._instance = new UsuarioMgr(archivoUsuarios);
		}

		return UsuarioMgr._instance;
	}


	/**
	 * Metodo para obtener un Alumno de la lista de Usuarios a partir de su id
	 *
	 * @param		idAlumno						int								ID del alumno a buscar
	 *
	 * @return										Alumno							Alumno encontrado (null si no)
	 */

	@Override
	public Alumno buscarAlumno(int idAlumno) {
		Usuario res = this.buscarUsuario(idAlumno);

		if(res instanceof Alumno) {
			return (Alumno) res;
		}
		else {
			return null;
		}
	}


	/**
	 * Metodo para obtener un Empleado de la lista de Usuarios a partir de su ID
	 *
	 * @param		idEmpleado						int								ID del empleado a buscar
	 *
	 * @return										Empleado						Empleado encontrado (null si no)
	 */

	@Override
	public Empleado buscarEmpleado(int idEmpleado) {
		Usuario res = this.buscarUsuario(idEmpleado);

		if(res instanceof Empleado) {
			return (Empleado) res;
		}
		else {
			return null;
		}
	}


	/**
	 * Metodo para obtener un Usuario de la lista de Usuarios a partir de su ID
	 *
	 * @param		idUsuario						int								ID del usuario que buscamos en la lista de usuarios
	 *
	 * @return										Usuario							Objeto Usuario que buscamos
	 */

	private Usuario buscarUsuario(int idUsuario) {
		Usuario res = null;

		for(Usuario u: this._usuarios) {
			if(u.id() == idUsuario) {
				res = u;

				break;
			}
		}

		return res;
	}


	/**
	 * Metodo para obtener un Usuario de la lista a partir de su email
	 *
	 * @param		email							String							Email del usuario a buscar
	 *
	 * @return										Alumno							Usuario encontrado (null si no)
	 */

	private Usuario buscarUsuario(String email) {
		Usuario res = null;

		for(Usuario u: this._usuarios) {
			if(u.correo().equals(email)) {
				res = u;
				break;
			}
		}

		return res;
	}


	/**
	 * Metodo que carga en la lista de usuarios los usuarios que se encuentran guardados en un fichero
	 *
	 * @return										Boolean							Inidicación si la carga de usuarios ha sido exitosa o erronea
	 */

	private boolean cargar() {
		String				linea;
		ArrayList<String>	campos;
		BufferedReader		buffer;
		StringTokenizer		stLinea;

		try {
			buffer = new BufferedReader(new FileReader(new File(this._archivoUsuarios)));

			while((linea = buffer.readLine()) != null) {
				stLinea = new StringTokenizer(linea, ",");

				campos = new ArrayList<String>();

				while(stLinea.hasMoreTokens()) {
					campos.add(stLinea.nextToken());
				}

				if(!(Boolean.parseBoolean(campos.get(0)))) {
					this._usuarios.add(new Alumno(Integer.parseInt(campos.get(1)), campos.get(2), campos.get(3)));
				}
				else {
					this._usuarios.add(new Empleado(Integer.parseInt(campos.get(1)), campos.get(2), campos.get(3)));
				}
			}

			buffer.close();

			return (this._usuarios.size() != 0);
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


	/**
	 * Metodo que guarda en un fichero de usuarios los usuarios que se encuentran almacenados en la lista de usuarios
	 *
	 * @return										Boolean							Inidicación si el guardado de usuarios ha sido exitosa o erronea
	 */


	private boolean guardar() {
		BufferedWriter		buffer;

		try {
			buffer = new BufferedWriter(new FileWriter(new File(this._archivoUsuarios)));

			for(Usuario u: this._usuarios) {
				buffer.write(u.toCsv() + System.getProperty("line.separator"));
			}

			buffer.close();

			return true;
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


	/**
	 * Metodo que envia un mensaje a un usuario, mostando el correo del usuario y el mensaje que se le envia
	 * Implementa el patrón strategy para facilitar el cambio del sistema de envío de notificaciones
	 *
	 * @param		idUsuario						String							Identificador del usuario al que se le quiere enviar el mensaje
	 * @param		mensaje							String							Mensjae que se enviará al usuario
	 *
	 * @return										Boolean							Resultado de la operación
	 */

	@Override
	public boolean enviarNotificacion(int idUsuario, String mensaje) {

		Alumno alumno = this.buscarAlumno(idUsuario);

		if((alumno != null) && this._mailer.enviarMensaje(alumno.correo(), mensaje)) {
			System.out.println("El mensaje ha sido enviado con exito");

			return true;
		}
		else {
			System.out.println("Ha habido algún error en el envío del mensaje");

			return false;
		}
	}


	/**
	 * Metodo que permite al usuario iniciar su sesión
	 *
	 * @return										int								ID del usuario que ha inicado sesion
	 */

	@Override
	public int iniciarSesion() {
		// String	contrasenya;
		String		email;
		Usuario		aux;

		System.out.print("Introduzca su correo electrónico: ");
		email = UsuarioMgr._entrada.nextLine();

		System.out.print("Introduzca su contraseña: ");
		/* contrasenya = */ UsuarioMgr._entrada.nextLine();

		System.out.println("Enviando datos al gestor de sesiones de la UCO");
		System.out.println("Espere, por favor...");

		// TODO: Contactar con el servidor de la UCO... algún día... el 15 de nunca seguramente...

		this.guardar();

		if(!this._DEBUG) {
			try {
				Thread.sleep(5 * 1000);
			} catch (InterruptedException e) {
				// No es necesario hacer nada
			}
		}

		aux = this.buscarUsuario(email);

		if((aux != null) && email.equals(aux.correo())) {
			return aux.id();
		}
		else {
			return -1;
		}
	}


	/**
	 * Observador del nombre de un usuario
	 *
	 * @param		idUsuario						int								ID del usuario
	 *
	 * @return										String							Nombre del usuario (null si no encontrado)
	 */

	@Override
	public String nombre(int idUsuario) {
		String res = null;

		for(Usuario u: this._usuarios) {
			if(u.id() == idUsuario) {
				res = u.nombre();

				break;
			}
		}

		return res;
	}
}
