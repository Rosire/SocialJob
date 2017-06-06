package controlador;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import modelo.dao.EstatusNotificacionDAO;
import modelo.dao.NotificacionSolicitudDAO;
import modelo.dao.NotificacionTareaAsignadaDAO;
import modelo.dao.NotificacionTareaRepetitivaAsignadaDAO;
import modelo.dao.UsuarioDAO;
import modelo.dto.BitacoraTareaAsignada;
import modelo.dto.NotificacionSolicitud;
import modelo.dto.NotificacionTareaAsignada;
import modelo.dto.NotificacionTareaRepetitivaAsignada;
import modelo.dto.Solicitud;
import modelo.dto.TareaAsignada;
import modelo.dto.TareaRepetitiva;
import modelo.dto.TareaRepetitivaAsignada;
import modelo.dto.Usuario;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

public class ControladorConsultarNotificacion extends
		SelectorComposer<Component> {
	@Wire
	private Window windowConsultarNotificacion;
	@Wire
	private Label labelCodigo;
	@Wire
	private Label labelTipoTarea;

	private EstatusNotificacionDAO estatusNotificacionDAO = new EstatusNotificacionDAO();
	private NotificacionSolicitudDAO notificacionSolicitudDAO = new NotificacionSolicitudDAO();
	private NotificacionTareaRepetitivaAsignadaDAO notificacionTareaRepetitivaAsignadaDAO = new NotificacionTareaRepetitivaAsignadaDAO();
	private NotificacionTareaAsignadaDAO notificacionTareaAsignadaDAO = new NotificacionTareaAsignadaDAO();

	private UsuarioDAO usuarioDAO = new UsuarioDAO();
	private SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");

	@Listen("onClick=#btnSalir")
	public void salirVentana() {
		windowConsultarNotificacion.onClose();
	}

	@Listen("onClick=#btnConsultarSolicitud")
	public void consultarSolicitud() {
		windowConsultarNotificacion.onClose();
		NotificacionSolicitud notificacionSolicitud = notificacionSolicitudDAO
				.obtenerNotificacionSolicitud(Integer.parseInt(labelCodigo
						.getValue()));
		Solicitud solicitudFiltro = notificacionSolicitud.getBitacora()
				.getSolicitud();
		Map<String, Object> arguments = new HashMap<String, Object>();
		String titulo = "Consultar Solicitud";
		String empleadoSolicitante = solicitudFiltro.getEmpleadoSolicitud()
				.getNombres()
				+ " "
				+ solicitudFiltro.getEmpleadoSolicitud().getApellidos()
				+ " (V-"
				+ solicitudFiltro.getEmpleadoSolicitud().getCedula()
				+ ")";
		arguments.put("titulo", titulo);
		arguments.put("icono", "z-icon-eye");
		arguments.put("solicitud", solicitudFiltro);
		arguments.put("empleadoSolicitante", empleadoSolicitante);
		arguments.put("btnSalir", true);
		arguments.put("divFechaTarea", true);
		Window window = (Window) Executions.getCurrent().createComponents(
				"/vista/ConsultarEditarSolicitud.zul", null, arguments);
		window.doModal();
	}

	@Listen("onClick=#btnConsultarTarea")
	public void consultarTarea() {
		windowConsultarNotificacion.onClose();
		if (labelTipoTarea.getValue().equals("Repetitiva")) {
			consultarTareaRepetitiva();
		} else {
			consultarTareaAsignada();
		}

	}

	public void consultarTareaAsignada() {
		NotificacionTareaAsignada notificacionTareaAsignadaFiltro = notificacionTareaAsignadaDAO
				.obtenerNotificacionTareaAsignada(Integer.parseInt(labelCodigo
						.getValue()));
		TareaAsignada tareaAsignadaFiltro = notificacionTareaAsignadaFiltro
				.getBitacora().getTareaAsignada();
		Map<String, Object> arguments = new HashMap<String, Object>();
		Usuario usuario = tareaAsignadaFiltro.getCargoEmpleado().getEmpleado();
		String supervisor = usuario.getNombres() + " " + usuario.getApellidos()
				+ " (V-" + usuario.getCedula() + ")";
		String titulo = "Consultar Tarea";
		String fechaTarea;
		try {
			fechaTarea = formatoFecha.format(tareaAsignadaFiltro.getFecha());
		} catch (Exception e) {
			fechaTarea = "";
		}
		arguments.put("fechaTarea", fechaTarea);
		arguments.put("titulo", titulo);
		arguments.put("supervisor", supervisor);
		arguments.put("icono", "z-icon-eye");
		arguments.put("tareaAsignada", tareaAsignadaFiltro);
		arguments.put("divProgreso", true);
		arguments.put("btnSalirLabel", true);
		Window window = (Window) Executions.getCurrent().createComponents(
				"/vista/ConsultarEditarTareaAsignada.zul", null, arguments);
		window.doModal();
	}

	public void consultarTareaRepetitiva() {
		NotificacionTareaRepetitivaAsignada notificacionTareaRepetitivaAsignadaFiltro = notificacionTareaRepetitivaAsignadaDAO
				.obtenerNotificacionTareaRepetitivaAsignada(Integer
						.parseInt(labelCodigo.getValue()));
		TareaRepetitivaAsignada tareaRepetitivaAsignadaFiltro = notificacionTareaRepetitivaAsignadaFiltro
				.getBitacora().getTareaRepetitivaAsignada();
		TareaRepetitiva tareaRepetitivaFiltro = tareaRepetitivaAsignadaFiltro
				.getTareaRepetitiva();
		Map<String, Object> arguments = new HashMap<String, Object>();
		String fecha;
		try {
			fecha = formatoFecha.format(tareaRepetitivaFiltro.getFechaInicio());
		} catch (Exception e) {
			fecha = "";
		}
		arguments.put("titulo", "Consultar Tarea Asignada");
		arguments.put("icono", "z-icon-eye");
		arguments.put("tareaRepetitiva", tareaRepetitivaFiltro);
		arguments.put("divConsultar", true);
		arguments.put("btnSalir", true);
		arguments.put("fecha", fecha);
		Window window = (Window) Executions.getCurrent().createComponents(
				"/vista/consultarEditarTareaRepetitiva.zul", null, arguments);
		window.doModal();
	}
}
