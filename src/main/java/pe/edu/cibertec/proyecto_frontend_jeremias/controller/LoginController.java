package pe.edu.cibertec.proyecto_frontend_jeremias.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.cibertec.proyecto_frontend_jeremias.client.AutenticacionClient;
import pe.edu.cibertec.proyecto_frontend_jeremias.dto.LoginRequestDTO;
import pe.edu.cibertec.proyecto_frontend_jeremias.dto.LoginResponseDTO;
import pe.edu.cibertec.proyecto_frontend_jeremias.wiewmodel.LoginModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    WebClient webClientAutenticacion;


    AutenticacionClient autenticacionClient;

    @GetMapping("/inicio")
    public String inicio(Model model) {
        // Se inicializa el LoginModel con código '00' y mensaje vacío
        LoginModel loginModel = new LoginModel("00", "");
        model.addAttribute("loginModel", loginModel);
        return "inicio";
    }

    @PostMapping("/autenticar")


    public String autenticar(@RequestParam("codigoAlumno") String codigoAlumno,
                             @RequestParam("password") String password,
                             Model model) {

        System.out.println("Consumiendo con RestTemplate!!!");

        // Validar campos de entrada (código de alumno y password)
        if (codigoAlumno == null || codigoAlumno.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            // Si hay un error en la validación, se devuelve a la vista de inicio con un mensaje
            LoginModel loginModel = new LoginModel("01", "Debe completar correctamente sus credenciales");
            model.addAttribute("loginModel", loginModel);
            return "inicio";
        }

        // Si todo está correcto, se pasa al modelo y se redirige a la página principal
        LoginModel loginModel = new LoginModel(codigoAlumno, "Autenticación exitosa");
        model.addAttribute("loginModel", loginModel);
        return "principal";  // Redirige a la vista principal
    }

    @PostMapping("/autenticar-Feing")
    public Mono<LoginResponseDTO> autenticarFeing(@RequestBody LoginRequestDTO loginResquestDTO) {
        //validar caompos de entrada

        System.out.println("Consumo autenticacionClient");

        if(loginResquestDTO.codigoAlumno() == null || loginResquestDTO.codigoAlumno().trim().length() ==0 ||
                loginResquestDTO.password() == null || loginResquestDTO.password().trim().length() ==0 ||
                loginResquestDTO.nombre() == null || loginResquestDTO.nombre().trim().length() ==0||
                loginResquestDTO.apellido() == null || loginResquestDTO.apellido().trim().length() ==0)
        {
            return Mono.just(new LoginResponseDTO("01", "Error : debe completar correctamente el campo","",""));
        }

        try {
            // Consume the authentication service via Feign client
            LoginResponseDTO response = autenticacionClient.login(loginResquestDTO);

            if ("00".equals(response.codigoAlumno())) {
                return Mono.just(new LoginResponseDTO("00", "", response.nombre(), ""));
            } else {
                return Mono.just(new LoginResponseDTO("02", "Error: Autenticación fallida", "", ""));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Mono.just(new LoginResponseDTO("02", "Ocurrió un problema en la autenticación", "", ""));
        }

    }

    @GetMapping("/listar-usuarios")
    public String listarUsuarios(Model model) {
        List<LoginRequestDTO> usuariosList = webClientAutenticacion.get()
                .uri("http://localhost:8081/autenticacion/listar-usuarios")
                .retrieve()
                .bodyToFlux(LoginRequestDTO.class)
                .collectList() // Recoge la lista sin bloquear
                .block(); // Bloquea para obtener la lista

        if (usuariosList != null) {
            model.addAttribute("usuarios", usuariosList); // Agrega la lista al modelo
        } else {
            System.out.println("La lista de usuarios está vacía.");
        }

        return "usuarios"; // Retorna el nombre de la vista
    }



}
