package co.dostf.bussiness.impl;

import java.io.IOException;
import java.nio.file.Paths;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.CanceledException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidConfigurationException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.RefNotAdvertisedException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import co.dostf.bussiness.IGitServices;
import co.dostf.bussiness.IMavenService;
import co.dostf.exception.CustomException;

@Service
public class GitServices implements IGitServices {

	Logger logger = LoggerFactory.getLogger(GitServices.class);

	@Autowired
	IMavenService mavenService;

	@Value("${gitUrl}")
	private String urlGit;

	@Value("${pathClone}")
	private String PATH_CLONE;
	
	
	private CredentialsProvider cp;
	private Git git;
	private Repository localRepo;
	
	public void init() throws IOException {
		this.localRepo = new FileRepository(PATH_CLONE+ "/.git");
		this.cp = new UsernamePasswordCredentialsProvider("jnsierra", "flaco1030585312");
		this.git = new Git(this.localRepo);
	}

	@Override
	public Boolean descargarCodigo()
			throws InvalidRemoteException, TransportException, GitAPIException, IOException, CustomException {
		Boolean validaProcesos = Boolean.TRUE;
		// Realizamos la extraccion del codigo
		validaProcesos = gitCloneCodigo();
		if (!validaProcesos) {
			throw new CustomException("Imposible descargar el codigo del repositorio");
		}
		logger.info("Proceso de actualizacion de codigo: " + validaProcesos);
		return validaProcesos;
	}

	public Boolean gitCloneCodigo() throws InvalidRemoteException, TransportException, GitAPIException, IOException {
		try {
			logger.info("Se va ha realizar la clonacion del proyecto");
			Git.cloneRepository().setURI(urlGit).setDirectory(Paths.get(PATH_CLONE).toFile()).call();
			logger.info("Clono el repo");
		} catch (JGitInternalException e) {
			logger.error("Error al clonar el projecto: " +  e);
			return gitPullCodigo();
		}
		return Boolean.TRUE;
	}
	
	public Boolean gitPullCodigo() throws IOException, WrongRepositoryStateException, InvalidConfigurationException, InvalidRemoteException, CanceledException, RefNotFoundException, RefNotAdvertisedException, NoHeadException, TransportException, GitAPIException {
		init();
		git.pull().call();
		logger.info("Realizo un pull del codigo");
		return Boolean.TRUE;
	}

}
