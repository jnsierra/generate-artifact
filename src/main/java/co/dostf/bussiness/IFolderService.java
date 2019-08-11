package co.dostf.bussiness;

import java.io.IOException;
import java.util.List;

import co.dostf.dto.CarpetaDespliegueDto;

public interface IFolderService {
	/**
	 * Metodo con el cual por medio de una url base se obtienen todos los folders que se encuentran de dicha ubicacion
	 * @param ubicacion
	 * @return
	 */
	List<CarpetaDespliegueDto> listarCarpetas(String ubicacion) throws IOException;
	
	/**
	 * Metodo con el cual elimino los archivos correspondientes para generar el artefacto
	 * @return
	 */
	Boolean eliminaArchivosCliente() throws IOException;
	/**
	 * Metodo con el cual copio los archivos necesarios para generar los artefactos
	 * @param url
	 */
	Boolean copyFiles(String url);

}
