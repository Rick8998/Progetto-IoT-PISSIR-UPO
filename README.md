# Progetto-PISSIR-UPO
Progetto IoT Progettazione e Implementazione dei Sistemi Software In Rete

# Servizio di gestione di uffici condivisi per professionisti nomadi
Contesto: 
Il modello "15 minuti" o "iperlocale", ovvero mettere a disposizione dei cittadini i servizi professionali e le risorse necessarie entro un raggio di 15 minuti a piedi o in bicicletta.

Idea: mettere a disposizione uno spazio di co-working per professionisti in diverse zone distanti dalla città. Immaginiamo che a questo scopo sia stato individuato un edificio dotato di un certo numero di uffici, un servizio di portineria e una sala d’attesa in comune. 

L’applicazione deve permettere al professionista di prenotare un ufficio in determinati slot di tempo indicando per ogni slot il numero stimato di clienti in attesa.
- Se durante la prenotazione il numero totale di clienti stimati in sala d’attesa supera una certa soglia il professionista viene avvertito di dover modificare la richiesta. Per esempio si può decidere una politica che preservi almeno un posto nella sala d’attesa per ogni ufficio, oppure non prevedere alcun vincolo tranne la verifica della disponibilità residua di posti a sedere nel periodo prenotato.
- La prenotazione può essere creata, cancellata o modificata riducendo il numero di slot prenotati.
- Non è necessario l’implementazione dell’interfaccia per popolare il DB con le informazioni sugli uffici disponibili e sulla sala d’attesa (supponiamo che queste informazioni  siano inserite nel DB tramite un’altra applicazione).

## Parte implementata
### - Occorre implementare un sistema automatico di gestione del riscaldamento/raffrescamento dei diversi uffici che venga attivato sulla base delle prenotazioni e della misurazione di parametri di confort (es. temperatura e umidità). Inoltre questi parametri verranno rilevati e memorizzati in una base di dati per una possibile consultazione da parte del gestore dell’edificio.
### - L’illuminazione della sala d’attesa è gestita automaticamente in base ad un sensore di luminosità che permette di decidere come impostare la luminosità delle lampade presenti.
### - Inoltre nell’edificio sono presenti dei sensori di fumo che possono attivarsi per segnalare la possibile presenza di un incendio. Nel caso questo venga rilevato il sistema dovrà attivare dei segnali di pericolo (lampadine e/o segnali sonori) che indicheranno la necessità di evacuare l’edificio.
