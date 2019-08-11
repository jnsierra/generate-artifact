package co.dostf.bussiness;

import java.io.IOException;
import java.util.List;

import co.dostf.dto.CarpetaDespliegueDto;

public interface IValidateService {
	/**
	 * Metodo con el cual valido las precondiciones para la creacion de artefactos
	 * @return
	 */
	List<CarpetaDespliegueDto> validate()throws IOException;
	/**
	 * Metodo con el cual listo las carpetas validas
	 * @return
	 * @throws IOException
	 */
	List<CarpetaDespliegueDto> listaCarpetasValidas()throws IOException;
	/**
	 * Metodo con el cual obtengo la lista de archivos invalidos
	 * @return
	 * @throws IOException
	 */
	List<CarpetaDespliegueDto> listaCarpetasInValidas()throws IOException;
	/**
	 * Metodo con el cual extraigo los errores encontradas en la validacion
	 * @return
	 * @throws IOException
	 */
	String getTraceInvalidas() throws IOException;
	

}
