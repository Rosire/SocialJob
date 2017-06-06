package controlador;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import modelo.dao.BitacoraTareaRepetitivaAsignadaDAO;
import modelo.dao.EstatusNotificacionDAO;
import modelo.dao.EstatusTareaAsignadaDAO;
import modelo.dao.NotificacionTareaRepetitivaAsignadaDAO;
import modelo.dao.TareaRepetitivaAsignadaDAO;
import modelo.dto.BitacoraTareaAsignada;
import modelo.dto.BitacoraTareaRepetitivaAsignada;
import modelo.dto.EstatusNotificacion;
import modelo.dto.EstatusTareaAsignada;
import modelo.dto.NotificacionTareaRepetitivaAsignada;
import modelo.dto.TareaRepetitivaAsignada;
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
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.Slider;
import org.zkoss.zul.Spinner;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

@SuppressWarnings("serial")
public class ControladorConsultarEditarTareaRepetitivaAsignadaSupervisor extends
		SelectorComposer<Component> {

	@Wire
	private Div divInformacion;
	@Wire
	private Div divBitacora;
	@Wire
	private Window windowConsultarEditarTareaSupervisor;
	@Wire
	private Button btnSalir;
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
	private Div divRechazo;
	@Wire
	private Div divSlider;
	@Wire
	private Div divCalificacion;
	@Wire
	private Slider sliderProgreso;
	@Wire
	private Textbox lblRazonRechazo;
	@Wire
	private Grid gridBitacoras;
	@Wire
	private Spinner lblCalificacion;
	
	
	// Variable Session
	private Session miSession = Sessions.getCurrent();
	private Usuario usuarioFiltro = (Usuario) miSession.getAttribute("usuario");

	private EstatusTareaAsignadaDAO estatusTareaAsignadaDAO = new EstatusTareaAsignadaDAO();
	private EstatusNotificacionDAO estatusNotificacionDAO = new EstatusNotificacionDAO();
	private BitacoraTareaRepetitivaAsignadaDAO bitacoraTareaRepetitivaAsignadaDAO = new BitacoraTareaRepetitivaAsignadaDAO();
	private TareaRepetitivaAsignadaDAO tareaRepetitivaAsignadaDAO = new TareaRepetitivaAsignadaDAO();
	private NotificacionTareaRepetitivaAsignadaDAO notificacionTareaRepetitivaAsignadaDAO = new NotificacionTareaRepetitivaAsignadaDAO();

	private EstatusTareaAsignada estatus = new EstatusTareaAsignada();

	private SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
	
	public ControladorConsultarEditarTareaRepetitivaAsignadaSupervisor() {
		super();
	}

	// Salir Ventana
	@Listen("onClick=#btnSalir;  onClick=#btnSalirBitacora")
	public void salirVentana() {
		windowConsultarEditarTareaSupervisor.onClose();
	}

	@Listen("onClick=#btnRechazarTarea")
	public void rechazarTarea() {
		btnCancelarModificacion.setVisible(true);
		btnGuardarModificacion.setVisible(true);
		btnSalir.setVisible(false);
		btnRechazarTarea.setVisible(false);
		btnAprobarTarea.setVisible(false);
		divSlider.setVisible(true);
		sliderProgreso.focus();
		divRechazo.setVisible(true);
		showNotifyWarning(
				"Indique la razón por la que rechaza la tarea culminada y el progreso actual.",
				windowConsultarEditarTareaSupervisor);
	}

	// Cancelar Modificacion
	@Listen("onClick=#btnCancelarModificacion")
	public void cancelarRegistro() {
		Messagebox.show("¿Seguro que desea Cancelar?", "Cancelar Modificación",
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
	public void aprobarTareaRepetitivaCulminada() {
		
		btnCancelarModificacion.setVisible(true);
		btnGuardarModificacion.setVisible(true);
		btnSalir.setVisible(false);
		btnRechazarTarea.setVisible(false);
		btnAprobarTarea.setVisible(false);
		divSlider.setVisible(false);
		divCalificacion.setVisible(true);
		lblCalificacion.focus();
		divRechazo.setVisible(false);
		showNotifyWarning(
				"Indique la Calificacion de la tarea culminada",
				lblCalificacion);
		
	}

	// Cancelar Modificacion
	@Listen("onClick=#btnGuardarModificacion")
	public void guardarRechazo() {
		
		if(divCalificacion.isVisible())
		{
			int id = Integer.parseInt(labelCodigo.getValue().toString());
			estatus = estatusTareaAsignadaDAO.obtenerEstatusPorNombre("APROBADA");
			TareaRepetitivaAsignada tareaRepetitivaAsignadaFiltro = tareaRepetitivaAsignadaDAO
					.obtenerTareaRepetitivaAsignada(id);
			long eficiencia=0;
			eficiencia=(tareaRepetitivaAsignadaFiltro.getFechaFinalizacion().getTime()-tareaRepetitivaAsignadaFiltro.getFechaInicio().getTime())/1000;
			tareaRepetitivaAsignadaFiltro.setEstatusTareaAsignada(estatus);
			tareaRepetitivaAsignadaFiltro.setCalificacion(lblCalificacion.getValue());
			tareaRepetitivaAsignadaFiltro.setEficiencia(eficiencia);
			
								tareaRepetitivaAsignadaDAO
										.actualizarTareaRepetitivaAsignada(tareaRepetitivaAsignadaFiltro);
								BitacoraTareaRepetitivaAsignada bitacoraTareaRepetitivaAsignada = new BitacoraTareaRepetitivaAsignada(
										tareaRepetitivaAsignadaFiltro,
										usuarioFiltro, new Date(),
										"Tarea Aprobada por el Supervisor",
										"APROBAR");

								bitacoraTareaRepetitivaAsignadaDAO
										.registrarBitacoraTareaRepetitivaAsignada(bitacoraTareaRepetitivaAsignada);
								String nombreCompleto = usuarioFiltro.getNombres()
										+ " " + usuarioFiltro.getApellidos();
								String descripcion = "El Supervisor "
										+ nombreCompleto
										+ " (V-"
										+ usuarioFiltro.getCedula()
										+ " ) ha aprobado la culminación de la tarea repetitiva asignada ("
										+ tareaRepetitivaAsignadaFiltro
												.getTareaRepetitiva().getTarea()
												.getNombre()
										+ "), verifique sus tareas repetitivas asignadas con estatus \"APROBADA\" ";
								EstatusNotificacion estatusNotificacion = estatusNotificacionDAO
										.obtenerEstatusNotificacionPorNombre("POR LEER");
								NotificacionTareaRepetitivaAsignada notificacionTareaRepetitivaAsignada = new NotificacionTareaRepetitivaAsignada(
										descripcion, tareaRepetitivaAsignadaFiltro
												.getEmpleado(),
										bitacoraTareaRepetitivaAsignada,
										estatusNotificacion);
								notificacionTareaRepetitivaAsignadaDAO
										.registrarNotificacionTareaRepetitivaAsignada(notificacionTareaRepetitivaAsignada);
								BindUtils.postGlobalCommand(null, null,
										"actualizarTablaRepetitivas", null);
								showNotifyInfo("Tarea APROBADA Exitosamente",
										windowConsultarEditarTareaSupervisor);
								windowConsultarEditarTareaSupervisor.onClose();

		}
		else if(divRechazo.isVisible())
		{
		int id = Integer.parseInt(labelCodigo.getValue().toString());
		TareaRepetitivaAsignada tareaRepetitivaAsignadaFiltro = tareaRepetitivaAsignadaDAO
				.obtenerTareaRepetitivaAsignada(id);
		int progreso = sliderProgreso.getCurpos();
		if (progreso < 100) {
			String razonRechazo = lblRazonRechazo.getValue();
			estatus = estatusTareaAsignadaDAO
					.obtenerEstatusPorNombre("EN MARCHA");
			tareaRepetitivaAsignadaFiltro.setEstatusTareaAsignada(estatus);
			tareaRepetitivaAsignadaFiltro.setProgreso(progreso);
			tareaRepetitivaAsignadaDAO
					.actualizarTareaRepetitivaAsignada(tareaRepetitivaAsignadaFiltro);
			BitacoraTareaRepetitivaAsignada bitacoraTareaRepetitivaAsignada = new BitacoraTareaRepetitivaAsignada(
					tareaRepetitivaAsignadaFiltro, usuarioFiltro, new Date(),
					razonRechazo, "RECHAZAR");

			String nombreCompleto = usuarioFiltro.getNombres() + " "
					+ usuarioFiltro.getApellidos();
			String descripcion = "El Supervisor "
					+ nombreCompleto
					+ " (V-"
					+ usuarioFiltro.getCedula()
					+ " ) ha Rechazado la culminación de la tarea repetitiva asignada ("
					+ tareaRepetitivaAsignadaFiltro.getTareaRepetitiva()
							.getTarea().getNombre()
					+ "), verifique sus tareas repetitivas asignadas con estatus \"EN MARCHA\" ";
			EstatusNotificacion estatusNotificacion = estatusNotificacionDAO
					.obtenerEstatusNotificacionPorNombre("POR LEER");
			NotificacionTareaRepetitivaAsignada notificacionTareaRepetitivaAsignada = new NotificacionTareaRepetitivaAsignada(
					descripcion, tareaRepetitivaAsignadaFiltro.getEmpleado(),
					bitacoraTareaRepetitivaAsignada, estatusNotificacion);

			bitacoraTareaRepetitivaAsignadaDAO
					.registrarBitacoraTareaRepetitivaAsignada(bitacoraTareaRepetitivaAsignada);
			notificacionTareaRepetitivaAsignadaDAO
					.registrarNotificacionTareaRepetitivaAsignada(notificacionTareaRepetitivaAsignada);
			showNotifyInfo("Tarea modificada Exitosamente",
					windowConsultarEditarTareaSupervisor);
			BindUtils.postGlobalCommand(null, null,
					"actualizarTablaRepetitivas", null);
			windowConsultarEditarTareaSupervisor.onClose();
		  }
			else {
			showNotify(
					"Para Colocar la tarea nuevamenente \"EN MARCHA\" el progreso debe ser menor que 100",
					sliderProgreso);
			sliderProgreso.focus();
		}
		} 
		

	}
	
	@Listen("onClick=#btnConsultarBitacora")
	public void consultarBitacora() {		
		
		String id=getIdSelectedRowTarea(gridBitacoras);
		if(id==null)
		{
			showNotify("Debe seleccionar una Bitacora a Consultar",
					gridBitacoras);
		}
		else
		{
			BitacoraTareaRepetitivaAsignada bitacoraTareaAsignada = bitacoraTareaRepetitivaAsignadaDAO.obtenerBitacoraTareaRepetitivaAsignada
					(Integer.parseInt(id));
			
			Map<String, Object> arguments = new HashMap<String, Object>();
			String titulo = "Consultar Bitacora";
			String usuario = bitacoraTareaAsignada.getEmpleado().getApellidos()+
					" "+bitacoraTareaAsignada.getEmpleado().getNombres();
			String fechaTarea;
			try {
				fechaTarea = formatoFecha.format(bitacoraTareaAsignada.getFecha());
			} catch (Exception e) {
				fechaTarea = "";
			}
			arguments.put("titulo", titulo);
			arguments.put("icono", "z-icon-eye");
			arguments.put("bitacora", bitacoraTareaAsignada);
			arguments.put("usuario", usuario);
			arguments.put("btnSalir", true);
			arguments.put("fecha", fechaTarea);
			if(bitacoraTareaAsignada.getAccion().equalsIgnoreCase("RECHAZAR"))
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
