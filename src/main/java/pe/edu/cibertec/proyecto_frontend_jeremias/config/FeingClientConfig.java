package pe.edu.cibertec.proyecto_frontend_jeremias.config;
import feign.Client;
import feign.Request;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeingClientConfig {

    @Bean
    public Client feignClient() {
        return new Client.Default(null, null); // Aquí puedes personalizar el cliente según sea necesario
    }

    @Bean
    public Request.Options requestOptions() {
        return new Request.Options(10000, 10000); // Tiempo de conexión y lectura
    }
}

