package modelo.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import modelo.dto.Cargo;
import modelo.hibernate.config.Sesion;

public class CargoDAO {

	private Sesion sesionPostgres;

	public CargoDAO() {
		super();
	}

	public void registrarCargo(Cargo dato) {
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

	public void actualizarCargo(Cargo dato) {
		@SuppressWarnings("static-access")
		Session em = sesionPostgres.openSession();
		Transaction tx = null;
		try {
			tx = em.beginTransaction();
			em.update(dato);
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			e.printStackTrace();
			throw e;
		} finally {
			em.close();
		}
	}

	public void cambioCargo(int idCargoCambio, int idCargoAnterior) {
		@SuppressWarnings("static-access")
		Session em = sesionPostgres.openSession();
		Transaction tx = null;
		try {
			tx = em.beginTransaction();
			String hqlUpdate = "update cargo c set c.idcargosuperior = :idCargoCambio where c.idcargosuperior = :idCargoAnterior";

			int updatedEntities = em.createSQLQuery(hqlUpdate)
					.setParameter("idCargoCambio", idCargoCambio)
					.setParameter("idCargoAnterior", idCargoAnterior)
					.executeUpdate();
			System.out.println(updatedEntities);
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			e.printStackTrace();
			throw e;
		} finally {
			em.close();
		}
	}

	public Cargo obtenerCargo(int id) {
		@SuppressWarnings("static-access")
		Session sesion = sesionPostgres.openSession();
		Cargo dato = null;
		try {
			dato = (Cargo) sesion.get(Cargo.class, id);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			sesion.close();
		}
		return dato;
	}

	public List<Cargo> obtenerCargosPorEmpresa(int idEmpresa) {

		List<Cargo> datos = new ArrayList<Cargo>();
		Session em = sesionPostgres.openSession();
		try {
			datos = (List<Cargo>) em
					.createQuery("FROM Cargo c WHERE c.empresa.id = :idEmpresa")
					.setParameter("idEmpresa", idEmpresa).list();
		} catch (Exception e) {

			throw e;
		} finally {
			em.close();
		}

		return datos;
	}

	public List<Cargo> obtenerCargosActivosPorEmpresa(int idEmpresa) {
		List<Cargo> datos = new ArrayList<Cargo>();
		Session em = sesionPostgres.openSession();
		try {
			datos = (List<Cargo>) em
					.createQuery(
							"FROM Cargo c WHERE c.estatusCargo.nombre = 'ACTIVO' AND c.empresa.id = :idEmpresa ORDER BY c.cargoSuperior.id")
					.setParameter("idEmpresa", idEmpresa).list();
		} catch (Exception e) {
			throw e;
		} finally {
			em.close();
		}

		return datos;
	}

	public List<Cargo> obtenerCargosActivosDisponiblesPorEmpresa(int idEmpresa) {
		List<Cargo> datos = new ArrayList<Cargo>();
		Session em = sesionPostgres.openSession();
		try {
			datos = (List<Cargo>) em
					.createQuery(
							"FROM Cargo c WHERE c.estatusCargo.nombre = 'ACTIVO' AND c.empresa.id = :idEmpresa AND c.id NOT IN ( SELECT ca.cargoSuperior.id FROM Cargo ca WHERE ca.cargoSuperior.id IN (SELECT ce.cargo.id FROM CargoEmpleado ce ))")
					.setParameter("idEmpresa", idEmpresa).list();
		} catch (Exception e) {
			throw e;
		} finally {
			em.close();
		}

		return datos;
	}

	public List<Cargo> obtenerCargosActivosDisponiblesPorEmpresaIncluyendoElDelUsuario(
			int idEmpresa, int idEmpleado) {
		List<Cargo> datos = new ArrayList<Cargo>();
		Session em = sesionPostgres.openSession();
		try {
			datos = (List<Cargo>) em
					.createQuery(
							"FROM Cargo c WHERE c.estatusCargo.nombre = 'ACTIVO' AND c.empresa.id = :idEmpresa AND c.id NOT IN ( SELECT ca.cargoSuperior.id FROM Cargo ca WHERE ca.cargoSuperior.id IN (SELECT ce.cargo.id FROM CargoEmpleado ce WHERE ce.empleado.id <> :idEmpleado))")
					.setParameter("idEmpresa", idEmpresa)
					.setParameter("idEmpleado", idEmpleado).list();
		} catch (Exception e) {
			throw e;
		} finally {
			em.close();
		}

		return datos;
	}

	public List<Cargo> obtenerCargosInactivosPorEmpresa(int idEmpresa) {
		List<Cargo> datos = new ArrayList<Cargo>();
		Session em = sesionPostgres.openSession();
		try {
			datos = (List<Cargo>) em
					.createQuery(
							"FROM Cargo c WHERE c.estatusCargo.nombre = 'INACTIVO' AND c.empresa.id = :idEmpresa")
					.setParameter("idEmpresa", idEmpresa).list();

		} catch (Exception e) {
			throw e;
		} finally {
			em.close();
		}

		return datos;
	}

	public List<Cargo> obtenerCargosActivosDistintoPorEmpresa(int id,
			int idEmpresa) {
		List<Cargo> datos = new ArrayList<Cargo>();
		Session session = sesionPostgres.openSession();
		try {
			datos = (List<Cargo>) session
					.createQuery(
							"FROM Cargo c WHERE c.estatusCargo.nombre = 'ACTIVO' AND c.id <> :id AND c.empresa.id = :idEmpresa")
					.setParameter("id", id)
					.setParameter("idEmpresa", idEmpresa).list();
		} catch (Exception e) {

			throw e;
		} finally {
			session.close();
		}

		return datos;
	}

	public Cargo obtenerCargoPorNombrePorEmpresa(String nombre, int idEmpresa) {
		@SuppressWarnings("static-access")
		Session session = sesionPostgres.openSession();
		Cargo dato = null;
		try {
			dato = (Cargo) session
					.createQuery(
							"FROM Cargo c WHERE c.nombre = :nombre AND c.empresa.id = :idEmpresa")
					.setParameter("nombre", nombre)
					.setParameter("idEmpresa", idEmpresa).uniqueResult();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			session.close();
		}
		return dato;
	}

	public List<Cargo> obtenerCargosActivosConSupervisorDistintoActualPorEmpresa(
			int idEmpleado, int idEmpresa) {
		List<Cargo> datos = new ArrayList<Cargo>();
		Session session = sesionPostgres.openSession();
		try {
			datos = (List<Cargo>) session
					.createQuery(
							"FROM Cargo c WHERE c.empresa.id = :idEmpresa AND c.cargoSuperior.id <> c.id AND c.estatusCargo.nombre = 'ACTIVO' AND c.cargoSuperior.id NOT IN (SELECT ce.cargo.id FROM CargoEmpleado ce WHERE ce.empleado.id = :idEmpleado) AND c.cargoSuperior.id  IN (SELECT DISTINCT ce.cargo.id FROM CargoEmpleado ce ) ")
					.setParameter("idEmpleado", idEmpleado)
					.setParameter("idEmpresa", idEmpresa).list();
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

		return datos;
	}

	public List<Cargo> obtenerCargosSubalternosPorEmpresa(int idEmpresa,
			int idCargo) {
		List<Cargo> datos = new ArrayList<Cargo>();
		Session session = sesionPostgres.openSession();
		try {
			datos = (List<Cargo>) session
					.createQuery(
							"FROM Cargo c WHERE c.empresa.id = :idEmpresa AND c.cargoSuperior.id <> c.id AND c.id <> :idCargo AND c.estatusCargo.nombre = 'ACTIVO' AND c.id NOT IN (SELECT ca.cargoSuperior.id FROM Cargo ca ) ")
					.setParameter("idEmpresa", idEmpresa)
					.setParameter("idCargo", idCargo).list();
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

		return datos;
	}

	public List<Cargo> obtenerCargosSupervisoresYSubAlternosConUnEmpleadoPorEmpresa(
			int idEmpresa, int idCargo) {
		List<Cargo> datos = new ArrayList<Cargo>();
		Session session = sesionPostgres.openSession();
		try {
			datos = (List<Cargo>) session
					.createQuery(
							"FROM Cargo c WHERE c.empresa.id = :idEmpresa AND c.id <> :idCargo AND c.estatusCargo.nombre = 'ACTIVO' AND (c.id IN (SELECT ca.cargoSuperior.id FROM Cargo ca ) OR ( (SELECT COUNT(ce.id) FROM CargoEmpleado ce WHERE ce.cargo.id = c.id) <= 1) )")

					.setParameter("idEmpresa", idEmpresa)
					.setParameter("idCargo", idCargo).list();
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

		return datos;
	}

	public List<Cargo> obtenerCargosActivosConSupervisorIguaActualPorEmpresa(
			int idEmpleado, int idEmpresa) {
		List<Cargo> datos = new ArrayList<Cargo>();
		Session session = sesionPostgres.openSession();
		try {
			datos = (List<Cargo>) session
					.createQuery(
							"FROM Cargo c WHERE c.empresa.id = :idEmpresa AND c.cargoSuperior.id <> c.id AND c.estatusCargo.nombre = 'ACTIVO' AND c.cargoSuperior.id IN (SELECT ce.cargo.id FROM CargoEmpleado ce WHERE ce.empleado.id = :idEmpleado)")
					.setParameter("idEmpleado", idEmpleado)
					.setParameter("idEmpresa", idEmpresa).list();
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
		return datos;
	}

	public List<Cargo> obtenerCargosActivosConSupervisoresPorEmpresa(
			int idEmpresa) {
		List<Cargo> datos = new ArrayList<Cargo>();
		Session session = sesionPostgres.openSession();
		try {
			datos = (List<Cargo>) session
					.createQuery(
							"FROM Cargo c WHERE c.empresa.id = :idEmpresa AND c.cargoSuperior.id <> c.id AND c.estatusCargo.nombre = 'ACTIVO'")
					.setParameter("idEmpresa", idEmpresa).list();
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
		return datos;
	}

	public List<Cargo> obtenerCargosPorEmpleado(int idEmpleado) {
		List<Cargo> datos = new ArrayList<Cargo>();
		Session session = sesionPostgres.openSession();
		try {
			datos = (List<Cargo>) session
					.createQuery(
							"FROM Cargo c WHERE c.id IN (SELECT ce.cargo.id FROM CargoEmpleado ce WHERE ce.empleado.id = :idEmpleado)")
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
		return datos;
	}

	public List<Cargo> obtenerCargosSubalternos(int idCargoSuperior) {

		List<Cargo> datos = new ArrayList<Cargo>();
		Session em = sesionPostgres.openSession();
		try {
			datos = (List<Cargo>) em
					.createQuery(
							"FROM Cargo c WHERE c.cargoSuperior.id = :idCargoSuperior AND c.id <> c.cargoSuperior.id AND c.estatusCargo.nombre = 'ACTIVO'")
					.setParameter("idCargoSuperior", idCargoSuperior).list();
		} catch (Exception e) {

			throw e;
		} finally {
			em.close();
		}

		return datos;
	}

	public Cargo obtenerCargosMatrizPorEmpresa(int idEmpresa) {

		Cargo datos = null;
		Session em = sesionPostgres.openSession();
		try {
			datos = (Cargo) em
					.createQuery(
							"FROM Cargo c WHERE c.empresa.id = :idEmpresa AND c.id = c.cargoSuperior.id AND c.estatusCargo.nombre = 'ACTIVO' order by c.id")
					.setParameter("idEmpresa", idEmpresa).uniqueResult();
		} catch (Exception e) {

			throw e;
		} finally {
			em.close();
		}

		return datos;
	}

}
