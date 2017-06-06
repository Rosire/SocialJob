package controlador;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import modelo.dao.CargoDAO;
import modelo.dao.CargoEmpleadoDAO;
import modelo.dao.MenuDAO;
import modelo.dto.Cargo;
import modelo.dto.Menu;
import modelo.dto.Usuario;

import org.zkoss.bind.BindUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zkmax.zul.Navbar;
import org.zkoss.zkmax.zul.Navitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;

public class ControladorIndexRegistrado extends SelectorComposer<Component> {

	@Wire
	private Div divNavbar;
	@Wire
	private Div divSidebar;
	@Wire
	private Navitem itemControlSupervisiones;
	@Wire
	private Navitem itemTareasRepetitivasSubalternos;
	@Wire
	private Navbar navbarSidebar;
	@Wire
	private Navitem itemTareasAsignadas;
	@Wire
	private Navitem itemTareasRepetitivas;
	@Wire
	private Navitem itemTareas;
	@Wire
	private Navitem itemCargosLateral;
	@Wire
	private Navitem itemUsuarios;
	@Wire
	private Navitem itemMensajesLateral;
	@Wire
	private Navitem itemPerfil;
	@Wire
	private Navitem itemTareasEnMarcha;

	// Variable Session
	private Session miSession = Sessions.getCurrent();
	private Usuario usuario = (Usuario) miSession.getAttribute("usuario");

	private MenuDAO menuDAO = new MenuDAO();
	private CargoDAO cargoDAO = new CargoDAO();
	private CargoEmpleadoDAO cargoEmpleadoDAO = new CargoEmpleadoDAO();

	public ControladorIndexRegistrado() {
		super();

		if (usuario != null) {

			long cantidad = cargoEmpleadoDAO
					.obtenerCantidadCargosActivosPorEmpleadoConSupervisor(usuario
							.getId());

			// Barra Superior - Navbar
			List<Menu> menu = menuDAO.obtenerMenuSuperiorPorTipoUsuario(usuario
					.getTipoUsuario().getId());
			Map<String, Object> arguments = new HashMap<String, Object>();

			for (int i = 0; i < menu.size(); i++) {
				arguments.put(menu.get(i).getItemMenu().getNombre(), true);
			}

			if (cantidad == 0) {
				arguments.put("itemTareasEnMarcha", false);
			}

			Component component = Executions.createComponents(
					"vista/navbar.zul", divNavbar, arguments);
			Selectors.wireComponents(component, this, false);
			Selectors.wireEventListeners(component, this);

			// Barra Lateral - Sidebar
			menu = menuDAO.obtenerMenuLateralPorTipoUsuario(usuario
					.getTipoUsuario().getId());
			arguments.clear();
			for (int i = 0; i < menu.size(); i++) {
				arguments.put(menu.get(i).getItemMenu().getNombre(), true);
			}
			arguments.put("tipoUsuario", usuario.getTipoUsuario().getNombre());
			component = Executions.createComponents("vista/sidebar.zul",
					divSidebar, arguments);
			Selectors.wireComponents(component, this, false);
			Selectors.wireEventListeners(component, this);

			List<Cargo> cargos = cargoDAO
					.obtenerCargosActivosConSupervisorIguaActualPorEmpresa(
							usuario.getId(), usuario.getEmpresa().getId());

			if (cargos.isEmpty()) {
				itemControlSupervisiones.setVisible(false);
				itemTareasRepetitivasSubalternos.setVisible(false);
			}
			if (cantidad == 0) {
				itemTareasAsignadas.setVisible(false);
				itemTareasRepetitivas.setVisible(false);

			}

		} else {

		}
	}

	@Listen("onClick=#itemInicio")
	public void volverInicio() {
		Map args = new HashMap();
		long cantidad = cargoEmpleadoDAO
				.obtenerCantidadCargosActivosPorEmpleadoConSupervisor(usuario
						.getId());

		if (cantidad == 0) {
			args.put("data", "vista/principalAdministrador.zul");
		} else {
			args.put("data", "vista/tareasPrincipal.zul");
		}
		BindUtils
				.postGlobalCommand(null, null, "actualizarPaginaCentral", args);
	}

	@Listen("onClick=#listcellNotificacionesTareasAsignadas; onClick=#verTodasNotificaciones; onClick=#itemBotonNotificaciones; onClick=#listcellNotificacionesTareasRepetitivasAsignadas; onClick=#listcellNotificacionesSolicitudes; onClick=#itemBotonNotificaciones")
	public void notificacionesTareasAsignadas() {
		Map args = new HashMap();
		args.put("data", "vista/notificaciones.zul");
		BindUtils
				.postGlobalCommand(null, null, "actualizarPaginaCentral", args);
	}

	@Listen("onClick=#itemBotonMensajes; onClick=#itemMensajesLateral; onClick=#verTodosMensajes ")
	public void mensajes() {
		Map args = new HashMap();
		args.put("data", "vista/mensajes.zul");
		BindUtils
				.postGlobalCommand(null, null, "actualizarPaginaCentral", args);
		navbarSidebar.selectItem(itemMensajesLateral);
	}

