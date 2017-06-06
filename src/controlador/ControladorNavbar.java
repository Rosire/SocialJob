package controlador;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import modelo.dao.BitacoraTareaAsignadaDAO;
import modelo.dao.CargoDAO;
import modelo.dao.CargoEmpleadoDAO;
import modelo.dao.EstatusMensajeDAO;
import modelo.dao.MensajeDAO;
import modelo.dao.NotificacionSolicitudDAO;
import modelo.dao.NotificacionTareaAsignadaDAO;
import modelo.dao.NotificacionTareaRepetitivaAsignadaDAO;
import modelo.dao.TareaAsignadaDAO;
import modelo.dao.UsuarioDAO;
import modelo.dto.BitacoraTareaAsignada;
import modelo.dto.Cargo;
import modelo.dto.CargoEmpleado;
import modelo.dto.EstatusMensaje;
import modelo.dto.Mensaje;
import modelo.dto.TareaAsignada;
import modelo.dto.Usuario;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.AbstractListModel;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Window;

public class ControladorNavbar {

	private Session miSession = Sessions.getCurrent();
	private Usuario usuario = (Usuario) miSession.getAttribute("usuario");

	private EstatusMensajeDAO estatusMensajeDAO = new EstatusMensajeDAO();

	private Mensaje mensaje = new Mensaje();
	private Cargo cargoSeleccionado = new Cargo();
	private TareaAsignada tareaAsignadaFiltro = new TareaAsignada();

	private CargoDAO cargoDAO = new CargoDAO();
	private MensajeDAO mensajeDAO = new MensajeDAO();
	private TareaAsignadaDAO tareaAsignadaDAO = new TareaAsignadaDAO();
	private NotificacionSolicitudDAO notificacionSolicitudDAO = new NotificacionSolicitudDAO();
	private NotificacionTareaAsignadaDAO notificacionTareaAsignadaDAO = new NotificacionTareaAsignadaDAO();
	private NotificacionTareaRepetitivaAsignadaDAO notificacionTareaRepetitivaAsignadaDAO = new NotificacionTareaRepetitivaAsignadaDAO();

	private List<Cargo> cargosEmpleado = new ArrayList<Cargo>();
	private List<TareaAsignada> tareasAsignadasEnMarcha = new ArrayList<TareaAsignada>();
	private List<Mensaje> mensajes = new ArrayList<Mensaje>();

	private ListModel<Cargo> listaCargosEmpleadoModel;
	private ListModel<TareaAsignada> listaTareasAsignadasEnMarchaModel;
	private ListModel<Mensaje> listaMensajesModel;

	private long cantidadNotificacionesTareasAsignadas;
	private long cantidadNotificacionesTareasRepetitivas;
	private long cantidadNotificacionesSolicitudes;
	private long cantidadNotificaciones;
	private long cantidadTareasEnMarcha;
	private long cantidadCagosEmpleado;
	private long cantidadMensajes;

	private String fotoPerfil;
	private String mensajePerfil;

	private static final String cantidadCargosTitulo = " %d Cargos";
	private static final String cantidadTareasEnMarchaTitulo = " %d Tareas En Marcha";
	private static final String cantidadNotificacionesTitulo = " %d Notificaciones";
	private static final String cantidadMensajesTitulo = " %d Mensajes";

	private SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");

	private BitacoraTareaAsignadaDAO bitacoraTareaAsignadaDAO = new BitacoraTareaAsignadaDAO();
	private UsuarioDAO usuarioDAO = new UsuarioDAO();
	private CargoEmpleadoDAO cargoEmpleadoDAO = new CargoEmpleadoDAO();

