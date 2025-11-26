import java.util.*;
import java.text.SimpleDateFormat;

/**
 * BankingSystem - Sistema Bancario Completo con Tests Automatizados
 * Sprint 3 - Testing de Aplicacion Bancaria
 * 
 * @author Jorge Pena - REM Consultancy
 * @version 3.0
 */
public class BankingSystem {
    
    private Map<String, Customer> customers;
    private Map<String, Account> accounts;
    private Scanner scanner;
    private boolean running;
    private String bankName;
    
    public BankingSystem() {
        this.bankName = "Banco Nacional";
        this.customers = new HashMap<>();
        this.accounts = new HashMap<>();
        this.scanner = new Scanner(System.in);
        this.running = true;
        initializeSampleData();
    }
    
    /**
     * Inicializa datos de ejemplo
     */
    private void initializeSampleData() {
        // Cliente de ejemplo
        Customer customer1 = new Customer("CUST001", "Juan", "Perez", "Abcd1234", 
                                         "juan.perez@email.com", "555-1234");
        customers.put("CUST001", customer1);
        
        // Cuenta de ejemplo: 123-4567-1234567890
        Account account1 = new Account("123", "4567", "1234567890", customer1, 1000.00);
        accounts.put("123-4567-1234567890", account1);
    }
    
    /**
     * Inicia el sistema bancario
     */
    public void start() {
        System.out.println("=".repeat(70));
        System.out.println("        SISTEMA BANCARIO - " + bankName.toUpperCase());
        System.out.println("        Sprint 3: Testing de Aplicacion Bancaria");
        System.out.println("=".repeat(70));
        System.out.println();
        
        while (running) {
            showMainMenu();
            int option = getIntInput("Seleccione una opcion: ");
            System.out.println();
            processOption(option);
        }
        
        scanner.close();
        System.out.println("\nGracias por usar el Sistema Bancario. Hasta luego!");
    }
    
