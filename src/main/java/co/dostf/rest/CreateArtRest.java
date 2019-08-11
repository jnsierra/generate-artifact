package co.dostf.rest;

import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import co.dostf.bussiness.IGenerateArtifact;
import co.dostf.bussiness.IValidateService;
import co.dostf.dto.CarpetaDespliegueDto;
import co.dostf.exception.CustomException;

@RestController
@RequestMapping("/v.1/crear-artefactos")
public class CreateArtRest {
	
	Logger logger = LoggerFactory.getLogger(CreateArtRest.class);
	
	@Autowired
	IValidateService validateService; 
	
	@Autowired
	IGenerateArtifact generateArtifact;
	
	@RequestMapping(value = "/{version}/", method = RequestMethod.GET)
	public ResponseEntity<String> ejecutarDespliegue(@PathVariable("version") String version) throws IOException, InvalidRemoteException, TransportException, GitAPIException, CustomException, InterruptedException{
		logger.info(".:: Esta es la version que debo desplegar:"+version+" ::.");
		List<CarpetaDespliegueDto> listaCarpetas = validateService.validate();
		logger.info("Este es el valor de la lista de carpetas: " + listaCarpetas);
		if(listaCarpetas != null && !listaCarpetas.isEmpty()) {
			if(!validateService.listaCarpetasValidas().isEmpty()) {
				Boolean creacion = generateArtifact.generateArtefactos(validateService.listaCarpetasValidas(), version);
				if(!creacion) {
					return  new ResponseEntity<>("No fue posible generar los artefactos",HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}else {
				return  new ResponseEntity<>("No existen carpetas validas: " + validateService.getTraceInvalidas() ,HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
		}else if(listaCarpetas.isEmpty()){
			logger.info("La lista esta vacía ");
			return new ResponseEntity<>("No existen clientes en la carpeta correspondiente",HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>("Artefactos creados correctamente",HttpStatus.OK);
	}

}
