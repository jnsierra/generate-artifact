package co.dostf.bussiness.impl;

import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.dostf.bussiness.IDockerService;
import co.dostf.bussiness.IFolderService;
import co.dostf.bussiness.IGenerateArtifact;
import co.dostf.bussiness.IGitServices;
import co.dostf.bussiness.IMavenService;
import co.dostf.bussiness.INexusServices;
import co.dostf.bussiness.INginxService;
import co.dostf.dto.CarpetaDespliegueDto;
import co.dostf.exception.CustomException;
import lombok.Getter;
import lombok.Setter;

@Service
@Getter
@Setter
public class GenerateArtifact implements IGenerateArtifact {

	Logger logger = LoggerFactory.getLogger(GenerateArtifact.class);

	@Autowired
	IGitServices gitServices;

	@Autowired
	IMavenService mavenService;

	@Autowired
	IFolderService folderService;

	@Autowired
	INexusServices nexusServices;

	@Autowired
	IDockerService dockerService;
	
	@Autowired
	INginxService nginxService;

	private List<CarpetaDespliegueDto> listArtefactos;

	@Override
	public Boolean generateArtefactos(List<CarpetaDespliegueDto> listArtefactos, String version)
			throws InvalidRemoteException, TransportException, GitAPIException, IOException, CustomException, InterruptedException {
		Boolean validarProceso = Boolean.TRUE;
		// Seteo lista de artefactos
		setListArtefactos(listArtefactos);
		// Genero el codigo por cada cliente
		if (listArtefactos != null && gitServices.descargarCodigo()) {
			logger.info("Ciclo para clientes: No. de clientes " + listArtefactos.size());
			for (CarpetaDespliegueDto item : listArtefactos) {
				logger.info("Se inicia con el proceso con el cliente: " + item.getCliente());
				if (!generateProceso(item, version)) {
					return Boolean.FALSE;
				}
			}
			// Genero el archivo docker compose
			logger.info("Genero archivo docker-compose.yml");
			validarProceso = dockerService.generateDockerCompose(listArtefactos, version);
			if (!validarProceso) {
				return Boolean.FALSE;
			}
			logger.info("Se procede a generar el archivo default.conf");
			validarProceso = nginxService.generateProxyInverse(listArtefactos);
			if (!validarProceso) {
				return Boolean.FALSE;
			}
			
			logger.info("Se procede a generar los comandos necesarios para generar contenedores");
			validarProceso = dockerService.ejecutaComandosDocker(listArtefactos);
			if (!validarProceso) {
				return Boolean.FALSE;
			}
		}
		return Boolean.TRUE;
	}

	private Boolean generateProceso(CarpetaDespliegueDto carpeta, String version) throws IOException, InterruptedException {
		logger.info("Limpiando el codigo");
		// Limpio el codigo antes de copiar los archivos necesarios para su construccion
		Boolean validarProceso;
		validarProceso = mavenService.limpiarCodigo();
		if (!validarProceso) {
			return Boolean.FALSE;
		}

		logger.info("Eliminando archivos del codigo");
		// Elimino los archivos que debo remplazar por cada cliente
		validarProceso = folderService.eliminaArchivosCliente();
		if (!validarProceso) {
			return Boolean.FALSE;
		}
		logger.info("Copiando los archivos");
		// Copio los archivos desde el repositorio y los pongo en su correspondiente
		// proyecto
		validarProceso = folderService.copyFiles(carpeta.getUrl());
		if (!validarProceso) {
			return Boolean.FALSE;
		}
		logger.info("Haciendo install");
		// Genero los artefactos .war de las aplicaciones
		validarProceso = mavenService.instalarCodigo();
		if (!validarProceso) {
			return Boolean.FALSE;
		}
		logger.info("Subir codigo a nexus");
		// Subo los artefactos .war de las aplicaciones
		validarProceso = nexusServices.subirCodigo(carpeta, version);
		if (!validarProceso) {
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}
}