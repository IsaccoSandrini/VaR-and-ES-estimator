package it.univr.riskmanagement;

import java.io.IOException;
import java.util.Arrays;

import java.time.LocalDate;


import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.random.JDKRandomGenerator;
	
/*
 * questa classe ci consente di effettuare delle simulazioni di rendimenti logaritmici e stampare il VaR e l'ES iterati 
 * di un portafoglio contenente due assets rischiosi
 */
public class MonteCarloSimulation {
	
	/*
	 * questo metodo trasforma un vettore di prezzi in un vettore di rendimenti logaritmici, allo scopo di modellizzarli
	 * con una distribuzione normale
	 */
	public static double[] logReturns (double[] data) {
		double[] logReturns = new double[data.length];
		logReturns[0]=0;
		for (int i=1; i<data.length;i++) {
			logReturns[i]= Math.log(data[i])- Math.log(data[i - 1]);			
		}
		return logReturns;
	}
	
	/* Questo metodo simula m rendimenti logaritmici possibili giornalieri da una distribuzione normale 
	 * di media mu e deviazione standard sigma calcolate all'interno del metodo, che prende come parametro i logReturns. 
	 */
	public static double [] simulateLogReturns (double[] data, int m) {
		int n = data.length;
		double mu=0;
		//calcoliamo la media campionaria mu
		for (int i=0; i<n; i++) {
			mu += data[i];		
		}
		mu = mu/(double)(n);
		//calcoliamo la deviazione standard campionaria sigma
		double sigma =0;
		for (int i=0; i<n; i++) {
			sigma += Math.pow(data[i]-mu,2);
		}
		sigma = Math.sqrt(sigma/ (double)(n));	
		//generiamo m numeri reali da una distribuzione normale di parametri mu e sigma
		NormalDistribution x = new NormalDistribution(mu, sigma);
		double [] simulations= new double [m];
		for (int i=0; i<m; i++) {
			simulations[i]= x.sample(); //questa funzione restituisce un valore random tra quelli della distribuzione normale
		}
		//restituiamo il vettore di log-rendimenti simulati
		return simulations;
	}
	
	/*
	 * in questo metodo aggreghiamo i rendimenti logaritmici simulati per i due stocks trasformandoli in rendimenti 
	 * assoluti per ottenere i rendimenti assoluti di portafoglio
	 */
	public static double[] aggregatePortfolio (double budget1, double budget2, double []data1, double []data2) {
		int m = data1.length; //numero di rendimenti simulati
		double [] portfolioReturns= new double [m];
		for (int i=0; i<m; i++) {
			//aggreghiamo e otteniamo i rendimenti assoluti di portafoglio
			portfolioReturns[i]= budget1 * Math.exp(data1[i]) + budget2 * Math.exp(data2[i]) -(budget1+budget2);			
		}
		return portfolioReturns;	//vettore di rendimenti assoluti di portafoglio	
	}
	
	/*
	 * in questo metodo procediamo alla produzione dei dati necessari per la stampa del grafico del VaR simulato
	 */
	public static void plotMonteCarloVaRSimulations(LocalDate[] dates, double[] data1, double[] data2, int s, int n,double alpha, double budget1, double budget2) {
		//calcoliamo i log rendimenti delle azioni
		double[] logData1 = logReturns(data1);
		double[] logData2 = logReturns(data2);

		int N = logData1.length - n; //N rappresenta quanti VaR simulati dovremo calcolarci
		//vettori vuoti
		double[] VaRs = new double[N]; 
		double[] sim1 = new double[s];
		double[] sim2 = new double[s];
		double[] simPortfolio = new double[s];
		//simulazione di N VaR di portafoglio
		for(int i=0; i < N; i++) {
			//simuliamo s log-rendimenti provenienti dalla distribuzione degli n log-rendimenti storici
			sim1 = simulateLogReturns(Arrays.copyOfRange(logData1, i, i+n-1), s); //consideriamo quindi per ogni giorno che passa un sotto intervallo dei rendimenti storici di lunghezza n.
			sim2 = simulateLogReturns(Arrays.copyOfRange(logData2, i, i+n-1), s); //come sopra
			//aggreghiamo i log-rendiemnti nel portafoglio e otteniamo i rendimenti assoluti di portafoglio
			simPortfolio = aggregatePortfolio(budget1, budget2, sim1, sim2);	
			//calcoliamo il VaR storico sui rendimenti di portafoglio simulati
			VaRs[i] = RiskMeasures.computeHistoricalVaR(simPortfolio, alpha);
			
			/*iteriamo il processo N volte, ovvero per il numero di giorni totali di osservazione degli assets meno n, ovvero 
			 * lunghezza della rollingWindow scelta
			 */
		}		
		//plottiamo i VaR simulati
		DataCollectionAndPlotting.plotData(dates, VaRs, "Simulated VaR with "+ s+" simulations and "+n+" observations for each VaR, with alpha = "+alpha*100+"% level");
	}
	
	/*
	 * in questo metodo procediamo alla produzione dei dati necessari per la stampa del grafico del ES simulato
	 */
	public static void plotMonteCarloESSimulations(LocalDate[] dates, double[] data1, double[] data2, int s, int n,double alpha, double budget1, double budget2) {
		//calcoliamo i log rendimenti delle azioni
		double[] logData1 = logReturns(data1);
		double[] logData2 = logReturns(data2);
		
		int N = logData1.length - n; //N rappresenta quanti ES simulati dovremo calcolarci
		
		//vettori vuoti
		double[] ESs = new double[N];
		double[] sim1 = new double[s];
		double[] sim2 = new double[s];
		double[] simPortfolio = new double[s];
		//simulazione di N ES di portafoglio
		for(int i=0; i < N; i++) {
			//simuliamo s log-rendimenti provenienti dalla distribuzione degli n log-rendimenti storici
			sim1 = simulateLogReturns(Arrays.copyOfRange(logData1, i, i+n-1), s); //consideriamo quindi per ogni giorno che passa un sotto intervallo dei rendimenti storici di lunghezza n.
			sim2 = simulateLogReturns(Arrays.copyOfRange(logData2, i, i+n-1), s); // come sopra
		
			//aggreghiamo i log-rendiemnti simulati nel portafoglio e otteniamo i rendimenti assoluti di portafoglio
			simPortfolio = aggregatePortfolio(budget1, budget2, sim1, sim2);	
			//calcoliamo l'ES storico sui rendimenti di portafoglio simulati
			ESs[i] = RiskMeasures.computeHistoricalES(simPortfolio, alpha);
			
			/* iteriamo il processo N volte, ovvero per il numero di giorni totali di osservazione degli assets meno n, ovvero 
			 * lunghezza della rollingWindow scelta
			 */
		}	
		//plottiamo gli ES simulati
		DataCollectionAndPlotting.plotData(dates, ESs, "Simulated ES with "+ s+" simulations and "+n+" observations for each ES, with alpha = "+alpha*100+"% level");
	}
	

}