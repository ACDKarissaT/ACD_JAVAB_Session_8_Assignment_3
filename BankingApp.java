 package assignment3;
 
class Account{
	volatile double balance;
	double unBalance;
	public Account(double balance) {
		this.balance = balance;
		this.unBalance = balance;
	}
	
	synchronized void withdraw(double amt) {
		if (amt > 0 && balance >= amt) {
			balance -= amt;
		}
	}
	
	synchronized void deposit(double amt) {
		if (amt > 0) {
			this.balance += amt;
		}
	}
	
	synchronized double getBalance() {
		return this.balance;
	}
	
	void unsyncDeposit(double amt) {
		if (amt > 0) {
			this.unBalance += amt;
		}
	}
	
	void unsyncWithdraw (double amt) {
		if (amt > 0 && balance >= amt) {
			unBalance -= amt;
		}
	}
	
	double getUnBalance() {
		return this.unBalance;
	}
}

class SyncTransaction extends Thread{
	volatile Account acc;
	int transactionType;
	double amt;
	
	public SyncTransaction(Account acc, int transactionType, double amt) {
		this.acc = acc;
		this.transactionType = transactionType;
		this.amt = amt;
	}
	
	@Override
	public void run() {
		synchronized (this) {
			if (transactionType == 0) {
				deposit(amt);
			} else {
				withdraw(amt);
			}
			notify();
		}
		
	}
	
	void withdraw (double amt) {
		acc.withdraw(amt);
	}
	
	void deposit  (double amt) {
		acc.deposit(amt);
	}
	
}

class UnsyncTransaction extends Thread{
	Account acc;
	int transactionType;
	double amt;
	
	public UnsyncTransaction(Account acc, int transactionType, double amt) {
		this.acc = acc;
		this.transactionType = transactionType;
		this.amt = amt;
	}
	
	@Override
	public void run() {
		if (transactionType == 0) {
			deposit(amt);
		} else {
			withdraw(amt);
		}
	}
	
	void withdraw (double amt) {
		acc.unsyncWithdraw(amt);
	}
	
	void deposit  (double amt) {
		acc.unsyncDeposit(amt);
	}
}


public class BankingApp {
	public static void main(String[] args) {
		
		Account aa = new Account(1000);
		Thread syn = null;
		int loop = 10000;
		for (int i = 0; i< loop; i++) {
			syn = new SyncTransaction(aa, 0, 100);
			syn.start();
			syn = new SyncTransaction(aa, 1, 50);
			syn.start();
		}
		
		synchronized (syn) {
			try {
				syn.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Deposits 100 and withdraws 50 " + loop + " times. With balance starting at 1000");
		System.out.println("Should be: " + (1000 + (100-50)*loop));
		System.out.println("Synchronized balance = " + aa.getBalance());
		for(int i = 0; i < loop; i++) {
			syn = new UnsyncTransaction(aa, 0, 100);
			syn.start();
			syn = new UnsyncTransaction(aa, 1, 50);
			syn.start();
		}
		
		
		System.out.println("Unsynchronized balance = " + aa.getUnBalance());
	}
}
