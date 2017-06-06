package controlador;

import java.awt.Button;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import modelo.dao.BitacoraSolicitudDAO;
import modelo.dao.BitacoraTareaRepetitivaAsignadaDAO;
import modelo.dao.CargoDAO;
import modelo.dao.EstatusSolicitudDAO;
import modelo.dao.NotificacionSolicitudDAO;
import modelo.dao.NotificacionTareaAsignadaDAO;
import modelo.dao.PrioridadDAO;
import modelo.dao.SolicitudDAO;
import modelo.dao.UsuarioDAO;
import modelo.dto.BitacoraSolicitud;
import modelo.dto.BitacoraTareaAsignada;
import modelo.dto.Cargo;
import modelo.dto.Estatus;
import modelo.dto.Prioridad;
import modelo.dto.Solicitud;
import modelo.dto.Usuario;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkmax.zul.Navbar;
import org.zkoss.zkmax.zul.Navitem;
import org.zkoss.zul.AbstractListModel;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.Window;

public class ControladorSolicitudes extends SelectorComposer<Component> {

	@Wire
	private Grid gridSolicitudesEnviadasTodas;
	@Wire
	private Grid gridSolicitudesEnviadasEnEspera;
	@Wire
	private Grid gridSolicitudesEnviadasAprobadas;
	@Wire
	private Grid gridSolicitudesEnviadasRechazadas;
	@Wire
	private Grid gridSolicitudesRecibidasTodas;
	@Wire
	private Grid gridSolicitudesRecibidasEnEspera;
	@Wire
	private Grid gridSolicitudesRecibidasAprobadas;
	@Wire
	private Grid gridSolicitudesRecibidasRechazadas;
	@Wire
	private Div divSolicitudesEnviadasTodas;
	@Wire
	private Div divSolicitudesEnviadasEnEspera;
	@Wire
	private Div divSolicitudesEnviadasAprobadas;
	@Wire
	private Div divSolicitudesEnviadasRechazadas;
	@Wire
	private Div divSolicitudesRecibidasTodas;
	@Wire
	private Div divSolicitudesRecibidasEnEspera;
	@Wire
	private Div divSolicitudesRecibidasAprobadas;
	@Wire
	private Div divSolicitudesRecibidasRechazadas;
	@Wire
	private Panel panelEnviadas;
	@Wire
	private Panel panelRecibidas;
	@Wire
	private Navitem navItemSolicitudesEnviadasTodas;
	@Wire
	private Navbar navBarSolicitudesEnviadas;
	@Wire
	private Navitem navItemSolicitudesRecibidasTodas;
	@Wire
	private Navbar navBarSolicitudesRecibidas;

	// Variable Session
	private Session miSession = Sessions.getCurrent();
	private Usuario usuarioSession = (Usuario) miSession
			.getAttribute("usuario");

	private boolean visible;

	private UsuarioDAO usuarioDAO = new UsuarioDAO();
	private CargoDAO cargoDAO = new CargoDAO();
	private SolicitudDAO solicitudDAO = new SolicitudDAO();
	private PrioridadDAO prioridadDAO = new PrioridadDAO();

	private BitacoraSolicitudDAO bitacoraSolicitudDAO = new BitacoraSolicitudDAO();

	private ListModel<BitacoraSolicitud> listaBitacorasModel;
	private List<BitacoraSolicitud> bitacoras;

	private ListModel<Solicitud> listaSolicitudesEnviadasTodasModel;
	private ListModel<Solicitud> listaSolicitudesEnviadasEnEsperaModel;
	private ListModel<Solicitud> listaSolicitudesEnviadasAprobadasModel;
	private ListModel<Solicitud> listaSolicitudesEnviadasRechazadasModel;
	private ListModel<Solicitud> listaSolicitudesRecibidasTodasModel;
	private ListModel<Solicitud> listaSolicitudesRecibidasEnEsperaModel;
	private ListModel<Solicitud> listaSolicitudesRecibidasAprobadasModel;
	private ListModel<Solicitud> listaSolicitudesRecibidasRechazadasModel;

	private List<Solicitud> solicitudesEnviadasTodas;
	private List<Solicitud> solicitudesEnviadasEnEspera;
	private List<Solicitud> solicitudesEnviadasAprobadas;
	private List<Solicitud> solicitudesEnviadasRechazadas;
	private List<Solicitud> solicitudesRecibidasTodas;
	private List<Solicitud> solicitudesRecibidasEnEspera;
	private List<Solicitud> solicitudesRecibidasAprobadas;
	private List<Solicitud> solicitudesRecibidasRechazadas;

