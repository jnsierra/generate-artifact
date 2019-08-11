package co.dostf.bussiness.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import co.dostf.bussiness.IShellServices;

@Service
public class ShellServices implements IShellServices {
	
	Logger logger = LoggerFactory.getLogger(ShellServices.class);

	@Override
	public void executeShell(String cmd) throws IOException, InterruptedException {
		System.out.println("***********************************************************************************");
		logger.info("Codigo a ejecutar en la consola: ".concat(cmd));
		Runtime run = Runtime.getRuntime();
		Process pr = run.exec(cmd);
		BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
		String line = "";
		while ((line = buf.readLine()) != null) {
			System.out.println(line);
		}
		System.out.println("***********************************************************************************");
	}

}
