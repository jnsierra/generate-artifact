package co.dostf.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CarpetaDespliegueDto {
	
	private String url;
	private Boolean valido;
	private String motivo;
	
	private String cliente;
	
	private String urlNexus;
	
	
	
	public static CarpetaDespliegueDto of(String url, Boolean valido, String cliente) {
		CarpetaDespliegueDto carpeta = new CarpetaDespliegueDto();
		carpeta.setUrl(url);
		carpeta.setValido(valido);
		carpeta.setCliente(cliente);
		return carpeta;
	}
	
	public String generateUrlNexusInfo(String host, String port, String version) {
		return "admin:12345678@" + host + ":" + port + "/repository/maven-releases/" + getCliente() + "/2tf-rest-info/0.0."+version+"/2tf-rest-info-0.0."+version+".war";
	}

	public String generateUrlNexusCuestionario(String host, String port, String version) {
		return "admin:12345678@" + host + ":" + port + "/repository/maven-releases/" + getCliente() + "/dostf-rest/0.0."+version+"/dostf-rest-0.0."+version+".war";
	}

	@Override
	public String toString() {
		return "CarpetaDespliegueDto [url=" + url + ", valido=" + valido + ", motivo=" + motivo + ", cliente=" + cliente
				+ ", urlNexus=" + urlNexus + "]";
	}
	
	
}
