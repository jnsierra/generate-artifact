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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import co.dostf.bussiness.INginxService;
import co.dostf.dto.CarpetaDespliegueDto;

@Service
public class NginxService implements INginxService {

	Logger logger = LoggerFactory.getLogger(NginxService.class);

	private StringBuilder archivo;

	@Value("${pathDespliegue}")
	private String path;

	@Value("${finalPathDockerFiles}")
	private String finalDestFiles;

	@Override
	public Boolean generateProxyInverse(List<CarpetaDespliegueDto> clientes) {
		try {
			init();
			// Creacion del servicio ngix
			Boolean validaProceso = createClientes(clientes);
			if (!validaProceso) {
				return Boolean.FALSE;
			}

			// Creacion del servicio ngix
			validaProceso = finish();
			if (!validaProceso) {
				return Boolean.FALSE;
			}

			// Creacion del servicio ngix
			validaProceso = creoArchivo();
			if (!validaProceso) {
				return Boolean.FALSE;
			}

			// Creacion del servicio ngix
			validaProceso = moveFile();
			if (!validaProceso) {
				return Boolean.FALSE;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	public void init() {
		// iniciamos el archivo
		archivo = new StringBuilder("server {\n");
		archivo.append("    listen       80;\n");
		archivo.append("    server_name  localhost;\n");
	}

	public Boolean createClientes(List<CarpetaDespliegueDto> clientes) {

		archivo.append("\n");

		for (CarpetaDespliegueDto item : clientes) {
			archivo.append("\n");
			archivo.append("    location /" + item.getCliente().toLowerCase() + " {\n");
			archivo.append("      rewrite /" + item.getCliente().toLowerCase() + "/(.*) /$1  break;\n");
			archivo.append("      proxy_pass        http://" + item.getCliente() + ":8080;\n");
			archivo.append("      proxy_redirect    off;\n");
			archivo.append("      proxy_set_header  Host $host;\n");
			archivo.append("      proxy_set_header  X-Real-IP $remote_addr;\n");
			archivo.append("      proxy_set_header  X-Forwarded-For $proxy_add_x_forwarded_for;\n");
			archivo.append("      proxy_set_header  X-Forwarded-Host $server_name;\n");
			archivo.append("    }");
			archivo.append("\n");
		}

		archivo.append("\n");
		return Boolean.TRUE;
	}

	public Boolean finish() {
		archivo.append("}");
		return Boolean.TRUE;
	}

	/**
	 * Metodo con el cual creo un archivo
	 * 
	 * @return
	 * @throws IOException
	 */
	public Boolean creoArchivo() throws IOException {
		// Creo el directorio
		Path file = Paths.get(path.concat("default.conf"));
		if (Files.exists(file)) {
			logger.info("El archivo  default.conf existe se procede a liminarlo");
			Files.delete(file);
		}
		Files.write(file, this.archivo.toString().getBytes(), StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING);
		return Boolean.TRUE;
	}

	public Boolean moveFile() throws IOException {

		Path srcFile = Paths.get(path.concat("default.conf"));
		Path destFile = Paths.get(finalDestFiles.concat("nginx/default.conf"));
		Files.copy(srcFile, destFile, StandardCopyOption.REPLACE_EXISTING);

		Set<PosixFilePermission> perms = new HashSet<PosixFilePermission>();
		// add owners permission
		perms.add(PosixFilePermission.OWNER_READ);
		perms.add(PosixFilePermission.OWNER_WRITE);
		perms.add(PosixFilePermission.OWNER_EXECUTE);
		// add group permissions
		perms.add(PosixFilePermission.GROUP_READ);
		perms.add(PosixFilePermission.GROUP_WRITE);
		perms.add(PosixFilePermission.GROUP_EXECUTE);
		// add others permissions
		perms.add(PosixFilePermission.OTHERS_READ);
		perms.add(PosixFilePermission.OTHERS_WRITE);
		perms.add(PosixFilePermission.OTHERS_EXECUTE);

		Files.setPosixFilePermissions(destFile, perms);
		return Boolean.TRUE;
	}

}
