package controlador;

import java.util.ArrayList;
import java.util.List;

import modelo.dao.CargoDAO;
import modelo.dao.EstatusDAO;
import modelo.dto.Cargo;
import modelo.dto.Estatus;
import modelo.dto.Usuario;

import org.zkoss.bind.BindUtils;
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
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

@SuppressWarnings("serial")
public class ControladorConsultarEditarCargo extends
		SelectorComposer<Component> {

	@Wire
	private Window windowConsultarEditarCargo;
	@Wire
	private Div divConsulta;
	@Wire
	private Div divEditar;
	@Wire
	private Textbox lblNombre;
	@Wire
	private Combobox cmbCargos;
	@Wire
	private Label labelCodigo;
	@Wire
	private Label labelNombre;
	@Wire
	private Div divCargoSuperior;

	// Session
	private Session miSession = Sessions.getCurrent();
	private Usuario usuarioSession = (Usuario) miSession
			.getAttribute("usuario");

	private CargoDAO cargoDAO = new CargoDAO();
	private EstatusDAO estatusDAO = new EstatusDAO();

	private List<Usuario> supervisores = new ArrayList<Usuario>();

	private ListModel<Usuario> listaSupervisoresModel;

	public ControladorConsultarEditarCargo() {
		super();
		listaSupervisoresModel = new ListModelList<Usuario>(supervisores);
	}

	public ListModel<Usuario> getSupervisores() {
		return listaSupervisoresModel;
	}

	// Guardar Modificacion de Cargo
	@Listen("onClick=#btnGuardarModificacion")
	public void guardarModificacionConsulta() {
		String nombre = lblNombre.getValue();
		String nombreAnterior = labelNombre.getValue();
		String nombreCargoSuperior = cmbCargos.getValue();
		int codigo = Integer.parseInt(labelCodigo.getValue().toString());
		Cargo cargoSuperiorBD = cargoDAO.obtenerCargoPorNombrePorEmpresa(
				nombreCargoSuperior, usuarioSession.getEmpresa().getId());
		Cargo cargoBD = cargoDAO.obtenerCargoPorNombrePorEmpresa(nombre,
				usuarioSession.getEmpresa().getId());
		if ((cargoBD != null) && !(cargoBD.getNombre().equals(nombreAnterior))) {
			showNotify("Cargo Registrado en Base de Datos", lblNombre);
			lblNombre.focus();
			return;
		}
		if (divCargoSuperior.isVisible()) {
			if (cargoSuperiorBD == null) {
				showNotify("Debe Seleccionar un Cargo Superior", cmbCargos);
				cmbCargos.focus();
				return;
			}
		} else {
			cargoSuperiorBD = null;
		}

		Estatus estatus = estatusDAO.obtenerEstatusPorNombre("ACTIVO");
		Cargo cargo = new Cargo(nombre, cargoSuperiorBD, estatus,
				usuarioSession.getEmpresa());
		if (!cargo.equals(null)) {
			cargo.setId(codigo);
			cargoDAO.actualizarCargo(cargo);
			BindUtils.postGlobalCommand(null, null, "actualizarTabla", null);
			BindUtils.postGlobalCommand(null, null, "cambioArbol", null);
			showNotifyInfo("Cargo Registrado Exitosamente",
					windowConsultarEditarCargo);
			windowConsultarEditarCargo.onClose();
		} else {
			showNotify("Error al Almacenar Cargo", windowConsultarEditarCargo);
		}
	}

	// Guardar Modificacion de Cargo
	@Listen("onClick=#btnGuardarCambio")
	public void guardarModificacionCambio() {
		String nombreCargoCambio = cmbCargos.getValue();
		int codigo = Integer.parseInt(labelCodigo.getValue().toString());

		Cargo cargoCambio = cargoDAO.obtenerCargoPorNombrePorEmpresa(
				nombreCargoCambio, usuarioSession.getEmpresa().getId());

		Cargo cargoAnterior = cargoDAO.obtenerCargo(codigo);
		if (cargoCambio == null) {
			showNotify("Debe Seleccionar un Cargo Superior", cmbCargos);
			cmbCargos.focus();
		} else {
			List<Cargo> cargosSubalternosAnterior = cargoDAO
					.obtenerCargosSubalternos(cargoAnterior.getId());

			List<Cargo> cargosSubalternosCambio = cargoDAO
					.obtenerCargosSubalternos(cargoCambio.getId());

			Cargo cargoSuperior = cargoDAO.obtenerCargo(cargoAnterior
					.getCargoSuperior().getId());

			if (cargoSuperior.getId() == cargoAnterior.getId()) {
				cargoCambio.setCargoSuperior(cargoCambio);
				cargoAnterior.setCargoSuperior(cargoCambio);
			} else {

				if (cargoCambio.getCargoSuperior().getId() == cargoCambio
						.getId()) {
					cargoAnterior.setCargoSuperior(cargoAnterior);
					cargoCambio.setCargoSuperior(cargoAnterior);

				} else if (cargoCambio.getId() == cargoSuperior.getId()) {

					cargoSuperior = cargoDAO.obtenerCargo(cargoCambio
							.getCargoSuperior().getId());
					cargoAnterior.setCargoSuperior(cargoSuperior);

					cargoCambio.setCargoSuperior(cargoAnterior);

				} else {

					cargoAnterior.setCargoSuperior(cargoSuperior);

					cargoCambio.setCargoSuperior(cargoAnterior);

				}

			}
			cargoDAO.actualizarCargo(cargoAnterior);
			cargoDAO.actualizarCargo(cargoCambio);
			cargoCambio = cargoDAO.obtenerCargoPorNombrePorEmpresa(
					nombreCargoCambio, usuarioSession.getEmpresa().getId());
			cargoAnterior = cargoDAO.obtenerCargo(codigo);

			for (int i = 0; i < cargosSubalternosAnterior.size(); i++) {
				Cargo subalterno = cargosSubalternosAnterior.get(i);
				if (subalterno.getId() != cargoCambio.getId()) {
					subalterno.setCargoSuperior(cargoCambio);
					cargoDAO.actualizarCargo(subalterno);
				}

			}

			for (int i = 0; i < cargosSubalternosCambio.size(); i++) {
				Cargo subalterno = cargosSubalternosCambio.get(i);
				if (subalterno.getId() != cargoAnterior.getId()) {
					subalterno.setCargoSuperior(cargoAnterior);

					cargoDAO.actualizarCargo(subalterno);
				}

			}
			BindUtils.postGlobalCommand(null, null, "actualizarTabla", null);
			BindUtils.postGlobalCommand(null, null, "cambioArbol", null);

			showNotifyInfo("Cargo Cambiado Exitosamente",
					windowConsultarEditarCargo);
			windowConsultarEditarCargo.onClose();
		}

	}

	// Mostrar Div Editar Cargo
	@NotifyChange({ "supervisores" })
	@Listen("onClick=#btnEditarCargo")
	public void editarCargoConsulta() {
		divConsulta.setVisible(false);
		divEditar.setVisible(true);
		int codigo = Integer.parseInt(labelCodigo.getValue().toString());

		Cargo cargoMatriz = cargoDAO
				.obtenerCargosMatrizPorEmpresa(usuarioSession.getEmpresa()
						.getId());
		if (cargoMatriz.getId() == codigo) {
			divCargoSuperior.setVisible(false);
		} else {
			divCargoSuperior.setVisible(true);
		}
	}

	// Salir
	@Listen("onClick=#btnSalirConsulta")
	public void salirConsulta() {
		windowConsultarEditarCargo.onClose();
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
							windowConsultarEditarCargo.onClose();
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
			showNotify("Debe ingresar el nombre del cargo", lblNombre);
			lblNombre.focus();
		}
		Cargo cargoBD = cargoDAO.obtenerCargoPorNombrePorEmpresa(nombre,
				usuarioSession.getEmpresa().getId());
		if ((cargoBD != null) && !(cargoBD.getNombre().equals(nombreAnterior))) {
			showNotify("Cargo Registrado en Base de Datos", lblNombre);
			lblNombre.focus();
		}
	}

}
