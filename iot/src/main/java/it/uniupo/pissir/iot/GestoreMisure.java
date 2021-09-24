package it.uniupo.pissir.iot;

import org.eclipse.paho.client.mqttv3.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GestoreMisure extends Thread{
    MqttClient client;
    public GestoreMisure(){
        String broker_url = "tcp://localhost:1883";
        String clientId = MqttClient.generateClientId();
        try{
            client = new MqttClient(broker_url, clientId);
        }catch (MqttException e){
            e.printStackTrace();
        }
    }

    public void startGestore(){
        try{
            /*Per mettersi in ascolto, il subscriber deve settare una callback (classe callback con metodi asincroni
             * chiamati nel momento in cui ci sarà la ricezione del messaggio*/
            client.setCallback(new GestoreMisure.GestoreMisureCallback());
            /*Il subscriber si connette al client*/
            client.connect();

            /*Il gestore si mette in ascolto su tutti i topic dei sensori */
            final String topic = "sensori/#";
            /*Subscribe su quel topic, per ricevere i messaggi che saranno pubblicati su quel topic*/
            client.subscribe(topic);
            System.out.println("The subscriber GestoreMisure is now listening to " + topic);

        }catch(MqttException e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        startGestore();
    }

    private class GestoreMisureCallback implements MqttCallback {
        @Override
        public void connectionLost(Throwable cause) { /*do nothing*/ }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            /* estrapolo l'unità di misura per determinare a quale attuatore è dedicata la misurazione
            *  estrapolo l'id dell'ufficio da cui è arrivata la misura
            *  estrapolo il valore della misurazione
            *  elaboro la misura
            *  se necessario invio all'attuatore corrispondente, nell'ufficio corretto, il segnale di settare la nuova misura */
            //System.out.println("Ricevuto");
            String unitaDiMisura = extrapolateUnitOfMeasurement(message.toString());
            float valoreMisuraSensore = extrapolateSensorMesure(message.toString());
            String idUfficio = extrapolateIdUfficio(message.toString());
            try{
                if(!unitaDiMisura.contains("Err") && valoreMisuraSensore != -1 && !idUfficio.contains("Err")){
                    /*Questa funzione è la inviaComandi dei diagrammi*/
                    //System.out.println("test");
                    sendNewMesureToActuators(unitaDiMisura, valoreMisuraSensore, idUfficio);
                }

            }catch (MqttException e){
                e.printStackTrace();
            }
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) { /*do nothing*/ }
    }

    /*InviaComandi*/
    private void sendNewMesureToActuators(String unitaDiMisura, float valoreMisuraSensore, String idUfficio) throws MqttException {
        if(unitaDiMisura.contains("bri")){
            /*Qui ci sarebbe l'interazione con il DB per salvare la misurazione ottenuta nella tabella delle misurazioni
            * della luce*/
            if(valoreMisuraSensore < 100){
                float newBrightness = 254-valoreMisuraSensore;
                String newBrightnessString = String.valueOf(newBrightness);
                LocalDate date = LocalDate.now();
                String dateString = date.toString();
                LocalTime time = LocalTime.now();
                String timeString = time.toString();
                MqttTopic topicAttuatoreLuce;
                if(idUfficio.contains("SalaAttesa")){
                    topicAttuatoreLuce = client.getTopic("attuatori/"+idUfficio+"/changeLux");
                }else{
                    topicAttuatoreLuce = client.getTopic("attuatori/ufficio"+idUfficio+"/changeLux");
                }
                Misura misura = new Misura(idUfficio, newBrightnessString, unitaDiMisura, dateString, timeString);
                topicAttuatoreLuce.publish(new MqttMessage(misura.toString().getBytes()));
            }
        }else if(unitaDiMisura.contains("C")){
            /*Qui ci sarebbe l'interazione con il DB per salvare la misurazione ottenuta nella tabella delle misurazioni
             * della temperatura*/
            if(valoreMisuraSensore < 21 || valoreMisuraSensore > 23){
                /*Se la temperatura è al di fuori di un range di "comfort" riporto la temperatura del locale a quella
                * di "comfort"*/
                MqttTopic topicAttuatoreTemperatura = client.getTopic("attuatori/ufficio"+idUfficio+"/changeTemperature");
                String newTempString = String.valueOf(21);
                LocalDate date = LocalDate.now();
                String dateString = date.toString();
                LocalTime time = LocalTime.now();
                String timeString = time.toString();
                Misura misura = new Misura(idUfficio, newTempString, unitaDiMisura, dateString, timeString);
                System.out.println("La temperatura varrà cambiata in: " + newTempString + "C");
                topicAttuatoreTemperatura.publish(new MqttMessage(misura.toString().getBytes()));
            }
        }else if(unitaDiMisura.contains("Fumo/Ossigeno")){
            /*Qui ci sarebbe l'interazione con il DB per salvare la misurazione ottenuta nella tabella delle misurazioni
             * del fumo */
            /*Per semplicità il fumo è valutato in particelle di fumo/particelle di ossigeno su una scala da 0 a 10,
             * se ho almeno la metà di particelle di fumo faccio partire l'allarme*/
            if(valoreMisuraSensore > 4){
                /*Lancio del segnale d'allarme*/
                MqttTopic topicAttuatoreFumo;
                if(idUfficio.contains("SalaAttesa")){
                    topicAttuatoreFumo = client.getTopic("attuatori/"+idUfficio+"/smokeAlarm");
                } else{
                    topicAttuatoreFumo  = client.getTopic("attuatori/ufficio"+idUfficio+"/smokeAlarm");
                }
                /*Messaggio mqtt, per non mandare un messaggio vuoto ma uno con un valore consistente*/
                String message = "true";
                topicAttuatoreFumo.publish(new MqttMessage(message.getBytes()));
            }
        }
    }

    /*Estrapola dal messaggio mqtt l'id dell'ufficio da cui proviene*/
    private String extrapolateIdUfficio(String messageToString) {
        if(messageToString != null){
            String valueString = null;
            String token[];
            String s1 = null;
            String tempRes = null;
            float valueTempNumeric;
            token = messageToString.split(",");

            while(s1 == null){
                for(String t : token) {
                    if (s1 == null && t.contains("idLocale")) s1 = t;
                }
            }
            //System.out.println(s1);
            token = s1.split("=");

            /*for (String s : token){
                System.out.println(s);
            }*/
            //while(tempRes == null){
                for(String t : token)
                    if(!t.contains("idLocale")){
                        valueString = t.replace("'", "");
                    }
            //}
            /*for (String s : list){
                System.out.println("inList: "+s);
            }*/
            //System.out.println("idLocale: "+valueString);
            //System.out.println("valueTempNumeric " +valueTempNumeric);

            return valueString;
        }
        return "Err";
    }

    /*Estrapola dal messaggio mqtt il valore della misura*/
    private float extrapolateSensorMesure(String messageToString) {
        if(messageToString != null){
            String valueTempString = null;
            String token[];
            String s1 = null;
            String tempRes = null;
            float valueTempNumeric;
            token = messageToString.split(",");

            while(s1 == null){
                for(String t : token) {
                    if (s1 == null && t.contains("valoreMisurazione")) s1 = t;
                }
            }
            //System.out.println(s1);
            token = s1.split("=");

            /*for (String s : token){
                System.out.println(s);
            }*/
            //while(tempRes == null){
                for(String t : token)
                    if(!t.contains("valoreMisurazione")){
                        valueTempString = t.replace("'", "");
                    }
            //}
            /*for (String s : list){
                System.out.println("inList: "+s);
            }*/
            //System.out.println(valueTempString);
            valueTempNumeric = Float.parseFloat(valueTempString);
            //System.out.println("valueTempNumeric " +valueTempNumeric);

            return valueTempNumeric;
        }
        return -1;
    }

    /*Estrapola dal messaggio mqtt l'unità di misura*/
    private String extrapolateUnitOfMeasurement(String messageToString) {
        if(messageToString != null){
            String valueString = null;
            String token[];
            String s1 = null;
            String tempRes = null;
            float valueTempNumeric;
            token = messageToString.split(",");

            while(s1 == null){
                for(String t : token) {
                    if (s1 == null && t.contains("unitaDiMisura")) s1 = t;
                }
            }
            //System.out.println(s1);
            token = s1.split("=");

            /*for (String s : token){
                System.out.println(s);
            }*/
            //while(tempRes == null){
                for(String t : token)
                    if(!t.contains("unitaDiMisura")){
                        valueString = t.replace("'", "");
                    }
            //}
            /*for (String s : list){
                System.out.println("inList: "+s);
            }*/
            //System.out.println(valueString);
            //System.out.println("valueTempNumeric " +valueTempNumeric);

            return valueString;
        }
        return "Err";
    }
}