	private static final String footerMessageSolicitudesEnviadasTodas = "Total de Solicitudes Enviadas: %d";
	private static final String footerMessageSolicitudesEnviadasEnEspera = "Total de Solicitudes Enviadas En Espera: %d";
	private static final String footerMessageSolicitudesEnviadasAprobadas = "Total de Solicitudes Enviadas Aprobadas: %d";
	private static final String footerMessageSolicitudesEnviadasRechazadas = "Total de Solicitudes Enviadas Rechazadas: %d";

	private static final String footerMessageSolicitudesRecibidasTodas = "Total de Solicitudes Recibidas: %d";
	private static final String footerMessageSolicitudesRecibidasEnEspera = "Total de Solicitudes Recibidas En Espera: %d";
	private static final String footerMessageSolicitudesRecibidasAprobadas = "Total de Solicitudes Recibidas Aprobadas: %d";
	private static final String footerMessageSolicitudesRecibidasRechazadas = "Total de Solicitudes Recibidas Rechazadas: %d";

	public ControladorSolicitudes() {
		super();
		solicitudesEnviadasTodas = solicitudDAO
				.obtenerSolicitudesEnviadasPorEmpleado(usuarioSession.getId());
		solicitudesEnviadasEnEspera = solicitudDAO
				.obtenerSolicitudesEnviadasPorEmpleadoPorEstatusNombre(
						usuarioSession.getId(), "EN ESPERA");
		solicitudesEnviadasAprobadas = solicitudDAO
				.obtenerSolicitudesEnviadasPorEmpleadoPorEstatusNombre(
						usuarioSession.getId(), "APROBADA");
		solicitudesEnviadasRechazadas = solicitudDAO
				.obtenerSolicitudesEnviadasPorEmpleadoPorEstatusNombre(
						usuarioSession.getId(), "RECHAZADA");

		solicitudesRecibidasTodas = solicitudDAO
				.obtenerSolicitudesRecibidasPorEmpleado(usuarioSession.getId());
		solicitudesRecibidasEnEspera = solicitudDAO
				.obtenerSolicitudesRecibidasPorEmpleadoPorEstatusNombre(
						usuarioSession.getId(), "EN ESPERA");
		solicitudesRecibidasAprobadas = solicitudDAO
				.obtenerSolicitudesRecibidasPorEmpleadoPorEstatusNombre(
						usuarioSession.getId(), "APROBADA");
		solicitudesRecibidasRechazadas = solicitudDAO
				.obtenerSolicitudesRecibidasPorEmpleadoPorEstatusNombre(
						usuarioSession.getId(), "RECHAZADA");
		List<Cargo> cargos = cargoDAO
				.obtenerCargosActivosConSupervisorIguaActualPorEmpresa(
						usuarioSession.getId(), usuarioSession.getEmpresa()
								.getId());
		if (cargos.isEmpty()) {
			visible = false;
		} else {
			visible = true;
		}

	}

	public boolean getVisibleNavbar() {
		return visible;
	}

	public ListModel<Solicitud> getSolicitudesEnviadasTodas() {
		listaSolicitudesEnviadasTodasModel = new ListModelList<Solicitud>(
				solicitudesEnviadasTodas);
		return listaSolicitudesEnviadasTodasModel;
	}

	public String getFooterMessageSolicitudesEnviadasTodas() {
		return String.format(footerMessageSolicitudesEnviadasTodas,
				solicitudesEnviadasTodas.size());
	}

	public ListModel<Solicitud> getSolicitudesEnviadasEnEspera() {
		listaSolicitudesEnviadasEnEsperaModel = new ListModelList<Solicitud>(
				solicitudesEnviadasEnEspera);
		return listaSolicitudesEnviadasEnEsperaModel;
	}

	public String getFooterMessageSolicitudesEnviadasEnEspera() {
		return String.format(footerMessageSolicitudesEnviadasEnEspera,
				solicitudesEnviadasEnEspera.size());
	}

	public ListModel<Solicitud> getSolicitudesEnviadasAprobadas() {
		listaSolicitudesEnviadasAprobadasModel = new ListModelList<Solicitud>(
				solicitudesEnviadasAprobadas);
		return listaSolicitudesEnviadasAprobadasModel;
	}

	public String getFooterMessageSolicitudesEnviadasAprobadas() {
		return String.format(footerMessageSolicitudesEnviadasAprobadas,
				solicitudesEnviadasAprobadas.size());
	}

	public ListModel<Solicitud> getSolicitudesEnviadasRechazadas() {
		listaSolicitudesEnviadasRechazadasModel = new ListModelList<Solicitud>(
				solicitudesEnviadasRechazadas);
		return listaSolicitudesEnviadasRechazadasModel;
	}

	public String getFooterMessageSolicitudesEnviadasRechazadas() {
		return String.format(footerMessageSolicitudesEnviadasRechazadas,
				solicitudesEnviadasRechazadas.size());
	}

