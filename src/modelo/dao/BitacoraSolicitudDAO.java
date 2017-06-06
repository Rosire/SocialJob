package modelo.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import modelo.dto.BitacoraSolicitud;
import modelo.dto.BitacoraTareaAsignada;
import modelo.hibernate.config.Sesion;

public class BitacoraSolicitudDAO {

	private Sesion sesionPostgres;

	public BitacoraSolicitudDAO() {
		super();
	}

	public void registrarBitacoraSolicitud(BitacoraSolicitud dato) {
		@SuppressWarnings("static-access")
		Session em = sesionPostgres.openSession();
		Transaction tx = null;
		try {
			tx = em.beginTransaction();
			em.save(dato);
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			e.printStackTrace();
			throw e;
		} finally {
			em.close();
		}
	}

	public BitacoraSolicitud obtenerBitacoraSolicitud(int id) throws Exception {
		@SuppressWarnings("static-access")
		Session sesion = sesionPostgres.openSession();
		BitacoraSolicitud dato = null;
		try {
			dato = (BitacoraSolicitud) sesion.get(BitacoraSolicitud.class, id);
		} catch (Exception e) {
			e.printStackTrace();

			throw new Exception(e.getMessage(), e.getCause());
		} finally {
			sesion.close();
		}

		return dato;
	}

	public List<BitacoraSolicitud> obtenerBitacorasPorSolicitud(int idSolicitud) {

		List<BitacoraSolicitud> datos = new ArrayList<BitacoraSolicitud>();
		Session em = sesionPostgres.openSession();
		try {
			datos = (List<BitacoraSolicitud>) em
					.createQuery(
							"FROM BitacoraSolicitud bs WHERE bs.solicitud.id = :idSolicitud ")
					.setParameter("idSolicitud", idSolicitud).list();
		} catch (Exception e) {

			throw e;
		} finally {
			em.close();
		}

		return datos;
	}
}
