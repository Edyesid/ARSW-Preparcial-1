package edu.eci.arsw.moneylaundering;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MyThread extends Thread {
	
	private List<File> transactionFiles;
	private TransactionReader transactionReader;
	private TransactionAnalyzer transactionAnalyzer;
	private AtomicInteger amountOfFilesProcessed;
	
	public MyThread(List<File> transactionFiles) {
		this.transactionFiles = transactionFiles;
		transactionReader = new TransactionReader();
		transactionAnalyzer = new TransactionAnalyzer();
	}
	
	@Override
	public void run() {
		System.out.println("running" + this.getName());
		for(File transactionFile : transactionFiles) {
			List<Transaction> transactions = transactionReader.readTransactionsFromFile(transactionFile);
			for(Transaction transaction : transactions) {
				
                transactionAnalyzer.addTransaction(transaction);
            }
		}
	}
	
	public AtomicInteger getAmountOfFilesProcessed() {
		return amountOfFilesProcessed;
	}
}
