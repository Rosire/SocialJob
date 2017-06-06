package controlador;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import modelo.dao.BitacoraSolicitudDAO;
import modelo.dao.BitacoraTareaAsignadaDAO;
import modelo.dao.CargoDAO;
import modelo.dao.CargoEmpleadoDAO;
import modelo.dao.EstatusNotificacionDAO;
import modelo.dao.EstatusSolicitudDAO;
import modelo.dao.EstatusTareaAsignadaDAO;
import modelo.dao.NotificacionSolicitudDAO;
import modelo.dao.NotificacionTareaAsignadaDAO;
import modelo.dao.PrioridadDAO;
import modelo.dao.SolicitudDAO;
import modelo.dao.TareaAsignadaDAO;
import modelo.dao.TareaDAO;
import modelo.dto.BitacoraSolicitud;
import modelo.dto.BitacoraTareaAsignada;
import modelo.dto.Cargo;
import modelo.dto.CargoEmpleado;
import modelo.dto.Estatus;
import modelo.dto.EstatusNotificacion;
import modelo.dto.EstatusSolicitud;
import modelo.dto.EstatusTareaAsignada;
import modelo.dto.NotificacionSolicitud;
import modelo.dto.NotificacionTareaAsignada;
import modelo.dto.Prioridad;
import modelo.dto.Solicitud;
import modelo.dto.Tarea;
import modelo.dto.TareaAsignada;
import modelo.dto.Usuario;

