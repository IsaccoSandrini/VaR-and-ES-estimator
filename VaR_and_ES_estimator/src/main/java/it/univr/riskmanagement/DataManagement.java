package it.univr.riskmanagement;

import java.io.IOException;
import java.util.Arrays;

import java.time.LocalDate;


import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.random.JDKRandomGenerator;

/*
Questa classe usa l'output di DataCollectionAndPlotting per calcolare i rendimenti storici del portafoglio d'investimento
e produrre i grafici corrispondenti.
*/

public class DataManagement {
		
	private double[] pricesStock1;
	private double[] pricesStock2;
	private LocalDate[] dates;
	
	public DataManagement() throws IOException {
		pricesStock1 = DataCollectionAndPlotting.getHistoricalPricesStock1();//Abbiamo utilizzato il metodo getHistoricalPricesStock1 per inizializzare il vettore dei prezzi giornalieri del titolo 1
		pricesStock2 = DataCollectionAndPlotting.getHistoricalPricesStock2();//Abbiamo utilizzato il metodo getHistoricalPricesStock2 per inizializzare il vettore dei prezzi giornalieri del titolo 2
		dates = DataCollectionAndPlotting.getDates(); // Qui abbiamo fatto lo stesso ma per inizializzare il vettore delle date
	}
	
	 // Questo metodo restituisce un vettore double di rendimenti assoluti giornalieri di portafoglio
	
	public double[] getPortfolioReturns(double budget1, double budget2) {
		int n = pricesStock1.length; // abbiamo salvato la lunghezza dell'array che inizializziamo di seguito (pricesStock1 e 2 hanno la stessa lunghezza)
		double[] returns = new double[n]; // questo array memorizza tutti i rendimenti giornalieri del nostro portafoglio titoli, al momento è vuoto
		returns[0] = 0;
		for (int t = 1; t < n; t++) { 			
			// Calcoliamo il valore del portafoglio al tempo t, moltiplicando il numero di azioni possedute al giorno t-1 per il prezzo
			// delle stesse azioni al giorno t, e sommando dunque il valore delle azioni possedute
			double valorePortafoglio = (budget1 / pricesStock1[t-1]) * pricesStock1[t] + (budget2 / pricesStock2[t-1]) * pricesStock2[t]; 
			// Qui calcoliamo invece il rendimento giornaliero al giorno t derivante dalla differenza del  valore del portafoglio con l'investimento inziale.
			double rendimentoAssoluto = valorePortafoglio - (budget1 + budget2); 
			returns[t] = rendimentoAssoluto; // Memorizziamo il rendimento registrato al giorno t nell'array "returns"
		}
		return returns;
	}
	
	/*
	 * Questi due metodi restituiscono gli array contenenti i prezzi giornalieri delle due azioni, 
	 * richiamati poi nella classe Tests per effettuare le simulazioni tramite Monte Carlo
	 */
	public double [] getPricesStock1() {
		return pricesStock1;
	}
	
	public double [] getPricesStock2() {
		return pricesStock2;
	}
	

	
	/*
	 * Con i metodi successivi abbiamo plottato i grafici dei prezzi delle due azioni e dei rendimenti 
	 * percentuali/logaritmici del nostro portafoglio
	 */
	
	public void plotPricesStock1() throws IOException {
		DataCollectionAndPlotting.plotData(dates, pricesStock1, "Prices Stock 1");
	}	
	
	public void plotPricesStock2() throws IOException {
		DataCollectionAndPlotting.plotData(dates, pricesStock2, "Prices Stock 2");
	}
	
	public double[] plotPortfolioReturns(double budget1, double budget2) throws IOException {	
		double[] returns = getPortfolioReturns(budget1, budget2);
		DataCollectionAndPlotting.plotData(dates, returns, "Portfolio Returns");
		return returns;
	}

}
