package javafxappaerolinea.utility;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class IdGeneratorUtil {

    public static String generateVueloId(int aerolineaId, String origen, String destino) {
        String originCode = origen.substring(0, Math.min(3, origen.length())).toUpperCase();
        String destCode = destino.substring(0, Math.min(3, destino.length())).toUpperCase();
        return "V" + aerolineaId + "-" + originCode + "-" + destCode + "-" + System.currentTimeMillis() % 10000;
    }
    
  
   public static String generateAdministrativeId() {
       String prefix = "ADM-";
       int randomNum = (int) (Math.random() * 90000) + 10000;
       return prefix + randomNum;
   }


   public static String generatePilotId() {
       String prefix = "PIL-";
       int randomNum = (int) (Math.random() * 90000) + 10000;
       return prefix + randomNum;
   }


   public static String generateAssistantId() {
       String prefix = "AST-";
       int randomNum = (int) (Math.random() * 90000) + 10000;
       return prefix + randomNum;
   }

 
    public static String generateBoletoId(String vueloId, String asiento) {
        return "B" + vueloId + "-" + asiento;
    }

    public static String generateRandomId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
    

    public static int generateAirlineId() {
        javafxappaerolinea.model.dao.AirlineDAO airlineDAO = new javafxappaerolinea.model.dao.AirlineDAO();
        try {
            List<javafxappaerolinea.model.pojo.Airline> airlines = airlineDAO.findAll();
            return airlines.stream()
                           .mapToInt(javafxappaerolinea.model.pojo.Airline::getIdentificationNumber)
                           .max()
                           .orElse(0) + 1;
        } catch (IOException e) {

            return (int) (System.currentTimeMillis() % 100000);
        }
    }
}