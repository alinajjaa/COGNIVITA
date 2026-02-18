package tn.esprit.cognivita;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CognivitaApplication {
    public static void main(String[] args) {
        SpringApplication.run(CognivitaApplication.class, args);

        System.out.println("\n  _____                  _       _ _        ");
        System.out.println(" / ____|                (_)     (_) |       ");
        System.out.println("| |     ___   __ _ _ __  ___   ___| |_ __ _ ");
        System.out.println("| |    / _ \\ / _` | '_ \\| \\ \\ / / | __/ _` |");
        System.out.println("| |___| (_) | (_| | | | | |\\ V /| | || (_| |");
        System.out.println(" \\_____\\___/ \\__, |_| |_|_| \\_/ |_|\\__\\__,_|");
        System.out.println("              __/ |                         ");
        System.out.println("             |___/                          ");
        System.out.println("\n✅ Cognivita Backend Started Successfully!");
        System.out.println("📖 Swagger UI: http://localhost:8082/api/swagger-ui.html");
        System.out.println("📡 API Docs: http://localhost:8082/api/api-docs\n");
    }
}