	public ListModel<Solicitud> getSolicitudesRecibidasTodas() {
		listaSolicitudesRecibidasTodasModel = new ListModelList<Solicitud>(
				solicitudesRecibidasTodas);
		return listaSolicitudesRecibidasTodasModel;
	}

	public String getFooterMessageSolicitudesRecibidasTodas() {
		return String.format(footerMessageSolicitudesRecibidasTodas,
				solicitudesRecibidasTodas.size());
	}

	public ListModel<Solicitud> getSolicitudesRecibidasEnEspera() {
		listaSolicitudesRecibidasEnEsperaModel = new ListModelList<Solicitud>(
				solicitudesRecibidasEnEspera);
		return listaSolicitudesRecibidasEnEsperaModel;
	}

	public String getFooterMessageSolicitudesRecibidasEnEspera() {
		return String.format(footerMessageSolicitudesRecibidasEnEspera,
				solicitudesRecibidasEnEspera.size());
	}

	public ListModel<Solicitud> getSolicitudesRecibidasAprobadas() {
		listaSolicitudesRecibidasAprobadasModel = new ListModelList<Solicitud>(
				solicitudesRecibidasAprobadas);
		return listaSolicitudesRecibidasAprobadasModel;
	}

	public String getFooterMessageSolicitudesRecibidasAprobadas() {
		return String.format(footerMessageSolicitudesRecibidasAprobadas,
				solicitudesRecibidasAprobadas.size());
	}

	public ListModel<Solicitud> getSolicitudesRecibidasRechazadas() {
		listaSolicitudesRecibidasRechazadasModel = new ListModelList<Solicitud>(
				solicitudesRecibidasRechazadas);
		return listaSolicitudesRecibidasRechazadasModel;
	}

	public String getFooterMessageSolicitudesRecibidasRechazadas() {
		return String.format(footerMessageSolicitudesRecibidasRechazadas,
				solicitudesRecibidasRechazadas.size());
	}

	@Listen("onClick=#navItemEnviadas")
	public void mostrarSolicitudesEnviadas() {
		panelEnviadas.setVisible(true);
		panelRecibidas.setVisible(false);
		navBarSolicitudesEnviadas
				.setSelectedItem(navItemSolicitudesEnviadasTodas);
		divSolicitudesEnviadasTodas.setVisible(true);
		divSolicitudesEnviadasEnEspera.setVisible(false);
		divSolicitudesEnviadasAprobadas.setVisible(false);
		divSolicitudesEnviadasRechazadas.setVisible(false);
	}

	@Listen("onClick=#navItemRecibidas")
	public void mostrarSolicitudesRecibidas() {
		panelEnviadas.setVisible(false);
		panelRecibidas.setVisible(true);
		navBarSolicitudesRecibidas
				.setSelectedItem(navItemSolicitudesRecibidasTodas);
		divSolicitudesRecibidasTodas.setVisible(true);
		divSolicitudesRecibidasEnEspera.setVisible(false);
		divSolicitudesRecibidasAprobadas.setVisible(false);
		divSolicitudesRecibidasRechazadas.setVisible(false);
	}

	@Listen("onClick=#navItemSolicitudesEnviadasTodas")
	public void mostrarSolicitudesEnviadasTodas() {
		divSolicitudesEnviadasTodas.setVisible(true);
		divSolicitudesEnviadasEnEspera.setVisible(false);
		divSolicitudesEnviadasAprobadas.setVisible(false);
		divSolicitudesEnviadasRechazadas.setVisible(false);
	}

	@Listen("onClick=#navItemSolicitudesEnviadasEnEspera")
	public void mostrarSolicitudesEnviadasEnEspera() {
		divSolicitudesEnviadasTodas.setVisible(false);
		divSolicitudesEnviadasEnEspera.setVisible(true);
		divSolicitudesEnviadasAprobadas.setVisible(false);
		divSolicitudesEnviadasRechazadas.setVisible(false);
	}

	@Listen("onClick=#navItemSolicitudesEnviadasAprobadas")
	public void mostrarSolicitudesEnviadasAprobadas() {
		divSolicitudesEnviadasTodas.setVisible(false);
		divSolicitudesEnviadasEnEspera.setVisible(false);
		divSolicitudesEnviadasAprobadas.setVisible(true);
		divSolicitudesEnviadasRechazadas.setVisible(false);
	}

	@Listen("onClick=#navItemSolicitudesEnviadasRechazadas")
	public void mostrarSolicitudesEnviadasRechazadas() {
		divSolicitudesEnviadasTodas.setVisible(false);
		divSolicitudesEnviadasEnEspera.setVisible(false);
		divSolicitudesEnviadasAprobadas.setVisible(false);
		divSolicitudesEnviadasRechazadas.setVisible(true);
	}

