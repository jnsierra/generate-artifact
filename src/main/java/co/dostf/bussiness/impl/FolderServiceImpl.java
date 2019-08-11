package co.dostf.bussiness.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import co.dostf.bussiness.IFolderService;
import co.dostf.dto.CarpetaDespliegueDto;
import co.dostf.dto.CopyFileDto;

@Service
public class FolderServiceImpl implements IFolderService {

	@Value("${pathDespliegue}")
	public String CARPETA_PRINCIPAL;

	@Value("${pathClone}")
	private String PATH_CLONE;

	@Override
	public List<CarpetaDespliegueDto> listarCarpetas(String ubicacion) throws IOException {
		return Files.list(Paths.get(ubicacion)).filter(Files::isDirectory)
				.map(item -> CarpetaDespliegueDto.of(item.toString(), Boolean.TRUE, item.getFileName().toString())).collect(Collectors.toList());
	}

	@Override
	public Boolean eliminaArchivosCliente() throws IOException {
		String proyectoUno = PATH_CLONE.concat("2tf-bussines-cuestionario/src/main/resources/");
		String proyectoDos = PATH_CLONE.concat("2tf-bussines-informacion/src/main/resources/");
		// Elimino los registros de que se encuentran en el proyecto uno
		Files.list(Paths.get(proyectoUno)).filter(Files::isRegularFile).forEach(this::deleteFile);
		// Elimino los registros de que se encuentran en el proyecto dos
		Files.list(Paths.get(proyectoDos)).filter(Files::isRegularFile).forEach(this::deleteFile);

		return Boolean.TRUE;
	}

	public void deleteFile(Path file) {
		try {
			Files.delete(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Boolean copyFiles(String url) {
		// Genero la lista de los nombres para los archivos del proyecto cuestionario.
		List<CopyFileDto> listaCuestionario = Arrays.asList(
				CopyFileDto.of("cuest_client-config.wsdd", "client-config.wsdd"),
				CopyFileDto.of("cuest_configuration.properities", "configuration.properities"),
				CopyFileDto.of("cuest_seguridad.properties", "seguridad.properties"),
				CopyFileDto.of("2Transfair-test.jks", "2Transfair-test.jks"));

		// Genero la lista de los nombres para los archivos del proyecto Info.
		List<CopyFileDto> listaInfo  = Arrays.asList(
				CopyFileDto.of("info_client-config.wsdd", "client-config.wsdd"),
				CopyFileDto.of("info_configurationInfo.properities", "configurationInfo.properities"),
				CopyFileDto.of("info_seguridad.properties", "seguridad.properties"),
				CopyFileDto.of("2Transfair-test.jks", "2Transfair-test.jks"));

		listaCuestionario.stream().parallel().forEach(item -> copyFile(item, url, "2tf-bussines-cuestionario"));
		
		listaInfo.stream().parallel().forEach(item -> copyFile(item, url, "2tf-bussines-informacion"));
		
		return Boolean.TRUE;
	}

	private void copyFile(CopyFileDto file, String url, String proyecto) {
		String urlOrigen = url.concat("/").concat(file.getOrigen());
		String urlDestino = PATH_CLONE.concat(proyecto).concat("/src/main/resources/");
		urlDestino = urlDestino.concat(file.getDestino());
		try {
			Files.copy(Paths.get(urlOrigen), Paths.get(urlDestino), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
