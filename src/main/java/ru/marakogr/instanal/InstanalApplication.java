package ru.marakogr.instanal;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@Theme(variant = Lumo.LIGHT)
@SpringBootApplication
public class InstanalApplication extends SpringBootServletInitializer implements AppShellConfigurator {

	public static void main(String[] args) {
		SpringApplication.run(InstanalApplication.class, args);
	}

}
