package it.uniupo.pissir.iot;
import java.util.ArrayList;

public class BackEndSimHandleIoT {
    public static void main(String[] args) {
        String localTimeSim = "15";
        String localDateSim = "14-09-21";
        SimBackendDBMock simBE = new SimBackendDBMock();
        AttuatoreTemperaturaSubscriber attuatoreTemp = null;
        AttuatoreLuceSubscriber attuatoreLuce = null;
        AttuatoreFumoSubscriber attuatoreFumo = null;
        /*gestoreMisure simulato tramite un Thread*/
        GestoreMisure gestoreMisure = new GestoreMisure();
        gestoreMisure.start();

        /*Simulazione di un'interrogazione a DB da parte del backend*/
        ArrayList<Prenotazione> interrogazioneSim = simBE.simGetPrenotazioneByHourAndDate(localTimeSim, localDateSim);
        for (Prenotazione p : interrogazioneSim) {
            System.out.println(p.toString());

            /*UFFICIO*/
            System.out.println("SIMULAZIONE UFFICIO "+p.getIdUfficio());


            /*Esempio simulazione attuatori temperatura e luce ufficio */
            attuatoreTemp = new AttuatoreTemperaturaSubscriber(p.getIdUfficio());
            attuatoreLuce = new AttuatoreLuceSubscriber(p.getIdUfficio());
            attuatoreTemp.start();
            attuatoreLuce.start();

            /*Esempio sensori luce e temperatura ufficio*/
            SensoreLucePublisher sensoreLuce = new SensoreLucePublisher("2", p.getIdUfficio());
            SensoreTemperaturaPublisher sensoreTemperatura = new SensoreTemperaturaPublisher("1", p.getIdUfficio());
            sensoreTemperatura.start();
            sensoreLuce.start();

            try {
                System.out.println("\nRilevazione del fumo nell'ufficio"+ p.getIdUfficio()+ "\n");
                Thread.sleep(8000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            /*Esempio simulazione sensore/attuatore FUMO ufficio */
            attuatoreFumo = new AttuatoreFumoSubscriber(p.getIdUfficio());
            attuatoreFumo.start();

            SensoreFumoPublisher sensoreFumo = new SensoreFumoPublisher("3", p.getIdUfficio());
            sensoreFumo.start();
        }

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /*Esempio simulazione attuatore fumo sala d'attesa*/
        AttuatoreFumoSubscriber attuatoreFumoSalaAttesa = new AttuatoreFumoSubscriber("SalaAttesa");
        attuatoreFumoSalaAttesa.start();

        SensoreFumoPublisher sensoreFumoSalaAttesa = new SensoreFumoPublisher("6", "SalaAttesa");
        sensoreFumoSalaAttesa.start();

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /*Simulazione stop degli attuatori*/
        /*E' passata un'ora*/
        localTimeSim = "16";
        for(Prenotazione pr : interrogazioneSim){
            if (Integer.parseInt(pr.getOra()) + pr.getDurata() == Integer.parseInt(localTimeSim)){
                attuatoreTemp.stopAttuatoreTemperatura();
                attuatoreLuce.stopAttuatoreLuce();
                attuatoreFumo.stopAttuatoreFumo();
            }
        }
    }
}
