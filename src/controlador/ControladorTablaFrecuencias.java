package controlador;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import modelo.dao.FrecuenciaDAO;
import modelo.dao.TareaRepetitivaDAO;
import modelo.dto.Frecuencia;
import modelo.dto.Usuario;

import org.zkoss.bind.BindUtils;
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
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.Window;

@SuppressWarnings("serial")
public class ControladorTablaFrecuencias extends SelectorComposer<Component> {
	@Wire
	private Listbox listaFrecuencia;
	@Wire
	private Grid gridFrecuencia;

	// Session
	private Session miSession = Sessions.getCurrent();
	private Usuario usuarioSession = (Usuario) miSession
			.getAttribute("usuario");

	private FrecuenciaDAO frecuenciaDAO = new FrecuenciaDAO();
	private Frecuencia frecuencia = new Frecuencia();
	private List<Frecuencia> frecuencias;

	private static final String footerMessageFrecuencias = "Total de Frecuencias: %d";

	public ControladorTablaFrecuencias() {
		super();
		frecuencias = frecuenciaDAO.obtenerFrecuenciasPorEmpresa(usuarioSession
				.getEmpresa().getId());
	}

	public ListModel<Frecuencia> getFrecuencias() {
		return new ListModelList<Frecuencia>(frecuencias);
	}

	public String getFooterMessageFrecuencias() {
		return String.format(footerMessageFrecuencias, getFrecuencias()
				.getSize());
	}

	// Incluir una Nueva Frecuencia
	@Listen("onClick=#btnIncluir")
	public void incluirFrecuencia() {
		Window window = (Window) Executions.createComponents(
				"/vista/registrarFrecuencia.zul", null, null);
		window.doModal();
	}

	private String getIdSelectedRowFrecuencia() {
		for (Component component : gridFrecuencia.getRows().getChildren()) {
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

	@Listen("onClick=#btnModificarFrecuencia")
	public void modificarFrecuencia() {
		String indexSeleccionado = getIdSelectedRowFrecuencia();
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una Frecuencia a Modificar",
					gridFrecuencia);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			frecuencia = frecuenciaDAO.obtenerFrecuencia(id);
			modificarFrecuenciaSeleccionada(frecuencia);
		}
	}

	@Listen("onClick=#btnEliminarFrecuencia")
	public void btnEliminarFrecuencia() {
		String indexSeleccionado = getIdSelectedRowFrecuencia();
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una Frecuencia a Eliminar",
					gridFrecuencia);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			frecuencia = frecuenciaDAO.obtenerFrecuencia(id);
			long cantidadTareas = TareaRepetitivaDAO
					.contarTareasRepetitivasPorFrecuencias(id);
			if (cantidadTareas > 0) {
				showNotifyWarning(
						"No se puede eliminar las Frecuencias que estan asociadas a Tareas Repetitivas",
						gridFrecuencia);
			} else {
				Messagebox
						.show("¿Seguro que desea Eliminar la Frecuencia Seleccionada?",
								"Eliminar Frecuencia", Messagebox.OK
										| Messagebox.CANCEL,
								Messagebox.QUESTION,
								new EventListener<Event>() {
									public void onEvent(Event e) {
										if (Messagebox.ON_OK.equals(e.getName())) {
											// OK is clicked

											frecuenciaDAO
													.eliminarFrecuencia(frecuencia);
											BindUtils.postGlobalCommand(null,
													null,
													"actualizarFrecuencias",
													null);
											showNotifyInfo(
													"Frecuencia Registrada Exitosamente",
													gridFrecuencia);
										} else if (Messagebox.ON_CANCEL
												.equals(e.getName())) {
											// Cancel is clicked
										}
									}
								});
			}
		}
	}

	public void modificarFrecuenciaSeleccionada(Frecuencia frecuencia) {
		Map<String, Object> arguments = new HashMap<String, Object>();
		arguments.put("frecuencia", frecuencia);
		Window window = (Window) Executions.getCurrent().createComponents(
				"/vista/editarFrecuencia.zul", null, arguments);
		window.doModal();
	}

	@GlobalCommand
	@NotifyChange({ "frecuencias", "footerMessageFrecuencias" })
	public void actualizarFrecuencias() {
		frecuencias = frecuenciaDAO.obtenerFrecuenciasPorEmpresa(usuarioSession
				.getEmpresa().getId());
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

}
