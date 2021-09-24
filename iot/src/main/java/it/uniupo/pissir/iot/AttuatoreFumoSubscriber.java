package it.uniupo.pissir.iot;

import org.eclipse.paho.client.mqttv3.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class AttuatoreFumoSubscriber {
    private MqttClient client;
    private String idLocale;
    public AttuatoreFumoSubscriber(String idLocale){
        this.idLocale = idLocale;
        String broker_url = "tcp://localhost:1883";
        String clientId = MqttClient.generateClientId();
        try{
            client = new MqttClient(broker_url, clientId);
        }catch (MqttException e){
            e.printStackTrace();
        }
    }

    /*questa è la funzione esegueComandi dei diagrammi*/
    public void start(){
        try{
            /*Per mettersi in ascolto, il subscriber deve settare una callback (classe callback con metodi asincroni
             * chiamati nel momento in cui ci sarà la ricezione del messaggio*/
            client.setCallback(new AttuatoreFumoSubscriber.AttuatoreFumoCallBack());
            /*Il subscriber si connette al client*/
            client.connect();

            /*Il subscriber si mette in ascolto su un particolare topic*/
            final String topic;
            if(idLocale.contains("SalaAttesa")){
                topic = "attuatori/"+idLocale+"/smokeAlarm";
            } else{
                topic  = "attuatori/ufficio"+idLocale+"/smokeAlarm";
            }
            /*Subscribe su quel topic, per ricevere i messaggi che saranno pubblicati su quel topic*/
            client.subscribe(topic);
            System.out.println("The subscriber is now listening to " + topic);

        }catch(MqttException e){
            e.printStackTrace();
        }
    }

    private class AttuatoreFumoCallBack implements MqttCallback {
        @Override
        public void connectionLost(Throwable cause) { /*do nothing*/ }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            System.out.println(message.toString());
            /*Basat che arrivi un messaggio su questo topic per far scattare l'allarme, indipendentemente dal suo
            contenuto*/
            String messageToString = message.toString();
            startLuxAlarm();
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) { /*do nothing*/ }
    }

    /*Accende le lampadine di rosso*/
    private void startLuxAlarm() {
        String baseURL = "http://localhost:8000";
        String username = "newdeveloper";
        String lightsURL = baseURL + "/api/" + username + "/lights/";
        RestTemplate rest = new RestTemplate();
        Map<String, ?> allLights = rest.getForObject(lightsURL, HashMap.class);
        if (allLights != null) {
            org.springframework.http.HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String color = "{\"on\":true,\"bri\":\"254\", \"sat\":\"254\", \"hue\":\"80\"}";
            HttpEntity<String> request = new HttpEntity<>(color, headers);
            for (String light : allLights.keySet()) {
                String callURL = lightsURL + light + "/state";
                rest.put(callURL, request);
            }
        }
    }

    public void stopAttuatoreFumo(){
        System.out.println("L'attuatore del fumo si è fermato");
        String baseURL = "http://localhost:8000";
        String username = "newdeveloper";
        String lightsURL = baseURL + "/api/" + username + "/lights/";
        RestTemplate rest = new RestTemplate();
        Map<String, ?> allLights = rest.getForObject(lightsURL, HashMap.class);
        if (allLights != null) {
            org.springframework.http.HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String color = "{\"on\":false}";
            HttpEntity<String> request = new HttpEntity<>(color, headers);
            for (String light : allLights.keySet()) {
                String callURL = lightsURL + light + "/state";
                rest.put(callURL, request);
            }
        }
    }
    /*public static void main(String[] args) {
        AttuatoreFumoSubscriber att = new AttuatoreFumoSubscriber("1");
        att.start();
    }*/
}