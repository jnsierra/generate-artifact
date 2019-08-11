package co.dostf.bussiness;

import java.util.List;

import co.dostf.dto.CarpetaDespliegueDto;

public interface IDockerService {
	/**
	 * Metodo con el cual genero los archivos necesarios para la creacion de los contenedores
	 * @param clientes
	 * @return
	 */
	Boolean generateDockerCompose(List<CarpetaDespliegueDto> clientes, String version);
	
	/**
	 * Metodo con el cual ejecuta los comandos necesarios para la ejecucicon de docker
	 * @param clientes
	 * @return
	 */
	Boolean ejecutaComandosDocker(List<CarpetaDespliegueDto> clientes);

}