    /**
     * Muestra el menu principal
     */
    private void showMainMenu() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("MENU PRINCIPAL");
        System.out.println("=".repeat(70));
        System.out.println("1. Crear Cliente");
        System.out.println("2. Crear Cuenta");
        System.out.println("3. Depositar");
        System.out.println("4. Retirar");
        System.out.println("5. Transferir");
        System.out.println("6. Consultar Saldo");
        System.out.println("7. Solicitar Chequera");
        System.out.println("8. Solicitar Estado de Cuenta");
        System.out.println("9. Ver Resumen del Banco");
        System.out.println("10. EJECUTAR TESTS AUTOMATIZADOS (12 casos del PDF)");
        System.out.println("0. Salir");
        System.out.println("=".repeat(70));
    }
    
    /**
     * Procesa la opcion seleccionada
     */
    private void processOption(int option) {
        switch (option) {
            case 1: createCustomerMenu(); break;
            case 2: createAccountMenu(); break;
            case 3: depositMenu(); break;
            case 4: withdrawMenu(); break;
            case 5: transferMenu(); break;
            case 6: checkBalanceMenu(); break;
            case 7: requestCheckbookMenu(); break;
            case 8: requestStatementMenu(); break;
            case 9: showBankSummary(); break;
            case 10: runAutomatedTests(); break;
            case 0: running = false; break;
            default: System.out.println("Opcion invalida. Intente nuevamente.");
        }
    }
    
    // ========== OPCIONES DEL MENU ==========
    
    private void createCustomerMenu() {
        System.out.println("\n--- CREAR CLIENTE ---");
        String customerId = getInput("ID del Cliente: ");
        String firstName = getInput("Nombre: ");
        String lastName = getInput("Apellido: ");
        String personalKey = getInput("Clave Personal (min 8 alfanumericos): ");
        String email = getInput("Email: ");
        String phone = getInput("Telefono: ");
        
        if (customers.containsKey(customerId)) {
            System.out.println("ERROR: Cliente ya existe.");
            return;
        }
        
        if (!ValidationService.isValidPersonalKey(personalKey)) {
            System.out.println("ERROR: Clave personal invalida (min 8 caracteres alfanumericos).");
            return;
        }
        
        Customer customer = new Customer(customerId, firstName, lastName, personalKey, email, phone);
        customers.put(customerId, customer);
        System.out.println("SUCCESS: Cliente creado exitosamente!");
        System.out.println("Cliente: " + customer.getFullName() + " (ID: " + customerId + ")");
    }
    
    private void createAccountMenu() {
        System.out.println("\n--- CREAR CUENTA ---");
        String bankCode = getInput("Codigo de Banco (3 digitos): ");
        String branchCode = getInput("Codigo de Sucursal (4 digitos): ");
        String accountNumber = getInput("Numero de Cuenta (10 digitos): ");
        String customerId = getInput("ID del Cliente: ");
        double initialBalance = getDoubleInput("Saldo Inicial: ");
        
        if (!ValidationService.isValidBankCode(bankCode) || 
            !ValidationService.isValidBranchCode(branchCode) ||
            !ValidationService.isValidAccountNumber(accountNumber)) {
            System.out.println("ERROR: Codigos de cuenta invalidos.");
            return;
        }
        
        Customer customer = customers.get(customerId);
        if (customer == null) {
            System.out.println("ERROR: Cliente no encontrado.");
            return;
        }
        
        String accountKey = bankCode + "-" + branchCode + "-" + accountNumber;
        if (accounts.containsKey(accountKey)) {
            System.out.println("ERROR: Cuenta ya existe.");
            return;
        }
        
        Account account = new Account(bankCode, branchCode, accountNumber, customer, initialBalance);
        accounts.put(accountKey, account);
        System.out.println("SUCCESS: Cuenta creada exitosamente!");
        System.out.println("Cuenta: " + accountKey);
        System.out.println("Saldo: $" + initialBalance);
    }
    
    private void depositMenu() {
        System.out.println("\n--- DEPOSITAR ---");
        String accountKey = getInput("Numero de Cuenta (XXX-XXXX-XXXXXXXXXX): ");
        double amount = getDoubleInput("Monto a depositar: ");
        
        Account account = accounts.get(accountKey);
        if (account == null) {
            System.out.println("ERROR: Cuenta no encontrada.");
            return;
        }
        
        account.deposit(amount);
        System.out.println("SUCCESS: Deposito realizado exitosamente!");
        System.out.println("Nuevo saldo: $" + account.getBalance());
    }
    
    private void withdrawMenu() {
        System.out.println("\n--- RETIRAR ---");
        String accountKey = getInput("Numero de Cuenta (XXX-XXXX-XXXXXXXXXX): ");
        double amount = getDoubleInput("Monto a retirar: ");
        
        Account account = accounts.get(accountKey);
        if (account == null) {
            System.out.println("ERROR: Cuenta no encontrada.");
            return;
        }
        
        if (account.withdraw(amount)) {
            System.out.println("SUCCESS: Retiro realizado exitosamente!");
            System.out.println("Nuevo saldo: $" + account.getBalance());
        } else {
            System.out.println("ERROR: Saldo insuficiente.");
        }
    }
    
    private void transferMenu() {
        System.out.println("\n--- TRANSFERIR ---");
        String fromAccount = getInput("Cuenta origen (XXX-XXXX-XXXXXXXXXX): ");
        String toAccount = getInput("Cuenta destino (XXX-XXXX-XXXXXXXXXX): ");
        double amount = getDoubleInput("Monto a transferir: ");
        
        Account origin = accounts.get(fromAccount);
        Account destination = accounts.get(toAccount);
        
        if (origin == null || destination == null) {
            System.out.println("ERROR: Una o ambas cuentas no encontradas.");
            return;
        }
        
        if (origin.transfer(destination, amount)) {
            System.out.println("SUCCESS: Transferencia realizada exitosamente!");
            System.out.println("Nuevo saldo cuenta origen: $" + origin.getBalance());
        } else {
            System.out.println("ERROR: Saldo insuficiente.");
        }
    }
    
    private void checkBalanceMenu() {
        System.out.println("\n--- CONSULTAR SALDO ---");
        String accountKey = getInput("Numero de Cuenta (XXX-XXXX-XXXXXXXXXX): ");
        
        Account account = accounts.get(accountKey);
        if (account != null) {
            System.out.println("\nCuenta: " + account.getFullAccountNumber());
            System.out.println("Titular: " + account.getOwner().getFullName());
            System.out.println("Saldo: $" + account.getBalance());
            System.out.println("Total de transacciones: " + account.getTransactionCount());
        } else {
            System.out.println("ERROR: Cuenta no encontrada.");
        }
    }
    
    private void requestCheckbookMenu() {
        System.out.println("\n--- SOLICITAR CHEQUERA ---");
        String accountKey = getInput("Numero de Cuenta (XXX-XXXX-XXXXXXXXXX): ");
        String personalKey = getInput("Clave Personal: ");
        
        Account account = accounts.get(accountKey);
        if (account == null) {
            System.out.println("ERROR: Cuenta no encontrada.");
            return;
        }
        
        if (account.getOwner().validatePersonalKey(personalKey)) {
            System.out.println("SUCCESS: Solicitud de chequera procesada exitosamente!");
        } else {
            System.out.println("ERROR: Clave personal incorrecta.");
        }
    }
    
    private void requestStatementMenu() {
        System.out.println("\n--- SOLICITAR ESTADO DE CUENTA ---");
        String accountKey = getInput("Numero de Cuenta (XXX-XXXX-XXXXXXXXXX): ");
        String personalKey = getInput("Clave Personal: ");
        
        Account account = accounts.get(accountKey);
        if (account == null) {
            System.out.println("ERROR: Cuenta no encontrada.");
            return;
        }
        
        if (account.getOwner().validatePersonalKey(personalKey)) {
            System.out.println("SUCCESS: Estado de cuenta generado exitosamente!");
            System.out.println("\n" + account.getStatement());
        } else {
            System.out.println("ERROR: Clave personal incorrecta.");
        }
    }
    
    private void showBankSummary() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("RESUMEN DEL BANCO: " + bankName);
        System.out.println("=".repeat(70));
        System.out.println("Total de Clientes: " + customers.size());
        System.out.println("Total de Cuentas: " + accounts.size());
        
        double totalBalance = 0;
        int totalTransactions = 0;
        for (Account account : accounts.values()) {
            totalBalance += account.getBalance();
            totalTransactions += account.getTransactionCount();
        }
        
        System.out.println("Saldo Total del Banco: $" + totalBalance);
        System.out.println("Total de Transacciones: " + totalTransactions);
        System.out.println("=".repeat(70));
    }
    
    // ========== TESTS AUTOMATIZADOS ==========
    
    /**
     * Ejecuta los 12 casos de prueba automatizados del PDF
     */
    private void runAutomatedTests() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("EJECUTANDO TESTS AUTOMATIZADOS");
        System.out.println("12 Casos de Prueba del PDF (4 Validos, 8 Invalidos)");
        System.out.println("=".repeat(70));
        
        int passed = 0;
        int failed = 0;
        
        // CASOS VALIDOS
        passed += runTest("TC01", "123", "4567", "1234567890", "Abcd1234", "Checkbook", true);
        passed += runTest("TC02", "999", "9999", "9999999999", "12345678", "Statement", true);
        passed += runTest("TC03", "456", "7890", "5678901234", "Pass1234", "Checkbook", true);
        passed += runTest("TC04", "111", "2222", "3333333333", "Secure99", "Statement", true);
        
        // CASOS INVALIDOS
        failed += runTest("TC05", "12", "4567", "1234567890", "Abcd1234", "Checkbook", false);
        failed += runTest("TC06", "123", "456", "1234567890", "Abcd1234", "Checkbook", false);
        failed += runTest("TC07", "123", "4567", "123456789", "Abcd1234", "Checkbook", false);
        failed += runTest("TC08", "123", "4567", "1234567890", "Short1", "Checkbook", false);
        failed += runTest("TC09", "123", "4567", "1234567890", "Abcd1234", "CreditCard", false);
        failed += runTest("TC10", "12A", "4567", "1234567890", "Abcd1234", "Checkbook", false);
        failed += runTest("TC11", "123", "45BC", "1234567890", "Abcd1234", "Checkbook", false);
        failed += runTest("TC12", "123", "4567", "12345ABCDE", "Abcd1234", "Checkbook", false);
        
        System.out.println("\n" + "=".repeat(70));
        System.out.println("RESULTADO DE TESTS");
        System.out.println("=".repeat(70));
        System.out.println("Casos Validos Pasados: " + passed + "/4");
        System.out.println("Casos Invalidos Fallidos: " + failed + "/8");
        System.out.println("Total: " + (passed + failed) + "/12");
        
        if (passed == 4 && failed == 8) {
            System.out.println("\nRESULT: ALL TESTS COMPLETED AS EXPECTED!");
        } else {
            System.out.println("\nWARNING: Some tests did not behave as expected.");
        }
        System.out.println("=".repeat(70));
    }
    
    /**
     * Ejecuta un caso de prueba individual
     */
    private int runTest(String testId, String bankCode, String branchCode, 
                       String accountNumber, String personalKey, String orderValue, 
                       boolean shouldPass) {
        
        System.out.println("\n--- " + testId + " ---");
        System.out.println("Input:");
        System.out.println("  Bank Code: " + bankCode);
        System.out.println("  Branch Code: " + branchCode);
        System.out.println("  Account Number: " + accountNumber);
        System.out.println("  Personal Key: " + personalKey);
        System.out.println("  Order Value: " + orderValue);
        
        boolean result = ValidationService.validateTransaction(bankCode, branchCode, 
                                                               accountNumber, personalKey, orderValue);
        
        System.out.println("Expected: " + (shouldPass ? "PASS" : "FAIL"));
        System.out.println("Result: " + (result ? "PASS" : "FAIL"));
        
        boolean testPassed = (result == shouldPass);
        if (testPassed) {
            System.out.println("Status: OK");
            return 1;
        } else {
            System.out.println("Status: MISMATCH");
            return 0;
        }
    }
    
    // ========== UTILIDADES ==========
    
    private String getInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
    
    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Por favor ingrese un numero valido.");
            }
        }
    }
    
    private double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Por favor ingrese un monto valido.");
            }
        }
    }
    
    // ========== MAIN ==========
    
    public static void main(String[] args) {
        BankingSystem system = new BankingSystem();
        system.start();
    }
}

