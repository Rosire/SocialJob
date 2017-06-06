package controlador;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import modelo.dao.EstatusNotificacionDAO;
import modelo.dao.NotificacionSolicitudDAO;
import modelo.dao.NotificacionTareaAsignadaDAO;
import modelo.dao.NotificacionTareaRepetitivaAsignadaDAO;
import modelo.dto.Estatus;
import modelo.dto.EstatusNotificacion;
import modelo.dto.NotificacionSolicitud;
import modelo.dto.NotificacionTareaAsignada;
import modelo.dto.NotificacionTareaRepetitivaAsignada;
import modelo.dto.Usuario;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.jsp.zul.GridTag;
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
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.Window;

public class ControladorNotificaciones extends SelectorComposer<Component> {

	@Wire
	private Grid gridNotificacionesSolicitudes;
	@Wire
	private Grid gridNotificacionesTareasAsignadas;
	@Wire
	private Grid gridNotificacionesTareasRepetitivasAsignadas;
	@Wire
	private Panel panelSolicitudes;
	@Wire
	private Panel panelTareasAsignadas;
	@Wire
	private Panel panelTareasRepetitivasAsignadas;

	// Variable Session
	private Session miSession = Sessions.getCurrent();
	private Usuario usuarioSession = (Usuario) miSession
			.getAttribute("usuario");

	private EstatusNotificacionDAO estatusNotificacionDAO = new EstatusNotificacionDAO();
	private NotificacionSolicitudDAO notificacionSolicitudDAO = new NotificacionSolicitudDAO();
	private NotificacionTareaRepetitivaAsignadaDAO notificacionTareaRepetitivaAsignadaDAO = new NotificacionTareaRepetitivaAsignadaDAO();
	private NotificacionTareaAsignadaDAO notificacionTareaAsignadaDAO = new NotificacionTareaAsignadaDAO();

	private ListModel<NotificacionSolicitud> listaNotificacionesSolicitudesModel;
	private List<NotificacionSolicitud> notificacionesSolicitudes;

	private ListModel<NotificacionTareaAsignada> listaNotificacionesTareasAsignadasModel;
	private List<NotificacionTareaAsignada> notificacionesTareasAsignadas;

	private ListModel<NotificacionTareaRepetitivaAsignada> listaNotificacionesTareasRepetitivasAsignadasModel;
	private List<NotificacionTareaRepetitivaAsignada> notificacionesTareasRepetitivasAsignadas;

	private NotificacionSolicitud notificacionSolicitud = new NotificacionSolicitud();
	private NotificacionTareaAsignada notificacionTareaAsignada = new NotificacionTareaAsignada();
	private NotificacionTareaRepetitivaAsignada notificacionTareaRepetitivaAsignada = new NotificacionTareaRepetitivaAsignada();

	private static final String footerMessageNotificacionesSolicitudes = "Total de Notificaciones de Solicitudes: %d";
	private static final String footerMessageNotificacionesTareasRepetitivasAsignadas = "Total de Notificaciones de Tareas Repetitivas Asignadas: %d";
	private static final String footerMessageNotificacionesTareasAsignadas = "Total de Notificaciones de Tareas Asignadas: %d";

	public ControladorNotificaciones() {
		super();
		notificacionesSolicitudes = notificacionSolicitudDAO
				.obtenerNotificacionesSolicitudesPorEmpleado(usuarioSession
						.getId());
		notificacionesTareasAsignadas = notificacionTareaAsignadaDAO
				.obtenerNotificacionesTareasAsignadasPorEmpleado(usuarioSession
						.getId());
		notificacionesTareasRepetitivasAsignadas = notificacionTareaRepetitivaAsignadaDAO
				.obtenerNotificacionesTareasRepetitivasAsignadasPorEmpleado(usuarioSession
						.getId());
	}

	public String getFooterMessageNotificacionesSolicitudes() {
		return String.format(footerMessageNotificacionesSolicitudes,
				notificacionesSolicitudes.size());
	}

	public ListModel<NotificacionSolicitud> getNotificacionesSolicitudes() {
		listaNotificacionesSolicitudesModel = new ListModelList<NotificacionSolicitud>(
				notificacionesSolicitudes);
		return listaNotificacionesSolicitudesModel;
	}

