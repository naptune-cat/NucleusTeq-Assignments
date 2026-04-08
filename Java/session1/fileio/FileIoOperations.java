package session1.fileio;

import java.io.File; //allow us to perform io operations
import java.io.FileNotFoundException; //use to throw exception when file not present
import java.util.Scanner;


import java.io.FileWriter;
import java.io.IOException;

public class FileIoOperations {
    //making a static scanner object so we do not have to make one again and again for every function
    static Scanner sc = new Scanner(System.in);

    //reading from a file
    static void readOperation() {

        try {
            //creating a fileobject which points at sample.txt
            File file = new File("sample.txt");
            //Scanner opens the file for reading
            Scanner s = new Scanner(file);

            //hasNextLine() checks whether there is more data after enter("\n")
            //or the file is empty
            while (s.hasNextLine()) {
                //storing the next line in a data variable
                String data = s.nextLine();
                System.out.println(data);
            }
            //s.close() to close the Scanner which if not done can cause resource leak
            s.close();
        } catch (FileNotFoundException e) {
            //if file is not present this block will take carre of it
            System.out.println("File not found");
        }

    }

    static void writeOperation() {
        try {
            //FileWriter class is used to perform write 
            //if we had written just FileWriter("filename.txt") then the previous data of the file would have been overridden by new data 
            FileWriter writer = new FileWriter("sample.txt", true);
            
            //write method is used to give wat we have to write
            writer.write("Today I learnt a lot of things like \n1. Reading from file \n2. Writing inside file");
            
            writer.close();
            System.out.println("Data added successfully");
        }
        catch(IOException e) {
            System.out.println("Error writing file");
        }
    }

    public static void main(String[] args) {
        System.out.println("What do you want?");
        System.out.println("1. Read from file");
        System.out.println("2. Write on file");

        int choice = sc.nextInt();

        switch (choice) {
            case 1: {
                readOperation();
                break;
            }
            case 2: {
                writeOperation();
                System.out.println("Updated file-");
                readOperation();
                break;
            }
            default: {
                System.out.println("Invalid Choice Selected");
                break;
            }
                
        }
        sc.close();
    }
}

