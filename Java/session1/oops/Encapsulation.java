package session1.oops;
import java.util.Scanner;
// Encapsulation means hiding data using private variables and accessing them using getter and setter methods
class Account {
    //private data members
    private String accountHolderName;
    private double bankBalance;

    //Constructor
    Account(String accountHolderName, double bankBalance) {
        this.accountHolderName = accountHolderName;
        this.bankBalance = bankBalance;
    }

    //setter 
    public void setBalance(double balance) {
        if (balance <= 0)
            System.out.println("Not enough balance");
        else
            bankBalance = balance;
        System.out.println("Updated balance: "+ bankBalance);
    }

    //getter method which will allow us to see only required detail
    public String getName() {
        return accountHolderName;
    }
    public double getBalance() {
        return bankBalance;
    }

}

public class Encapsulation {
    public static void main(String[] args) {
        //using constructor to initialize our data member from class
        Account account = new Account("Kriti Ahuja", 20000.00);
        
        
        Scanner s = new Scanner(System.in);
        
        System.out.println("Choose an option:");
        System.out.println("1. Deposit Amount");
        System.out.println("2. Withdraw Amount");
        System.out.println("3. View Account Holder Name");
        System.out.println("4. Check Balance");
        System.out.println("5. Exit");

        int choice = s.nextInt();

        switch (choice) {
            case 1:{
                System.out.println("Add amount");
                double amount = s.nextInt();
                //calling getter
                double prevAmount = account.getBalance();
                // calling setter
                account.setBalance(prevAmount+amount);
                break;
            }
            case 2:{
                System.out.println("Enter amount to be debited");
                double amount = s.nextInt();
                //calling getter
                double prevAmount = account.getBalance();
                // calling setter
                account.setBalance(prevAmount - amount);
                break;
            }
            case 3:{
                System.out.println("Account details");
                //calling getter
                System.out.println("Account holder name: " + account.getName());
                break;
            }
            case 4: {
                System.out.println("Account details");
                //calling getter
                System.out.println("Balance: " + account.getBalance());
                break;
            }
            case 5: {
                System.out.println("Thank You for using our bank\n Have a great day!");
                break;
            }
            default:
                System.out.println("Invalid choice");
                break;
        }
        s.close();
    }
}
