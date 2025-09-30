package it.univr.riskmanagement;

import java.time.LocalDate;
import java.util.Arrays;

import org.apache.commons.math3.distribution.NormalDistribution;

/*
Questa classe definisce la misura di rischio di riferimento.
*/

public class RiskMeasures {

	/*
	 * Questo metodo calcola il VaR storico data la sequenza di rendimenti assoluti e il livello alpha, attraverso la formula vista a lezione.
	 */
	public static double computeHistoricalVaR(double[] data, double alpha) {	
		Arrays.sort(data); // questa funzione ordina in modo crescente l'array
		int n = data.length;
		int k = (int)((double)(n) * alpha) +1;	//parte intera di n*alpha, poi +1
		double VaR = -data[k];		
		return VaR;
	}

	
	/*
	 * Questo metodo calcola l'ES storico data la sequenza di rendimenti assoluti e il livello alpha, utilizzando la formula vista a lezione.
	 */
	public static double computeHistoricalES(double[] data, double alpha) {
		
		Arrays.sort(data);
		int n = data.length;
		int k = (int)((double)(n) * alpha);  //parte intera di n*alpha
		double ES1 = 0;
		
		for(int i=0; i < k; i++) {
			ES1 += data[i];
		}
		
		ES1 = -ES1/((double)(n) * alpha);
		//moltiplichiamo il rendimento in posizione k+1 per (alpha - k/n) / alpha
		double ES2 = -data[(k+1)]*(alpha - ((double)(k)/(double)(n)) )/alpha;  
		double ES = ES1 + ES2;		
		return ES;		
	}

	/*
	 * calcoliamo la sequenza di VaR iterati, calcolando il VaR dei dati presenti all'interno della rolling window di lunghezza
	 * pari a windowLength che continua ad avanzare giornalmente.
	 */
	public static double[] iterateHistoricalVaR(double[] data, double alpha, int windowLength) throws  IllegalArgumentException{
        
		int N = (data.length) - windowLength;
		double[] VaR = new double[N];
		
		for(int i=0; i<N; i++) {
			VaR[i]= computeHistoricalVaR(Arrays.copyOfRange(data, i, i + windowLength - 1), alpha);
		}	
		return VaR;	
	}		
	
	/*
	 * calcoliamo la sequenza di ES iterati, calcolando l'ES dei dati presenti all'interno della rolling window di lunghezza
	 * pari a windowLength che continua ad avanzare giornalmente.
	 */
	public static double[] iterateHistoricalES(double[] data, double alpha, int windowLength) throws  IllegalArgumentException{
		
		int N = (data.length) - windowLength;
		double[] ES = new double[N];
		for(int i=0; i<N; i++) {
			ES[i]= computeHistoricalES(Arrays.copyOfRange(data, i, i + windowLength - 1), alpha);
		}		
		return ES;
	}
		
	/*
	 * in questi due metodi abbiamo calcolato le misure iterate, e successivamente le abbiamo plottate tramite il metodo 
	 * "plotData()" della classe "DataCollectionAndPlotting".
	 */
	public static void plotIterateHistoricalVaR(LocalDate[] dates, double[] data, double alpha, int windowLength) throws  IllegalArgumentException{
		double [] iHVaR = iterateHistoricalVaR(data, alpha, windowLength);	
		DataCollectionAndPlotting.plotData(dates, iHVaR, "Historical VaR with alpha: " + alpha + " and window length: " + windowLength);
	}
	
	public static void plotIterateHistoricalES(LocalDate[] dates, double[] data, double alpha, int windowLength) throws  IllegalArgumentException{
		double [] iHES = iterateHistoricalES(data, alpha, windowLength);		
		DataCollectionAndPlotting.plotData(dates, iHES, "Historical ES with alpha: " + alpha + " and window length: " + windowLength);
	}
	
}
