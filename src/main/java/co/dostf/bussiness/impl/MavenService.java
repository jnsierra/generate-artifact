package co.dostf.bussiness.impl;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import co.dostf.bussiness.IMavenService;
import co.dostf.bussiness.IShellServices;
import co.dostf.dto.CarpetaDespliegueDto;

@Service
public class MavenService implements IMavenService {

	@Value("${pathClone}")
	private String PATH_CLONE;
	
	@Value("${pathShell}")
	private String PATH_SHELL;
	
	@Value("${nexusUrl}")
	private String URL_NEXUS;
	
	@Autowired
	IShellServices shellServices;

	Logger logger = LoggerFactory.getLogger(MavenService.class);

	@Override
	public Boolean limpiarCodigo() throws IOException, InterruptedException {
		String cmd = "mvn --file " + PATH_CLONE + " clean";
		shellServices.executeShell(cmd);
		return Boolean.TRUE;
	}

	@Override
	public Boolean instalarCodigo() throws IOException, InterruptedException {
		String cmd = "mvn --file " + PATH_CLONE + " install ";
		shellServices.executeShell(cmd);
		return Boolean.TRUE;
	}

	@Override
	public Boolean subirArtefacto(CarpetaDespliegueDto carpeta, String urlNexus, String version) throws IOException, InterruptedException {
		subirInfo(carpeta, urlNexus, version);
		subirCuestionario(carpeta, urlNexus, version);
		return Boolean.TRUE;
	}

	public Boolean subirCuestionario(CarpetaDespliegueDto carpeta, String urlNexus, String version) throws IOException, InterruptedException {
		String cmd = PATH_SHELL +" " + PATH_CLONE.concat("2tf-rest/") + " " + carpeta.getCliente() + " target/dostf-rest.war ";
		cmd += " dostf-rest " + version + " " + URL_NEXUS;
		shellServices.executeShell(cmd);
		return Boolean.TRUE;
	}

	public Boolean subirInfo(CarpetaDespliegueDto carpeta, String urlNexus, String version) throws IOException, InterruptedException {
		String cmd = PATH_SHELL +" " + PATH_CLONE.concat("2tf-rest-info/") + " " + carpeta.getCliente() + " target/dostf-rest-info.war " ;
		cmd += " 2tf-rest-info " + version + " " + URL_NEXUS;
		shellServices.executeShell(cmd);
		return Boolean.TRUE;
	}

}
