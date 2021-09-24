package it.uniupo.pissir.iot;

public class MainIoT {
    public static void main(String[] args) {

        /*UFFICIO1*/
        System.out.println("SIMULAZIONE UFFICIO 1");
        /*gestoreMisure*/
        GestoreMisure gestoreMisure = new GestoreMisure();
        gestoreMisure.start();

        /*Esempio simulazione attuatori temperatura e luce ufficio 1*/
        AttuatoreTemperaturaSubscriber attuatoreTemp = new AttuatoreTemperaturaSubscriber("1");
        AttuatoreLuceSubscriber attuatoreLuce = new AttuatoreLuceSubscriber("1");
        attuatoreTemp.start();
        attuatoreLuce.start();

        /*Esempio sensori luce e temperatura ufficio*/
        SensoreLucePublisher sensoreLuce = new SensoreLucePublisher("2", "1");
        SensoreTemperaturaPublisher sensoreTemperatura = new SensoreTemperaturaPublisher("1", "1");
        sensoreTemperatura.start();
        sensoreLuce.start();

        try {
            System.out.println("\nRilevazione del fumo\n");
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /*Esempio simulazione sensore/attuatore FUMO ufficio 1*/
        AttuatoreFumoSubscriber attuatoreFumo = new AttuatoreFumoSubscriber("1");
        attuatoreFumo.start();

        SensoreFumoPublisher sensoreFumo = new SensoreFumoPublisher("3", "1");
        sensoreFumo.start();


        /*SALA D'ATTESA*/


        /*Esempio simulazione attuatori temperatura e luce sala d'attesa*/
        AttuatoreTemperaturaSubscriber attuatoreTempSalaAttesa = new AttuatoreTemperaturaSubscriber("SalaAttesa");
        AttuatoreLuceSubscriber attuatoreLuceSalaAttesa = new AttuatoreLuceSubscriber("SalaAttesa");
        attuatoreLuceSalaAttesa.start();
        attuatoreTempSalaAttesa.start();

        /*Esempio sensori temperatura e luce sala d'attesa*/
        SensoreLucePublisher sensoreLuceSalaAttesa = new SensoreLucePublisher("5", "SalaAttesa");
        SensoreTemperaturaPublisher sensoreTemperaturaSalaAttesa = new SensoreTemperaturaPublisher("4", "SalaAttesa");
        sensoreLuceSalaAttesa.start();
        sensoreTemperaturaSalaAttesa.start();

        try {
            System.out.println("\nRilevazione del fumo della sala d'attesa\n");
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /*Esempio simulazione attuatore fumo sala d'attesa*/
        AttuatoreFumoSubscriber attuatoreFumoSalaAttesa = new AttuatoreFumoSubscriber("SalaAttesa");
        attuatoreFumoSalaAttesa.start();

        SensoreFumoPublisher sensoreFumoSalaAttesa = new SensoreFumoPublisher("6", "SalaAttesa");
        sensoreFumoSalaAttesa.start();
    }
}
