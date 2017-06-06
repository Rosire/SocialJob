package arbol.demo.tree.dynamic_tree;

import org.zkoss.zul.DefaultTreeNode;

import arbol.demo.data.pojo.CargoArbol;

public class CargoTreeNode extends DefaultTreeNode<CargoArbol> {
	private static final long serialVersionUID = -7012663776755277499L;

	private boolean open = false;

	public CargoTreeNode(CargoArbol data, DefaultTreeNode<CargoArbol>[] children) {
		super(data, children);
	}

	public CargoTreeNode(CargoArbol data,
			DefaultTreeNode<CargoArbol>[] children, boolean open) {
		super(data, children);
		setOpen(open);
	}

	public CargoTreeNode(CargoArbol data) {
		super(data);

	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public String getNombre() {
		return getData().getNombre();
	}

	public String getSupervisor() {
		return getData().getSupervisor();
	}

	public String getFoto() {
		return getData().getFoto();
	}

	public int getId() {
		return getData().getId();
	}

	public boolean getVisibleFoto() {
		if (getData().getFoto() == null) {
			return false;
		} else {
			return true;
		}

	}
}
