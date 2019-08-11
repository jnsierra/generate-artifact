
package co.dostf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("file:/opt/application.properties")
public class Application extends  SpringBootServletInitializer{
	
	public static void main(String ... args) {
		SpringApplication.run(Application.class, args);
	}

}
