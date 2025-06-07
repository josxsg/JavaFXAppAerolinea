/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javafxappaerolinea.utility;

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
}
