package rmsbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

// The project currently has JPA on the classpath but no database configuration yet.
// Exclude DB auto_configuration so the app can start until persistence is wired in.
@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
public class RmsBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(RmsBackendApplication.class, args);
    }

}
