package controlador;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import modelo.dao.CargoDAO;
import modelo.dao.CargoEmpleadoDAO;
import modelo.dao.EmpresaDAO;
import modelo.dto.Cargo;
import modelo.dto.CargoEmpleado;
import modelo.dto.Empresa;
import modelo.dto.Usuario;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.AbstractListModel;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.TreeModel;
import org.zkoss.zul.TreeNode;
import org.zkoss.zul.Window;

import arbol.demo.data.CargoList;
import arbol.demo.data.pojo.CargoArbol;

@SuppressWarnings("serial")
public class ControladorEmpresa extends SelectorComposer<Component> {

	@Wire
	private Div divInformacion;
	@Wire
	private Div divArbol;

	// Variable Sesion
	private Session miSession = Sessions.getCurrent();
	private Usuario usuarioSession = (Usuario) miSession
			.getAttribute("usuario");

	private String foto;

	private String linkTwitterEmpresa;

	private Empresa empresa = new Empresa();

	private boolean visibleEditar;

	private TreeModel<TreeNode<CargoArbol>> cargosModel;
	private ListModel<Cargo> listaCargosSubalternosModel;
	private List<Cargo> cargosSubalternos;

	private CargoDAO cargoDAO = new CargoDAO();
	private CargoEmpleadoDAO cargoEmpleadoDAO = new CargoEmpleadoDAO();

	public ControladorEmpresa() {
		super();
		empresa = usuarioSession.getEmpresa();
		if (usuarioSession.getId() == usuarioSession.getEmpresa().getUsuario()
				.getId()) {
			visibleEditar = true;
		} else {
			visibleEditar = false;
		}
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public boolean getVisibleEditar() {
		return visibleEditar;
	}

	public TreeModel<TreeNode<CargoArbol>> getModeloArbol() {

		cargosModel = new DefaultTreeModel<CargoArbol>(
				new CargoList().getRoot());
		((DefaultTreeModel<CargoArbol>) cargosModel)
				.addOpenPath(new int[] { 0 });
		return cargosModel;

	}

	public String getLinkTwitterEmpresa() {
		String twitter = empresa.getTwitter();
		linkTwitterEmpresa = "https://twitter.com/" + twitter;
		return linkTwitterEmpresa;
	}

	public String getFoto() {
		foto = ".." + empresa.getLogo();
		return foto;
	}

	@Listen("onClick=#btnEditar")
	public void modificarEmpleado() {
		Map<String, Object> arguments = new HashMap<String, Object>();
		arguments.put("empresa", empresa);
		Window window = (Window) Executions.createComponents(
				"/vista/editarEmpresa.zul", null, arguments);
		window.doModal();
	}

	@GlobalCommand
	@NotifyChange({ "empresa", "foto", "linkTwitterEmpresa" })
	public void actualizarEmpresa() {
		miSession = Sessions.getCurrent();
		usuarioSession = (Usuario) miSession.getAttribute("usuario");
		empresa = usuarioSession.getEmpresa();
	}

	@Command
	public void detalleCargo(@BindingParam("id") int id) {
		Cargo cargoSeleccionado = cargoDAO.obtenerCargo(id);

		Map<String, Object> arguments = new HashMap<String, Object>();
		String titulo = "Consultar Cargo";
		CargoEmpleado supervisor = cargoEmpleadoDAO
				.obtenerUsuarioSupervisorPorCargo(cargoSeleccionado
						.getCargoSuperior().getId());
		String nombreSupervisor = "";
		if (supervisor != null) {
			nombreSupervisor = supervisor.getEmpleado().getNombres() + " "
					+ supervisor.getEmpleado().getApellidos() + "(V-"
					+ supervisor.getEmpleado().getCedula() + ")";
		}
		cargosSubalternos = cargoDAO.obtenerCargosSubalternos(id);
		listaCargosSubalternosModel = new ListModelList<Cargo>(
				cargosSubalternos);

		arguments.put("subalternos", listaCargosSubalternosModel);
		arguments.put("nombreSupervisor", nombreSupervisor);
		arguments.put("titulo", titulo);
		arguments.put("icono", "z-icon-eye");
		arguments.put("cargoSeleccionado", cargoSeleccionado);
		arguments.put("divConsulta", true);
		arguments.put("btnEditar", false);

		Window window = (Window) Executions.createComponents(
				"/vista/ConsultarEditarCargo.zul", null, arguments);
		window.doModal();

	}

	@Listen("onClick=#navItemArbolJerarquico")
	public void mostrarArbol() {
		divInformacion.setVisible(false);
		divArbol.setVisible(true);
	}

	@Listen("onClick=#navItemInformacion")
	public void mostrarTabla() {
		divInformacion.setVisible(true);
		divArbol.setVisible(false);
	}

}
