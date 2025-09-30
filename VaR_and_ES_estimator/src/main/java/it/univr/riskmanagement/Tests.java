package it.univr.riskmanagement;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;


/*
Questa è la classe test che produce i grafici a partire dalla specificazione dei vari parametri di modello. 
*/

public class Tests {

	public static void main(String[] args) throws IOException {
		DataManagement tester = new DataManagement();
		
		//chiedete a tester di plottare i prezzi del titolo 1
		//chiedete a tester di plottare i prezzi del titolo 2
		tester.plotPricesStock1(); // Tramite tester siamo in grado di richiamare i metodi plotPricesStock1 e plotPricesStock2
		tester.plotPricesStock2(); // i quali ci permettono di plottare i grafici dei prezzi dei due stocks
		
		
		double budget1 = 1000;// Assegniamo in modo arbitrario un valore €1000 da investire in ciascun titolo
		double budget2 = 1000;// 
		
		/* Qui abbiamo chiesto a tester di richiamare il metodo getPortfolioReturns dalla classe DataManagement per calcolare i rendimenti assoluti giornalieri
		   del portafoglio. Successivamente abbiamo chiesto a tester di plottare il grafico dei rendimenti del portafoglio
		 */ 
		double[] returns = tester.getPortfolioReturns(budget1, budget2); 
		tester.plotPortfolioReturns(budget1, budget2);
		
		// windowLength è la lunghezza della finestra mobile (in giorni) per il calcolo di VaR/ES.
		// i due double alphaVaR e alphaES sono i livelli scelti dal regolatore per il calcolo delle misure di rischio, rispettivamente in BasileaII e BasileaIII.
		int windowLength = 250;  
		double alphaVaR = 0.01;
		double alphaES = 0.025;
		
		
		//abbiamo creato un array di tipo LocalDate che considera l'intero arco temporale esclusi i primi 250 dati, e per selezionare il sotto intervallo abbiamo usato la funzione copyOfRange
		LocalDate[] newDates = Arrays.copyOfRange(DataCollectionAndPlotting.getDates(), windowLength-1, DataCollectionAndPlotting.getDates().length -1);
		

		//usate i metodi della classe RiskMeasures per plottare le due misure di rischio nel tempo
		// Abbiamo richiamato i metodi "plotIterateHistoricalVaR" e "plotIterateHistoricalES" dalla classe RiskMeasures per calcolare e 
		// plottare le due misure di rischio iterate, calcolate secondo il metodo storico derivante dall'analisi dei rendimenti passati
		RiskMeasures.plotIterateHistoricalVaR(newDates, returns, alphaVaR, windowLength);
		RiskMeasures.plotIterateHistoricalES(newDates, returns, alphaES, windowLength);
		
		// MONTECARLO
		// Qui abbiamo dovuto richiamare i metodi "getPricesStock1()" e "getPricesStock2()" dalla classe Data Management i quali forniscono 
		// gli array dei prezzi storici dei due titoli, necessari per sviluppare la simulazione MonteCarlo
		double [] price1= tester.getPricesStock1();
		double [] price2= tester.getPricesStock2();
		
		int numberOfSimulations=10000; // Abbiamo scelto in modo arbitrario il numero di simulazioni che verranno generate per stimare VaR e ES secondo il metodo MonteCarlo
		

		// Infine qui richiamiamo i metodi "plotMonteCarloVaRSimulations" e "plotMonteCarloESSimulations" dalla classe MonteCarloSimulation,
		// i quali procedono alla produzione dei dati necessari per il calcolo e la stampa dei grafici dei VaR ed ES simulati
		MonteCarloSimulation.plotMonteCarloVaRSimulations(newDates, price1, price2, numberOfSimulations, windowLength, alphaVaR, budget1, budget2);
		MonteCarloSimulation.plotMonteCarloESSimulations(newDates, price1, price2, numberOfSimulations, windowLength, alphaES, budget1, budget2);
		
		
		
	}
}
