package controlador;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import modelo.dao.BitacoraSolicitudDAO;
import modelo.dao.CargoDAO;
import modelo.dao.CargoEmpleadoDAO;
import modelo.dao.EstatusNotificacionDAO;
import modelo.dao.EstatusSolicitudDAO;
import modelo.dao.NotificacionSolicitudDAO;
import modelo.dao.SolicitudDAO;
import modelo.dao.TareaDAO;
import modelo.dao.UsuarioDAO;
import modelo.dto.BitacoraSolicitud;
import modelo.dto.Cargo;
import modelo.dto.CargoEmpleado;
import modelo.dto.Estatus;
import modelo.dto.EstatusNotificacion;
import modelo.dto.EstatusSolicitud;
import modelo.dto.NotificacionSolicitud;
import modelo.dto.Solicitud;
import modelo.dto.Tarea;
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
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

public class ControladorRegistrarSolicitud extends SelectorComposer<Component> {

	@Wire
	private Combobox cmbTareas;
	@Wire
	private Datebox lblFechaTarea;
	@Wire
	private Combobox cmbCargos;
	@Wire
	private Listbox listaSupervisores;
	@Wire
	private Window windowGenerarSolicitud;
	@Wire
	private Listbox listaEmpleados;

	// Variable Session
	private Session miSession = Sessions.getCurrent();
	private Usuario usuarioFiltro = (Usuario) miSession.getAttribute("usuario");

	private CargoDAO cargoDAO = new CargoDAO();
	private SolicitudDAO solicitudDAO = new SolicitudDAO();
	private TareaDAO tareaDAO = new TareaDAO();
	private UsuarioDAO usuarioDAO = new UsuarioDAO();
	private EstatusSolicitudDAO estatusSolicitudDAO = new EstatusSolicitudDAO();
	private EstatusNotificacionDAO estatusNotificacionDAO = new EstatusNotificacionDAO();
	private NotificacionSolicitudDAO notificacionSolicitudDAO = new NotificacionSolicitudDAO();
	private BitacoraSolicitudDAO bitacoraSolicitudDAO = new BitacoraSolicitudDAO();
	private CargoEmpleadoDAO cargoEmpleadoDAO = new CargoEmpleadoDAO();

	private List<Cargo> cargos;
	private List<Tarea> tareas;
	private ListModel<Cargo> listaCargosModel;
	private ListModel<Tarea> listaTareasModel;

	private ListModel<Usuario> listaEmpleadosCargoModel;
	private List<Usuario> empleadosCargo = new ArrayList<Usuario>();

	public ControladorRegistrarSolicitud() {
		super();

	}

	public ListModel<Cargo> getCargos() {
		cargos = cargoDAO
				.obtenerCargosActivosConSupervisorDistintoActualPorEmpresa(
						usuarioFiltro.getId(), usuarioFiltro.getEmpresa()
								.getId());
		listaCargosModel = new ListModelList<Cargo>(cargos);
		return listaCargosModel;
	}

	public ListModel<Tarea> getTareas() {
		tareas = tareaDAO.obtenerTareasActivasPorEmpresa(usuarioFiltro
				.getEmpresa().getId());
		listaTareasModel = new ListModelList<Tarea>(tareas);
		return listaTareasModel;
	}

	public ListModel<Usuario> getEmpleadosCargo() {
		listaEmpleadosCargoModel = new ListModelList<Usuario>(empleadosCargo);
		((ListModelList<Usuario>) listaEmpleadosCargoModel).setMultiple(false);
		return listaEmpleadosCargoModel;
	}

	@Command
	@NotifyChange("empleadosCargo")
	public void cambioEmpleados(@BindingParam("data") int data) {
		empleadosCargo = usuarioDAO.obtenerUsuariosActivosPorCargo(cargos.get(
				data).getId());
	}

	@Listen("onClick=#btnGenerarSolicitud")
	public void registrarSolicitud() {
		Date fechaTarea = lblFechaTarea.getValue();
		Date fechaSolicitud = new Date();
		ListModelList<Object> listaTarea = (ListModelList<Object>) cmbTareas
				.getModel();
		Set<Object> conjuntoTarea = listaTarea.getSelection();

		ListModelList<Object> listaCargo = (ListModelList<Object>) cmbCargos
				.getModel();
		Set<Object> conjuntoCargo = listaCargo.getSelection();

		ListModelList<Object> listaEmpleado = (ListModelList<Object>) listaEmpleados
				.getModel();
		Set<Object> conjuntoEmpleado = listaEmpleado.getSelection();

		Usuario empleado = null;

		if (conjuntoTarea.size() == 0) {
			showNotify("Debe seleccionar una tarea a solicitar", cmbTareas);

		} else if (conjuntoCargo.size() == 0) {
			showNotify("Debe seleccionar un cargo para la tarea solicitada",
					cmbCargos);

		} else {
			if (conjuntoEmpleado.size() != 0) {
				empleado = (Usuario) conjuntoEmpleado.toArray()[0];
			}
			Tarea tarea = (Tarea) conjuntoTarea.toArray()[0];
			Cargo cargo = (Cargo) conjuntoCargo.toArray()[0];
			EstatusSolicitud estatus = estatusSolicitudDAO
					.obtenerEstatusSolicitudPorNombre("EN ESPERA");
			Solicitud solicitud = new Solicitud(tarea, usuarioFiltro, empleado,
					cargo, estatus, fechaSolicitud, fechaTarea);
			CargoEmpleado supervisor = cargoEmpleadoDAO
					.obtenerUsuarioSupervisorPorCargo(cargo.getCargoSuperior()
							.getId());

			solicitudDAO.registrarSolicitud(solicitud);
			BitacoraSolicitud bitacoraSolicitud = new BitacoraSolicitud(
					solicitud, usuarioFiltro, new Date(),
					"Solicitud creada y notificacion enviada a supervisor ",
					"CREAR");

			bitacoraSolicitudDAO.registrarBitacoraSolicitud(bitacoraSolicitud);
			String nombreCompleto = usuarioFiltro.getNombres() + " "
					+ usuarioFiltro.getApellidos();
			String descripcion = "El empleado " + nombreCompleto + " (V-"
					+ usuarioFiltro.getCedula()
					+ " ) le ha enviado una solicitud de Tarea.";
			EstatusNotificacion estatusNotificacion = estatusNotificacionDAO
					.obtenerEstatusNotificacionPorNombre("POR LEER");

			NotificacionSolicitud notificacionSolicitud = new NotificacionSolicitud(
					descripcion, estatusNotificacion, bitacoraSolicitud,
					supervisor.getEmpleado());
			notificacionSolicitudDAO
					.registrarNotificacionSolicitud(notificacionSolicitud);
			showNotifyInfo("Solicitud Generada Exitosamente",
					windowGenerarSolicitud);

			BindUtils.postGlobalCommand(null, null, "actualizarTablaEnviadas",
					null);

			windowGenerarSolicitud.onClose();
		}
	}

	@Listen("onClick=#btnCancelarSolicitud; onOK=#btnCancelarSolicitud")
	public void cancelarRegistro() {
		Messagebox.show("¿Seguro que desea Cancelar?", "Cancelar Registro",
				Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION,
				new EventListener<Event>() {
					public void onEvent(Event e) {
						if (Messagebox.ON_OK.equals(e.getName())) {
							// OK is clicked
							windowGenerarSolicitud.onClose();
						} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
							// Cancel is clicked
						}
					}
				});
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
