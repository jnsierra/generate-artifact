package co.dostf.bussiness;

import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

import co.dostf.dto.CarpetaDespliegueDto;
import co.dostf.exception.CustomException;

public interface IGenerateArtifact {
	/**
	 * Metodo con el cual genero los artefactos
	 * @param listArtefactos
	 * @return
	 */
	Boolean generateArtefactos(List<CarpetaDespliegueDto> listArtefactos, String version) throws InvalidRemoteException, TransportException, GitAPIException,IOException, CustomException, InterruptedException;

}