	public String getFooterMessageNotificacionesTareasRepetitivasAsignadas() {
		return String.format(
				footerMessageNotificacionesTareasRepetitivasAsignadas,
				notificacionesTareasRepetitivasAsignadas.size());
	}

	public ListModel<NotificacionTareaRepetitivaAsignada> getNotificacionesTareasRepetitivasAsignadas() {
		listaNotificacionesTareasRepetitivasAsignadasModel = new ListModelList<NotificacionTareaRepetitivaAsignada>(
				notificacionesTareasRepetitivasAsignadas);
		return listaNotificacionesTareasRepetitivasAsignadasModel;
	}

	public String getFooterMessageNotificacionesTareasAsignadas() {
		return String.format(footerMessageNotificacionesTareasAsignadas,
				notificacionesTareasAsignadas.size());
	}

	public ListModel<NotificacionTareaAsignada> getNotificacionesTareasAsignadas() {
		listaNotificacionesTareasAsignadasModel = new ListModelList<NotificacionTareaAsignada>(
				notificacionesTareasAsignadas);
		return listaNotificacionesTareasAsignadasModel;
	}

	// Mostrar Tabla de Notificaciones de Solicitudes
	@Listen("onClick=#navItemSolicitudes")
	public void mostrarPanelSolicitudes() {
		panelSolicitudes.setVisible(true);
		panelTareasRepetitivasAsignadas.setVisible(false);
		panelTareasAsignadas.setVisible(false);
	}

	// Mostrar Tabla de Notificaciones de Tareas Asignadas
	@Listen("onClick=#navItemTareasAsignadas")
	public void mostrarPanelTareasAsignadas() {
		panelSolicitudes.setVisible(false);
		panelTareasAsignadas.setVisible(true);
		panelTareasRepetitivasAsignadas.setVisible(false);
	}

	// Mostrar Tabla de Notificaciones de Tareas Repetitivas Asignadas
	@Listen("onClick=#navItemTareasRepetitivasAsignadas")
	public void mostrarPanelTareasRepetitivasAsignadas() {
		panelSolicitudes.setVisible(false);
		panelTareasAsignadas.setVisible(false);
		panelTareasRepetitivasAsignadas.setVisible(true);
	}

	// Consultar una Notificacion Solicitud
	@Listen("onClick=#btnConsultarNotificacionSolicitud")
	public void consultarNotificacionSolicitud() {
		String indexSeleccionado = getIdSelectedRowCargo(gridNotificacionesSolicitudes);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una notificación a Consultar",
					gridNotificacionesSolicitudes);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			notificacionSolicitud = notificacionSolicitudDAO
					.obtenerNotificacionSolicitud(id);
			EstatusNotificacion estatus = estatusNotificacionDAO
					.obtenerEstatusNotificacionPorNombre("LEIDA");

