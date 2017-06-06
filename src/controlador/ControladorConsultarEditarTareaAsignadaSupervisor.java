package controlador;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import modelo.dao.BitacoraTareaAsignadaDAO;
import modelo.dao.CargoDAO;
import modelo.dao.EstatusNotificacionDAO;
import modelo.dao.EstatusTareaAsignadaDAO;
import modelo.dao.NotificacionTareaAsignadaDAO;
import modelo.dao.TareaAsignadaDAO;
import modelo.dto.BitacoraTareaAsignada;
import modelo.dto.BitacoraTareaRepetitivaAsignada;
import modelo.dto.Cargo;
import modelo.dto.CargoEmpleado;
import modelo.dto.EstatusNotificacion;
import modelo.dto.EstatusTareaAsignada;
import modelo.dto.NotificacionTareaAsignada;
import modelo.dto.Tarea;
import modelo.dto.TareaAsignada;
import modelo.dto.Usuario;

import org.zkoss.bind.BindUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
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
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.Slider;
import org.zkoss.zul.Spinner;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class ControladorConsultarEditarTareaAsignadaSupervisor extends
		SelectorComposer<Component> {
	@Wire
	private Window windowConsultarEditarTareaSupervisor;
	@Wire
	private Div divInformacion;
	@Wire
	private Div divBitacora;
	@Wire
	private Button btnSalir;
	@Wire
	private Button btnEditarTarea;
	@Wire
	private Button btnAprobarTarea;
	@Wire
	private Button btnRechazarTarea;
	@Wire
	private Button btnGuardarModificacion;
	@Wire
	private Button btnCancelarModificacion;
	@Wire
	private Label labelCodigo;
	@Wire
	private Label labelEstatus;
	@Wire
	private Label labelCodigoCargo;
	@Wire
	private Label labelIdEmpleado;
	@Wire
	private Label labelTarea;
	@Wire
	private Div divTextIndicaciones;
	@Wire
	private Div divConsulta;
	@Wire
	private Div divLabelIndicaciones;
	@Wire
	private Div divModificar;
	@Wire
	private Div divRechazo;
	@Wire
	private Div divSlider;
	@Wire
	private Div divCalificacion;
	@Wire
	private Slider sliderProgreso;
	@Wire
	private Datebox lblFechaTarea;
	@Wire
	private Textbox lblIndicaciones;
	@Wire
	private Listbox listaEmpleados;
	@Wire
	private Textbox lblIndicacionesModificacion;
	@Wire
	private Combobox cmbTareas;
	@Wire
	private Textbox lblRazonRechazo;
	@Wire
	private Combobox cmbPrioridades;
	@Wire
	private Grid gridBitacoras;
	@Wire
	private Spinner lblCalificacion;

	// Variable Session
	private Session miSession = Sessions.getCurrent();
	private Usuario usuarioFiltro = (Usuario) miSession.getAttribute("usuario");

	private TareaAsignadaDAO tareaAsignadaDAO = new TareaAsignadaDAO();
	private BitacoraTareaAsignadaDAO bitacoraTareaAsignadaDAO = new BitacoraTareaAsignadaDAO();
	private EstatusTareaAsignadaDAO estatusTareaAsignadaDAO = new EstatusTareaAsignadaDAO();
	private EstatusNotificacionDAO estatusNotificacionDAO = new EstatusNotificacionDAO();
	private NotificacionTareaAsignadaDAO notificacionTareaAsignadaDAO = new NotificacionTareaAsignadaDAO();
	private CargoDAO cargoDAO = new CargoDAO();
	private EstatusTareaAsignada estatus = new EstatusTareaAsignada();

	private SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");

	public ControladorConsultarEditarTareaAsignadaSupervisor() {
		super();
	}

	// Salir Ventana
	@Listen("onClick=#btnSalir;  onClick=#btnSalirBitacora")
	public void salirVentana() {
		windowConsultarEditarTareaSupervisor.onClose();
	}

	@Listen("onClick=#btnEditarTarea")
	public void editarTarea() {

		if (labelEstatus.getValue().equals("POR INICIAR")) {
			divConsulta.setVisible(false);
			divModificar.setVisible(true);
		} else {
			btnCancelarModificacion.setVisible(true);
			btnGuardarModificacion.setVisible(true);
			divLabelIndicaciones.setVisible(false);
			btnEditarTarea.setVisible(false);

			btnSalir.setVisible(false);
			showNotifyWarning("Sólo puede modificar las indicaciones",
					windowConsultarEditarTareaSupervisor);
			divTextIndicaciones.setVisible(true);
		}

	}

	@Listen("onClick=#btnRechazarTarea")
	public void rechazarTarea() {
		btnCancelarModificacion.setVisible(true);
		btnGuardarModificacion.setVisible(true);
		btnEditarTarea.setVisible(false);
		btnSalir.setVisible(false);
		btnRechazarTarea.setVisible(false);
		btnAprobarTarea.setVisible(false);
		divSlider.setVisible(true);
		divSlider.focus();
		divRechazo.setVisible(true);
		divCalificacion.setVisible(false);
		showNotifyWarning(
				"Indique la razón por la que rechaza la tarea culminada y el progreso actual.",
				windowConsultarEditarTareaSupervisor);

	}

	// Cancelar Modificacion
	@Listen("onClick=#btnCancelarModificacion; onClick=#btnCancelar")
	public void cancelarRegistro() {
		Messagebox.show("¿Seguro que desea Cancelar?", "Cancelar",
				Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION,
				new EventListener<Event>() {
					public void onEvent(Event e) {
						if (Messagebox.ON_OK.equals(e.getName())) {
							// OK is clicked
							windowConsultarEditarTareaSupervisor.onClose();
						} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
							// Cancel is clicked
						}
					}
				});
	}

	// Aprobar Tarea
	@Listen("onClick=#btnAprobarTarea")
	public void aprobarTarea() {
		btnCancelarModificacion.setVisible(true);
		btnGuardarModificacion.setVisible(true);
		btnEditarTarea.setVisible(false);
		btnSalir.setVisible(false);
		btnRechazarTarea.setVisible(false);
		btnAprobarTarea.setVisible(false);
		divSlider.setVisible(false);
		lblCalificacion.focus();
		divRechazo.setVisible(false);
		divCalificacion.setVisible(true);
		showNotifyWarning("Indique la Calificacion de la tarea culminada",
				lblCalificacion);

	}

	// Guardar Modificacion de Indicaciones o Rechazo de tarea
	@Listen("onClick=#btnGuardarModificacion")
	public void guardarModificacion() {
		int id = Integer.parseInt(labelCodigo.getValue().toString());
		TareaAsignada tareaAsignadaFiltro = tareaAsignadaDAO
				.obtenerTareaAsignada(id);
		if (divCalificacion.isVisible()) {
			estatus = estatusTareaAsignadaDAO
					.obtenerEstatusPorNombre("APROBADA");
			long eficiencia = 0;
			TareaAsignada tareaAsignada = tareaAsignadaDAO
					.obtenerTareaAsignada(id);
			eficiencia = Math
					.abs((tareaAsignada.getFechaFinalizacion().getTime() - tareaAsignada
							.getFechaInicio().getTime()) / 1000);

			tareaAsignada.setEstatusTareaAsignada(estatus);
			tareaAsignada.setCalificacion(lblCalificacion.getValue());
			tareaAsignada.setEficiencia(eficiencia);
			tareaAsignadaDAO.actualizarTareaAsignada(tareaAsignada);
			showNotifyInfo("Tarea Aprobada Exitosamente",
					windowConsultarEditarTareaSupervisor);

			BitacoraTareaAsignada bitacoraTareaAsignada = new BitacoraTareaAsignada(
					tareaAsignada, usuarioFiltro, new Date(),
					"Tarea Aprobada por el Supervisor", "APROBAR");

			bitacoraTareaAsignadaDAO
					.registrarBitacoraTareaAsignada(bitacoraTareaAsignada);
			String nombreCompleto = usuarioFiltro.getNombres() + " "
					+ usuarioFiltro.getApellidos();
			String descripcion = "El Supervisor " + nombreCompleto + " ("
					+ usuarioFiltro.getCedula()
					+ " ) ha aprobado la culminación de la tarea ("
					+ tareaAsignada.getTarea().getNombre()
					+ "), verifique sus tareas aprobadas ";
			EstatusNotificacion estatusNotificacion = estatusNotificacionDAO
					.obtenerEstatusNotificacionPorNombre("POR LEER");

			NotificacionTareaAsignada notificacionTareaAsignada = new NotificacionTareaAsignada(
					descripcion,
					tareaAsignada.getCargoEmpleado().getEmpleado(),
					bitacoraTareaAsignada, estatusNotificacion);
			notificacionTareaAsignadaDAO
					.registrarNotificacionTareaAsignada(notificacionTareaAsignada);
			BindUtils.postGlobalCommand(null, null, "actualizarTablaAsignadas",
					null);
			windowConsultarEditarTareaSupervisor.onClose();
		} else if (divRechazo.isVisible()) {

			// Guardar Rechazo de una Tarea Asignada Culminada
			int progreso = sliderProgreso.getCurpos();
			if (progreso < 100) {

				String razonRechazo = lblRazonRechazo.getValue();
				estatus = estatusTareaAsignadaDAO
						.obtenerEstatusPorNombre("EN MARCHA");
				tareaAsignadaFiltro.setEstatusTareaAsignada(estatus);
				tareaAsignadaFiltro.setProgreso(progreso);
				tareaAsignadaDAO.actualizarTareaAsignada(tareaAsignadaFiltro);
				BitacoraTareaAsignada bitacoraTareaAsignada = new BitacoraTareaAsignada(
						tareaAsignadaFiltro, usuarioFiltro, new Date(),
						razonRechazo, "RECHAZAR");
				String nombreCompleto = usuarioFiltro.getNombres() + " "
						+ usuarioFiltro.getApellidos();
				String descripcion = "El Supervisor "
						+ nombreCompleto
						+ " (V-"
						+ usuarioFiltro.getCedula()
						+ " ) ha Rechazado la culminación de la tarea  asignada ("
						+ tareaAsignadaFiltro.getTarea().getNombre()
						+ "), verifique sus tareas repetitivas asignadas con estatus \"EN MARCHA\" ";
				EstatusNotificacion estatusNotificacion = estatusNotificacionDAO
						.obtenerEstatusNotificacionPorNombre("POR LEER");
				NotificacionTareaAsignada notificacionTareaAsignada = new NotificacionTareaAsignada(
						descripcion, tareaAsignadaFiltro.getCargoEmpleado()
								.getEmpleado(), bitacoraTareaAsignada,
						estatusNotificacion);
				bitacoraTareaAsignadaDAO
						.registrarBitacoraTareaAsignada(bitacoraTareaAsignada);
				notificacionTareaAsignadaDAO
						.registrarNotificacionTareaAsignada(notificacionTareaAsignada);
				showNotifyInfo("Tarea modificada Exitosamente",
						windowConsultarEditarTareaSupervisor);
				BindUtils.postGlobalCommand(null, null,
						"actualizarTablaAsignadas", null);
				windowConsultarEditarTareaSupervisor.onClose();
			} else {
				showNotify(
						"Para Colocar la tarea nuevamenente \"EN MARCHA\" el progreso debe ser menor que 100",
						sliderProgreso);
				sliderProgreso.focus();
			}

		} else {
			BitacoraTareaAsignada bitacoraTareaAsignada = new BitacoraTareaAsignada(
					tareaAsignadaFiltro, usuarioFiltro, new Date(),
					"Tarea modificada por el Supervisor del Cargo", "MODIFICAR");
			Cargo cargo = tareaAsignadaFiltro.getCargoEmpleado().getCargo();
			String indicaciones = lblIndicaciones.getValue();
			tareaAsignadaFiltro.setIndicaciones(indicaciones);
			tareaAsignadaDAO.actualizarTareaAsignada(tareaAsignadaFiltro);
			String nombreCompleto = usuarioFiltro.getNombres() + " "
					+ usuarioFiltro.getApellidos();
			String descripcion = "El Supervisor " + nombreCompleto + " (V-"
					+ usuarioFiltro.getCedula()
					+ " ) ha modificado la tarea de " + labelTarea.getValue()
					+ " que tiene asignada en su cargo " + cargo.getNombre();
			EstatusNotificacion estatusNotificacion = estatusNotificacionDAO
					.obtenerEstatusNotificacionPorNombre("POR LEER");
			NotificacionTareaAsignada notificacionTareaAsignada = new NotificacionTareaAsignada(
					descripcion, tareaAsignadaFiltro.getCargoEmpleado()
							.getEmpleado(), bitacoraTareaAsignada,
					estatusNotificacion);
			bitacoraTareaAsignadaDAO
					.registrarBitacoraTareaAsignada(bitacoraTareaAsignada);
			notificacionTareaAsignadaDAO
					.registrarNotificacionTareaAsignada(notificacionTareaAsignada);
			showNotifyInfo("Tarea Modificada Exitosamente",
					windowConsultarEditarTareaSupervisor);
			BindUtils.postGlobalCommand(null, null, "actualizarTablaAsignadas",
					null);
			windowConsultarEditarTareaSupervisor.onClose();

		}
	}

	// Guardar Tarea Modificada
	@Listen("onClick=#btnGuardarTareaModificada; onOK=#btnGuardarTareaModificada")
	public void guardarTareaModificada() {
		// Guardar Modificacion de la Tarea Asignada
		int id = Integer.parseInt(labelCodigo.getValue().toLowerCase());
		TareaAsignada tareaAsignadaFiltro = tareaAsignadaDAO
				.obtenerTareaAsignada(id);

		Date fecha = lblFechaTarea.getValue();
		String indicaciones = lblIndicacionesModificacion.getValue();
		ListModelList<Object> listaTarea = (ListModelList<Object>) cmbTareas
				.getModel();
		Set<Object> conjuntoTarea = listaTarea.getSelection();

		ListModelList<Object> listaEmpleadosSeleccion = (ListModelList<Object>) listaEmpleados
				.getModel();
		Set<Object> conjuntoEmpleados = listaEmpleadosSeleccion.getSelection();

		if (conjuntoTarea.size() == 0) {
			showNotify("Debe Seleccionar un Rol", cmbTareas);
			cmbTareas.focus();
		} else if (fecha.before(new Date())) {
			showNotify(
					"Debe Seleccionar una fecha mayor o igual a la Fecha Actual del Sistema",
					lblFechaTarea);
			lblFechaTarea.focus();
		} else if (conjuntoEmpleados.size() == 0) {
			showNotify("Debe Seleccionar al menos un cargo del empleado",
					listaEmpleados);
			listaEmpleados.focus();
		} else {
			Tarea tarea = (Tarea) conjuntoTarea.toArray()[0];
			CargoEmpleado cargoEmpleado = (CargoEmpleado) conjuntoEmpleados
					.toArray()[0];
			CargoEmpleado cargoEmpleadoAnterior = tareaAsignadaFiltro
					.getCargoEmpleado();

			tareaAsignadaFiltro.setIndicaciones(indicaciones);
			tareaAsignadaFiltro.setFecha(fecha);
			tareaAsignadaFiltro.setTarea(tarea);
			tareaAsignadaFiltro.setCargoEmpleado(cargoEmpleado);
			tareaAsignadaDAO.actualizarTareaAsignada(tareaAsignadaFiltro);

			EstatusTareaAsignada estatus = estatusTareaAsignadaDAO
					.obtenerEstatusPorNombre("POR INICIAR");
			EstatusNotificacion estatusNotificacion = estatusNotificacionDAO
					.obtenerEstatusNotificacionPorNombre("POR LEER");

			BitacoraTareaAsignada bitacoraTareaAsignada = new BitacoraTareaAsignada(
					tareaAsignadaFiltro, usuarioFiltro, new Date(),
					"Tarea modificada por el Supervisor del Cargo", "MODIFICAR");

			tareaAsignadaDAO.actualizarTareaAsignada(tareaAsignadaFiltro);
			String nombreCompleto;
			String descripcion;
			NotificacionTareaAsignada notificacionTareaAsignada;
			// ALMACENAR EN LA BITACORA LA MODIFICACION DE LA TAREA ASIGNADA
			bitacoraTareaAsignadaDAO
					.registrarBitacoraTareaAsignada(bitacoraTareaAsignada);
			if (cargoEmpleado.getId() != cargoEmpleadoAnterior.getId()) {
				// GENERAR LA NOTIFICACION AL NUEVO EMPLEADO
				nombreCompleto = usuarioFiltro.getNombres() + " "
						+ usuarioFiltro.getApellidos();
				descripcion = "El Supervisor " + nombreCompleto + " (V-"
						+ usuarioFiltro.getCedula()
						+ " ) le ha asignado una Tarea, para su cargo "
						+ cargoEmpleado.getCargo()
						+ " verifique sus tareas \"PENDIENTES\" ";
				notificacionTareaAsignada = new NotificacionTareaAsignada(
						descripcion, cargoEmpleado.getEmpleado(),
						bitacoraTareaAsignada, estatusNotificacion);
				notificacionTareaAsignadaDAO
						.registrarNotificacionTareaAsignada(notificacionTareaAsignada);

				// GENERAR LA NOTIFICACION AL EMPLEADO ANTERIOR
				nombreCompleto = usuarioFiltro.getNombres() + " "
						+ usuarioFiltro.getApellidos();
				descripcion = "El Supervisor " + nombreCompleto + " (V-"
						+ usuarioFiltro.getCedula()
						+ " ) ha reasignado la tarea de " + tarea.getNombre()
						+ " que tenia en su cargo "
						+ cargoEmpleado.getCargo().getNombre();

				notificacionTareaAsignada = new NotificacionTareaAsignada(
						descripcion, cargoEmpleadoAnterior.getEmpleado(),
						bitacoraTareaAsignada, estatusNotificacion);
				notificacionTareaAsignadaDAO
						.registrarNotificacionTareaAsignada(notificacionTareaAsignada);

			} else {
				nombreCompleto = usuarioFiltro.getNombres() + " "
						+ usuarioFiltro.getApellidos();
				descripcion = "El Supervisor " + nombreCompleto + " (V-"
						+ usuarioFiltro.getCedula()
						+ " ) le ha modificado la tarea de "
						+ tarea.getNombre()
						+ " que tiene asignada en su cargo "
						+ cargoEmpleado.getCargo().getNombre();

				notificacionTareaAsignada = new NotificacionTareaAsignada(
						descripcion, cargoEmpleadoAnterior.getEmpleado(),
						bitacoraTareaAsignada, estatusNotificacion);
				notificacionTareaAsignadaDAO
						.registrarNotificacionTareaAsignada(notificacionTareaAsignada);
			}
			showNotifyInfo("Tarea Modificada Exitosamente",
					windowConsultarEditarTareaSupervisor);
			BindUtils.postGlobalCommand(null, null, "actualizarTablaAsignadas",
					null);
			windowConsultarEditarTareaSupervisor.onClose();
		}

	}

	@Listen("onClick=#btnConsultarBitacora")
	public void consultarBitacora() {

		String id = getIdSelectedRowTarea(gridBitacoras);
		if (id == null) {
			showNotify("Debe seleccionar una Bitacora a Consultar",
					gridBitacoras);
		} else {
			BitacoraTareaAsignada bitacoraTareaAsignada = bitacoraTareaAsignadaDAO
					.obtenerBitacoraTareaAsignada(Integer.parseInt(id));

			Map<String, Object> arguments = new HashMap<String, Object>();
			String titulo = "Consultar Bitacora";
			String usuario = bitacoraTareaAsignada.getEmpleado().getApellidos()
					+ " " + bitacoraTareaAsignada.getEmpleado().getNombres();
			String fechaTarea;
			try {
				fechaTarea = formatoFecha.format(bitacoraTareaAsignada
						.getFecha());
			} catch (Exception e) {
				fechaTarea = "";
			}
			arguments.put("titulo", titulo);
			arguments.put("icono", "z-icon-eye");
			arguments.put("bitacora", bitacoraTareaAsignada);
			arguments.put("usuario", usuario);
			arguments.put("btnSalir", true);
			arguments.put("fecha", fechaTarea);
			if (bitacoraTareaAsignada.getAccion().equalsIgnoreCase("RECHAZAR"))
				arguments.put("divRechazo", true);
			else
				arguments.put("divDescripcion", true);
			Window window = (Window) Executions.getCurrent().createComponents(
					"/vista/ConsultarBitacora.zul", null, arguments);
			window.doModal();
		}

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
		Clients.showNotification(msg, "warning", ref, "middle_center", 6000,
				true);
	}

	@Listen("onClick=#navItemInformacion")
	public void mostrarInformacion() {
		divBitacora.setVisible(false);
		divInformacion.setVisible(true);
	}

	@Listen("onClick=#navItemBitacora")
	public void mostrarTabla() {
		divBitacora.setVisible(true);
		divInformacion.setVisible(false);
	}
}