	public ControladorNavbar() {
		super();

		if (usuario.getTipoUsuario().getNombre().equals("ADMINISTRADOR")) {
			tareasAsignadasEnMarcha = new ArrayList<TareaAsignada>();
			cargosEmpleado = new ArrayList<Cargo>();
			cantidadNotificacionesTareasAsignadas = 0;
			cantidadNotificacionesSolicitudes = 0;
			cantidadNotificacionesTareasRepetitivas = 0;
			cantidadTareasEnMarcha = 0;
			cantidadCagosEmpleado = 0;

		} else {

			tareasAsignadasEnMarcha = tareaAsignadaDAO
					.obtenerTareasAsignadasPorEmpleadoPorEstatusNombreLimite(
							usuario.getId(), "EN MARCHA");
			cargosEmpleado = cargoDAO.obtenerCargosPorEmpleado(usuario.getId());
			cantidadTareasEnMarcha = tareaAsignadaDAO
					.obtenerCantidadTareasAsignadasPorEmpleadoPorEstatusNombre(
							usuario.getId(), "EN MARCHA");
			cantidadCagosEmpleado = cargoDAO.obtenerCargosPorEmpleado(
					usuario.getId()).size();

			cantidadNotificacionesTareasAsignadas = notificacionTareaAsignadaDAO
					.contarNotificacionesTareasAsignadasPorEmpleadoPorLeer(usuario
							.getId());

			cantidadNotificacionesSolicitudes = notificacionSolicitudDAO
					.contarNotificacionesSolicitudesPorEmpleadoPorLeer(usuario
							.getId());
			cantidadNotificacionesTareasRepetitivas = notificacionTareaRepetitivaAsignadaDAO
					.contarNotificacionesTareaRepetitivaAsignadaEmpleadoPorLeer(usuario
							.getId());

		}
		cantidadMensajes = mensajeDAO
				.obtenerCantidadMensajesRecibidosPorLeer(usuario.getId());
		cantidadNotificaciones = getCantidadNotificacionesTareasAsignadas()
				+ getCantidadNotificacionesTareasRepetitivas()
				+ getCantidadNotificacionesSolicitudes();
		mensajes = mensajeDAO.obtenerMensajesRecibidosPorLeerLimite(usuario
				.getId());

		fotoPerfil = usuario.getFoto().toString();
		mensajePerfil = usuario.getNombres() + " " + usuario.getApellidos();
	}

	public ListModel<Cargo> getCargosEmpleado() {
		listaCargosEmpleadoModel = new ListModelList<Cargo>(cargosEmpleado);
		return listaCargosEmpleadoModel;
	}

	public String getCantidadCargosTitulo() {
		return String.format(cantidadCargosTitulo, cantidadCagosEmpleado);
	}

	public ListModel<TareaAsignada> getTareasAsignadasEnMarcha() {
		listaTareasAsignadasEnMarchaModel = new ListModelList<TareaAsignada>(
				tareasAsignadasEnMarcha);
		return listaTareasAsignadasEnMarchaModel;
	}

	public String getCantidadTareasEnMarchaTitulo() {
		return String.format(cantidadTareasEnMarchaTitulo,
				cantidadTareasEnMarcha);
	}

	public ListModel<Mensaje> getMensajes() {
		listaMensajesModel = new ListModelList<Mensaje>(mensajes);
		return listaMensajesModel;
	}

	public String getCantidadMensajesTitulo() {
		return String.format(cantidadMensajesTitulo, cantidadMensajes);
	}

	public long getCantidadMensajes() {
		return cantidadMensajes;
	}

	public String getFotoPerfil() {
		return fotoPerfil;
	}

	public String getMensajePerfil() {
		return mensajePerfil;

	}

	public long getCantidadCargos() {
		return cantidadCagosEmpleado;
	}

	public long getCantidadTareasEnMarcha() {
		return cantidadTareasEnMarcha;
	}

	public long getCantidadNotificacionesTareasAsignadas() {
		return cantidadNotificacionesTareasAsignadas;
	}

	public long getCantidadNotificacionesTareasRepetitivas() {
		return cantidadNotificacionesTareasRepetitivas;
	}

	public long getCantidadNotificacionesSolicitudes() {
		return cantidadNotificacionesSolicitudes;
	}

	public long getCantidadNotificaciones() {
		return cantidadNotificaciones;
	}

	public String getCantidadNotificacionesTitulo() {
		return String.format(cantidadNotificacionesTitulo,
				cantidadNotificaciones);
	}

	@GlobalCommand
	@NotifyChange({ "cantidadNotificacionesTareasAsignadas",
			"cantidadNotificacionesSolicitudes",
			"cantidadNotificacionesTareasRepetitivas", "cantidadNotificaciones" })
	public void actualizarNotificaciones() {
		cantidadNotificacionesTareasAsignadas = notificacionTareaAsignadaDAO
				.contarNotificacionesTareasAsignadasPorEmpleadoPorLeer(usuario
						.getId());

		cantidadNotificacionesSolicitudes = notificacionSolicitudDAO
				.contarNotificacionesSolicitudesPorEmpleadoPorLeer(usuario
						.getId());
		cantidadNotificacionesTareasRepetitivas = notificacionTareaRepetitivaAsignadaDAO
				.contarNotificacionesTareaRepetitivaAsignadaEmpleadoPorLeer(usuario
						.getId());
		cantidadNotificaciones = getCantidadNotificacionesTareasAsignadas()
				+ getCantidadNotificacionesTareasRepetitivas()
				+ getCantidadNotificacionesSolicitudes();
	}

	@GlobalCommand
	@NotifyChange({ "fotoPerfil", "mensajePerfil" })
	public void actualizarInformacionUsuario() {
		miSession = Sessions.getCurrent();
		usuario = (Usuario) miSession.getAttribute("usuario");
		fotoPerfil = usuario.getFoto();
		mensajePerfil = usuario.getNombres() + " " + usuario.getApellidos();
	}