	@Listen("onClick=#navItemSolicitudesRecibidasTodas")
	public void mostrarSolicitudesRecibidasTodas() {
		divSolicitudesRecibidasTodas.setVisible(true);
		divSolicitudesRecibidasEnEspera.setVisible(false);
		divSolicitudesRecibidasAprobadas.setVisible(false);
		divSolicitudesRecibidasRechazadas.setVisible(false);
	}

	@Listen("onClick=#navItemSolicitudesRecibidasEnEspera")
	public void mostrarSolicitudesRecibidasEnEspera() {
		divSolicitudesRecibidasTodas.setVisible(false);
		divSolicitudesRecibidasEnEspera.setVisible(true);
		divSolicitudesRecibidasAprobadas.setVisible(false);
		divSolicitudesRecibidasRechazadas.setVisible(false);
	}

	@Listen("onClick=#navItemSolicitudesRecibidasAprobadas")
	public void mostrarSolicitudesRecibidasAprobadas() {
		divSolicitudesRecibidasTodas.setVisible(false);
		divSolicitudesRecibidasEnEspera.setVisible(false);
		divSolicitudesRecibidasAprobadas.setVisible(true);
		divSolicitudesRecibidasRechazadas.setVisible(false);
	}

	@Listen("onClick=#navItemSolicitudesRecibidasRechazadas")
	public void mostrarSolicitudesRecibidasRechazadas() {
		divSolicitudesRecibidasTodas.setVisible(false);
		divSolicitudesRecibidasEnEspera.setVisible(false);
		divSolicitudesRecibidasAprobadas.setVisible(false);
		divSolicitudesRecibidasRechazadas.setVisible(true);
	}

	@Listen("onClick=#btnGenerarSolicitud; onClick=#btnGenerarSolicitud; onClick=#btnGenerarSolicitudEnEspera; onClick=#btnGenerarSolicitudAprobada; onClick=#btnGenerarSolicitudRechazada")
	public void generarSolicitud() {
		Window window = (Window) Executions.getCurrent().createComponents(
				"/vista/RegistrarSolicitud.zul", null, null);
		window.doModal();
	}

