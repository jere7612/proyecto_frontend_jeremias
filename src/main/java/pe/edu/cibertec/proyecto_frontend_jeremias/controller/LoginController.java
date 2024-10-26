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
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    WebClient webClientAutenticacion;

  AutenticacionClient autenticacionClient;

    private final List<LoginRequestDTO> usuariosList = new ArrayList<>();

    @GetMapping("/inicio")
    public String inicio(Model model) {
        LoginModel loginModel = new LoginModel("00", "");
        model.addAttribute("loginModel", loginModel);
        return "inicio";
    }

    @PostMapping("/autenticar")
    public String autenticar(@RequestParam("codigoAlumno") String codigoAlumno,
                             @RequestParam("password") String password,
                             Model model) {

        System.out.println("Consumiendo con RestTemplate!!!");

        if (codigoAlumno == null || codigoAlumno.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            LoginModel loginModel = new LoginModel("01", "Debe completar correctamente sus credenciales");
            model.addAttribute("loginModel", loginModel);
            return "inicio";
        }

        LoginModel loginModel = new LoginModel(codigoAlumno, "Autenticación exitosa");
        model.addAttribute("loginModel", loginModel);
        return "principal";
    }

    @PostMapping("/autenticar-Feing")
    public Mono<LoginResponseDTO> autenticarFeing(@RequestBody LoginRequestDTO loginResquestDTO) {
        System.out.println("Consumo autenticacionClient");

        if (loginResquestDTO.codigoAlumno() == null || loginResquestDTO.codigoAlumno().trim().isEmpty() ||
                loginResquestDTO.password() == null || loginResquestDTO.password().trim().isEmpty() ||
                loginResquestDTO.nombre() == null || loginResquestDTO.nombre().trim().isEmpty() ||
                loginResquestDTO.apellido() == null || loginResquestDTO.apellido().trim().isEmpty()) {
            return Mono.just(new LoginResponseDTO("01", "Error: debe completar correctamente el campo", "", ""));
        }

        try {
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
    public Mono<String> listarUsuarios(Model model) {
        return webClientAutenticacion.get()
                .uri("http://localhost:8081/autenticacion/listar-usuarios")
                .retrieve()
                .bodyToFlux(LoginRequestDTO.class)
                .collectList()
                .doOnNext(usuarios -> {
                    usuariosList.clear(); // Limpia la lista anterior
                    usuariosList.addAll(usuarios); // Agrega la nueva lista de usuarios
                })
                .doOnSuccess(usuarios -> {
                    // Agrega la lista al modelo antes de retornar la vista
                    model.addAttribute("usuarios", usuariosList);
                })
                .then(Mono.just("usuarios")); // Retorna el nombre de la vista
    }
}