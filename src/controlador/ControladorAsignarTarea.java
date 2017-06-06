package controlador;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import modelo.dao.BitacoraTareaAsignadaDAO;
import modelo.dao.CargoDAO;
import modelo.dao.CargoEmpleadoDAO;
import modelo.dao.EstatusDAO;
import modelo.dao.EstatusNotificacionDAO;
import modelo.dao.EstatusTareaAsignadaDAO;
import modelo.dao.NotificacionTareaAsignadaDAO;
import modelo.dao.PrioridadDAO;
import modelo.dao.TareaAsignadaDAO;
import modelo.dao.TareaDAO;
import modelo.dao.UsuarioDAO;
import modelo.dto.BitacoraTareaAsignada;
import modelo.dto.Cargo;
import modelo.dto.CargoEmpleado;
import modelo.dto.Estatus;
import modelo.dto.EstatusNotificacion;
import modelo.dto.EstatusTareaAsignada;
import modelo.dto.NotificacionTareaAsignada;
import modelo.dto.Prioridad;
import modelo.dto.Tarea;
import modelo.dto.TareaAsignada;
import modelo.dto.Usuario;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Spinner;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class ControladorAsignarTarea extends SelectorComposer<Component> {

	@Wire
	private Window windowAsignarTarea;
	@Wire
	private Combobox cmbTareas;
	@Wire
	private Combobox cmbCargos;
	@Wire
	private Combobox cmbPrioridades;
	@Wire
	private Datebox lblFechaTarea;
	@Wire
	private Textbox lblIndicaciones;
	@Wire
	private Listbox listaEmpleados;
	@Wire
	private Spinner lblHoras;
	@Wire
	private Spinner lblMinutos;
	@Wire
	private Div divDuracion;

	private CargoDAO cargoDAO = new CargoDAO();
	private TareaAsignadaDAO tareaAsignadaDAO = new TareaAsignadaDAO();
	private TareaDAO tareaDAO = new TareaDAO();
	private CargoEmpleadoDAO cargoEmpleadoDAO = new CargoEmpleadoDAO();
	private PrioridadDAO prioridadDAO = new PrioridadDAO();
	private UsuarioDAO usuarioDAO = new UsuarioDAO();
	private EstatusTareaAsignadaDAO estatusTareaAsignadaDAO = new EstatusTareaAsignadaDAO();
	private EstatusNotificacionDAO estatusNotificacionDAO = new EstatusNotificacionDAO();
	private BitacoraTareaAsignadaDAO bitacoraTareaAsignadaDAO = new BitacoraTareaAsignadaDAO();
	private NotificacionTareaAsignadaDAO notificacionTareaAsignadaDAO = new NotificacionTareaAsignadaDAO();
	private EstatusDAO estatusDAO = new EstatusDAO();

	// Variable Sesion
	private Session miSession = Sessions.getCurrent();
	private Usuario usuarioSession = (Usuario) miSession
			.getAttribute("usuario");

	private ListModel<Cargo> listaCargosModel;
	private ListModel<Tarea> listaTareasModel;
	private ListModel<Usuario> listaEmpleadosCargoModel;
	private ListModel<Prioridad> listaPrioridadesModel;

	private List<Cargo> cargos = cargoDAO
			.obtenerCargosActivosConSupervisorIguaActualPorEmpresa(
					usuarioSession.getId(), usuarioSession.getEmpresa().getId());

	private List<Tarea> tareas = tareaDAO
			.obtenerTareasActivasPorEmpresa(usuarioSession.getEmpresa().getId());
	private List<Usuario> empleadosCargo = new ArrayList<Usuario>();
	private List<Prioridad> prioridades = new ArrayList<Prioridad>();

	public ControladorAsignarTarea() {
		super();
	}

	public ListModel<Cargo> getCargos() {
		listaCargosModel = new ListModelList<Cargo>(cargos);
		return listaCargosModel;
	}

	public ListModel<Usuario> getEmpleadosCargo() {
		listaEmpleadosCargoModel = new ListModelList<Usuario>(empleadosCargo);
		((ListModelList<Usuario>) listaEmpleadosCargoModel).setMultiple(true);
		return listaEmpleadosCargoModel;
	}

	public ListModel<Tarea> getTareas() {
		listaTareasModel = new ListModelList<Tarea>(tareas);
		return listaTareasModel;
	}

	public ListModel<Prioridad> getPrioridades() {
		prioridades = prioridadDAO.obtenerPrioridades();
		listaPrioridadesModel = new ListModelList<Prioridad>(prioridades);
		return listaPrioridadesModel;
	}

	@Command
	@NotifyChange("empleadosCargo")
	public void cambioFiltro(@BindingParam("data") int data) {
		empleadosCargo = usuarioDAO.obtenerUsuariosActivosPorCargo(cargos.get(
				data).getId());
	}

	@Listen("onClick=#btnGuardarTarea")
	public void registrarAsignacion() {
		Date fecha = lblFechaTarea.getValue();
		String indicaciones = lblIndicaciones.getValue();

		ListModelList<Object> listaCargo = (ListModelList<Object>) cmbCargos
				.getModel();
		Set<Object> conjuntoCargo = listaCargo.getSelection();

		ListModelList<Object> listaEmpleadosSeleccion = (ListModelList<Object>) listaEmpleados
				.getModel();
		Set<Object> conjuntoEmpleados = listaEmpleadosSeleccion.getSelection();

		ListModelList<Object> listaPrioriad = (ListModelList<Object>) cmbPrioridades
				.getModel();
		Set<Object> conjuntoPrioridad = listaPrioriad.getSelection();
		if (fecha.before(new Date())) {
			showNotify(
					"Debe Seleccionar una fecha mayor o igual a la Fecha Actual del Sistema",
					lblFechaTarea);
			lblFechaTarea.focus();
		} else if (conjuntoCargo.size() == 0) {
			showNotify("Debe Seleccionar al menos un cargo del empleado",
					cmbCargos);
			cmbCargos.focus();
		} else if (conjuntoEmpleados.size() == 0) {
			showNotify("Debe Seleccionar al menos un cargo del empleado",
					listaEmpleados);
			listaEmpleados.focus();
		} else {

			String nombre = cmbTareas.getValue();
			Tarea tarea = tareaDAO.obtenerTareaPorNombrePorEmpresa(nombre,
					usuarioSession.getEmpresa().getId());
			if (tarea == null) {
				Estatus estatus = estatusDAO.obtenerEstatusPorNombre("ACTIVO");

				long duracion = lblHoras.getValue() * 3600
						+ lblMinutos.getValue() * 60;
				tarea = new Tarea(nombre, duracion, estatus,
						usuarioSession.getEmpresa());
				if (!tarea.equals(null)) {
					tareaDAO.registrarTarea(tarea);
				}
			}
			tarea = tareaDAO.obtenerTareaPorNombrePorEmpresa(nombre,
					usuarioSession.getEmpresa().getId());

			Cargo cargo = (Cargo) conjuntoCargo.toArray()[0];
			Prioridad prioridad = (Prioridad) conjuntoPrioridad.toArray()[0];

			List<Object> empleadosSeleccionados = new ArrayList<Object>(
					conjuntoEmpleados);

			EstatusTareaAsignada estatus = estatusTareaAsignadaDAO
					.obtenerEstatusPorNombre("POR INICIAR");
			EstatusNotificacion estatusNotificacion = estatusNotificacionDAO
					.obtenerEstatusNotificacionPorNombre("POR LEER");
			int progreso = 0;

			// ASIGNAR TAREAS A CADA EMPLEADO
			for (int i = 0; i < empleadosSeleccionados.size(); i++) {

				Usuario empleado = (Usuario) empleadosSeleccionados.get(i);
				CargoEmpleado cargoEmpleado = cargoEmpleadoDAO
						.obtenerCargoEmpledo(cargo.getId(), empleado.getId());
				TareaAsignada tareaAsignada = new TareaAsignada(tarea, estatus,
						prioridad, cargoEmpleado, fecha, indicaciones,
						progreso, 0, null, null, tarea.getDuracion(), 0);
				tareaAsignadaDAO.registrarTareaAsignada(tareaAsignada);
				BitacoraTareaAsignada bitacoraTareaAsignada = new BitacoraTareaAsignada(
						tareaAsignada, usuarioSession, new Date(),
						"Tarea Asignada por el Supervisor del Cargo", "CREAR");

				String nombreCompleto = usuarioSession.getNombres() + " "
						+ usuarioSession.getApellidos();
				String descripcion = "El Supervisor " + nombreCompleto + " (V-"
						+ usuarioSession.getCedula()
						+ " ) le ha asignado una Tarea, para el cargo "
						+ cargo.getNombre()
						+ " verifique sus tareas pendientes ";

				NotificacionTareaAsignada notificacionTareaAsignada = new NotificacionTareaAsignada(
						descripcion, cargoEmpleado.getEmpleado(),
						bitacoraTareaAsignada, estatusNotificacion);

				// ALMACENAR EN LA BITACORA LA CREACION DE LA TAREA ASIGNADA

				bitacoraTareaAsignadaDAO
						.registrarBitacoraTareaAsignada(bitacoraTareaAsignada);
				// GENERAR LA NOTIFICACION
				notificacionTareaAsignadaDAO
						.registrarNotificacionTareaAsignada(notificacionTareaAsignada);
			}
			showNotifyInfo("Tarea Asignada Exitosamente", windowAsignarTarea);
			BindUtils.postGlobalCommand(null, null, "actualizarTablaAsignadas",
					null);
			windowAsignarTarea.onClose();
		}
	}

	@Listen("onClick=#btnCancelar")
	public void cancelarAsignacion() {
		Messagebox.show("¿Seguro que desea Cancelar?", "Cancelar Asignación",
				Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION,
				new EventListener<Event>() {
					public void onEvent(Event e) {
						if (Messagebox.ON_OK.equals(e.getName())) {
							// OK is clicked
							windowAsignarTarea.onClose();
						} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
							// Cancel is clicked
						}
					}
				});
	}

	@Listen("onChange = #lblHoras")
	public void cambioMinimoMinutos() {
		int horas = lblHoras.getValue();
		if (horas == 0) {
			lblMinutos.setConstraint("no empty, min 1, max 60");
		} else {
			lblMinutos.setConstraint("no empty, min 0, max 60");
		}
	}

	@Listen("onChange = #cmbTareas")
	public void cambioTarea() {
		String nombre = cmbTareas.getValue();
		Tarea tarea = tareaDAO.obtenerTareaPorNombrePorEmpresa(nombre,
				usuarioSession.getEmpresa().getId());
		if (tarea == null) {
			divDuracion.setVisible(true);
			showNotify("Ingrese la Duracion de la Tarea Nueva", lblHoras);
		} else {
			divDuracion.setVisible(false);
		}
	}

	private void showNotify(String msg, Component ref) {
		Clients.showNotification(msg, "error", ref, "end_center", 3000, true);
	}

	private void showNotifyInfo(String msg, Component ref) {
		Clients.showNotification(msg, "info", ref, "middle_center", 5000, true);
	}

}
