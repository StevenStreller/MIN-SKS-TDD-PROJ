# 1 TDD-Projekt

Sie erinnern sich hoffentlich an die von Ihnen bereits mehrfach durchdachten Anforderungen
an ein neues Ticketverkaufssystem.

In dieser Aufgabe sollen Sie einige Klassen der Fachschicht und der Datenzugriffsschicht
entwickeln, und zwar in testgetriebenem Vorgehen unter Einsatz von JUnit 5 (s. Teilaufgabe
1.a). Sie sollen zudem unter Einsatz des Werkzeuges EclEmma einen Bericht über die
erreichte Testabdeckung anfertigen (1.b) und mockito zur Erzeugung von Test Doubles
verwenden (1.c). Lernziel bei dieser Aufgabe ist der Erfahrungsaufbau mit der testgetriebenen
Entwicklung, und zwar über ein einfaches Mini-Beispiel hinaus.

Das Ergebnis sollen Sie bis zum **3 .0 1 .202 5 , 17 : 00 Uhr** als Zip-Datei (Code und
Codeabdeckungsbericht) im moodle-Kurs abgeben und am **7 .0 1 .2 025** live und einsatzfähig
installiert demonstrieren (Raum 1H.2. 06 ). Melden Sie sich spätestens bis zum **3 .0 1 .202 5 ,
17 :00 Uhr** im moodle-Kurs für einen Termin für Ihre Demonstration an, um einen Zeitslot zu
reservieren.

Beim Demonstrationstermin sollen Sie u. a. zeigen, wie Sie einen weiteren Dienst in
„testdriven“-Manier in Ihr bisheriges Programm einbauen. Außerdem ist Gelegenheit für
Rückfragen. Sie dürfen in Teams zu 2-3 Personen arbeiten und einreichen. **Das benotete
Testat für diese Übungseinreichung und Präsentation hat einen 15%-Anteil an der
Modulnote.** Diesbezügliche Anforderungen entnehmen Sie bitte dem unten stehenden
Abschnitt 1.d.

## 1.a JUnit 5 und TDD anwenden

Eine grobe Sammlung von Anforderungen:

Domänenmodell:

- [x] Veranstaltung: Identifikator, Titel, Datum und Uhrzeit, Ticketpreis, insgesamt
  verfügbare Sitzplätze.
- [ ] Findet eine Veranstaltung an mehreren Terminen statt, wird diese (um es hier nicht
  zu kompliziert zu machen) mehrfach gepflegt.
- [x] Kunde: Name, Adresse. Der Name sei eindeutig.
- [x] Buchung: bezieht sich auf einen Kunden und eine Veranstaltung. Enthält die Anzahl
  der gebuchten Sitzplätze und einen Identifikator.

Services:

- [x] Das System soll einen Kunden bzw. eine Veranstaltung mit vom Benutzer
  eingegebenen Daten erzeugen können.
- [x] Das System soll alle Veranstaltungen auflisten können.
- [x] Das System soll zu einer Veranstaltung die noch verfügbaren Sitzplätze zeigen.
- [x] Das System soll alle Kunden auflisten können.
- [ ] Das System soll zu einem Kunden eine Buchung anlegen können. 
- [ ] Das System soll mehrere Buchungen desselben Kunden zur selben Veranstaltung
  zu einer Buchung zusammenfassen. Der Identifikator der neueren Buchung wird
  dabei übernommen.
- [x] Das System soll zu einem Kunden und einer Veranstaltung die zugehörige Buchung
  liefern können.
- [x] Das System soll eine Buchung zurückweisen, wenn nicht mehr genügend Sitzplätze
  vorhanden sind.
- [x] Das System soll alle Kunden, Veranstaltungen und Buchungen persistent speichern.

Die Benutzungsschnittstelle müssen Sie überhaupt nicht implementieren, da Ihre Tests als
Benutzer der Services auftreten werden.

Um es nicht zu komplex zu machen, müssen Sie keine Datenbankanbindung realisieren. Es
reicht, wenn Ihre Ticketshop-Anwendung alle Kunden, Veranstaltungen und Buchungen im
Hauptspeicher z. B. in Form von Collections vorhält und diese auf Anforderung komplett
speichert bzw. lädt. Als Datenhaltung bietet sich Serialisierung einer Java Collection in eine
Datei an.

## 1.b Testabdeckung

- [x] Fertigen Sie schließlich mit EclEmma einen Bericht über die erreichte Codeabdeckung an
(Anleitung: [http://www.eclemma.org/userdoc/importexport.html](http://www.eclemma.org/userdoc/importexport.html) -
Abschnitt Session Export).

## 1.c Test Doubles mit mockito

Erweitern Sie Ihre Anwendung erneut in test-driven Manier um die folgenden Funktionen.
Setzen Sie hierzu die Bibliothek mockito ein.

- [x] Eine Buchung wird zurückgewiesen, wenn der Kunde auf einer Blacklist steht, die über
  einen im Internet verfügbaren Dienst abgefragt wird. Der Blacklist-Dienst nimmt den
  Namen entgegen und liefert true, falls der Kunde auf der Blacklist steht, sonst false.
  Realisieren Sie die Blacklist als Test Stub mit mockito.
- [x] Einzelbuchungen über mindestens 10% aller Sitzplätze in der jeweiligen Veranstaltung werden (bei erfolgreicher Annahme) direkt zur Information an den Veranstalter per E-Mail weitergeleitet. Die Veranstaltung wird dazu um eine E-Mail-Adresse des Veranstalters erweitert. Testen Sie den E-Mail-Versand durch ein Mock-Objekt.

## 1.d Anforderungen beim Präsentationstermin

Insgesamt plane ich für die Präsentation pro Team mit einem Zeitbedarf von 25 bis 30
Minuten.

Beim Demonstrationstermin haben Sie zunächst 15 Minuten Zeit, Ihre Realisierung zu
präsentieren. Sie benötigen keine besonderen Präsentationsmedien. Führen Sie mich bitte
durch Ihren Quelltext hindurch. Die folgenden Aspekte sollen Sie im Rahmen Ihrer
Präsentation von sich aus ansprechen und direkt am Quelltext aufzeigen:
a) Wo steht der Testcode, der zur Entwicklung der Domänenklasse Kunde, Veranstaltung,
Buchung führte?
b) Wo steht der Testcode, der zur Entwicklung einer Methode zur Erzeugung einer
Buchung führte.
c) Wo steht der Testcode, der den Sonderfall "gleicher Kunde, gleiche Veranstaltung,
zusätzliche Buchung" testet? Wo steht der daraus entstandene Code der Anwendung?
d) Wo steht der Testcode, der die Auflistung aller Kunden testet?
e) Wo steht der Testcode, der zu einem Kunden und einer Veranstaltung die zugehörige
Buchung anfordert?
f) Wo steht der Testcode, der das Zurückweisen einer überbuchten Veranstaltung testet?
g) Wo steht der Testcode, der die Persistierung testet?
h) Wo steht der Testcode, der die Blacklist-Funktion testet (hier sind zwei Fälle interessant:
Kunde ist/ist nicht auf der Blacklist)?
i) Wo steht der Testcode, der die E-Mail-Funktion testet (hier sind zwei Fälle interessant:
Sitzplätze >= 10% und Sitzplätze < 10%)?
j) Zeigen Sie den Abdeckungsreport und erläutern Sie, warum einzelne Passagen rot
sind.

In den verbleibenden 10 bis 15 Minuten werde ich Rückfragen stellen, ggf. Fragen Ihrerseits
beantworten, und Sie bitten, eine neue Funktion testgetrieben in Ihre Anwendung einzubauen.