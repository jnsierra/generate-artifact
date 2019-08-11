package co.dostf.bussiness;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

import co.dostf.exception.CustomException;

public interface IGitServices {
	/**
	 * Metodo con el cual descargo el codigo del repositorio que corresponde
	 * @return
	 */
	Boolean descargarCodigo()throws InvalidRemoteException, TransportException, GitAPIException,IOException,CustomException;

}
