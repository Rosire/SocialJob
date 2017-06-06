package controlador;

import modelo.dao.CargoEmpleadoDAO;
import modelo.dto.Usuario;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;

public class ControladorCambioPagina extends SelectorComposer<Component> {

	@Wire
	private org.zkoss.zul.Include includeCentral;

	private String srcPaginaCentral;

	private Session session = Sessions.getCurrent();
	private Usuario usuario = (Usuario) session.getAttribute("usuario");

	private CargoEmpleadoDAO cargoEmpleadoDAO = new CargoEmpleadoDAO();

	public ControladorCambioPagina() {
		super();

		long cantidad = cargoEmpleadoDAO
				.obtenerCantidadCargosActivosPorEmpleadoConSupervisor(usuario
						.getId());
		if (cantidad == 0) {
			srcPaginaCentral = "vista/principalAdministrador.zul";
		} else {
			srcPaginaCentral = "vista/TareasPrincipal.zul";
		}

	}

	public String getSrcPaginaCentral() {
		return srcPaginaCentral;

	}

	// Actualiza paginacentral Seleccionado
	@GlobalCommand
	@NotifyChange({ "srcPaginaCentral" })
	public void actualizarPaginaCentral(@BindingParam("data") String data) {
		srcPaginaCentral = data;
	}

}
