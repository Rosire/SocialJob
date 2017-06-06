package controlador;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import modelo.dao.BitacoraTareaRepetitivaAsignadaDAO;
import modelo.dao.EstatusTareaAsignadaDAO;
import modelo.dao.TareaRepetitivaAsignadaDAO;
import modelo.dto.BitacoraTareaAsignada;
import modelo.dto.BitacoraTareaRepetitivaAsignada;
import modelo.dto.EstatusTareaAsignada;
import modelo.dto.TareaAsignada;
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
import org.zkoss.zul.Progressmeter;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.Slider;
import org.zkoss.zul.Window;

@SuppressWarnings("serial")
public class ControladorConsultarEditarTareaRepetitivaAsignada extends
		SelectorComposer<Component> {

	@Wire
	private Div divInformacion;
	@Wire
	private Div divBitacora;
	@Wire
	private Div divBtnConsultarTareaEnMarcha;
	@Wire
	private Div divBtnEditarTareaEnMarcha;
	@Wire
	private Div divBtnConsultarTareaPendiente;
	@Wire
	private Div divBtnConsultarTareaCulminada;
	@Wire
	private Div divBtnConsultarTareaPausada;
	@Wire
	private Div divSlider;
	@Wire
	private Div divProgreso;
	@Wire
	private Slider sliderProgreso;
	@Wire
	private Window windowConsultarEditarTareaAsignada;
	@Wire
	private Button btnSalir;
	@Wire
	private Button btnIniciarTarea;
	@Wire
	private Button btnSalirLabel;
	@Wire
	private Button btnEditarTarea;
	@Wire
	private Button btnPausarTarea;
	@Wire
	private Button btnFinalizarTarea;
	@Wire
	private Button btnGuardarModificacion;
	@Wire
	private Button btnCancelarModificacion;
	@Wire
	private Button btnEditarLabel;
	@Wire
	private Label labelCodigo;
	@Wire
	private Label labelEstatus;
	@Wire
	private Grid gridBitacoras;
	
	// Variable Session
	private Session miSession = Sessions.getCurrent();
	private Usuario usuarioFiltro = (Usuario) miSession.getAttribute("usuario");

	private TareaRepetitivaAsignadaDAO tareaRepetitivaAsignadaDAO = new TareaRepetitivaAsignadaDAO();
	private BitacoraTareaRepetitivaAsignadaDAO bitacoraTareaRepetitivaAsignadaDAO = new BitacoraTareaRepetitivaAsignadaDAO();
	private EstatusTareaAsignadaDAO estatusTareaAsignadaDAO = new EstatusTareaAsignadaDAO();
	private EstatusTareaAsignada estatus = new EstatusTareaAsignada();
	
	private SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");

	public ControladorConsultarEditarTareaRepetitivaAsignada() {
		super();
	}

	@Listen("onClick=#btnSalir; onClick=#btnSalirLabel; onClick=#btnSalirBitacora")
	public void salirVentana() {
		windowConsultarEditarTareaAsignada.onClose();
	}

	@Listen("onClick=#btnEditarTarea; onClick=#btnEditarLabel")
	public void editarTarea() {
		divSlider.setVisible(true);
		divProgreso.setVisible(true);
		btnIniciarTarea.setVisible(false);
		btnEditarTarea.setVisible(false);
		btnPausarTarea.setVisible(false);
		btnSalir.setVisible(false);
		btnSalirLabel.setVisible(false);
		btnFinalizarTarea.setVisible(false);
		btnGuardarModificacion.setVisible(true);
		btnCancelarModificacion.setVisible(true);
		btnEditarLabel.setVisible(false);
	}

	@Listen("onClick=#btnIniciarTarea")
	public void iniciarTarea() {

		int id = Integer.parseInt(labelCodigo.getValue());
		TareaRepetitivaAsignada tareaAsignadaFiltro = tareaRepetitivaAsignadaDAO.obtenerTareaRepetitivaAsignada(id);
		
			String status=tareaAsignadaFiltro.getEstatusTareaAsignada().getNombre();
			
			estatus = estatusTareaAsignadaDAO
					.obtenerEstatusPorNombre("EN MARCHA");

			tareaAsignadaFiltro.setEstatusTareaAsignada(estatus);
			Messagebox.show("¿Seguro que desea INICIAR la Tarea seleccionada ?",
					"Iniciar Tarea", Messagebox.OK | Messagebox.CANCEL,
					Messagebox.QUESTION, new EventListener<Event>() {
						public void onEvent(Event e) {
							if (Messagebox.ON_OK.equals(e.getName())) {
								// OK is clicked
								String descripcion = "";
								String accion = "";
								if (status.equalsIgnoreCase("POR INICIAR")) {
									tareaAsignadaFiltro.setFechaInicio(new Date());
									descripcion = "Tarea iniciada por el Empleado";
									accion = "INICIAR";
								} else if (status.equalsIgnoreCase("PAUSADA")) {
									descripcion = "Tarea puesta en MARCHA nuevamente por el Empleado";
									accion = "REINICIAR";
								}
								tareaRepetitivaAsignadaDAO
										.actualizarTareaRepetitivaAsignada(tareaAsignadaFiltro);
								BitacoraTareaRepetitivaAsignada bitacoraTareaAsignada = new BitacoraTareaRepetitivaAsignada(
										tareaAsignadaFiltro, usuarioFiltro,
										new Date(), descripcion, accion);
								bitacoraTareaRepetitivaAsignadaDAO
										.registrarBitacoraTareaRepetitivaAsignada(bitacoraTareaAsignada);
								
								BindUtils.postGlobalCommand(null, null,
										"actualizarTabla", null);
								showNotifyInfo("Tarea INICIADA Exitosamente",
										windowConsultarEditarTareaAsignada);
								windowConsultarEditarTareaAsignada.onClose();
							} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
								// Cancel is clicked
							}
						}
					});

		

	}

	// Cancelar Modificacion
	@Listen("onClick=#btnCancelarModificacion")
	public void cancelarRegistro() {
		Messagebox.show("¿Seguro que desea Cancelar?", "Question",
				Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION,
				new EventListener<Event>() {
					public void onEvent(Event e) {
						if (Messagebox.ON_OK.equals(e.getName())) {
							// OK is clicked
							windowConsultarEditarTareaAsignada.onClose();
						} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
							// Cancel is clicked
						}
					}
				});
	}

	@Listen("onClick=#btnPausarTarea")
	public void pausarTarea() {
		int id = Integer.parseInt(labelCodigo.getValue().toString());
		TareaRepetitivaAsignada tareaRepetitivaAsignada = tareaRepetitivaAsignadaDAO
				.obtenerTareaRepetitivaAsignada(id);
		estatus = estatusTareaAsignadaDAO.obtenerEstatusPorNombre("PAUSADA");
		tareaRepetitivaAsignada.setEstatusTareaAsignada(estatus);
		Messagebox.show(
				"¿Seguro que desea Pausar la Tarea(" + labelCodigo.getValue()
						+ ") ?", "Question", Messagebox.OK | Messagebox.CANCEL,
				Messagebox.QUESTION, new EventListener<Event>() {
					public void onEvent(Event e) {
						if (Messagebox.ON_OK.equals(e.getName())) {
							// OK is clicked
							tareaRepetitivaAsignadaDAO
									.actualizarTareaRepetitivaAsignada(tareaRepetitivaAsignada);
							BitacoraTareaRepetitivaAsignada bitacoraTareaRepetitivaAsignada = new BitacoraTareaRepetitivaAsignada(
									tareaRepetitivaAsignada, usuarioFiltro,
									new Date(),
									"Tarea Pausada por el Empleado", "PAUSAR");

							bitacoraTareaRepetitivaAsignadaDAO
									.registrarBitacoraTareaRepetitivaAsignada(bitacoraTareaRepetitivaAsignada);
							showNotifyInfo("Tarea Pusada Exitosamente",
									windowConsultarEditarTareaAsignada);
							BindUtils.postGlobalCommand(null, null,
									"actualizarTabla", null);
							windowConsultarEditarTareaAsignada.onClose();
						} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
							// Cancel is clicked
						}
					}
				});
	}

	@Listen("onClick=#btnFinalizarTarea")
	public void finalizarTarea() {
		int id = Integer.parseInt(labelCodigo.getValue().toString());
		TareaRepetitivaAsignada tareaRepetitivaAsignada = tareaRepetitivaAsignadaDAO
				.obtenerTareaRepetitivaAsignada(id);
		estatus = estatusTareaAsignadaDAO.obtenerEstatusPorNombre("CULMINADA");
		tareaRepetitivaAsignada.setEstatusTareaAsignada(estatus);
		tareaRepetitivaAsignada.setProgreso(100);
		tareaRepetitivaAsignada.setFechaFinalizacion(new Date());
		Messagebox.show(
				"¿Seguro que ya finalizo la tarea (" + labelCodigo.getValue()
						+ ") ?", "Question", Messagebox.OK | Messagebox.CANCEL,
				Messagebox.QUESTION, new EventListener<Event>() {
					public void onEvent(Event e) {
						if (Messagebox.ON_OK.equals(e.getName())) {
							// OK is clicked
							tareaRepetitivaAsignadaDAO
									.actualizarTareaRepetitivaAsignada(tareaRepetitivaAsignada);
							showNotifyInfo("Tarea Culminada Exitosamente",
									windowConsultarEditarTareaAsignada);

							BitacoraTareaRepetitivaAsignada bitacoraTareaRepetitivaAsignada = new BitacoraTareaRepetitivaAsignada(
									tareaRepetitivaAsignada, usuarioFiltro,
									new Date(),
									"Tarea Culminada por el Empleado",
									"CULMINAR");

							bitacoraTareaRepetitivaAsignadaDAO
									.registrarBitacoraTareaRepetitivaAsignada(bitacoraTareaRepetitivaAsignada);

							BindUtils.postGlobalCommand(null, null,
									"actualizarTabla", null);
							windowConsultarEditarTareaAsignada.onClose();
						} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
							// Cancel is clicked
						}
					}
				});
	}

	@Listen("onClick=#btnGuardarModificacion")
	public void guardarModificacionTarea() {
		int progreso = sliderProgreso.getCurpos();
		int id = Integer.parseInt(labelCodigo.getValue().toString());
		TareaRepetitivaAsignada tareaRepetitivaAsignada = tareaRepetitivaAsignadaDAO
				.obtenerTareaRepetitivaAsignada(id);
		if (progreso < 100) {
			String estatusActual = labelEstatus.getValue();
			String accion = "";
			String descripcion = "";
			if (estatusActual.equals("EN MARCHA")) {
				accion = "EDITAR PROGRESO";
				descripcion = "Tarea editada por el Empleado";
			}
			if (estatusActual.equals("POR INICIAR")
					|| estatusActual.equals("PAUSADA")) {
				accion = "INICIAR";
				descripcion = "Tarea iniciada por el empleado";
			}
			estatus = estatusTareaAsignadaDAO
					.obtenerEstatusPorNombre("EN MARCHA");
			tareaRepetitivaAsignada.setProgreso(progreso);
			tareaRepetitivaAsignada.setEstatusTareaAsignada(estatus);
			tareaRepetitivaAsignadaDAO
					.actualizarTareaRepetitivaAsignada(tareaRepetitivaAsignada);
			showNotifyInfo("Tarea modificada Exitosamente",
					windowConsultarEditarTareaAsignada);

			BitacoraTareaRepetitivaAsignada bitacoraTareaRepetitivaAsignada = new BitacoraTareaRepetitivaAsignada(
					tareaRepetitivaAsignada, usuarioFiltro, new Date(),
					descripcion, accion);

			bitacoraTareaRepetitivaAsignadaDAO
					.registrarBitacoraTareaRepetitivaAsignada(bitacoraTareaRepetitivaAsignada);

			BindUtils.postGlobalCommand(null, null, "actualizarTabla", null);
			windowConsultarEditarTareaAsignada.onClose();
		} else {
			estatus = estatusTareaAsignadaDAO
					.obtenerEstatusPorNombre("CULMINADA");
			tareaRepetitivaAsignada.setProgreso(progreso);
			tareaRepetitivaAsignada.setEstatusTareaAsignada(estatus);
			tareaRepetitivaAsignada.setFechaFinalizacion(new Date());
			Messagebox
					.show("Al colocar 100% en el progreso, su tarea sera almancena como culminada, ¿Seguro que ya finalizo la tarea ("
							+ labelCodigo.getValue() + ") ?", "Question",
							Messagebox.OK | Messagebox.CANCEL,
							Messagebox.QUESTION, new EventListener<Event>() {
								public void onEvent(Event e) {
									if (Messagebox.ON_OK.equals(e.getName())) {
										// OK is clicked

										tareaRepetitivaAsignadaDAO
												.actualizarTareaRepetitivaAsignada(tareaRepetitivaAsignada);

										showNotifyInfo(
												"Tarea Culminada Exitosamente",
												windowConsultarEditarTareaAsignada);
										BitacoraTareaRepetitivaAsignada bitacoraTareaRepetitivaAsignada = new BitacoraTareaRepetitivaAsignada(
												tareaRepetitivaAsignada,
												usuarioFiltro,
												new Date(),
												"Tarea Culminada por el Empleado",
												"CULMINAR");

										bitacoraTareaRepetitivaAsignadaDAO
												.registrarBitacoraTareaRepetitivaAsignada(bitacoraTareaRepetitivaAsignada);

										BindUtils.postGlobalCommand(null, null,
												"actualizarTabla", null);
										windowConsultarEditarTareaAsignada
												.onClose();
									} else if (Messagebox.ON_CANCEL.equals(e
											.getName())) {
										// Cancel is clicked
									}
								}
							});

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
			BitacoraTareaRepetitivaAsignada bitacoraTareaAsignada = bitacoraTareaRepetitivaAsignadaDAO.obtenerBitacoraTareaRepetitivaAsignada(Integer.parseInt(id));
			
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
		Clients.showNotification(msg, "warning", ref, "middle_center", 5000,
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