			Map<String, Object> arguments = new HashMap<String, Object>();
			String titulo = "Consultar Notificacion";
			String accion = notificacionSolicitud.getBitacora().getAccion();
			if (accion.equals("RECHAZAR")) {
				arguments.put("divRechazo", true);
			}
			arguments.put("titulo", titulo);
			arguments.put("icono", "z-icon-eye");
			arguments.put("notificacion", notificacionSolicitud);
			arguments.put("btnConsultarSolicitud", true);
			Window window = (Window) Executions.createComponents(
					"/vista/ConsultarNotificacion.zul", null, arguments);
			window.doModal();
			notificacionSolicitud.setEstatusNotificacion(estatus);
			notificacionSolicitudDAO
					.actualizarNotificacionSolicitud(notificacionSolicitud);
			BindUtils.postGlobalCommand(null, null,
					"actualizarNotificacionesSolicitudes", null);
			BindUtils.postGlobalCommand(null, null, "actualizarNotificaciones",
					null);
		}
	}

	@Listen("onClick=#btnConsultarNotificacionTareaAsignada")
	public void consultarNotificacionTareaAsignada() {
		String indexSeleccionado = getIdSelectedRowCargo(gridNotificacionesTareasAsignadas);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una notificación a Consultar",
					gridNotificacionesTareasAsignadas);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			notificacionTareaAsignada = notificacionTareaAsignadaDAO
					.obtenerNotificacionTareaAsignada(id);
			EstatusNotificacion estatus = estatusNotificacionDAO
					.obtenerEstatusNotificacionPorNombre("LEIDA");
			Map<String, Object> arguments = new HashMap<String, Object>();
			String titulo = "Consultar Notificacion";
			String accion = notificacionTareaAsignada.getBitacora().getAccion();
			if (accion.equals("RECHAZAR")) {
				arguments.put("divRechazo", true);
			}
			arguments.put("titulo", titulo);
			arguments.put("icono", "z-icon-eye");
			arguments.put("tipoTarea", "Asignada");
			arguments.put("notificacion", notificacionTareaAsignada);
			arguments.put("btnConsultarTarea", true);
			Window window = (Window) Executions.createComponents(
					"/vista/consultarNotificacion.zul", null, arguments);
			window.doModal();
			notificacionTareaAsignada.setEstatusNotificacion(estatus);
			notificacionTareaAsignadaDAO
					.actualizarNotificacionTareaAsignada(notificacionTareaAsignada);
			BindUtils.postGlobalCommand(null, null,
					"actualizarNotificacionesTareasAsignadas", null);
			BindUtils.postGlobalCommand(null, null, "actualizarNotificaciones",
					null);
		}
	}

	@Listen("onClick=#btnConsultarNotificacionTareaRepetitivaAsignada")
	public void consultarNotificacionTareaRepetitivaAsignada() {
		String indexSeleccionado = getIdSelectedRowCargo(gridNotificacionesTareasRepetitivasAsignadas);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una notificación a Consultar",
					gridNotificacionesTareasRepetitivasAsignadas);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			notificacionTareaRepetitivaAsignada = notificacionTareaRepetitivaAsignadaDAO
					.obtenerNotificacionTareaRepetitivaAsignada(id);
			EstatusNotificacion estatus = estatusNotificacionDAO
					.obtenerEstatusNotificacionPorNombre("LEIDA");
			Map<String, Object> arguments = new HashMap<String, Object>();
			String titulo = "Consultar Notificación";
			String accion = notificacionTareaRepetitivaAsignada.getBitacora()
					.getAccion();
			if (accion.equals("RECHAZAR")) {
				arguments.put("divRechazo", true);
			}
			arguments.put("titulo", titulo);
			arguments.put("icono", "z-icon-eye");
			arguments.put("tipoTarea", "Repetitiva");
			arguments.put("notificacion", notificacionTareaRepetitivaAsignada);
			arguments.put("btnConsultarTarea", true);
			Window window = (Window) Executions.createComponents(
					"/vista/ConsultarNotificacion.zul", null, arguments);
			window.doModal();
			notificacionTareaRepetitivaAsignada.setEstatusNotificacion(estatus);
			notificacionTareaRepetitivaAsignadaDAO
					.actualizarNotificacionTareaRepetitivaAsignada(notificacionTareaRepetitivaAsignada);
			BindUtils.postGlobalCommand(null, null,
					"actualizarNotificacionesTareasRepetitivasAsignadas", null);
			BindUtils.postGlobalCommand(null, null, "actualizarNotificaciones",
					null);
		}
	}

	@GlobalCommand
	@NotifyChange({ "notificacionesSolicitudes" })
	public void actualizarNotificacionesSolicitudes() {
		notificacionesSolicitudes = notificacionSolicitudDAO
				.obtenerNotificacionesSolicitudesPorEmpleado(usuarioSession
						.getId());
	}

	@GlobalCommand
	@NotifyChange({ "notificacionesTareasAsignadas" })
	public void actualizarNotificacionesTareasAsignadas() {
		notificacionesTareasAsignadas = notificacionTareaAsignadaDAO
				.obtenerNotificacionesTareasAsignadasPorEmpleado(usuarioSession
						.getId());
	}

	@GlobalCommand
	@NotifyChange({ "notificacionesTareasRepetitivasAsignadas" })
	public void actualizarNotificacionesTareasRepetitivasAsignadas() {
		notificacionesTareasRepetitivasAsignadas = notificacionTareaRepetitivaAsignadaDAO
				.obtenerNotificacionesTareasRepetitivasAsignadasPorEmpleado(usuarioSession
						.getId());
	}

	private void showNotify(String msg, Component ref) {
		Clients.showNotification(msg, "error", ref, "end_center", 3000, true);
	}

	private void showNotifyInfo(String msg, Component ref) {
		Clients.showNotification(msg, "info", ref, "middle_center", 5000, true);
	}

	private String getIdSelectedRowCargo(Grid grid) {
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

}
