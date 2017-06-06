package arbol.demo.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;

import modelo.dao.CargoDAO;
import modelo.dao.CargoEmpleadoDAO;
import modelo.dao.UsuarioDAO;
import modelo.dto.Cargo;
import modelo.dto.CargoEmpleado;
import modelo.dto.Usuario;
import arbol.demo.data.pojo.CargoArbol;
import arbol.demo.tree.dynamic_tree.CargoTreeNode;

public class CargoList {
	public final static String Category = "Category";
	public final static String Contact = "CargoArbol";

	private List<Cargo> listaCargos = new ArrayList<Cargo>();
	private Cargo cargoMatriz = new Cargo();

	private CargoDAO cargoDAO = new CargoDAO();
	private CargoEmpleadoDAO cargoEmpleadoDAO = new CargoEmpleadoDAO();

	private CargoTreeNode root;

	// Session
	private Session miSession = Sessions.getCurrent();
	private Usuario usuarioSession = (Usuario) miSession
			.getAttribute("usuario");

	public CargoTreeNode[] retornarhijos(Cargo cargo) {

		List<Cargo> listaCargos1 = cargoDAO.obtenerCargosSubalternos(cargo
				.getId());

		CargoTreeNode[] listaTree = new CargoTreeNode[listaCargos1.size()];

		Cargo aux;

		for (int i = 0; i < listaTree.length; i++) {
			aux = listaCargos1.get(i);
			List<Cargo> listaCargos2 = cargoDAO.obtenerCargosSubalternos(aux
					.getId());

			if (listaCargos2.size() != 0) {
				CargoEmpleado cargoEmpleado = cargoEmpleadoDAO
						.obtenerUsuarioSupervisorPorCargo(aux.getId());

				if (cargoEmpleado == null) {
					listaTree[i] = new CargoTreeNode(new CargoArbol(
							aux.getNombre(), aux.getId()), retornarhijos(aux),
							true);
				} else {
					String supervisor = cargoEmpleado.getEmpleado()
							.getNombres()
							+ " "
							+ cargoEmpleado.getEmpleado().getApellidos();
					String foto = cargoEmpleado.getEmpleado().getFoto();
					listaTree[i] = new CargoTreeNode(new CargoArbol(
							aux.getNombre(), foto, supervisor, aux.getId()),
							retornarhijos(aux), true);
				}

			} else {
				listaTree[i] = new CargoTreeNode(new CargoArbol(
						aux.getNombre(), aux.getId()));
			}

		}
		return listaTree;

	}

	public CargoList() {

		super();
	}

	public CargoTreeNode getRoot() {
		cargoMatriz = cargoDAO.obtenerCargosMatrizPorEmpresa(usuarioSession
				.getEmpresa().getId());
		listaCargos = cargoDAO.obtenerCargosSubalternos(cargoMatriz.getId());
		CargoTreeNode[] listaTree;
		CargoEmpleado cargoEmpleado = cargoEmpleadoDAO
				.obtenerUsuarioSupervisorPorCargo(cargoMatriz.getId());

		if (listaCargos.size() == 0) {
			if (cargoEmpleado == null) {
				listaTree = new CargoTreeNode[] { new CargoTreeNode(
						new CargoArbol(cargoMatriz.getNombre(),
								cargoMatriz.getId())) };
			} else {
				String supervisor = cargoEmpleado.getEmpleado().getNombres()
						+ " " + cargoEmpleado.getEmpleado().getApellidos();
				String foto = cargoEmpleado.getEmpleado().getFoto();
				listaTree = new CargoTreeNode[] { new CargoTreeNode(
						new CargoArbol(cargoMatriz.getNombre(), foto,
								supervisor, cargoMatriz.getId())) };
			}

		} else {

			if (cargoEmpleado == null) {
				listaTree = new CargoTreeNode[] { new CargoTreeNode(
						new CargoArbol(cargoMatriz.getNombre(),
								cargoMatriz.getId()),
						retornarhijos(cargoMatriz), true) };
			} else {
				String supervisor = cargoEmpleado.getEmpleado().getNombres()
						+ " " + cargoEmpleado.getEmpleado().getApellidos();
				String foto = cargoEmpleado.getEmpleado().getFoto();
				listaTree = new CargoTreeNode[] { new CargoTreeNode(
						new CargoArbol(cargoMatriz.getNombre(), foto,
								supervisor, cargoMatriz.getId()),
						retornarhijos(cargoMatriz), true) };
			}

		}

		root = new CargoTreeNode(null, listaTree, true);
		return root;
	}

}
