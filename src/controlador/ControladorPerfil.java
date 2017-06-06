package controlador;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import modelo.dao.CargoDAO;
import modelo.dto.Cargo;
import modelo.dto.Usuario;

import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Window;

@SuppressWarnings("serial")
public class ControladorPerfil extends SelectorComposer<Component> {

	// Variable Sesion
	private Session miSession = Sessions.getCurrent();
	private Usuario usuario = (Usuario) miSession.getAttribute("usuario");

	private String foto;

	private CargoDAO cargoDAO = new CargoDAO();

	private ListModel<Cargo> listaCargosEmpleadoModel;

	private List<Cargo> cargosEmpleado;

	private boolean visibleCargos;

	public ControladorPerfil() {
		super();
		cargosEmpleado = cargoDAO.obtenerCargosPorEmpleado(usuario.getId());

		if (usuario.getTipoUsuario().getNombre().equals("ADMINISTRADOR")) {
			visibleCargos = false;
		} else {
			visibleCargos = true;
		}
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public ListModel<Cargo> getCargosEmpleado() {
		listaCargosEmpleadoModel = new ListModelList<Cargo>(cargosEmpleado);
		return listaCargosEmpleadoModel;
	}

	public boolean getVisibleCargos() {
		return visibleCargos;
	}

	public String getFoto() {
		foto = ".." + usuario.getFoto();
		return foto;
	}

	@Listen("onClick=#btnEditar")
	public void modificarEmpleado() {
		Map<String, Object> arguments = new HashMap<String, Object>();
		arguments.put("usuario", usuario);
		if (usuario.getSexo().equals("FEMENINO")) {
			arguments.put("femenino", true);
		} else {
			arguments.put("masculino", true);
		}

		Window window = (Window) Executions.createComponents(
				"/vista/editarPerfil.zul", null, arguments);
		window.doModal();
	}

	@GlobalCommand
	@NotifyChange({ "usuario", "foto" })
	public void actualizarUsuario() {
		miSession = Sessions.getCurrent();
		usuario = (Usuario) miSession.getAttribute("usuario");
	}

}
