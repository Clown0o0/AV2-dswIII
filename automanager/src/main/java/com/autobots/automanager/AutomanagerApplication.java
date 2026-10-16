package com.autobots.automanager;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.entidades.Documento;
import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.entidades.Telefone;
import com.autobots.automanager.repositorios.ClienteRepositorio;

/**
 * Ponto de entrada da aplicação AutoManager.
 */
@SpringBootApplication
public class AutomanagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutomanagerApplication.class, args);
    }

    /**
     * Popula o banco com um cliente de exemplo ao iniciar.
     */
    @Component
    public static class Runner implements ApplicationRunner {

        @Autowired
        public ClienteRepositorio repositorio;

        @Override
        public void run(ApplicationArguments args) throws Exception {
            Calendar nascimento = Calendar.getInstance();
            nascimento.set(2002, 05, 15);

            Cliente cliente = new Cliente();
            cliente.setNome("Pedro Alcântara de Bragança e Bourbon");
            cliente.setNomeSocial("Dom Pedro");
            cliente.setDataNascimento(nascimento.getTime());
            cliente.setDataCadastro(Calendar.getInstance().getTime());

            // --- telefone ---
            Telefone celular = new Telefone();
            celular.setDdd("21");
            celular.setNumero("981234576");
            celular.setCliente(cliente);
            cliente.getTelefones().add(celular);

            // --- endereço ---
            Endereco residencia = new Endereco();
            residencia.setEstado("Rio de Janeiro");
            residencia.setCidade("Rio de Janeiro");
            residencia.setBairro("Copacabana");
            residencia.setRua("Avenida Atlântica");
            residencia.setNumero("1702");
            residencia.setCodigoPostal("22021001");
            residencia.setInformacoesAdicionais("Hotel Copacabana palace");
            cliente.setEndereco(residencia);

            // --- documentos ---
            Documento rg = new Documento();
            rg.setTipo("RG");
            rg.setNumero("1500");
            rg.setCliente(cliente);

            Documento cpf = new Documento();
            cpf.setTipo("CPF");
            cpf.setNumero("00000000001");
            cpf.setCliente(cliente);

            cliente.getDocumentos().add(rg);
            cliente.getDocumentos().add(cpf);

            repositorio.save(cliente);
        }
    }
}
