package arbol.demo.tree.dynamic_tree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import modelo.dao.CargoDAO;
import modelo.dao.CargoEmpleadoDAO;
import modelo.dto.Cargo;
import modelo.dto.CargoEmpleado;
import modelo.dto.Usuario;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.AbstractListModel;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Tree;
import org.zkoss.zul.TreeModel;
import org.zkoss.zul.TreeNode;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Treerow;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;

import arbol.demo.data.CargoList;
import arbol.demo.data.pojo.CargoArbol;

public class DemoComposer extends SelectorComposer<Component> {
	private static final long serialVersionUID = 3814570327995355261L;

	@Wire
	private Tree tree;

	private TreeModel<TreeNode<CargoArbol>> cargosModel;

	private AdvancedTreeModel contactTreeModel;

	private CargoDAO cargoDAO = new CargoDAO();

	private Session miSession = Sessions.getCurrent();
	private Usuario usuarioSession = (Usuario) miSession
			.getAttribute("usuario");

	private CargoEmpleadoDAO cargoEmpleadoDAO = new CargoEmpleadoDAO();

	@GlobalCommand("cambioArbol")
	@NotifyChange
	public void doAfterCompose(Component comp) throws Exception {
		System.out.println("aa");
		super.doAfterCompose(comp);
		// contactTreeModel = new AdvancedTreeModel(new CargoList().getRoot());
		// tree.setItemRenderer(new ContactTreeRenderer());
		// tree.setModel(contactTreeModel);
	}

	/**
	 * The structure of tree
	 * 
	 * <pre>
	 * &lt;treeitem>
	 *   &lt;treerow>
	 *     &lt;treecell>...&lt;/treecell>
	 *   &lt;/treerow>
	 *   &lt;treechildren>
	 *     &lt;treeitem>...&lt;/treeitem>
	 *   &lt;/treechildren>
	 * &lt;/treeitem>
	 * </pre>
	 */
	private final class ContactTreeRenderer implements
			TreeitemRenderer<CargoTreeNode> {
		@Override
		public void render(final Treeitem treeItem, CargoTreeNode treeNode,
				int index) throws Exception {
			CargoTreeNode ctn = treeNode;

			CargoArbol cargo = (CargoArbol) ctn.getData();
			Treerow dataRow = new Treerow();
			dataRow.setParent(treeItem);
			treeItem.setValue(ctn);
			treeItem.setOpen(ctn.isOpen());

			if (!isSupervisor(cargo)) { // Cargo Row

				Hlayout hl = new Hlayout();
				Image ima = new Image(cargo.getFoto());
				ima.setStyle("border-radius: 4%; border: 2px solid #808080; max-width: 25px;");
				Label label = new Label(cargo.getNombre());
				label.setStyle("font-weight: bold;");
				hl.appendChild(label);
				hl.appendChild(new Separator());
				hl.appendChild(ima);
				hl.appendChild(new Label(cargo.getSupervisor()));
				hl.setSclass("h-inline-block");

				Treecell treeCell = new Treecell();
				treeCell.appendChild(hl);

				dataRow.appendChild(treeCell);

			} else { // Category Row
				dataRow.appendChild(new Treecell(cargo.getNombre()));
			}

			dataRow.addEventListener(Events.ON_CLICK,
					new EventListener<Event>() {
						@Override
						public void onEvent(Event event) throws Exception {
							CargoTreeNode clickedNodeValue = (CargoTreeNode) ((Treeitem) event
									.getTarget().getParent()).getValue();
							CargoArbol cargoArbol = ((CargoArbol) clickedNodeValue
									.getData());
							Map args = new HashMap();
							args.put("data", cargoArbol.getId());
							BindUtils.postGlobalCommand(null, null,
									"actualizarTablaEmpleados", args);
						}
					});

			dataRow.addEventListener(Events.ON_DOUBLE_CLICK,
					new EventListener<Event>() {
						@Override
						public void onEvent(Event event) throws Exception {
							CargoTreeNode clickedNodeValue = (CargoTreeNode) ((Treeitem) event
									.getTarget().getParent()).getValue();
							CargoArbol cargoArbol = ((CargoArbol) clickedNodeValue
									.getData());
							Cargo cargoSeleccionado = cargoDAO
									.obtenerCargo(cargoArbol.getId());

							Map<String, Object> arguments = new HashMap<String, Object>();
							String titulo = "Consultar Cargo";
							CargoEmpleado supervisor = cargoEmpleadoDAO
									.obtenerUsuarioSupervisorPorCargo(cargoSeleccionado
											.getCargoSuperior().getId());
							String nombreSupervisor = "";
							if (supervisor != null) {
								nombreSupervisor = supervisor.getEmpleado()
										.getNombres()
										+ " "
										+ supervisor.getEmpleado()
												.getApellidos()
										+ "(V-"
										+ supervisor.getEmpleado().getCedula()
										+ ")";
							}

							List<Cargo> cargosSuperior = cargoDAO.obtenerCargosActivosDistintoPorEmpresa(
									cargoSeleccionado.getId(), usuarioSession
											.getEmpresa().getId());
							ListModel<Cargo> listaCargosModel = new ListModelList<Cargo>(
									cargosSuperior);
							if (cargoSeleccionado.getCargoSuperior() != null) {

								for (int i = 0; i < cargosSuperior.size(); i++) {
									if (cargosSuperior.get(i).getId() == cargoSeleccionado
											.getCargoSuperior().getId()) {
										((AbstractListModel<Cargo>) listaCargosModel)
												.addToSelection(((List<Cargo>) listaCargosModel)
														.get(i));
									}
								}
							}
							arguments.put("titulo", titulo);
							arguments.put("icono", "z-icon-eye");
							arguments.put("cargoSeleccionado",
									cargoSeleccionado);
							arguments.put("divConsulta", true);
							arguments.put("btnEditar", true);
							arguments.put("cargos", listaCargosModel);
							arguments.put("nombreSupervisor", nombreSupervisor);
							Window window = (Window) Executions
									.createComponents(
											"/vista/ConsultarEditarCargo.zul",
											null, arguments);
							window.doModal();
						}
					});
		}

		private boolean isSupervisor(CargoArbol cargo) {
			return cargo.getSupervisor() == null;
		}
	}

	
}