package co.dostf.bussiness;

import java.io.IOException;

public interface IShellServices {
	
	void executeShell(String cmd) throws IOException, InterruptedException; 

}
