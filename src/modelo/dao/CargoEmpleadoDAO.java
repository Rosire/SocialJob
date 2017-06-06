package modelo.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import modelo.dto.CargoEmpleado;
import modelo.dto.Usuario;
import modelo.hibernate.config.Sesion;

public class CargoEmpleadoDAO {

	private Sesion sesionPostgres;

	public CargoEmpleadoDAO() {
		super();
	}

	public void registrarCargoEmpleado(CargoEmpleado dato) {
		@SuppressWarnings("static-access")
		Session session = sesionPostgres.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.save(dato);
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			e.printStackTrace();
			throw e;
		} finally {
			session.close();
		}
	}

	public void eliminarCargosEmpleado(CargoEmpleado dato) {
		@SuppressWarnings("static-access")
		Session session = sesionPostgres.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.delete(dato);
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			e.printStackTrace();
			throw e;
		} finally {
			session.close();
		}
	}

	public List<CargoEmpleado> obtenerCargosPorEmpleado(int idEmpleado) {
		List<CargoEmpleado> cargosEmpleado = new ArrayList<CargoEmpleado>();
		Session session = sesionPostgres.openSession();
		try {
			cargosEmpleado = (List<CargoEmpleado>) session
					.createQuery(
							"FROM CargoEmpleado ce WHERE ce.empleado.id = :idEmpleado ")
					.setParameter("idEmpleado", idEmpleado).list();
		} catch (Exception e) {

			try {
				throw new Exception(e.getMessage(), e.getCause());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} finally {
			session.close();
		}
		return cargosEmpleado;
	}

	public List<CargoEmpleado> obtenerUsuariosEmpleadosActivosPorCargo(
			int idCargo) {
		List<CargoEmpleado> datos = new ArrayList<CargoEmpleado>();
		Session em = sesionPostgres.openSession();
		try {
			datos = (List<CargoEmpleado>) em
					.createQuery(
							"FROM CargoEmpleado ce WHERE ce.empleado.estatusUsuario.nombre = :estatus AND ce.cargo.id=:idCargo")
					.setParameter("estatus", "ACTIVO")
					.setParameter("idCargo", idCargo).list();
		} catch (Exception e) {
			try {
				throw new Exception(e.getMessage(), e.getCause());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			em.close();
		}

		return datos;
	}

	public List<CargoEmpleado> obtenerUsuariosEmpleadosActivosPorSupervisor(
			int idSupervisor) {
		List<CargoEmpleado> datos = new ArrayList<CargoEmpleado>();
		Session em = sesionPostgres.openSession();
		try {
			datos = (List<CargoEmpleado>) em
					.createQuery(
							"FROM CargoEmpleado ce WHERE ce.empleado.estatusUsuario.nombre = :estatus AND ce.cargo.cargoSuperior.id IN (SELECT cem.cargo.id FROM CargoEmpleado cem WHERE cem.empleado.id = :idSupervisor)")
					.setParameter("estatus", "ACTIVO")
					.setParameter("idSupervisor", idSupervisor).list();
		} catch (Exception e) {
			try {
				throw new Exception(e.getMessage(), e.getCause());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			em.close();
		}

		return datos;
	}

	public CargoEmpleado obtenerUsuarioSupervisorPorCargo(int idCargo) {
		CargoEmpleado dato = new CargoEmpleado();
		Session em = sesionPostgres.openSession();
		try {
			dato = (CargoEmpleado) em
					.createQuery(
							"FROM CargoEmpleado ce WHERE ce.empleado.estatusUsuario.nombre = :estatus AND ce.cargo.id = :idCargo")
					.setParameter("estatus", "ACTIVO")
					.setParameter("idCargo", idCargo).uniqueResult();
		} catch (Exception e) {
			try {
				throw new Exception(e.getMessage(), e.getCause());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			em.close();
		}
		return dato;
	}

	public CargoEmpleado obtenerCargoEmpledo(int idCargo, int idEmpleado) {
		CargoEmpleado dato = new CargoEmpleado();
		Session em = sesionPostgres.openSession();
		try {
			dato = (CargoEmpleado) em
					.createQuery(
							"FROM CargoEmpleado ce WHERE ce.empleado.id = :idEmpleado AND ce.cargo.id = :idCargo")
					.setParameter("idEmpleado", idEmpleado)
					.setParameter("idCargo", idCargo).uniqueResult();
		} catch (Exception e) {
			try {
				throw new Exception(e.getMessage(), e.getCause());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			em.close();
		}
		return dato;
	}

	public long obtenerCantidadCargosActivosPorEmpleadoConSupervisor(
			int idEmpleado) {
		Session session = sesionPostgres.openSession();
		long dato = 0;
		try {
			dato = (Long) session
					.createQuery(
							"SELECT COUNT(ce.id) FROM CargoEmpleado ce WHERE ce.cargo.id <> ce.cargo.cargoSuperior.id AND ce.empleado.id = :idEmpleado")
					.setParameter("idEmpleado", idEmpleado).uniqueResult();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				throw new Exception(e.getMessage(), e.getCause());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} finally {
			session.close();
		}
		return dato;
	}
	
	
	

}