import org.zkoss.bind.BindUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class ControladorConsultarEditarSolicitud extends
		SelectorComposer<Component> {
	@Wire
	private Window windowConsultarEditarSolicitud;
	@Wire
	private Button btnAprobarSolicitud;
	@Wire
	private Button btnRechazarSolicitud;
	@Wire
	private Button btnGuardar;
	@Wire
	private Button btnSalir;
	@Wire
	private Button btnCancelar;
	@Wire
	private Div divTarea;
	@Wire
	private Div divRechazo;
	@Wire
	private Div divFechaTarea;
	@Wire
	private Textbox lblRazonRechazo;
	@Wire
	private Datebox lblFechaTarea;
	@Wire
	private Textbox lblIndicaciones;
	@Wire
	private Listbox listaEmpleados;
	@Wire
	private Label labelCodigo;
	@Wire
	private Combobox cmbPrioridades;

	private SolicitudDAO solicitudDAO = new SolicitudDAO();
	private NotificacionSolicitudDAO notificacionSolicitudDAO = new NotificacionSolicitudDAO();
	private TareaAsignadaDAO tareaAsignadaDAO = new TareaAsignadaDAO();
	private BitacoraTareaAsignadaDAO bitacoraTareaAsignadaDAO = new BitacoraTareaAsignadaDAO();
	private NotificacionTareaAsignadaDAO notificacionTareaAsignadaDAO = new NotificacionTareaAsignadaDAO();
	private EstatusNotificacionDAO estatusNotificacionDAO = new EstatusNotificacionDAO();
	private EstatusSolicitudDAO estatusSolicitudDAO = new EstatusSolicitudDAO();
	private EstatusTareaAsignadaDAO estatusTareaAsignadaDAO = new EstatusTareaAsignadaDAO();
	private BitacoraSolicitudDAO bitacoraSolicitudDAO = new BitacoraSolicitudDAO();
	private PrioridadDAO prioridadDAO = new PrioridadDAO();
	private CargoEmpleadoDAO cargoEmpleadoDAO = new CargoEmpleadoDAO();

	private ListModel<Prioridad> listaPrioridadesModel;
	private List<Prioridad> prioridades;

	// Variable Sesion
	private Session miSession = Sessions.getCurrent();
	private Usuario usuarioFiltro = (Usuario) miSession.getAttribute("usuario");

	public ControladorConsultarEditarSolicitud() {
		super();

	}

	public ListModel<Prioridad> getPrioridades() {
		prioridades = prioridadDAO.obtenerPrioridades();
		listaPrioridadesModel = new ListModelList<Prioridad>(prioridades);
		return listaPrioridadesModel;
	}

	// Salir
	@Listen("onClick=#btnSalir")
	public void salirVentana() {
		windowConsultarEditarSolicitud.onClose();
	}

	// Aprobar
	@Listen("onClick=#btnAprobarSolicitud")
	public void aprobarSolicitud() {
		btnAprobarSolicitud.setVisible(false);
		btnRechazarSolicitud.setVisible(false);
		btnGuardar.setVisible(true);
		btnSalir.setVisible(false);
		btnCancelar.setVisible(true);
		divTarea.setVisible(true);
		divRechazo.setVisible(false);
		divFechaTarea.setVisible(false);
		showNotifyWarning("Completa la Información para asignar la tarea.",
				windowConsultarEditarSolicitud);
	}

	// Rechazar
	@Listen("onClick=#btnRechazarSolicitud")
	public void rechazarSolicitud() {
		btnAprobarSolicitud.setVisible(false);
		btnRechazarSolicitud.setVisible(false);
		btnGuardar.setVisible(true);
		btnSalir.setVisible(false);
		btnCancelar.setVisible(true);
		divTarea.setVisible(false);
		divRechazo.setVisible(true);
		divFechaTarea.setVisible(true);
		showNotifyWarning("Ingresa la razón del rechazo de la solicitud",
				windowConsultarEditarSolicitud);
	}

	// Cancelar
	@Listen("onClick=#btnCancelar")
	public void cancelarRegistro() {
		Messagebox.show("¿Seguro que desea Cancelar?", "Cancelar",
				Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION,
				new EventListener<Event>() {
					public void onEvent(Event e) {
						if (Messagebox.ON_OK.equals(e.getName())) {
							// OK is clicked
							windowConsultarEditarSolicitud.onClose();
						} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
							// Cancel is clicked
						}
					}
				});
	}

	// Guardar Modificacion
	@Listen("onClick=#btnGuardar")
	public void guardarModificacion() {
		int idSolicitud = Integer.parseInt(labelCodigo.getValue().toString());
		Solicitud solicitud = solicitudDAO.obtenerSolicitud(idSolicitud);

		if (divRechazo.isVisible()) {
			String razonRechazo = lblRazonRechazo.getValue();
			EstatusSolicitud estatusSolicitud = estatusSolicitudDAO
					.obtenerEstatusSolicitudPorNombre("RECHAZADA");
			String nombreCompleto = usuarioFiltro.getNombres() + " "
					+ usuarioFiltro.getApellidos();
			String descripcion = "El Supervisor " + nombreCompleto + " (V-"
					+ usuarioFiltro.getCedula()
					+ ") ha rechazado su solicitud ";
			solicitud.setEstatusSolicitud(estatusSolicitud);
			solicitudDAO.actualizarSolicitud(solicitud);
			BitacoraSolicitud bitacoraSolicitud = new BitacoraSolicitud(
					solicitud, usuarioFiltro, new Date(), razonRechazo,
					"RECHAZAR");
			bitacoraSolicitudDAO.registrarBitacoraSolicitud(bitacoraSolicitud);
			EstatusNotificacion estatusNotificacion = estatusNotificacionDAO
					.obtenerEstatusNotificacionPorNombre("POR LEER");

			NotificacionSolicitud notificacionSolicitud = new NotificacionSolicitud(
					descripcion, estatusNotificacion, bitacoraSolicitud,
					solicitud.getEmpleadoSolicitud());
			notificacionSolicitudDAO
					.registrarNotificacionSolicitud(notificacionSolicitud);
			showNotifyInfo(
					"Solicitud Rechazada y notificación enviada al Empleado solicitante exitosamente",
					windowConsultarEditarSolicitud);
			windowConsultarEditarSolicitud.onClose();
		} else {
			Date fecha = lblFechaTarea.getValue();
			String indicaciones = lblIndicaciones.getValue();
			ListModelList<Object> listaEmpleadosSeleccion = (ListModelList<Object>) listaEmpleados
					.getModel();
			Set<Object> conjuntoEmpleados = listaEmpleadosSeleccion
					.getSelection();
			if (conjuntoEmpleados.size() == 0) {
				showNotify("Debe Seleccionar al menos un cargo del empleado",
						listaEmpleados);
				listaEmpleados.focus();
			} else {

				Tarea tarea = solicitud.getTarea();
				Cargo cargo = solicitud.getCargoTarea();
				EstatusSolicitud estatusSolicitud = estatusSolicitudDAO
						.obtenerEstatusSolicitudPorNombre("APROBADA");

				List<Object> list = new ArrayList<Object>(conjuntoEmpleados);

				EstatusTareaAsignada estatusTareaAsignada = estatusTareaAsignadaDAO
						.obtenerEstatusPorNombre("POR INICIAR");
				int progreso = 0;
				String nombreCompleto = usuarioFiltro.getNombres() + " "
						+ usuarioFiltro.getApellidos();
				String descripcion = "El Supervisor " + nombreCompleto + " (V-"
						+ usuarioFiltro.getCedula()
						+ " ) ha aprobado su solicitud";
				solicitud.setEstatusSolicitud(estatusSolicitud);
				solicitudDAO.actualizarSolicitud(solicitud);
				BitacoraSolicitud bitacoraSolicitud = new BitacoraSolicitud(
						solicitud, usuarioFiltro, new Date(),
						"Solicitud Aprobada por el Supervisor", "APROBAR");
				bitacoraSolicitudDAO
						.registrarBitacoraSolicitud(bitacoraSolicitud);

				EstatusNotificacion estatusNotificacion = estatusNotificacionDAO
						.obtenerEstatusNotificacionPorNombre("POR LEER");

				NotificacionSolicitud notificacionSolicitud = new NotificacionSolicitud(
						descripcion, estatusNotificacion, bitacoraSolicitud,
						solicitud.getEmpleadoSolicitud());
				notificacionSolicitudDAO
						.registrarNotificacionSolicitud(notificacionSolicitud);
				ListModelList<Object> listaPrioriad = (ListModelList<Object>) cmbPrioridades
						.getModel();
				Set<Object> conjuntoPrioridad = listaPrioriad.getSelection();
				Prioridad prioridad = (Prioridad) conjuntoPrioridad.toArray()[0];
				// ASIGNAR TAREAS A CADA EMPLEADO
				for (int i = 0; i < list.size(); i++) {
					Usuario empleado = (Usuario) list.get(i);
					CargoEmpleado cargoEmpleado = cargoEmpleadoDAO
							.obtenerCargoEmpledo(cargo.getId(),
									empleado.getId());
					TareaAsignada tareaAsignada = new TareaAsignada(tarea,
							estatusTareaAsignada, prioridad, cargoEmpleado,
							fecha, indicaciones, progreso, 0, null, null,
							tarea.getDuracion(), 0);

					tareaAsignadaDAO.registrarTareaAsignada(tareaAsignada);

					BitacoraTareaAsignada bitacoraTareaAsignada = new BitacoraTareaAsignada(
							tareaAsignada,
							usuarioFiltro,
							new Date(),
							"Tarea creada por el Supervisor",
							"CREAR");

					descripcion = "El Supervisor "
							+ nombreCompleto
							+ " (V-"
							+ usuarioFiltro.getCedula()
							+ " ) le ha asignado una Tarea, verifique sus tareas pendientes ";
					NotificacionTareaAsignada notificacionTareaAsignada = new NotificacionTareaAsignada(
							descripcion, empleado, bitacoraTareaAsignada,
							estatusNotificacion);

					// ALMACENAR EN LA BITACORA LA CREACION DE LA TAREA ASIGNADA
					// Y GENERAR LA NOTIFICACION
					bitacoraTareaAsignadaDAO
							.registrarBitacoraTareaAsignada(bitacoraTareaAsignada);
					notificacionTareaAsignadaDAO
							.registrarNotificacionTareaAsignada(notificacionTareaAsignada);
				}
				showNotifyInfo("Tarea Asignada Exitosamente",
						windowConsultarEditarSolicitud);
				windowConsultarEditarSolicitud.onClose();
			}

		}
		BindUtils.postGlobalCommand(null, null, "actualizarTablaRecibidas",
				null);
	}

	private void showNotify(String msg, Component ref) {
		Clients.showNotification(msg, "error", ref, "end_center", 3000, true);
	}

	private void showNotifyInfo(String msg, Component ref) {
		Clients.showNotification(msg, "info", ref, "middle_center", 5000, true);
	}

	private void showNotifyWarning(String msg, Component ref) {
		Clients.showNotification(msg, "warning", ref, "middle_center", 5000,
				true);
	}

}
