/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javafxappaerolinea.utility;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Dell
 */
public class IdGeneratorUtil {
    /**
     * Genera un ID único para un vuelo
     * @param aerolineaId ID de la aerolínea
     * @param origen Ciudad de origen
     * @param destino Ciudad de destino
     * @return ID único para el vuelo
     */
    public static String generateVueloId(int aerolineaId, String origen, String destino) {
        String originCode = origen.substring(0, Math.min(3, origen.length())).toUpperCase();
        String destCode = destino.substring(0, Math.min(3, destino.length())).toUpperCase();
        return "V" + aerolineaId + "-" + originCode + "-" + destCode + "-" + System.currentTimeMillis() % 10000;
    }

    /**
     * Genera un ID único para un boleto
     * @param vueloId ID del vuelo
     * @param asiento Número de asiento
     * @return ID único para el boleto
     */
    public static String generateBoletoId(String vueloId, String asiento) {
        return "B" + vueloId + "-" + asiento;
    }

    /**
     * Genera un ID único aleatorio
     * @return ID único aleatorio
     */
    public static String generateRandomId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
    
    /**
     * Genera un ID numérico único para una aerolínea.
     * @return Nuevo ID de aerolínea.
     */
    public static int generateAirlineId() {
        javafxappaerolinea.model.dao.AirlineDAO airlineDAO = new javafxappaerolinea.model.dao.AirlineDAO();
        try {
            List<javafxappaerolinea.model.pojo.Airline> airlines = airlineDAO.findAll();
            return airlines.stream()
                           .mapToInt(javafxappaerolinea.model.pojo.Airline::getIdentificationNumber)
                           .max()
                           .orElse(0) + 1;
        } catch (IOException e) {
            // Como fallback en caso de error, genera un ID basado en el tiempo.
            // Esto es menos robusto pero evita que la app se detenga.
            return (int) (System.currentTimeMillis() % 100000);
        }
    }
}