	// Consultar Solicitud Enviada del Grid Todas
	@Listen("onClick=#btnConsultarSolicitudEnviada")
	public void consultarSolicitudEnviadaTodas() {
		String indexSeleccionado = getIdSelectedRowTarea(gridSolicitudesEnviadasTodas);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una Solicitud a Consultar",
					gridSolicitudesEnviadasTodas);
		} else {
			consultarSolicitud(indexSeleccionado);
		}
	}

	// Consultar Solicitud Enviada del Grid En Espera
	@Listen("onClick=#btnConsultarSolicitudEnviadaEnEspera")
	public void consultarSolicitudEnviadaEnEspera() {
		String indexSeleccionado = getIdSelectedRowTarea(gridSolicitudesEnviadasEnEspera);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una Solicitud a Consultar",
					gridSolicitudesEnviadasEnEspera);
		} else {
			consultarSolicitud(indexSeleccionado);
		}
	}

	// Consultar Solicitud Enviada del Grid Aprobadas
	@Listen("onClick=#btnConsultarSolicitudEnviadaAprobada")
	public void consultarSolicitudEnviadaAprobada() {
		String indexSeleccionado = getIdSelectedRowTarea(gridSolicitudesEnviadasAprobadas);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una Solicitud a Consultar",
					gridSolicitudesEnviadasAprobadas);
		} else {
			consultarSolicitud(indexSeleccionado);
		}
	}

	// Consultar Solicitud Enviada del Grid Rechazadas
	@Listen("onClick=#btnConsultarSolicitudEnviadaRechazada")
	public void consultarSolicitudEnviadaRechazada() {
		String indexSeleccionado = getIdSelectedRowTarea(gridSolicitudesEnviadasRechazadas);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una Solicitud a Consultar",
					gridSolicitudesEnviadasRechazadas);
		} else {
			consultarSolicitud(indexSeleccionado);
		}
	}

	public void consultarSolicitud(String indexSeleccionado) {
		int id = Integer.parseInt(indexSeleccionado);
		Solicitud solicitudFiltro = solicitudDAO.obtenerSolicitud(id);
		Map<String, Object> arguments = new HashMap<String, Object>();
		String titulo = "Consultar Solicitud";
		String empleadoSolicitante = solicitudFiltro.getEmpleadoSolicitud()
				.getNombres()
				+ " "
				+ solicitudFiltro.getEmpleadoSolicitud().getApellidos()
				+ " (V-"
				+ solicitudFiltro.getEmpleadoSolicitud().getCedula()
				+ ")";
		bitacoras = bitacoraSolicitudDAO.obtenerBitacorasPorSolicitud(id);
		listaBitacorasModel = new ListModelList<BitacoraSolicitud>(bitacoras);
		String footer = "Total de Movimientos de la tarea " + bitacoras.size();
		if (solicitudFiltro.getEmpleadoTarea() != null) {
			String empleadoTarea = solicitudFiltro.getEmpleadoSolicitud()
					.getNombres()
					+ " "
					+ solicitudFiltro.getEmpleadoTarea().getApellidos()
					+ " (V-" + solicitudFiltro.getEmpleadoTarea().getCedula()

					+ ")";
			arguments.put("empleadoTarea", empleadoTarea);
			arguments.put("divEmpleado", true);
		}

		arguments.put("bitacoras", listaBitacorasModel);
		arguments.put("footer", footer);
		arguments.put("titulo", titulo);
		arguments.put("icono", "z-icon-eye");
		arguments.put("solicitud", solicitudFiltro);
		arguments.put("empleadoSolicitante", empleadoSolicitante);
		arguments.put("btnEditarSolicitud", false);
		arguments.put("btnAprobarSolicitud", false);
		arguments.put("btnRechazarSolicitud", false);
		arguments.put("btnGuardar", false);
		arguments.put("btnSalir", true);
		arguments.put("btnCancelar", false);
		arguments.put("divTarea", false);
		arguments.put("divRechazo", false);
		arguments.put("divFechaTarea", true);
		Window window = (Window) Executions.getCurrent().createComponents(
				"/vista/ConsultarEditarSolicitud.zul", null, arguments);
		window.doModal();
	}

	// Consultar Solicitud Recibida del Grid Todas
	@Listen("onClick=#btnConsultarSolicitudRecibida")
	public void consultarSolicitudRecibidaTodas() {
		String indexSeleccionado = getIdSelectedRowTarea(gridSolicitudesRecibidasTodas);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una Solicitud a Consultar",
					gridSolicitudesRecibidasTodas);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			Solicitud solicitudFiltro = solicitudDAO.obtenerSolicitud(id);
			if (solicitudFiltro.getEstatusSolicitud().getNombre()
					.equals("EN ESPERA")) {
				consultarSolicitudRecibidaEnEspera(solicitudFiltro);
			} else {
				consultarSolicitud(indexSeleccionado);
			}
		}
	}

	// Aprobar Solicitud Recibida del Grid Todas
	@Listen("onClick=#btnAprobarSolicitudRecibida")
	public void aprobarSolicitudRecibidaTodas() {
		String indexSeleccionado = getIdSelectedRowTarea(gridSolicitudesRecibidasTodas);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una Solicitud a Aprobar",
					gridSolicitudesRecibidasTodas);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			Solicitud solicitudFiltro = solicitudDAO.obtenerSolicitud(id);
			if (solicitudFiltro.getEstatusSolicitud().getNombre()
					.equals("EN ESPERA")) {
				aprobarSolicitudRecibidaEnEspera(solicitudFiltro);
			} else {
				showNotifyWarning(
						"Solo las solicitudes con estatus \"EN ESPERA\" se pueden aprobar ",
						gridSolicitudesRecibidasTodas);
			}
		}
	}

	// Aprobar Solicitud Recibida del Grid Todas
	@Listen("onClick=#btnRechazarSolicitudRecibida")
	public void rechazarSolicitudRecibidaTodas() {
		String indexSeleccionado = getIdSelectedRowTarea(gridSolicitudesRecibidasTodas);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una Solicitud a Rechazar",
					gridSolicitudesRecibidasTodas);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			Solicitud solicitudFiltro = solicitudDAO.obtenerSolicitud(id);
			if (solicitudFiltro.getEstatusSolicitud().getNombre()
					.equals("EN ESPERA")) {
				rechazarSolicitudRecibidaEnEspera(solicitudFiltro);
			} else {
				showNotifyWarning(
						"Solo las solicitudes con estatus \"EN ESPERA\" se pueden rechazar ",
						gridSolicitudesRecibidasTodas);
			}
		}
	}

	// Consultar Solicitud Recibida En espera
	@Listen("onClick=#btnConsultarSolicitudRecibidaEnEspera")
	public void consultarSolicitudRecibidaEnEspera() {
		String indexSeleccionado = getIdSelectedRowTarea(gridSolicitudesRecibidasEnEspera);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una Solicitud a Consultar",
					gridSolicitudesRecibidasEnEspera);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			Solicitud solicitudFiltro = solicitudDAO.obtenerSolicitud(id);
			consultarSolicitudRecibidaEnEspera(solicitudFiltro);

		}
	}

	// Aprobar Solicitud Recibida En espera
	@Listen("onClick=#btnAprobarSolicitudRecibidaEnEspera")
	public void aprobarSolicitudRecibidaEnEspera() {
		String indexSeleccionado = getIdSelectedRowTarea(gridSolicitudesRecibidasEnEspera);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una Solicitud a Aprobar",
					gridSolicitudesRecibidasEnEspera);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			Solicitud solicitudFiltro = solicitudDAO.obtenerSolicitud(id);
			aprobarSolicitudRecibidaEnEspera(solicitudFiltro);

		}
	}

	// Rechazar Solicitud Recibida En espera
	@Listen("onClick=#btnRechazarSolicitudRecibidaEnEspera")
	public void rechazarSolicitudRecibidaEnEspera() {
		String indexSeleccionado = getIdSelectedRowTarea(gridSolicitudesRecibidasEnEspera);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una Solicitud a Rechazar",
					gridSolicitudesRecibidasEnEspera);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			Solicitud solicitudFiltro = solicitudDAO.obtenerSolicitud(id);
			rechazarSolicitudRecibidaEnEspera(solicitudFiltro);

		}
	}

	// Consultar Solicitud Recibida Aprobada
	@Listen("onClick=#btnConsultarSolicitudRecibidaAprobada")
	public void consultarSolicitudRecibidaAprobada() {
		String indexSeleccionado = getIdSelectedRowTarea(gridSolicitudesRecibidasAprobadas);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una Solicitud a Consultar",
					gridSolicitudesRecibidasAprobadas);
		} else {
			consultarSolicitud(indexSeleccionado);
		}
	}

	// Consultar Solicitud Recibida Rechazada
	@Listen("onClick=#btnConsultarSolicitudRecibidaRechazada")
	public void consultarSolicitudRecibidaRechazada() {
		String indexSeleccionado = getIdSelectedRowTarea(gridSolicitudesRecibidasRechazadas);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una Solicitud a Consultar",
					gridSolicitudesRecibidasRechazadas);
		} else {
			consultarSolicitud(indexSeleccionado);
		}
	}

	public void consultarSolicitudRecibidaEnEspera(Solicitud solicitudFiltro) {
		Map<String, Object> arguments = new HashMap<String, Object>();
		String titulo = "Consultar Solicitud";
		String empleadoSolicitante = solicitudFiltro.getEmpleadoSolicitud()
				.getNombres()
				+ " "
				+ solicitudFiltro.getEmpleadoSolicitud().getApellidos()
				+ " (V-"
				+ solicitudFiltro.getEmpleadoSolicitud().getCedula()
				+ ")";
		ListModel<Usuario> listaEmpleadosCargoModel;
		List<Usuario> empleadosCargo = usuarioDAO
				.obtenerUsuariosActivosPorCargo(solicitudFiltro.getCargoTarea()
						.getId());
		listaEmpleadosCargoModel = new ListModelList<Usuario>(empleadosCargo);
		((ListModelList<Usuario>) listaEmpleadosCargoModel).setMultiple(true);

		if (solicitudFiltro.getEmpleadoTarea() != null) {
			String empleadoTarea = solicitudFiltro.getEmpleadoSolicitud()
					.getNombres()
					+ " "
					+ solicitudFiltro.getEmpleadoTarea().getApellidos()
					+ " (V-" + solicitudFiltro.getEmpleadoTarea().getCedula()

					+ ")";
			arguments.put("empleadoTarea", empleadoTarea);
			arguments.put("divEmpleado", true);
			for (int i = 0; i < empleadosCargo.size(); i++) {
				if (empleadosCargo.get(i).getId() == solicitudFiltro
						.getEmpleadoTarea().getId()) {
					((AbstractListModel<Usuario>) listaEmpleadosCargoModel)
							.addToSelection(((List<Usuario>) listaEmpleadosCargoModel)
									.get(i));
				}
			}
		}
		bitacoras = bitacoraSolicitudDAO
				.obtenerBitacorasPorSolicitud(solicitudFiltro.getId());
		listaBitacorasModel = new ListModelList<BitacoraSolicitud>(bitacoras);
		String footer = "Total de Movimientos de la tarea " + bitacoras.size();
		arguments.put("bitacoras", listaBitacorasModel);
		arguments.put("footer", footer);
		arguments.put("titulo", titulo);
		arguments.put("icono", "z-icon-eye");
		arguments.put("solicitud", solicitudFiltro);
		arguments.put("empleadoSolicitante", empleadoSolicitante);
		arguments.put("btnAprobarSolicitud", true);
		arguments.put("btnRechazarSolicitud", true);
		arguments.put("btnGuardar", false);
		arguments.put("btnSalir", true);
		arguments.put("btnCancelar", false);
		arguments.put("divTarea", false);
		arguments.put("divRechazo", false);
		arguments.put("divFechaTarea", true);
		arguments.put("empleadosCargo", listaEmpleadosCargoModel);
		Window window = (Window) Executions.getCurrent().createComponents(
				"/vista/ConsultarEditarSolicitud.zul", null, arguments);
		window.doModal();
	}

	// Vista para aprobar solicitud
	public void aprobarSolicitudRecibidaEnEspera(Solicitud solicitudFiltro) {
		Map<String, Object> arguments = new HashMap<String, Object>();
		String titulo = "Aprobar Solicitud";
		String empleadoSolicitante = solicitudFiltro.getEmpleadoSolicitud()
				.getNombres()
				+ " "
				+ solicitudFiltro.getEmpleadoSolicitud().getApellidos()
				+ " (V-"
				+ solicitudFiltro.getEmpleadoSolicitud().getCedula()
				+ ")";
		ListModel<Usuario> listaEmpleadosCargoModel;
		List<Usuario> empleadosCargo = usuarioDAO
				.obtenerUsuariosActivosPorCargo(solicitudFiltro.getCargoTarea()
						.getId());
		listaEmpleadosCargoModel = new ListModelList<Usuario>(empleadosCargo);
		((ListModelList<Usuario>) listaEmpleadosCargoModel).setMultiple(true);

		ListModel<Prioridad> listaPrioridadesModel;
		List<Prioridad> prioridades = prioridadDAO.obtenerPrioridades();
		listaPrioridadesModel = new ListModelList<Prioridad>(prioridades);
		if (solicitudFiltro.getEmpleadoTarea() != null) {
			String empleadoTarea = solicitudFiltro.getEmpleadoSolicitud()
					.getNombres()
					+ " "
					+ solicitudFiltro.getEmpleadoTarea().getApellidos()
					+ " (V-" + solicitudFiltro.getEmpleadoTarea().getCedula()

					+ ")";
			arguments.put("empleadoTarea", empleadoTarea);
			arguments.put("divEmpleado", true);
			for (int i = 0; i < empleadosCargo.size(); i++) {
				if (empleadosCargo.get(i).getId() == solicitudFiltro
						.getEmpleadoTarea().getId()) {
					((AbstractListModel<Usuario>) listaEmpleadosCargoModel)
							.addToSelection(((List<Usuario>) listaEmpleadosCargoModel)
									.get(i));
				}
			}
		}

		arguments.put("titulo", titulo);
		arguments.put("icono", "z-icon-eye");
		arguments.put("solicitud", solicitudFiltro);
		arguments.put("prioridades", listaPrioridadesModel);
		arguments.put("empleadoSolicitante", empleadoSolicitante);
		arguments.put("btnAprobarSolicitud", false);
		arguments.put("btnRechazarSolicitud", false);
		arguments.put("btnGuardar", true);
		arguments.put("btnSalir", false);
		arguments.put("btnCancelar", true);
		arguments.put("divTarea", true);
		arguments.put("divRechazo", false);
		arguments.put("divFechaTarea", false);
		arguments.put("empleadosCargo", listaEmpleadosCargoModel);
		Window window = (Window) Executions.getCurrent().createComponents(
				"/vista/ConsultarEditarSolicitud.zul", null, arguments);
		showNotifyWarning("Completa la Información para asignar la tarea.",
				window);
		window.doModal();
	}

	// Vista para Rechazar solicitud
	public void rechazarSolicitudRecibidaEnEspera(Solicitud solicitudFiltro) {
		Map<String, Object> arguments = new HashMap<String, Object>();
		String titulo = "Rechazar Solicitud";
		String empleadoSolicitante = solicitudFiltro.getEmpleadoSolicitud()
				.getNombres()
				+ " "
				+ solicitudFiltro.getEmpleadoSolicitud().getApellidos()
				+ " (V-"
				+ solicitudFiltro.getEmpleadoSolicitud().getCedula()
				+ ")";
		ListModel<Usuario> listaEmpleadosCargoModel;
		List<Usuario> empleadosCargo = usuarioDAO
				.obtenerUsuariosActivosPorCargo(solicitudFiltro.getCargoTarea()
						.getId());
		listaEmpleadosCargoModel = new ListModelList<Usuario>(empleadosCargo);
		((ListModelList<Usuario>) listaEmpleadosCargoModel).setMultiple(true);

		if (solicitudFiltro.getEmpleadoTarea() != null) {
			String empleadoTarea = solicitudFiltro.getEmpleadoSolicitud()
					.getNombres()
					+ " "
					+ solicitudFiltro.getEmpleadoTarea().getApellidos()
					+ " (V-" + solicitudFiltro.getEmpleadoTarea().getCedula()

					+ ")";
			arguments.put("empleadoTarea", empleadoTarea);
			arguments.put("divEmpleado", true);
			for (int i = 0; i < empleadosCargo.size(); i++) {
				if (empleadosCargo.get(i).getId() == solicitudFiltro
						.getEmpleadoTarea().getId()) {
					((AbstractListModel<Usuario>) listaEmpleadosCargoModel)
							.addToSelection(((List<Usuario>) listaEmpleadosCargoModel)
									.get(i));
				}
			}
		}

		bitacoras = bitacoraSolicitudDAO
				.obtenerBitacorasPorSolicitud(solicitudFiltro.getId());
		listaBitacorasModel = new ListModelList<BitacoraSolicitud>(bitacoras);
		String footer = "Total de Movimientos de la tarea " + bitacoras.size();
		arguments.put("bitacoras", listaBitacorasModel);
		arguments.put("footer", footer);
		arguments.put("titulo", titulo);
		arguments.put("icono", "z-icon-eye");
		arguments.put("solicitud", solicitudFiltro);
		arguments.put("empleadoSolicitante", empleadoSolicitante);
		arguments.put("btnAprobarSolicitud", false);
		arguments.put("btnRechazarSolicitud", false);
		arguments.put("btnGuardar", true);
		arguments.put("btnSalir", false);
		arguments.put("btnCancelar", true);
		arguments.put("divTarea", false);
		arguments.put("divRechazo", true);
		arguments.put("divFechaTarea", true);
		arguments.put("empleadosCargo", listaEmpleadosCargoModel);
		Window window = (Window) Executions.getCurrent().createComponents(
				"/vista/ConsultarEditarSolicitud.zul", null, arguments);
		showNotifyWarning("Ingresa la razón del rechazo de la solicitud",
				window);
		window.doModal();
	}

	// Actualiza las Tablas de Solicitudes Enviadas
	@GlobalCommand
	@NotifyChange({ "solicitudesEnviadasTodas", "solicitudesEnviadasEnEspera",
			"solicitudesEnviadasAprobadas", "solicitudesEnviadasRechazadas",
			"footerMessageSolicitudesEnviadasEnEspera",
			"footerMessageSolicitudesEnviadasTodas",
			"footerMessageSolicitudesEnviadasAprobadas",
			"footerMessageSolicitudesEnviadasRechazadas" })
	public void actualizarTablaEnviadas() {
		solicitudesEnviadasTodas = solicitudDAO
				.obtenerSolicitudesEnviadasPorEmpleado(usuarioSession.getId());
		solicitudesEnviadasEnEspera = solicitudDAO
				.obtenerSolicitudesEnviadasPorEmpleadoPorEstatusNombre(
						usuarioSession.getId(), "EN ESPERA");
		solicitudesEnviadasAprobadas = solicitudDAO
				.obtenerSolicitudesEnviadasPorEmpleadoPorEstatusNombre(
						usuarioSession.getId(), "APROBADA");
		solicitudesEnviadasRechazadas = solicitudDAO
				.obtenerSolicitudesEnviadasPorEmpleadoPorEstatusNombre(
						usuarioSession.getId(), "RECHAZADA");

	}

	// Actualiza las Tablas de solicitudes recibidas
	@GlobalCommand
	@NotifyChange({ "solicitudesRecibidasTodas",
			"solicitudesRecibidasEnEspera", "solicitudesRecibidasAprobadas",
			"solicitudesRecibidasRechazadas",
			"footerMessageSolicitudesRecibidasTodas",
			"footerMessageSolicitudesRecibidasEnEspera",
			"footerMessageSolicitudesRecibidasAprobadas",
			"footerMessageSolicitudesRecibidasRechazadas" })
	public void actualizarTablaRecibidas() {

		solicitudesRecibidasTodas = solicitudDAO
				.obtenerSolicitudesRecibidasPorEmpleado(usuarioSession.getId());
		solicitudesRecibidasEnEspera = solicitudDAO
				.obtenerSolicitudesRecibidasPorEmpleadoPorEstatusNombre(
						usuarioSession.getId(), "EN ESPERA");
		solicitudesRecibidasAprobadas = solicitudDAO
				.obtenerSolicitudesRecibidasPorEmpleadoPorEstatusNombre(
						usuarioSession.getId(), "APROBADA");
		solicitudesRecibidasRechazadas = solicitudDAO
				.obtenerSolicitudesRecibidasPorEmpleadoPorEstatusNombre(
						usuarioSession.getId(), "RECHAZADA");
	}

	private String getIdSelectedRowTarea(Grid grid) {
		for (Component component : grid.getRows().getChildren()) {
			if (component instanceof Row) {
				Row row = (Row) component;
				for (Component component2 : row.getChildren()) {
					if (component2 instanceof Radio) {
						Radio radio = (Radio) component2;

						if (radio.isSelected()) {
							return radio.getValue().toString();
						}
					}
				}
			}
		}
		return null;
	}

	private void showNotify(String msg, Component ref) {
		Clients.showNotification(msg, "error", ref, "end_center", 3000);
	}

	private void showNotifyInfo(String msg, Component ref) {
		Clients.showNotification(msg, "info", ref, "middle_center", 5000, true);
	}

	private void showNotifyWarning(String msg, Component ref) {
		Clients.showNotification(msg, "warning", ref, "middle_center", 3000,
				true);
	}
}
