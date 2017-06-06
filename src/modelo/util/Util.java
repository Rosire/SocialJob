package modelo.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Executions;

/**
 * Util class to upload files
 */

public class Util {

	public static final String separator = System.getProperty("file.separator");// Get
																				// de
																				// system
																				// separator

	public static boolean uploadFile(Media media, String cedulaEmpleado) {
		return saveFile(media, getPath(), cedulaEmpleado);

	}

	// Gets the path of the current web application
	public static String getPath() {
		return Executions.getCurrent().getDesktop().getWebApp()
				.getRealPath(separator)
				+ "uploads" + separator;
	}

	// save file
	public static boolean saveFile(Media media, String path,
			String cedulaEmpleado) {
		
		boolean uploaded = false;
		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		try {
			InputStream ins = media.getStreamData();
			in = new BufferedInputStream(ins);
			String typeFoto = media.getContentType();
			String extension = typeFoto.substring(typeFoto.indexOf("/") + 1);
			String fileName = cedulaEmpleado + "." + extension;
			File arc = new File(
					"D:/Rosiré/workspace/socialJob/WebContent/uploads/"
							+ fileName);
			OutputStream aout = new FileOutputStream(arc);
			out = new BufferedOutputStream(aout);
			byte buffer[] = new byte[1024];
			int read = in.read(buffer);
			while (read != -1) {
				out.write(buffer, 0, read);
				read = in.read(buffer);
			}
			uploaded = true;
		} catch (IOException ie) {
			throw new RuntimeException(ie);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (out != null)
					out.close();
				if (in != null)
					in.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return uploaded;
	}

	public static boolean borrar(String fileName) {

		Path path = FileSystems.getDefault().getPath(
				"D:/Rosiré/workspace/socialJob/WebContent", fileName);

		try {
			if (Files.deleteIfExists(path)) {
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
