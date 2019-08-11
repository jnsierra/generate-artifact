package co.dostf.bussiness;

import java.io.IOException;

import co.dostf.dto.CarpetaDespliegueDto;

public interface IMavenService {
	/**
	 * Metodo con el cual limpio los artefactos necesarios
	 * @param url
	 * @return
	 */
	Boolean limpiarCodigo() throws IOException, InterruptedException ;
	/**
	 * Metodo con el cual genero los artefactos
	 * @param url
	 * @return
	 * @throws IOException
	 */
	Boolean instalarCodigo() throws IOException, InterruptedException ;
	/**
	 * Metodo con el cual subo el artefacto a Nexus
	 * @return
	 */
	Boolean subirArtefacto(CarpetaDespliegueDto carpeta,String urlNexus, String versio) throws IOException, InterruptedException ;

}
