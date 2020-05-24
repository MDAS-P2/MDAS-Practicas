package mdas.p2.gestorusuariomgr;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
 * @date		24/05/2020
 * @version		1.3.0
 *
 */

public class UsuarioMgr implements IUsuarioMgt {
	static	private	Scanner				_entrada	= new Scanner(System.in);
	static	private	UsuarioMgr			_instance	= null;
	private			ArrayList<Usuario>	_usuarios;


	/**
	 * Constructor de clase
	 * Privado, requisito del patrón Singleton
	 * Inicializa la lista del gestor
	 *
	 * @param		archivoUsuarios					String							Ruta del archivo donde se encuentran los usuarios
	 */

	private UsuarioMgr(String archivoUsuarios) {
		this._usuarios	= new ArrayList<Usuario>();

		this.cargar(archivoUsuarios);
	}


	/**
	 * Método estático para obtener la única instancia válida (o crearla si no existe) del gestor
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
	 * Metodo para obtener un Alumno de la lista de usuarios a partir de su email
	 *
	 * @param		email							String							Email del alumno a buscar
	 *
	 * @return										Alumno							Alumno encontrado (null si no)
	 */

	private Alumno buscarAlumno(String email) {
		Alumno res = null;

		for(Usuario u : this._usuarios) {
			if((u instanceof Alumno) && ((Alumno) u).correo().equals(email)) {
				res = (Alumno) u;
				break;
			}
		}

		return res;
	}


	/**
	 * Metodo para obtener un Empleado de la lista de Usuarios a partir de su ID
	 *
	 * @param		idAlumno						int								ID del empleado a buscar
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

		for(Usuario u : this._usuarios) {
			if(u.id() == idUsuario) {
				res = u;

				break;
			}
		}

		return res;
	}


	/**
	 * Metodo que carga en la lista de usuarios los usuarios que se encuentran guardados en un fichero
	 *
	 * @param		archivoUsuarios					String							Ruta del archivo donde se encuentran los usuarios
	 *
	 * @return										Boolean							Inidicación si la carga de usuarios ha sido exitosa o erronea
	 */

	@Override
	public boolean cargar(String archivoUsuarios) {
		String				linea;
		ArrayList<String>	campos;
		BufferedReader		buffer;
		StringTokenizer		stLinea;

		try {
			buffer = new BufferedReader(new FileReader(new File(archivoUsuarios)));

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
	 * Metodo que envia un mensaje a un usuario, mostando el correo del usuario y el mensaje que se le envia
	 *
	 * @param		idUsuario						String							Identificador del usuario al que se le quiere enviar el mensaje
	 * @param		mensaje							String							Mensjae que se enviará al usuario
	 *
	 * @return										Boolean							Resultado de la operación
	 */

	@Override
	public boolean enviarNotificacion(int idUsuario, String mensaje) {

		Alumno aux = this.buscarAlumno(idUsuario);

		if(aux == null) {
			System.out.println("Error en el envío del mensaje");
			return false;
		}

		System.out.println("Email: " + aux.correo());

		System.out.println("Mensaje: ");

		System.out.println(mensaje);

		System.out.println("Mensaje enviado con exito");

		return true;

	}


	/**
	 * Metodo que permite al usuario iniciar su sesión
	 *
	 * @return										int								ID del usuario que ha inicado sesion
	 */

	@Override
	public int iniciarSesion() {
		// String contrasenya;
		String email;

		System.out.print("Introduzca su correo electrónico: ");
		email = UsuarioMgr._entrada.nextLine();

		System.out.print("Introduzca su contraseña: ");
		/* contrasenya = */ UsuarioMgr._entrada.nextLine();

		System.out.println("Enviando datos al gestor de sesiones de la UCO");
		System.out.println("Espere, por favor...");

		try {
			Thread.sleep(5 * 1000);
		} catch (InterruptedException e) {
			// No es necesario hacer nada
		}

		Alumno aux = this.buscarAlumno(email);

		if((aux != null) && email.equals(aux.correo())) {
			return aux.id();
		}
		else {
			return -1;
		}
	}


	// TODO: Método de guardado


	/**
	 * Metodo que muestra un mensaje por pantalla
	 *
	 * @param		mensaje							String							Mensaje que se muestra por pantalla
	 *
	 */

	@Override
	public void mostrarMensaje(String mensaje) {
		System.out.println(mensaje);
	}


	/**
	 * Observador del nombre de un usuario
	 *
	 * @param		int								idUsuario						ID del usuario
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
