package controlador;

import modelo.dao.TareaDAO;
import modelo.dao.EstatusDAO;
import modelo.dto.Tarea;
import modelo.dto.Estatus;
import modelo.dto.Usuario;

import org.postgresql.util.PGInterval;
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
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Spinner;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

@SuppressWarnings("serial")
public class ControladorConsultarEditarTarea extends
		SelectorComposer<Component> {

	@Wire
	private Window windowConsultarEditarTarea;
	@Wire
	private Div divConsulta;
	@Wire
	private Div divEditar;
	@Wire
	private Textbox lblNombre;
	@Wire
	private Label labelCodigo;
	@Wire
	private Label labelNombre;
	@Wire
	private Spinner lblHoras;
	@Wire
	private Spinner lblMinutos;

	// Session
	private Session miSession = Sessions.getCurrent();
	private Usuario usuarioSession = (Usuario) miSession
			.getAttribute("usuario");

	private TareaDAO tareaDAO = new TareaDAO();
	private EstatusDAO estatusDAO = new EstatusDAO();

	public ControladorConsultarEditarTarea() {
		super();
	}

	// Guardar Modificacion de la Tarea
	@Listen("onClick=#btnGuardarModificacion")
	public void guardarModificacionConsulta() {
		String nombre = lblNombre.getValue();
		String nombreAnterior = labelNombre.getValue();
		int codigo = Integer.parseInt(labelCodigo.getValue());
		Tarea tareaBD = tareaDAO.obtenerTareaPorNombrePorEmpresa(nombre,
				usuarioSession.getEmpresa().getId());

		if ((tareaBD != null) && !(tareaBD.getNombre().equals(nombreAnterior))) {
			showNotify("Tarea con nombre Registrado en Base de Datos",
					lblNombre);
			lblNombre.focus();
			return;
		}
		long duracion = lblHoras.getValue() * 3600 + lblMinutos.getValue() * 60;
		Estatus estatus = estatusDAO.obtenerEstatusPorNombre("ACTIVO");
		Tarea tarea = new Tarea(nombre, duracion, estatus,
				usuarioSession.getEmpresa());
		if (!tarea.equals(null)) {
			tarea.setId(codigo);
			tareaDAO.actualizarTarea(tarea);
			BindUtils.postGlobalCommand(null, null, "actualizarTabla", null);
			showNotifyInfo("Tarea Registrado Exitosamente",
					windowConsultarEditarTarea);
			windowConsultarEditarTarea.onClose();
		} else {
			showNotify("Error al Almacenar Tarea", windowConsultarEditarTarea);
		}
	}

	// Mostrar Div Editar Tarea
	@Listen("onClick=#btnEditarTarea")
	public void editarTareaConsulta() {
		divConsulta.setVisible(false);
		divEditar.setVisible(true);

	}

	// Salir
	@Listen("onClick=#btnSalirConsulta")
	public void salirConsulta() {
		windowConsultarEditarTarea.onClose();
	}

	// CancelarModificacion
	@Listen("onClick=#btnCancelarModificacion")
	public void cancelarModificacionEditar() {
		Messagebox.show("¿Seguro que desea Cancelar la modificacion?",
				"Cancelar Modificación", Messagebox.OK | Messagebox.CANCEL,
				Messagebox.QUESTION, new EventListener<Event>() {
					public void onEvent(Event e) {
						if (Messagebox.ON_OK.equals(e.getName())) {
							// OK is clicked
							windowConsultarEditarTarea.onClose();
						} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
							// Cancel is clicked
						}
					}
				});
	}

	private void showNotify(String msg, Component ref) {
		Clients.showNotification(msg, "error", ref, "end_center", 3000);
	}

	private void showNotifyInfo(String msg, Component ref) {
		Clients.showNotification(msg, "info", ref, "middle_center", 5000, true);
	}

	@Listen("onChange = #lblNombre")
	public void cambioNombre() {
		String nombreAnterior = labelNombre.getValue();
		String nombre = lblNombre.getValue().toString();
		if (nombre.equalsIgnoreCase("")) {
			showNotify("Debe ingresar el nombre de la tarea", lblNombre);
			lblNombre.focus();
		}
		Tarea tareaBD = tareaDAO.obtenerTareaPorNombrePorEmpresa(nombre,
				usuarioSession.getEmpresa().getId());
		if ((tareaBD != null) && !(tareaBD.getNombre().equals(nombreAnterior))) {
			showNotify("Tarea Registrada en Base de Datos", lblNombre);
			lblNombre.focus();
		}
	}
}
