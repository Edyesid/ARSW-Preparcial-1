package edu.eci.arsw.moneylaundering;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MoneyLaundering
{
    private TransactionAnalyzer transactionAnalyzer;
    private int amountOfFilesTotal;
    private static AtomicInteger amountOfFilesProcessed;
    private int NUMTHREADS = 5;
    private int div;
    private int mod;
    private static CopyOnWriteArrayList<MyThread> lista;

    public MoneyLaundering()
    {
        transactionAnalyzer = new TransactionAnalyzer();
        amountOfFilesProcessed = new AtomicInteger();
    }

    public void processTransactionData()
    {
    	
        amountOfFilesProcessed.set(0);
        
        List<File> transactionFiles = getTransactionFileList();
        amountOfFilesTotal = transactionFiles.size();
        
        div = amountOfFilesTotal / NUMTHREADS;
        mod = amountOfFilesTotal % NUMTHREADS;
        
        lista = new CopyOnWriteArrayList<MyThread>();
        
        boolean check = (mod == 0);
        
        if(check) {
        	
        	for (int i = 0; i < NUMTHREADS; i++) {
        		lista.add(new MyThread(transactionFiles.subList(i * div, (i * div) + div)));
        	}
        } else {
        	for (int j = 0; j < NUMTHREADS; j++) {
        		if (j == NUMTHREADS - 1) {
        			lista.add(new MyThread(transactionFiles.subList(j * div, div + mod + (j * div))));
        		} else {
        			lista.add(new MyThread(transactionFiles.subList(j * div, (j * div) + div)));
        		}
        	}
        }
        
        for (MyThread thread : lista) {
        	thread.start();
        }
        
    }

    public List<String> getOffendingAccounts()
    {
        return transactionAnalyzer.listOffendingAccounts();
    }

    private List<File> getTransactionFileList()
    {
        List<File> csvFiles = new ArrayList<>();
        try (Stream<Path> csvFilePaths = Files.walk(Paths.get("src/main/resources/")).filter(path -> path.getFileName().toString().endsWith(".csv"))) {
            csvFiles = csvFilePaths.map(Path::toFile).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return csvFiles;
    }

    public static void main(String[] args) {
        MoneyLaundering moneyLaundering = new MoneyLaundering();
        Thread processingThread = new Thread(() -> moneyLaundering.processTransactionData());
        processingThread.start();
        while(true)
        {
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            if(line.contains("exit"))
                break;
            String message = "Processed %d out of %d files.\nFound %d suspect accounts:\n%s";
            List<String> offendingAccounts = moneyLaundering.getOffendingAccounts();
            String suspectAccounts = offendingAccounts.stream().reduce("", (s1, s2)-> s1 + "\n"+s2);
            message = String.format(message, moneyLaundering.amountOfFilesProcessed.get(), moneyLaundering.amountOfFilesTotal, offendingAccounts.size(), suspectAccounts);
            System.out.println(message);
        }

    }


}