	@GlobalCommand
	@NotifyChange({ "cantidadMensajes", "cantidadMensajesTitulo", "mensajes" })
	public void actualizarMensajes() {
		cantidadMensajes = mensajeDAO
				.obtenerCantidadMensajesRecibidosPorLeer(usuario.getId());
		mensajes = mensajeDAO.obtenerMensajesRecibidosPorLeerLimite(usuario
				.getId());
	}

	@Command
	public void mostrarMensaje(@BindingParam("id") int id) {
		EstatusMensaje estatus = estatusMensajeDAO
				.obtenerEstatusMensajePorNombre("LEIDO");
		mensaje = mensajeDAO.obtenerMensaje(id);
		Map<String, Object> arguments = new HashMap<String, Object>();
		String fechaMensaje;
		try {
			fechaMensaje = formatoFecha.format(mensaje.getFecha());
		} catch (Exception e) {
			fechaMensaje = "";
		}
		arguments.put("fechaMensaje", fechaMensaje);
		arguments.put("icono", "z-icon-envelope");
		arguments.put("mensaje", mensaje);
		arguments.put("btnResponder", true);
		Window window = (Window) Executions.createComponents(
				"/vista/consultarMensaje.zul", null, arguments);
		window.doModal();
		mensaje.setEstatusMensaje(estatus);
		mensajeDAO.actualizarMensaje(mensaje);
		BindUtils.postGlobalCommand(null, null, "actualizarMensajes", null);
	}

	@Command
	public void mostrarCargo(@BindingParam("id") int id) {
		cargoSeleccionado = cargoDAO.obtenerCargo(id);
		CargoEmpleado supervisor = cargoEmpleadoDAO
				.obtenerUsuarioSupervisorPorCargo(cargoSeleccionado
						.getCargoSuperior().getId());
		String nombreSupervisor = "";
		if (supervisor != null) {
			nombreSupervisor = supervisor.getEmpleado().getNombres() + " "
					+ supervisor.getEmpleado().getApellidos() + "(V-"
					+ supervisor.getEmpleado().getCedula() + ")";
		}
		List<Cargo> cargosSubalternos = cargoDAO.obtenerCargosSubalternos(id);
		ListModel<Cargo> listaCargosSubalternosModel = new ListModelList<Cargo>(
				cargosSubalternos);

		Map<String, Object> arguments = new HashMap<String, Object>();
		String titulo = "Consultar Cargo";
		arguments.put("subalternos", listaCargosSubalternosModel);
		arguments.put("nombreSupervisor", nombreSupervisor);
		arguments.put("titulo", titulo);
		arguments.put("icono", "z-icon-eye");
		arguments.put("cargoSeleccionado", cargoSeleccionado);
		arguments.put("divConsulta", true);
		Window window = (Window) Executions.createComponents(
				"/vista/ConsultarEditarCargo.zul", null, arguments);
		window.doModal();
	}

	@Command
	public void mostrarTareaEnMarcha(@BindingParam("id") int id) {
		tareaAsignadaFiltro = tareaAsignadaDAO.obtenerTareaAsignada(id);
		Map<String, Object> arguments = new HashMap<String, Object>();
		String titulo = "Consultar Tarea";
		CargoEmpleado cargoEmpleado = cargoEmpleadoDAO
				.obtenerUsuarioSupervisorPorCargo(tareaAsignadaFiltro
						.getCargoEmpleado().getCargo().getCargoSuperior()
						.getId());
		String supervisor = cargoEmpleado.getEmpleado().getNombres() + " "
				+ cargoEmpleado.getEmpleado().getApellidos() + " (V-"
				+ cargoEmpleado.getEmpleado().getCedula() + ")";
		String fechaTarea;
		try {
			fechaTarea = formatoFecha.format(tareaAsignadaFiltro.getFecha());
		} catch (Exception e) {
			fechaTarea = "";
		}
		arguments.put("fechaTarea", fechaTarea);
		arguments.put("supervisor", supervisor);
		arguments.put("titulo", titulo);
		arguments.put("icono", "z-icon-eye");
		arguments.put("tareaAsignada", tareaAsignadaFiltro);
		arguments.put("divProgreso", true);
		arguments.put("btnSalirLabel", true);
		arguments.put("btnEditarLabel", true);
		Window window = (Window) Executions.getCurrent().createComponents(
				"/vista/ConsultarEditarTareaAsignada.zul", null, arguments);
		BitacoraTareaAsignada bitacoraTareaAsignada = new BitacoraTareaAsignada(
				tareaAsignadaFiltro, usuario, new Date(),
				"Tarea Consultada por el Empleado", "CONSULTAR");
		bitacoraTareaAsignadaDAO
				.registrarBitacoraTareaAsignada(bitacoraTareaAsignada);
		window.doModal();

	}
}
