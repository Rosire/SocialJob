package controlador;

import java.util.List;

import modelo.dao.CargoDAO;
import modelo.dao.CargoEmpleadoDAO;
import modelo.dto.Cargo;
import modelo.dto.Usuario;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zkmax.zul.Navitem;
import org.zkoss.zul.Div;

public class ControladorEstadisticas extends SelectorComposer<Component> {

	@Wire
	private Div divEmpresa;
	@Wire
	private Div divCargosPersonal;
	@Wire
	private Div divPersonal;
	@Wire
	private Div divCargosSubalternos;
	@Wire
	private Div divEmpleadoSubalternos;
	@Wire
	private Div divCargosEmpresa;
	@Wire
	private Div divEmpleadosEmpresa;

	@Wire
	private Navitem navItemEmpresa;
	@Wire
	private Navitem navItemCargosPersonal;
	@Wire
	private Navitem navItemPersonal;
	@Wire
	private Navitem navItemCargosSubalternos;
	@Wire
	private Navitem navItemEmpleadoSubalternos;
	@Wire
	private Navitem navItemEmpleadosEmpresa;
	@Wire
	private Navitem navItemCargosEmpresa;

	// Variable Sesion
	private Session miSession = Sessions.getCurrent();
	private Usuario usuarioSession = (Usuario) miSession
			.getAttribute("usuario");

	private CargoDAO cargoDAO = new CargoDAO();
	private CargoEmpleadoDAO cargoEmpleadoDAO = new CargoEmpleadoDAO();

	public ControladorEstadisticas() {
		super();

	}

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		if (usuarioSession.getTipoUsuario().getNombre()
				.equalsIgnoreCase("ADMINISTRADOR")) {
			navItemCargosSubalternos.setVisible(false);
			navItemEmpleadoSubalternos.setVisible(false);
			navItemCargosPersonal.setVisible(false);
			navItemPersonal.setVisible(false);
		} else {
			if (usuarioSession.getTipoUsuario().getNombre()
					.equalsIgnoreCase("EMPLEADO")) {
				navItemEmpleadosEmpresa.setVisible(false);
				navItemCargosEmpresa.setVisible(false);
			}
			long cantidad = cargoEmpleadoDAO
					.obtenerCantidadCargosActivosPorEmpleadoConSupervisor(usuarioSession
							.getId());
			List<Cargo> cargos = cargoDAO
					.obtenerCargosActivosConSupervisorIguaActualPorEmpresa(
							usuarioSession.getId(), usuarioSession.getEmpresa()
									.getId());

			if (cargos.isEmpty()) {
				navItemCargosSubalternos.setVisible(false);
				navItemEmpleadoSubalternos.setVisible(false);
			}
			if (cantidad == 0) {
				navItemCargosPersonal.setVisible(false);
				navItemPersonal.setVisible(false);
			}

		}
	}

	@Listen("onClick=#navItemEmpresa")
	public void mostrarEstadisticasEmpresa() {
		divEmpresa.setVisible(true);
		divCargosPersonal.setVisible(false);
		divPersonal.setVisible(false);
		divCargosSubalternos.setVisible(false);
		divEmpleadoSubalternos.setVisible(false);
		divCargosEmpresa.setVisible(false);
		divEmpleadosEmpresa.setVisible(false);
	}

	@Listen("onClick=#navItemCargosPersonal")
	public void mostrarEstadisticasCargosPersonal() {
		divEmpresa.setVisible(false);
		divCargosPersonal.setVisible(true);
		divPersonal.setVisible(false);
		divCargosSubalternos.setVisible(false);
		divEmpleadoSubalternos.setVisible(false);
		divCargosEmpresa.setVisible(false);
		divEmpleadosEmpresa.setVisible(false);
	}

	@Listen("onClick=#navItemPersonal")
	public void mostrarEstadisticasPersonal() {
		divEmpresa.setVisible(false);
		divCargosPersonal.setVisible(false);
		divPersonal.setVisible(true);
		divCargosSubalternos.setVisible(false);
		divEmpleadoSubalternos.setVisible(false);
		divCargosEmpresa.setVisible(false);
		divEmpleadosEmpresa.setVisible(false);
	}

	@Listen("onClick=#navItemCargosSubalternos")
	public void mostrarEstadisticasCargosSubalternos() {
		divEmpresa.setVisible(false);
		divCargosPersonal.setVisible(false);
		divPersonal.setVisible(false);
		divCargosSubalternos.setVisible(true);
		divEmpleadoSubalternos.setVisible(false);
		divCargosEmpresa.setVisible(false);
		divEmpleadosEmpresa.setVisible(false);
	}

	@Listen("onClick=#navItemEmpleadoSubalternos")
	public void mostrarEstadisticasEmpleadosSubalternos() {
		divEmpresa.setVisible(false);
		divCargosPersonal.setVisible(false);
		divPersonal.setVisible(false);
		divCargosSubalternos.setVisible(false);
		divEmpleadoSubalternos.setVisible(true);
		divCargosEmpresa.setVisible(false);
		divEmpleadosEmpresa.setVisible(false);
	}

	@Listen("onClick=#navItemEmpleadosEmpresa")
	public void mostrarEstadisticasEmpleadosEmpresa() {
		divEmpresa.setVisible(false);
		divCargosPersonal.setVisible(false);
		divPersonal.setVisible(false);
		divCargosSubalternos.setVisible(false);
		divEmpleadoSubalternos.setVisible(false);
		divCargosEmpresa.setVisible(false);
		divEmpleadosEmpresa.setVisible(true);
	}

	@Listen("onClick=#navItemCargosEmpresa")
	public void mostrarEstadisticasCargosEmpresa() {
		divEmpresa.setVisible(false);
		divCargosPersonal.setVisible(false);
		divPersonal.setVisible(false);
		divCargosSubalternos.setVisible(false);
		divEmpleadoSubalternos.setVisible(false);
		divCargosEmpresa.setVisible(true);
		divEmpleadosEmpresa.setVisible(false);
	}

}
