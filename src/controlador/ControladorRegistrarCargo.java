package controlador;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import modelo.dao.CargoDAO;
import modelo.dao.EstatusDAO;
import modelo.dto.Cargo;
import modelo.dto.Estatus;
import modelo.dto.Usuario;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.GlobalCommand;
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
import org.zkoss.zul.AbstractListModel;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

@SuppressWarnings("serial")
public class ControladorRegistrarCargo extends SelectorComposer<Component> {

	// wire components
	@Wire
	private Textbox lblNombre;
	@Wire
	private Listbox listaCargos;
	@Wire("#windowsRegistrarCargo")
	private Window windowsRegistrarCargo;

	// Session
	private Session miSession = Sessions.getCurrent();
	private Usuario usuarioSession = (Usuario) miSession
			.getAttribute("usuario");

	private CargoDAO cargoDAO = new CargoDAO();
	private EstatusDAO estatusDAO = new EstatusDAO();

	private List<Cargo> cargos = cargoDAO
			.obtenerCargosActivosPorEmpresa(usuarioSession.getId());
	private List<Usuario> supervisores = new ArrayList<Usuario>();

	private ListModel<Usuario> listaSupervisoresModel;
	private ListModel<Cargo> listaCargosModel;

	private Cargo cargoSeleccionado = null;

	public ControladorRegistrarCargo() {
		super();
	}

	public ListModel<Cargo> getCargos() {
		listaCargosModel = new ListModelList<Cargo>(cargos);

		if (cargoSeleccionado != null) {

			for (int i = 0; i < cargos.size(); i++) {
				if (cargos.get(i).getId() == cargoSeleccionado.getId()) {
					((AbstractListModel<Cargo>) listaCargosModel)
							.addToSelection(((List<Cargo>) listaCargosModel)
									.get(i));
				}
			}
		}
		return listaCargosModel;
	}

	public ListModel<Usuario> getSupervisores() {
		listaSupervisoresModel = new ListModelList<Usuario>(supervisores);
		return listaSupervisoresModel;
	}

	@Listen("onClick=#btnGuardarCargo")
	public void registrarCargo() {
		String nombre = lblNombre.getValue();
		Cargo cargoSuperior = null;
		ListModelList<Object> listaCargo = (ListModelList<Object>) listaCargos
				.getModel();
		Set<Object> conjuntoCargo = listaCargo.getSelection();
		Cargo cargoBD = cargoDAO.obtenerCargoPorNombrePorEmpresa(nombre,
				usuarioSession.getId());
		if ((cargoBD != null)) {
			showNotify("Cargo Registrado en Base de Datos", lblNombre);
			lblNombre.focus();
		} else if (conjuntoCargo.size() == 0) {
			showNotify("Debe Seleccionar el Cargo Superior", listaCargos);
			lblNombre.focus();

		} else {
			cargoSuperior = (Cargo) conjuntoCargo.toArray()[0];
			Estatus estatus = estatusDAO.obtenerEstatusPorNombre("ACTIVO");
			Cargo cargo = new Cargo(nombre, cargoSuperior, estatus,
					usuarioSession.getEmpresa());
			if (!cargo.equals(null)) {
				cargoDAO.registrarCargo(cargo);
				showNotifyInfo("Cargo Registrado Exitosamente",
						windowsRegistrarCargo);
				BindUtils.postGlobalCommand(null, null, "cambioArbol", null);
				windowsRegistrarCargo.onClose();
				BindUtils.postGlobalCommand(null, null, "cambiarSeleccion",
						null);
				windowsRegistrarCargo.onClose();
			} else {
				showNotify("Error al Almacenar Cargo", windowsRegistrarCargo);
			}
		}
	}

	@Listen("onClick=#btnCancelar")
	public void cancelarRegistro() {
		Messagebox.show("¿Seguro que desea Cancelar?", "Question",
				Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION,
				new EventListener<Event>() {
					public void onEvent(Event e) {
						if (Messagebox.ON_OK.equals(e.getName())) {
							// OK is clicked
							windowsRegistrarCargo.onClose();
						} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
							// Cancel is clicked
						}
					}
				});
	}

	@Listen("onChange = #lblNombre")
	public void cambioNombre() {
		String nombre = lblNombre.getValue().toString();
		if (nombre.equalsIgnoreCase("")) {
			showNotify("Debe ingresar el nombre del cargo", lblNombre);
			lblNombre.focus();
		}
		Cargo cargo = cargoDAO.obtenerCargoPorNombrePorEmpresa(nombre,
				usuarioSession.getId());
		if ((cargo != null)) {
			showNotify("Cargo Registrado en Base de Datos", lblNombre);
			lblNombre.focus();
		}
	}

	private void showNotify(String msg, Component ref) {
		Clients.showNotification(msg, "error", ref, "end_center", 3000, true);
	}

	private void showNotifyInfo(String msg, Component ref) {
		Clients.showNotification(msg, "info", ref, "middle_center", 5000, true);
	}

	// Actualiza la Tabla de Cargos al realizar algun cambio
	@GlobalCommand
	@NotifyChange({ "cargos" })
	public void actualizarSeleccion(@BindingParam("id") int id) {
		cargoSeleccionado = cargoDAO.obtenerCargo(id);
	}

	// Actualiza la Tabla de Cargos al realizar algun cambio
	@GlobalCommand
	@NotifyChange({ "cargos" })
	public void cambiarSeleccion() {
		cargoSeleccionado = null;
	}

}