	@Listen("onClick=#itemCargosLateral")
	public void tablaCargos() {
		Map args = new HashMap();
		args.put("data", "vista/tablaCargos.zul");
		BindUtils
				.postGlobalCommand(null, null, "actualizarPaginaCentral", args);
	}

	@Listen("onClick=#itemUsuarios; onClick=#itemBotonUsuarios")
	public void tablaEmpleados() {
		Map args = new HashMap();
		args.put("data", "vista/tablaUsuarios.zul");
		BindUtils
				.postGlobalCommand(null, null, "actualizarPaginaCentral", args);
		navbarSidebar.selectItem(itemUsuarios);
	}

	@Listen("onClick=#itemTareasAsignadas")
	public void tablaTareasAsignadas() {
		Map args = new HashMap();
		args.put("data", "vista/tareasAsignadas.zul");
		BindUtils
				.postGlobalCommand(null, null, "actualizarPaginaCentral", args);
	}

	@Listen("onClick=#itemTareasRepetitivas")
	public void tablaTareasRepetitivas() {
		Map args = new HashMap();
		args.put("data", "vista/tareasRepetitivasAsignadas.zul");
		BindUtils
				.postGlobalCommand(null, null, "actualizarPaginaCentral", args);
	}

	@Listen("onClick=#itemSolicitudes")
	public void tablaSolicitudes() {
		Map args = new HashMap();
		args.put("data", "vista/solicitudes.zul");
		BindUtils
				.postGlobalCommand(null, null, "actualizarPaginaCentral", args);
	}

	@Listen("onClick=#itemBotonCargos")
	public void botonCargos() {
		if (usuario.getTipoUsuario().getNombre().equals("EMPLEADO")) {

		} else {
			Map args = new HashMap();
			args.put("data", "vista/tablaCargos.zul");
			BindUtils.postGlobalCommand(null, null, "actualizarPaginaCentral",
					args);
		}
	}

	@Listen("onClick=#itemBotonTareas")
	public void botonTareas() {
		Map args = new HashMap();
		if (usuario.getTipoUsuario().getNombre().equals("ADMINISTRADOR")) {
			args.put("data", "vista/tablaTareas.zul");
			navbarSidebar.selectItem(itemTareas);
		} else {
			long cantidad = cargoEmpleadoDAO
					.obtenerCantidadCargosActivosPorEmpleadoConSupervisor(usuario
							.getId());
			if (cantidad == 0) {
				args.put("data", "vista/principalAdministrador.zul");
			} else {
				args.put("data", "vista/tareasPrincipal.zul");
			}
		}

		BindUtils
				.postGlobalCommand(null, null, "actualizarPaginaCentral", args);

	}

	@Listen("onClick=#itemCerrarSesion")
	public void cerrarSesion() {
		miSession.invalidate();
		Executions.sendRedirect("index.zul");
	}

	@Listen("onClick=#itemControlSupervisiones")
	public void controlSupervisiones() {
		Map args = new HashMap();
		args.put("data", "vista/controlSupervisiones.zul");
		BindUtils
				.postGlobalCommand(null, null, "actualizarPaginaCentral", args);

	}

	@Listen("onClick=#itemPerfil; onClick=#itemPerfilMenu")
	public void verPerfil() {
		Map args = new HashMap();
		args.put("data", "vista/perfil.zul");
		BindUtils
				.postGlobalCommand(null, null, "actualizarPaginaCentral", args);
		navbarSidebar.selectItem(itemPerfil);

	}

	@Listen("onClick=#itemTareasRepetitivasSubalternos")
	public void tareasRepetitivasSubalternos() {
		Map args = new HashMap();
		args.put("data", "vista/tareasRepetitivas.zul");
		BindUtils
				.postGlobalCommand(null, null, "actualizarPaginaCentral", args);

	}

	@Listen("onClick=#itemConfiguracionPerfil")
	public void configuracionPerfil() {
		Window window = (Window) Executions.getCurrent().createComponents(
				"/vista/configuracionPerfil.zul", null, null);
		window.doModal();

	}

	@Listen("onClick=#itemTareas")
	public void tareasAdministrador() {
		Map args = new HashMap();
		args.put("data", "/vista/tablaTareas.zul");
		BindUtils
				.postGlobalCommand(null, null, "actualizarPaginaCentral", args);
	}

	@Listen("onClick=#itemFrecuencias")
	public void secuencias() {
		Map args = new HashMap();
		args.put("data", "/vista/tablaFrecuencias.zul");
		BindUtils
				.postGlobalCommand(null, null, "actualizarPaginaCentral", args);

	}

	@Listen("onClick=#itemEmpresa")
	public void empresa() {
		Map args = new HashMap();
		args.put("data", "/vista/empresa.zul");
		BindUtils
				.postGlobalCommand(null, null, "actualizarPaginaCentral", args);

	}

	@Listen("onClick=#itemEstadisticas")
	public void estadisticas() {
		Map args = new HashMap();
		args.put("data", "/vista/estadisticas.zul");
		BindUtils
				.postGlobalCommand(null, null, "actualizarPaginaCentral", args);

	}
}
