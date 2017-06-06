package modelo.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import modelo.dto.Solicitud;
import modelo.hibernate.config.Sesion;

public class SolicitudDAO {

	private Sesion sesionPostgres;

	public SolicitudDAO() {
		super();
	}

	public void registrarSolicitud(Solicitud dato) {
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

	public void actualizarSolicitud(Solicitud dato) {
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

	public List<Solicitud> obtenerSolicitudes() {
		List<Solicitud> datos = new ArrayList<Solicitud>();
		Session session = sesionPostgres.openSession();
		try {
			datos = (List<Solicitud>) session.createCriteria(Solicitud.class)
					.list();
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

	public Solicitud obtenerSolicitud(int id) {
		@SuppressWarnings("static-access")
		Session sesion = sesionPostgres.openSession();
		Solicitud dato = null;
		try {
			dato = (Solicitud) sesion.get(Solicitud.class, id);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				throw new Exception(e.getMessage(), e.getCause());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} finally {
			sesion.close();
		}
		return dato;
	}

	public List<Solicitud> obtenerSolicitudesEnviadasPorEmpleado(int idEmpleado) {
		List<Solicitud> datos = new ArrayList<Solicitud>();
		Session session = sesionPostgres.openSession();
		try {
			datos = (List<Solicitud>) session
					.createQuery(
							"FROM Solicitud s WHERE s.empleadoSolicitud.id=:idEmpleado")
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

	public List<Solicitud> obtenerSolicitudesEnviadasPorEmpleadoPorEstatusNombre(
			int idEmpleado, String nombreEstatus) {
		List<Solicitud> datos = new ArrayList<Solicitud>();
		Session session = sesionPostgres.openSession();
		try {
			datos = (List<Solicitud>) session
					.createQuery(
							"FROM Solicitud s WHERE s.empleadoSolicitud.id=:idEmpleado AND s.estatusSolicitud.nombre=:nombreEstatus")
					.setParameter("idEmpleado", idEmpleado)
					.setParameter("nombreEstatus", nombreEstatus).list();
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

	public List<Solicitud> obtenerSolicitudesEnviadasPorEmpleadoPorEstatus(
			int idEmpleado, int idEstatusSolicitud) {
		List<Solicitud> datos = new ArrayList<Solicitud>();
		Session session = sesionPostgres.openSession();
		try {
			datos = (List<Solicitud>) session
					.createQuery(
							"FROM Solicitud s WHERE s.empleadoSolicitud.id=:idEmpleado AND s.estatusSolicitud.id=:idEstatusSolicitud")
					.setParameter("idEmpleado", idEmpleado)
					.setParameter("idEstatusSolicitud", idEstatusSolicitud)
					.list();
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

	public List<Solicitud> obtenerSolicitudesRecibidasPorEmpleadoPorEstatusNombre(
			int idSupervisor, String nombreEstatus) {
		List<Solicitud> datos = new ArrayList<Solicitud>();
		Session session = sesionPostgres.openSession();
		try {
			datos = (List<Solicitud>) session
					.createQuery(
							"FROM Solicitud s WHERE s.estatusSolicitud.nombre=:nombreEstatus AND s.cargoTarea.cargoSuperior.id IN (SELECT ce.cargo.id FROM CargoEmpleado ce WHERE ce.empleado.id = :idSupervisor)")
					.setParameter("idSupervisor", idSupervisor)
					.setParameter("nombreEstatus", nombreEstatus).list();
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

	public List<Solicitud> obtenerSolicitudesRecibidasPorEmpleado(
			int idSupervisor) {
		List<Solicitud> datos = new ArrayList<Solicitud>();
		Session session = sesionPostgres.openSession();
		try {
			datos = (List<Solicitud>) session
					.createQuery(
							"FROM Solicitud s WHERE s.cargoTarea.cargoSuperior.id IN (SELECT ce.cargo.id FROM CargoEmpleado ce WHERE ce.empleado.id = :idSupervisor)")
					.setParameter("idSupervisor", idSupervisor).list();
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

}