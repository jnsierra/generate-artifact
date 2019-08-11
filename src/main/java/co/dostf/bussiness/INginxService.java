package co.dostf.bussiness;

import java.util.List;

import co.dostf.dto.CarpetaDespliegueDto;

public interface INginxService {
	
	Boolean generateProxyInverse(List<CarpetaDespliegueDto> clientes);

}
