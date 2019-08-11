package co.dostf.bussiness;

import java.io.IOException;

import co.dostf.dto.CarpetaDespliegueDto;

public interface INexusServices {
	/**
	 * Metodo con el cual se suben los artefactos a nexus
	 * @return
	 */
	Boolean subirCodigo(CarpetaDespliegueDto carpeta, String version)throws IOException, InterruptedException;

}
