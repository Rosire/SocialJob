package modelo.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import modelo.dto.Mensaje;
import modelo.hibernate.config.Sesion;

public class MensajeDAO {

	private Sesion sesionPostgres;

	public MensajeDAO() {
		super();
	}

	public void registrarMensaje(Mensaje dato) {
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

	public void actualizarMensaje(Mensaje dato) {
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

	public Mensaje obtenerMensaje(int id) {
		@SuppressWarnings("static-access")
		Session sesion = sesionPostgres.openSession();
		Mensaje dato = null;
		try {
			dato = (Mensaje) sesion.get(Mensaje.class, id);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			sesion.close();
		}
		return dato;
	}

	@SuppressWarnings({ "unchecked", "static-access" })
	public List<Mensaje> obtenerMensajesEnviados(int idUsuarioRemitente) {
		List<Mensaje> datos = new ArrayList<Mensaje>();
		Session session = sesionPostgres.openSession();

		try {
			datos = (List<Mensaje>) session
					.createQuery(
							"FROM Mensaje m WHERE m.usuarioRemitente.id = :idUsuarioRemitente")
					.setParameter("idUsuarioRemitente", idUsuarioRemitente)
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

	@SuppressWarnings({ "unchecked", "static-access" })
	public List<Mensaje> obtenerMensajesRecibidos(int idUsuarioDestinatario) {
		List<Mensaje> datos = new ArrayList<Mensaje>();
		Session session = sesionPostgres.openSession();
		try {
			datos = (List<Mensaje>) session
					.createQuery(
							"FROM Mensaje m WHERE m.usuarioDestinatario.id = :idUsuarioDestinatario")
					.setParameter("idUsuarioDestinatario",
							idUsuarioDestinatario).list();
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

	@SuppressWarnings({ "unchecked", "static-access" })
	public List<Mensaje> obtenerMensajesRecibidosPorEstatus(
			int idUsuarioDestinatario, int idEstatusMensaje) {
		List<Mensaje> datos = new ArrayList<Mensaje>();
		Session session = sesionPostgres.openSession();
		try {
			datos = (List<Mensaje>) session
					.createQuery(
							"FROM Mensaje m WHERE m.usuarioDestinatario.id = :idUsuarioDestinatario AND m.estatusMensaje.id= :idEstatusMensaje")
					.setParameter("idUsuarioDestinatario",
							idUsuarioDestinatario)
					.setParameter("idEstatusMensaje", idEstatusMensaje).list();
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

	@SuppressWarnings({ "unchecked", "static-access" })
	public List<Mensaje> obtenerMensajesRecibidosPorEstatusNombre(
			int idUsuarioDestinatario, String nombreEstatus) {
		List<Mensaje> datos = new ArrayList<Mensaje>();
		Session session = sesionPostgres.openSession();
		try {
			datos = (List<Mensaje>) session
					.createQuery(
							"FROM Mensaje m WHERE m.usuarioDestinatario.id = :idUsuarioDestinatario AND m.estatusMensaje.nombre= :nombreEstatus")
					.setParameter("idUsuarioDestinatario",
							idUsuarioDestinatario)
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

	@SuppressWarnings({ "unchecked", "static-access" })
	public List<Mensaje> obtenerMensajesRecibidosPorLeerLimite(
			int idUsuarioDestinatario) {
		List<Mensaje> datos = new ArrayList<Mensaje>();
		Session session = sesionPostgres.openSession();
		try {
			datos = (List<Mensaje>) session
					.createQuery(
							"FROM Mensaje m WHERE m.usuarioDestinatario.id = :idUsuarioDestinatario AND m.estatusMensaje.nombre= :nombreEstatus ORDER BY m.fecha DESC LIMIT 3")
					.setParameter("idUsuarioDestinatario",
							idUsuarioDestinatario)
					.setParameter("nombreEstatus", "POR LEER").list();
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

	@SuppressWarnings("static-access")
	public long obtenerCantidadMensajesRecibidosPorLeer(
			int idUsuarioDestinatario) {
		Session session = sesionPostgres.openSession();
		long dato = 0;
		try {
			dato = (Long) session
					.createQuery(
							"SELECT COUNT(m.id) FROM Mensaje m WHERE m.usuarioDestinatario.id = :idUsuarioDestinatario AND m.estatusMensaje.nombre= :nombreEstatus ")
					.setParameter("idUsuarioDestinatario",
							idUsuarioDestinatario)
					.setParameter("nombreEstatus", "POR LEER").uniqueResult();
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
