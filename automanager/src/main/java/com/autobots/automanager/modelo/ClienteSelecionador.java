package com.autobots.automanager.modelo;

import java.util.List;

import org.springframework.stereotype.Component;

import com.autobots.automanager.entidades.Cliente;

@Component
public class ClienteSelecionador {

    public Cliente selecionar(List<Cliente> clientes, long id) {
        Cliente encontrado = null;
        for (Cliente atual : clientes) {
            if (atual.getId() == id) {
                encontrado = atual;
            }
        }
        return encontrado;
    }
}
