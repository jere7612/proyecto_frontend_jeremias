package pe.edu.cibertec.proyecto_frontend_jeremias.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import pe.edu.cibertec.proyecto_frontend_jeremias.dto.LoginRequestDTO;
import pe.edu.cibertec.proyecto_frontend_jeremias.dto.LoginResponseDTO;

@FeignClient(name = "autenticacion", url = "http://localhost:8086/autenticacion", configuration = FeignClient.class)
public interface AutenticacionClient {

    @PostMapping("/login")
    LoginResponseDTO login(@RequestBody LoginRequestDTO loginRequestDTO);

}
