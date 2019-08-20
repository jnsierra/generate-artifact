package co.dostf.bussiness.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import co.dostf.bussiness.IDockerService;
import co.dostf.bussiness.IShellServices;
import co.dostf.dto.CarpetaDespliegueDto;

@Service
public class DockerService implements IDockerService {
	
	Logger logger = LoggerFactory.getLogger(DockerService.class);
	
	@Value("${pathDespliegue}")
	private String path;
	
	private StringBuilder archivo;
	
	@Value("${nexus.host}")
	private String host;
	
	@Value("${nexus.port}")
	private String port;
	
	@Value("${finalPathDockerFiles}")
	private String finalDestFiles;
	
	@Autowired
	IShellServices shellServices;
	
	public void init() {
		archivo = new StringBuilder("version: '3'\n");
		archivo.append("services:\n");
	}

	@Override
	public Boolean generateDockerCompose(List<CarpetaDespliegueDto> clientes, String version) {
		try {
			init();
			//Creacion de los servicios para los clientes
			Boolean validaProceso = creaClientes(clientes, version);
			if(!validaProceso) {
				return Boolean.FALSE;
			}
					
			//Creacion del servicio ngix
			validaProceso = creaNginxService();
			if(!validaProceso) {
				return Boolean.FALSE;
			}
			
			//Creacion de la red 
			validaProceso = creaRed();
			if(!validaProceso) {
				return Boolean.FALSE;
			}
			
			//Creacion del archivo
			validaProceso = creoArchivo();
			if(!validaProceso) {
				return Boolean.FALSE;
			}
			
			//Creacion del archivo
			validaProceso = moveFile();
			if(!validaProceso) {
				return Boolean.FALSE;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}
	/**
	 * Metodo con el cual creo un archivo
	 * @return
	 * @throws IOException 
	 */
	public Boolean creoArchivo() throws IOException {
		//Creo el directorio
		Path file = Paths.get(path.concat("docker-compose.yml"));
		if(Files.exists(file)) {
			logger.info("El archivo docker-compose.yml existe se procede a liminarlo");
			Files.delete(file);
		}
		Files.write(file, this.archivo.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING );
		return Boolean.TRUE;
	}
	
	public Boolean moveFile() throws IOException {
		
		Path srcFile = Paths.get(path.concat("docker-compose.yml"));
		Path destFile = Paths.get(finalDestFiles.concat("docker-compose.yml"));
		Files.copy(srcFile, destFile,StandardCopyOption.REPLACE_EXISTING);
		
		Set<PosixFilePermission> perms = new HashSet<PosixFilePermission>();
		 //add owners permission
        perms.add(PosixFilePermission.OWNER_READ);
        perms.add(PosixFilePermission.OWNER_WRITE);
        perms.add(PosixFilePermission.OWNER_EXECUTE);
        //add group permissions
        perms.add(PosixFilePermission.GROUP_READ);
        perms.add(PosixFilePermission.GROUP_WRITE);
        perms.add(PosixFilePermission.GROUP_EXECUTE);
        //add others permissions
        perms.add(PosixFilePermission.OTHERS_READ);
        perms.add(PosixFilePermission.OTHERS_WRITE);
        perms.add(PosixFilePermission.OTHERS_EXECUTE);
        
		Files.setPosixFilePermissions(destFile, perms);
		return Boolean.TRUE;
	}
	
	public Boolean creaNginxService() {
		archivo.append("  nginx:\n");
		archivo.append("    container_name: nginx_load_balancer\n");
		archivo.append("    image: image_nginx\n");
		archivo.append("    build:\n");
		archivo.append("      context: nginx\n");
		archivo.append("    ports:\n");
		archivo.append("       - \"5000:80\"\n");
		archivo.append("    networks:\n");
		archivo.append("      - net\n");
		return Boolean.TRUE;
	} 
	
	public Boolean creaRed() {
		archivo.append("networks:\n");
		archivo.append("  net:\n");
		return Boolean.TRUE;
	}
	
	public Boolean creaClientes(List<CarpetaDespliegueDto> clientes,String version) {
		if(clientes != null) {
			//int i = 5001;
			for(CarpetaDespliegueDto item : clientes ) {
				archivo.append("  ".concat(item.getCliente()).concat(":\n"));
				archivo.append("    container_name: container_".concat(item.getCliente().toLowerCase()).concat("\n"));
				archivo.append("    image: image_".concat(item.getCliente().toLowerCase()).concat("\n")   );
				archivo.append("    build:\n");
				archivo.append("      context: dostfdockerfile\n");
				archivo.append("      args:\n");
				archivo.append("        - URL_WAR_CUESTIONARIO=".concat(item.generateUrlNexusCuestionario(this.host, this.port, version)).concat("\n"));
				archivo.append("        - URL_WAR_INFO=".concat(item.generateUrlNexusInfo(this.host, this.port, version)).concat("\n"));
				archivo.append("    networks:\n");
				archivo.append("      - net\n");
				//i++;
			}
		}
		return Boolean.TRUE;
	}
	
	
	public Boolean ejecutaComandosDocker(List<CarpetaDespliegueDto> clientes) {
		try {
			//Paro el docker-compose
			Boolean validaProceso = stopDockerCompose();
			if(!validaProceso) {
				return Boolean.FALSE;
			}
			
			//Borro el docker-compose
			validaProceso = borrarDockerCompose();
			if(!validaProceso) {
				return Boolean.FALSE;
			}
			
			//Elimino las imagenes 
			validaProceso = borraContenedores(clientes);
			if(!validaProceso) {
				return Boolean.FALSE;
			}
			
			//Subo los contenedores
			validaProceso = subirContenedores();
			if(!validaProceso) {
				return Boolean.FALSE;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}
	
	public Boolean stopDockerCompose() throws IOException, InterruptedException {
		//Creo el comando cmd 
		String cmd = "docker-compose -f ".concat(finalDestFiles) .concat("docker-compose.yml").concat(" stop");	
		shellServices.executeShell(cmd);
		return Boolean.TRUE;
	}
	
	public Boolean borrarDockerCompose() throws IOException, InterruptedException{
		//Creo el comando cmd
		String cmd = "docker-compose -f ".concat(finalDestFiles) .concat("docker-compose.yml").concat(" rm -f");
		shellServices.executeShell(cmd);
		return Boolean.TRUE;
	}
	
	public Boolean borraContenedores(List<CarpetaDespliegueDto> clientes) throws IOException, InterruptedException {
		String contenedores = " image_nginx";
		for(CarpetaDespliegueDto item : clientes) {
			contenedores += " image_".concat(item.getCliente().toLowerCase());
		}
		String cmd = "docker image rm ".concat(contenedores);
		shellServices.executeShell(cmd);
		return Boolean.TRUE;
	}
	
	
	public Boolean subirContenedores() throws IOException, InterruptedException {
		String cmd = "docker-compose -f ".concat(finalDestFiles) .concat("docker-compose.yml").concat(" up -d ");
		shellServices.executeShell(cmd);
		return Boolean.TRUE;
	}

}
