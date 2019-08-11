package co.dostf.bussiness.impl;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import co.dostf.bussiness.IMavenService;
import co.dostf.bussiness.INexusServices;
import co.dostf.dto.CarpetaDespliegueDto;
import lombok.Getter;
import lombok.Setter;

@Service
@Getter @Setter
public class NexusServices implements INexusServices {

	@Autowired
	IMavenService mavenService;
	
	@Value("${nexus.host}")
	private String host;
	@Value("${nexus.port}")
	private String port;
	
	private String url;
	
	@PostConstruct
	public void init() {
		this.url = "http://admin:12345678@".concat(getHost()).concat(":").concat(port);
	}

	@Override
	public Boolean subirCodigo(CarpetaDespliegueDto carpeta, String version) throws IOException, InterruptedException {
		return mavenService.subirArtefacto(carpeta, url + "/repository/maven-releases/", version);
	}

}
