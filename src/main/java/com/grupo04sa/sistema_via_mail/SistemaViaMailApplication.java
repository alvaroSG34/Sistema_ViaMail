package com.grupo04sa.sistema_via_mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.grupo04sa.sistema_via_mail.scheduler.EmailScheduler;

/**
 * Aplicación Principal - Sistema Via Mail
 * Trans Comarapa - Grupo04 SA
 * 
 * Sistema de gestión de transporte vía correo electrónico
 */
@SpringBootApplication
@EnableScheduling
public class SistemaViaMailApplication {

    private static final Logger log = LoggerFactory.getLogger(SistemaViaMailApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SistemaViaMailApplication.class, args);
    }

    @Bean
    public CommandLineRunner inicializar(EmailScheduler scheduler) {
        return args -> {
            log.info("==============================================");
            log.info("Sistema de Gestión Vía Email - Iniciado");
            log.info("Trans Comarapa - Grupo04 SA");
            log.info("==============================================");
            scheduler.logInicio();
        };
    }
}
