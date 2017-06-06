package controlador;

import modelo.dao.TareaDAO;
import modelo.dao.EstatusDAO;
import modelo.dto.Tarea;
import modelo.dto.Estatus;
import modelo.dto.Usuario;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.postgresql.util.PGInterval;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
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
import org.zkoss.zul.AbstractListModel;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;

public class ControladorTablaTareas extends SelectorComposer<Component> {

	@Wire
	private Grid gridTareas;
	// Session
	private Session miSession = Sessions.getCurrent();
	private Usuario usuarioSession = (Usuario) miSession
			.getAttribute("usuario");

	private TareaDAO datosTarea = new TareaDAO();
	private ListModel<Tarea> listaTareasModel;
	private List<Tarea> tareas;
	private Tarea tareaSeleccionado;
	private TareaDAO tareaDAO = new TareaDAO();
	private EstatusDAO estatusDAO = new EstatusDAO();

	private static final String footerMessageTareas = "Total de Tareas Activas: %d";

	public ControladorTablaTareas() {
		super();
	}

	public ListModel<Tarea> getTareas() {
		tareas = datosTarea.obtenerTareasActivasPorEmpresa(usuarioSession
				.getEmpresa().getId());
		listaTareasModel = new ListModelList<Tarea>(tareas);
		((ListModelList<Tarea>) listaTareasModel).setMultiple(false);
		return listaTareasModel;
	}

	public String getFooterMessageTareas() {
		return String.format(footerMessageTareas, tareas.size());
	}

	// Incluir una Nueva tarea
	@Listen("onClick=#btnIncluirTarea")
	public void incluirTarea() {
		Window window = (Window) Executions.createComponents(
				"/vista/registrarTarea.zul", null, null);
		window.doModal();
	}

	// Consultar una Tarea
	@Listen("onClick=#btnConsultarTarea")
	public void consultarTarea() {
		String indexSeleccionado = getIdSelectedRowTarea();
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una tarea a Consultar", gridTareas);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			tareaSeleccionado = datosTarea.obtenerTarea(id);
			String duracion;
			long d = tareaSeleccionado.getDuracion();
			int horas = (int) (d / 3600);
			int minutos = (int) ((d % 3600) / 60);

			if (horas < 10) {
				duracion = "0" + horas + ":";
			} else {
				duracion = horas + ":";
			}

			if (minutos < 10) {
				duracion = duracion + "0" + minutos;
			} else {
				duracion = duracion + minutos;
			}

			Map<String, Object> arguments = new HashMap<String, Object>();
			String titulo = "Consultar Tarea";
			arguments.put("titulo", titulo);
			arguments.put("icono", "z-icon-eye");
			arguments.put("tareaSeleccionado", tareaSeleccionado);
			arguments.put("divConsulta", true);
			arguments.put("divEditar", false);
			arguments.put("duracion", duracion);
			arguments.put("horas", horas);
			arguments.put("minutos", minutos);
			Window window = (Window) Executions.createComponents(
					"/vista/ConsultarEditarTarea.zul", null, arguments);
			window.doModal();
		}
	}

	// Mostrar Ventana de Editar Tarea desde la Tabla de Tareas
	@Listen("onClick=#btnModificarTarea")
	public void editarTarea() {
		String indexSeleccionado = getIdSelectedRowTarea();
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una tarea a Consultar", gridTareas);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			tareaSeleccionado = datosTarea.obtenerTarea(id);
			String duracion = "";
			long d = tareaSeleccionado.getDuracion();
			int horas = (int) (d / 3600);
			int minutos = (int) ((d % 3600) / 60);

			if (horas < 10) {
				duracion = "0" + horas + " Horas ";
			} else {
				duracion = horas + " Horas ";
			}

			if (minutos < 10) {
				duracion = duracion + "0" + minutos + " Minutos ";
			} else {
				duracion = duracion + minutos + " Minutos ";
			}
			Map<String, Object> arguments = new HashMap<String, Object>();
			String titulo = "Editar Tarea";
			arguments.put("titulo", titulo);
			arguments.put("icono", "z-icon-edit");
			arguments.put("tareaSeleccionado", tareaSeleccionado);
			arguments.put("divConsulta", false);
			arguments.put("divEditar", true);
			arguments.put("duracion", duracion);
			arguments.put("horas", horas);
			arguments.put("minutos", minutos);
			Window window = (Window) Executions.createComponents(
					"/vista/ConsultarEditarTarea.zul", null, arguments);
			window.doModal();
		}
	}

	// Eliminar Tarea Seleccionada
	@Listen("onClick=#btnEliminarTarea")
	public void eliminarTarea() {
		String indexSeleccionado = getIdSelectedRowTarea();
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una tarea a Eliminar", gridTareas);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			final Tarea tarea = tareaDAO.obtenerTarea(id);

			Messagebox.show(
					"¿Seguro que desea Eliminar la tarea Seleccionada?",
					"Question", Messagebox.OK | Messagebox.CANCEL,
					Messagebox.QUESTION, new EventListener<Event>() {
						public void onEvent(Event e) {
							if (Messagebox.ON_OK.equals(e.getName())) {
								// OK is clicked
								Estatus estatus = estatusDAO
										.obtenerEstatusPorNombre("INACTIVO");
								tarea.setEstatus(estatus);
								tareaDAO.actualizarTarea(tarea);
								BindUtils.postGlobalCommand(null, null,
										"actualizarTabla", null);
							} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
								// Cancel is clicked
							}
						}
					});
		}
	}

	// Actualiza la Tabla de Tareas al realizar algun cambio
	@GlobalCommand
	@NotifyChange({ "tareas", "footerMessageTareas" })
	public void actualizarTabla() {
		tareas = datosTarea.obtenerTareasActivasPorEmpresa(usuarioSession
				.getEmpresa().getId());
	}

	private void showNotify(String msg, Component ref) {
		Clients.showNotification(msg, "error", ref, "end_center", 3000);
	}

	private void showNotifyInfo(String msg, Component ref) {
		Clients.showNotification(msg, "info", ref, "middle_center", 5000, true);
	}

	private String getIdSelectedRowTarea() {
		for (Component component : gridTareas.getRows().getChildren()) {
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
