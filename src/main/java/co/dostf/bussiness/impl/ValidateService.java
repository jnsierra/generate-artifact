package co.dostf.bussiness.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import co.dostf.bussiness.IFolderService;
import co.dostf.bussiness.IValidateService;
import co.dostf.dto.CarpetaDespliegueDto;
import lombok.Getter;
import lombok.Setter;

@Service
@Getter
@Setter
public class ValidateService implements IValidateService {

	@Autowired
	IFolderService folderService;

	Logger logger = LoggerFactory.getLogger(ValidateService.class);

	List<CarpetaDespliegueDto> listCarpeta;
	
	@Value("${pathDespliegue}")
	public String CARPETA_PRINCIPAL;

	@Override
	public List<CarpetaDespliegueDto> validate() throws IOException {
		setListCarpeta(folderService.listarCarpetas(CARPETA_PRINCIPAL));
		validateListArchivos();
		return getListCarpeta();
	}

	/**
	 * Metodo el cual valida si extien los archivos
	 */
	private void validateListArchivos() {
		if (getListCarpeta() != null && !getListCarpeta().isEmpty()) {
			setListCarpeta(getListCarpeta().stream().parallel()
					.map(item -> validaArchivosDespliegue(item))
					.collect(Collectors.toList()));
		}
	}

	private CarpetaDespliegueDto validaArchivosDespliegue(CarpetaDespliegueDto carpeta) {
		logger.info("Url en la cual se va ha buscar: " + carpeta.getUrl());
		//client-config.wsdd
		File config = new File(carpeta.getUrl().concat("/cuest_client-config.wsdd"));
		if(!config.exists()) {
			carpeta.setValido(Boolean.FALSE);
			carpeta.setMotivo("No existe el archivo wsdd cuestinoario cuest_client-config");
			return carpeta;
		}
		File configDos = new File(carpeta.getUrl().concat("/info_client-config.wsdd"));
		if(!configDos.exists()) {
			carpeta.setValido(Boolean.FALSE);
			carpeta.setMotivo("No existe el archivo wsdd info info_client-config");
			return carpeta;
		}
		File configTres = new File(carpeta.getUrl().concat("/prosp_client-config.wsdd"));
		if(!configTres.exists()) {
			carpeta.setValido(Boolean.FALSE);
			carpeta.setMotivo("No existe el archivo wsdd info prosp_client-config");
			return carpeta;
		}
		
		//configurationInfo.properties
		File propertiesInfo = new File(carpeta.getUrl().concat("/cuest_configuration.properities"));
		if(!propertiesInfo.exists()) {
			carpeta.setValido(Boolean.FALSE);
			carpeta.setMotivo("No existe el archivo cuest_configuration.properties");
			return carpeta;
		}
		
		File properties = new File(carpeta.getUrl().concat("/info_configurationInfo.properities"));
		if(!properties.exists()) {
			carpeta.setValido(Boolean.FALSE);
			carpeta.setMotivo("No existe el archivo  info_configurationInfo.properties");
			return carpeta;
		}
		File propertiesPros = new File(carpeta.getUrl().concat("/prosp_configurationInfo.properities"));
		if(!propertiesPros.exists()) {
			carpeta.setValido(Boolean.FALSE);
			carpeta.setMotivo("No existe el archivo  prosp_configurationInfo.properties");
			return carpeta;
		}
		
		//seguridad.properties
		File security = new File(carpeta.getUrl().concat("/cuest_seguridad.properties"));
		if(!security.exists()) {
			carpeta.setValido(Boolean.FALSE);
			carpeta.setMotivo("No existe el archivo cuest_seguridad.properties");
			return carpeta;
		}
		
		File securityDos = new File(carpeta.getUrl().concat("/info_seguridad.properties"));
		if(!securityDos.exists()) {
			carpeta.setValido(Boolean.FALSE);
			carpeta.setMotivo("No existe el archivo info_seguridad.properties");
			return carpeta;
		}
		File securityTres = new File(carpeta.getUrl().concat("/prosp_seguridad.properties"));
		if(!securityTres.exists()) {
			carpeta.setValido(Boolean.FALSE);
			carpeta.setMotivo("No existe el archivo prosp_seguridad.properties");
			return carpeta;
		}
		return carpeta;
	}

	@Override
	public List<CarpetaDespliegueDto> listaCarpetasValidas() throws IOException {
		if(getListCarpeta()==null)
			return new ArrayList<CarpetaDespliegueDto>();
		return getListCarpeta()
				.stream()
				.parallel()
				.filter(item -> item.getValido())
				.collect(Collectors.toList());
	}

	@Override
	public List<CarpetaDespliegueDto> listaCarpetasInValidas() throws IOException {
		if(getListCarpeta()==null)
			return new ArrayList<CarpetaDespliegueDto>();
		return getListCarpeta()
				.stream()
				.parallel()
				.filter(item -> !item.getValido())
				.collect(Collectors.toList());
	}
	
	
	public String getTraceInvalidas() throws IOException {
		Optional<String> valor = listaCarpetasInValidas()
		.stream()
		.parallel()
		.map(item -> "[ cliente= " + item.getCliente() +", error= "+ item.getMotivo() + "]," )
		.reduce((x,y) -> x+y);
		if(valor.isPresent()) {
			return valor.get();
		}else {
		    return "[Todos los valores son validos]";
		}
		
	}

}