// ========== CLASES DE SOPORTE ==========

/**
 * Customer - Cliente del banco
 */
class Customer {
    private String customerId;
    private String firstName;
    private String lastName;
    private String personalKey;
    private String email;
    private String phone;
    
    public Customer(String customerId, String firstName, String lastName, 
                   String personalKey, String email, String phone) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.personalKey = personalKey;
        this.email = email;
        this.phone = phone;
    }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public boolean validatePersonalKey(String key) {
        return this.personalKey.equals(key);
    }
    
    public String getCustomerId() { return customerId; }
}

/**
 * Account - Cuenta bancaria
 */
class Account {
    private String bankCode;
    private String branchCode;
    private String accountNumber;
    private Customer owner;
    private double balance;
    private List<Transaction> transactions;
    
    public Account(String bankCode, String branchCode, String accountNumber, 
                  Customer owner, double initialBalance) {
        this.bankCode = bankCode;
        this.branchCode = branchCode;
        this.accountNumber = accountNumber;
        this.owner = owner;
        this.balance = initialBalance;
        this.transactions = new ArrayList<>();
        
        if (initialBalance > 0) {
            transactions.add(new Transaction("DEPOSIT", initialBalance, "Saldo inicial"));
        }
    }
    
    public String getFullAccountNumber() {
        return bankCode + "-" + branchCode + "-" + accountNumber;
    }
    
