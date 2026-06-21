package com.example.estadisticas;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class EstadisticasService {

    private final PointOfSaleRepository pvRepo;
    private final UserRepository userRepo;

    public EstadisticasService(PointOfSaleRepository pvRepo, UserRepository userRepo) {
        this.pvRepo = pvRepo;
        this.userRepo = userRepo;
    }

    public Map<String, Object> getResumen() {
        List<PointOfSale> todos = pvRepo.findAll();
        long activos = todos.stream().filter(PointOfSale::isActivo).count();
        long inactivos = todos.size() - activos;

        List<UserAccount> usuarios = userRepo.findAll();
        long usuariosActivos = usuarios.stream().filter(UserAccount::isActive).count();

        Map<String, Object> resumen = new LinkedHashMap<>();
        resumen.put("totalPuntosVenta", todos.size());
        resumen.put("puntosVentaActivos", activos);
        resumen.put("puntosVentaInactivos", inactivos);
        resumen.put("totalUsuarios", usuarios.size());
        resumen.put("usuariosActivos", usuariosActivos);
        return resumen;
    }

    public Map<String, Long> getPvPorCiudad() {
        return pvRepo.findAll().stream()
                .collect(Collectors.groupingBy(PointOfSale::getCity, Collectors.counting()));
    }

    public Map<String, Long> getUsuariosPorRol() {
        return userRepo.findAll().stream()
                .filter(u -> u.getRol() != null)
                .collect(Collectors.groupingBy(UserAccount::getRol, Collectors.counting()));
    }

    public Map<String, Long> getPvActivosVsInactivos() {
        Map<String, Long> resultado = new LinkedHashMap<>();
        resultado.put("Activos", pvRepo.findByActivo(true).stream().count());
        resultado.put("Inactivos", pvRepo.findByActivo(false).stream().count());
        return resultado;
    }
}
