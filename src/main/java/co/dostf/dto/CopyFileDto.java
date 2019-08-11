package co.dostf.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CopyFileDto {
	
	private String origen;
	private String destino;
	
	
	public static CopyFileDto of(String origen, String destino) {
		CopyFileDto aux = new CopyFileDto();
		aux.setOrigen(origen);
		aux.setDestino(destino);
		return aux;
	}

}