    public void deposit(double amount) {
        balance += amount;
        transactions.add(new Transaction("DEPOSIT", amount, "Deposito"));
    }
    
    public boolean withdraw(double amount) {
        if (balance >= amount) {
            balance -= amount;
            transactions.add(new Transaction("WITHDRAWAL", amount, "Retiro"));
            return true;
        }
        return false;
    }
    
    public boolean transfer(Account destination, double amount) {
        if (this.withdraw(amount)) {
            destination.deposit(amount);
            transactions.add(new Transaction("TRANSFER", amount, 
                "Transferencia a " + destination.getFullAccountNumber()));
            return true;
        }
        return false;
    }
    
    public String getStatement() {
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(60)).append("\n");
        sb.append("ESTADO DE CUENTA\n");
        sb.append("=".repeat(60)).append("\n");
        sb.append("Cuenta: ").append(getFullAccountNumber()).append("\n");
        sb.append("Titular: ").append(owner.getFullName()).append("\n");
        sb.append("Saldo Actual: $").append(balance).append("\n");
        sb.append("=".repeat(60)).append("\n");
        sb.append("TRANSACCIONES:\n");
        sb.append("-".repeat(60)).append("\n");
        
        for (Transaction t : transactions) {
            sb.append(t.toString()).append("\n");
        }
        
        sb.append("=".repeat(60));
        return sb.toString();
    }
    
    public double getBalance() { return balance; }
    public Customer getOwner() { return owner; }
    public int getTransactionCount() { return transactions.size(); }
}

/**
 * Transaction - Registro de transaccion
 */
class Transaction {
    private static int nextId = 1;
    private int id;
    private String type;
    private double amount;
    private String description;
    private Date timestamp;
    
    public Transaction(String type, double amount, String description) {
        this.id = nextId++;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.timestamp = new Date();
    }
    
    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return String.format("[%d] %s | %s | $%.2f | %s", 
            id, sdf.format(timestamp), type, amount, description);
    }
}

/**
 * ValidationService - Servicio de validacion
 */
class ValidationService {
    
    public static boolean isValidBankCode(String bankCode) {
        return bankCode != null && bankCode.matches("\\d{3}");
    }
    
    public static boolean isValidBranchCode(String branchCode) {
        return branchCode != null && branchCode.matches("\\d{4}");
    }
    
    public static boolean isValidAccountNumber(String accountNumber) {
        return accountNumber != null && accountNumber.matches("\\d{10}");
    }
    
    public static boolean isValidPersonalKey(String personalKey) {
        return personalKey != null && personalKey.matches("[A-Za-z0-9]{8,}");
    }
    
    public static boolean isValidOrderValue(String orderValue) {
        return orderValue != null && 
               (orderValue.equals("Checkbook") || orderValue.equals("Statement"));
    }
    
    public static boolean validateTransaction(String bankCode, String branchCode,
                                              String accountNumber, String personalKey,
                                              String orderValue) {
        return isValidBankCode(bankCode) &&
               isValidBranchCode(branchCode) &&
               isValidAccountNumber(accountNumber) &&
               isValidPersonalKey(personalKey) &&
               isValidOrderValue(orderValue);
    }
}
