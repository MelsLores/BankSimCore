import com.sun.net.httpserver.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * BankingWebServer - Servidor Web REST API para Sistema Bancario
 * Sprint 3 - Testing de Aplicacion Bancaria con Frontend
 * Versión simplificada SIN dependencias externas (sin Gson)
 * 
 * @author Jorge Pena - REM Consultancy
 * @version 4.0
 */
public class BankingWebServer {
    
    private static Map<String, Customer> customers = new HashMap<>();
    private static Map<String, Account> accounts = new HashMap<>();
    private static int PORT = 8080;
    
    public static void main(String[] args) throws IOException {
        // Inicializar datos de ejemplo
        initializeSampleData();
        
        // Crear servidor HTTP
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        
        // Configurar endpoints
        server.createContext("/", new StaticFileHandler());
        server.createContext("/api/customers", new CustomerHandler());
        server.createContext("/api/accounts", new AccountHandler());
        server.createContext("/api/deposit", new DepositHandler());
        server.createContext("/api/withdraw", new WithdrawHandler());
        server.createContext("/api/transfer", new TransferHandler());
        server.createContext("/api/balance", new BalanceHandler());
        server.createContext("/api/statement", new StatementHandler());
        server.createContext("/api/checkbook", new CheckbookHandler());
        server.createContext("/api/summary", new SummaryHandler());
        server.createContext("/api/tests", new TestsHandler());
        
        server.setExecutor(null);
        server.start();
        
        System.out.println("=".repeat(70));
        System.out.println("SISTEMA BANCARIO - SERVIDOR WEB INICIADO");
        System.out.println("=".repeat(70));
        System.out.println("Puerto: " + PORT);
        System.out.println("URL: http://localhost:" + PORT);
        System.out.println("\nAbra su navegador en: http://localhost:" + PORT);
        System.out.println("Presione Ctrl+C para detener el servidor");
        System.out.println("=".repeat(70));
    }
    
    private static void initializeSampleData() {
        Customer customer1 = new Customer("CUST001", "Juan", "Perez", "Abcd1234", 
                                         "juan.perez@email.com", "555-1234");
        customers.put("CUST001", customer1);
        
        Account account1 = new Account("123", "4567", "1234567890", customer1, 1000.00);
        accounts.put("123-4567-1234567890", account1);
    }
    
    // Handler para archivos estáticos
    static class StaticFileHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) path = "/index.html";
            
            File file = new File("frontend" + path);
            if (file.exists() && !file.isDirectory()) {
                String contentType = getContentType(path);
                byte[] content = readFileBytes(file);
                
                exchange.getResponseHeaders().set("Content-Type", contentType);
                exchange.sendResponseHeaders(200, content.length);
                OutputStream os = exchange.getResponseBody();
                os.write(content);
                os.close();
            } else {
                String response = "404 Not Found";
                exchange.sendResponseHeaders(404, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
        
        private String getContentType(String path) {
            if (path.endsWith(".html")) return "text/html";
            if (path.endsWith(".css")) return "text/css";
            if (path.endsWith(".js")) return "application/javascript";
            if (path.endsWith(".json")) return "application/json";
            return "text/plain";
        }
        
        private byte[] readFileBytes(File file) throws IOException {
            FileInputStream fis = new FileInputStream(file);
            byte[] content = new byte[(int) file.length()];
            fis.read(content);
            fis.close();
            return content;
        }
    }
    
    // Handler para clientes
    static class CustomerHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            enableCORS(exchange);
            
            if ("POST".equals(exchange.getRequestMethod())) {
                String body = readRequestBody(exchange);
                Map<String, String> data = parseJSON(body);
                
                String customerId = data.get("customerId");
                if (customers.containsKey(customerId)) {
                    sendResponse(exchange, 400, "{\"error\":\"Cliente ya existe\"}");
                    return;
                }
                
                if (!ValidationService.isValidPersonalKey(data.get("personalKey"))) {
                    sendResponse(exchange, 400, "{\"error\":\"Clave personal invalida\"}");
                    return;
                }
                
                Customer customer = new Customer(customerId, data.get("firstName"), 
                    data.get("lastName"), data.get("personalKey"), 
                    data.get("email"), data.get("phone"));
                customers.put(customerId, customer);
                
                sendResponse(exchange, 200, "{\"success\":true,\"message\":\"Cliente creado\"}");
            } else if ("GET".equals(exchange.getRequestMethod())) {
                StringBuilder json = new StringBuilder("[");
                int count = 0;
                for (Customer c : customers.values()) {
                    if (count++ > 0) json.append(",");
                    json.append("{\"customerId\":\"").append(c.getCustomerId())
                        .append("\",\"name\":\"").append(escapeJSON(c.getFullName()))
                        .append("\"}");
                }
                json.append("]");
                sendResponse(exchange, 200, json.toString());
            }
        }
    }
    
    // Handler para cuentas
    static class AccountHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            enableCORS(exchange);
            
            if ("POST".equals(exchange.getRequestMethod())) {
                String body = readRequestBody(exchange);
                Map<String, String> data = parseJSON(body);
                
                String bankCode = data.get("bankCode");
                String branchCode = data.get("branchCode");
                String accountNumber = data.get("accountNumber");
                String customerId = data.get("customerId");
                double initialBalance = Double.parseDouble(data.get("initialBalance"));
                
                if (!ValidationService.isValidBankCode(bankCode) || 
                    !ValidationService.isValidBranchCode(branchCode) ||
                    !ValidationService.isValidAccountNumber(accountNumber)) {
                    sendResponse(exchange, 400, "{\"error\":\"Codigos invalidos\"}");
                    return;
                }
                
                Customer customer = customers.get(customerId);
                if (customer == null) {
                    sendResponse(exchange, 400, "{\"error\":\"Cliente no encontrado\"}");
                    return;
                }
                
                String accountKey = bankCode + "-" + branchCode + "-" + accountNumber;
                if (accounts.containsKey(accountKey)) {
                    sendResponse(exchange, 400, "{\"error\":\"Cuenta ya existe\"}");
                    return;
                }
                
                Account account = new Account(bankCode, branchCode, accountNumber, customer, initialBalance);
                accounts.put(accountKey, account);
                
                sendResponse(exchange, 200, "{\"success\":true,\"message\":\"Cuenta creada\",\"accountKey\":\"" + accountKey + "\"}");
            } else if ("GET".equals(exchange.getRequestMethod())) {
                StringBuilder json = new StringBuilder("[");
                int count = 0;
                for (Account a : accounts.values()) {
                    if (count++ > 0) json.append(",");
                    json.append("{\"accountNumber\":\"").append(a.getFullAccountNumber())
                        .append("\",\"owner\":\"").append(escapeJSON(a.getOwner().getFullName()))
                        .append("\",\"balance\":").append(a.getBalance())
                        .append("}");
                }
                json.append("]");
                sendResponse(exchange, 200, json.toString());
            }
        }
    }
    
    // Handler para depósitos
    static class DepositHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            enableCORS(exchange);
            
            if ("POST".equals(exchange.getRequestMethod())) {
                String body = readRequestBody(exchange);
                Map<String, String> data = parseJSON(body);
                
                String accountKey = data.get("accountKey");
                double amount = Double.parseDouble(data.get("amount"));
                
                Account account = accounts.get(accountKey);
                if (account == null) {
                    sendResponse(exchange, 400, "{\"error\":\"Cuenta no encontrada\"}");
                    return;
                }
                
                account.deposit(amount);
                sendResponse(exchange, 200, "{\"success\":true,\"balance\":" + account.getBalance() + "}");
            }
        }
    }
    
    // Handler para retiros
    static class WithdrawHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            enableCORS(exchange);
            
            if ("POST".equals(exchange.getRequestMethod())) {
                String body = readRequestBody(exchange);
                Map<String, String> data = parseJSON(body);
                
                String accountKey = data.get("accountKey");
                double amount = Double.parseDouble(data.get("amount"));
                
                Account account = accounts.get(accountKey);
                if (account == null) {
                    sendResponse(exchange, 400, "{\"error\":\"Cuenta no encontrada\"}");
                    return;
                }
                
                if (account.withdraw(amount)) {
                    sendResponse(exchange, 200, "{\"success\":true,\"balance\":" + account.getBalance() + "}");
                } else {
                    sendResponse(exchange, 400, "{\"error\":\"Saldo insuficiente\"}");
                }
            }
        }
    }
    
    // Handler para transferencias
    static class TransferHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            enableCORS(exchange);
            
            if ("POST".equals(exchange.getRequestMethod())) {
                String body = readRequestBody(exchange);
                Map<String, String> data = parseJSON(body);
                
                String fromAccount = data.get("fromAccount");
                String toAccount = data.get("toAccount");
                double amount = Double.parseDouble(data.get("amount"));
                
                Account origin = accounts.get(fromAccount);
                Account destination = accounts.get(toAccount);
                
                if (origin == null || destination == null) {
                    sendResponse(exchange, 400, "{\"error\":\"Cuenta(s) no encontrada(s)\"}");
                    return;
                }
                
                if (origin.transfer(destination, amount)) {
                    sendResponse(exchange, 200, "{\"success\":true,\"balance\":" + origin.getBalance() + "}");
                } else {
                    sendResponse(exchange, 400, "{\"error\":\"Saldo insuficiente\"}");
                }
            }
        }
    }
    
    // Handler para consulta de saldo
    static class BalanceHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            enableCORS(exchange);
            
            if ("GET".equals(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI().getQuery();
                String accountKey = query.split("=")[1].replace("%2D", "-");
                
                Account account = accounts.get(accountKey);
                if (account == null) {
                    sendResponse(exchange, 400, "{\"error\":\"Cuenta no encontrada\"}");
                    return;
                }
                
                String json = "{\"accountNumber\":\"" + account.getFullAccountNumber() + "\"," +
                             "\"owner\":\"" + escapeJSON(account.getOwner().getFullName()) + "\"," +
                             "\"balance\":" + account.getBalance() + "," +
                             "\"transactions\":" + account.getTransactionCount() + "}";
                
                sendResponse(exchange, 200, json);
            }
        }
    }
    
    // Handler para estado de cuenta
    static class StatementHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            enableCORS(exchange);
            
            if ("POST".equals(exchange.getRequestMethod())) {
                String body = readRequestBody(exchange);
                Map<String, String> data = parseJSON(body);
                
                String accountKey = data.get("accountKey");
                String personalKey = data.get("personalKey");
                
                Account account = accounts.get(accountKey);
                if (account == null) {
                    sendResponse(exchange, 400, "{\"error\":\"Cuenta no encontrada\"}");
                    return;
                }
                
                if (!account.getOwner().validatePersonalKey(personalKey)) {
                    sendResponse(exchange, 400, "{\"error\":\"Clave personal incorrecta\"}");
                    return;
                }
                
                String statement = account.getStatement().replace("\n", "\\n").replace("\"", "\\\"");
                sendResponse(exchange, 200, "{\"success\":true,\"statement\":\"" + statement + "\"}");
            }
        }
    }
    
    // Handler para solicitud de chequera
    static class CheckbookHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            enableCORS(exchange);
            
            if ("POST".equals(exchange.getRequestMethod())) {
                String body = readRequestBody(exchange);
                Map<String, String> data = parseJSON(body);
                
                String accountKey = data.get("accountKey");
                String personalKey = data.get("personalKey");
                
                Account account = accounts.get(accountKey);
                if (account == null) {
                    sendResponse(exchange, 400, "{\"error\":\"Cuenta no encontrada\"}");
                    return;
                }
                
                if (account.getOwner().validatePersonalKey(personalKey)) {
                    sendResponse(exchange, 200, "{\"success\":true,\"message\":\"Chequera solicitada\"}");
                } else {
                    sendResponse(exchange, 400, "{\"error\":\"Clave personal incorrecta\"}");
                }
            }
        }
    }
    
    // Handler para resumen del banco
    static class SummaryHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            enableCORS(exchange);
            
            if ("GET".equals(exchange.getRequestMethod())) {
                double totalBalance = 0;
                int totalTransactions = 0;
                for (Account account : accounts.values()) {
                    totalBalance += account.getBalance();
                    totalTransactions += account.getTransactionCount();
                }
                
                String json = "{\"customers\":" + customers.size() + "," +
                             "\"accounts\":" + accounts.size() + "," +
                             "\"totalBalance\":" + totalBalance + "," +
                             "\"transactions\":" + totalTransactions + "}";
                
                sendResponse(exchange, 200, json);
            }
        }
    }
    
    // Handler para tests automatizados
    static class TestsHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            enableCORS(exchange);
            
            if ("GET".equals(exchange.getRequestMethod())) {
                StringBuilder json = new StringBuilder("[");
                
                // Casos válidos
                json.append(createTestJSON("TC01", "123", "4567", "1234567890", "Abcd1234", "Checkbook", true)).append(",");
                json.append(createTestJSON("TC02", "999", "9999", "9999999999", "12345678", "Statement", true)).append(",");
                json.append(createTestJSON("TC03", "456", "7890", "5678901234", "Pass1234", "Checkbook", true)).append(",");
                json.append(createTestJSON("TC04", "111", "2222", "3333333333", "Secure99", "Statement", true)).append(",");
                
                // Casos inválidos
                json.append(createTestJSON("TC05", "12", "4567", "1234567890", "Abcd1234", "Checkbook", false)).append(",");
                json.append(createTestJSON("TC06", "123", "456", "1234567890", "Abcd1234", "Checkbook", false)).append(",");
                json.append(createTestJSON("TC07", "123", "4567", "123456789", "Abcd1234", "Checkbook", false)).append(",");
                json.append(createTestJSON("TC08", "123", "4567", "1234567890", "Short1", "Checkbook", false)).append(",");
                json.append(createTestJSON("TC09", "123", "4567", "1234567890", "Abcd1234", "CreditCard", false)).append(",");
                json.append(createTestJSON("TC10", "12A", "4567", "1234567890", "Abcd1234", "Checkbook", false)).append(",");
                json.append(createTestJSON("TC11", "123", "45BC", "1234567890", "Abcd1234", "Checkbook", false)).append(",");
                json.append(createTestJSON("TC12", "123", "4567", "12345ABCDE", "Abcd1234", "Checkbook", false));
                
                json.append("]");
                sendResponse(exchange, 200, json.toString());
            }
        }
        
        private String createTestJSON(String testId, String bankCode, String branchCode,
                                      String accountNumber, String personalKey, String orderValue,
                                      boolean shouldPass) {
            boolean result = ValidationService.validateTransaction(bankCode, branchCode, 
                                                                   accountNumber, personalKey, orderValue);
            String status = (result == shouldPass) ? "OK" : "MISMATCH";
            
            return "{\"id\":\"" + testId + "\"," +
                   "\"bankCode\":\"" + bankCode + "\"," +
                   "\"branchCode\":\"" + branchCode + "\"," +
                   "\"accountNumber\":\"" + accountNumber + "\"," +
                   "\"personalKey\":\"" + personalKey + "\"," +
                   "\"orderValue\":\"" + orderValue + "\"," +
                   "\"expected\":\"" + (shouldPass ? "PASS" : "FAIL") + "\"," +
                   "\"result\":\"" + (result ? "PASS" : "FAIL") + "\"," +
                   "\"status\":\"" + status + "\"}";
        }
    }
    
    // Utilidades
    private static void enableCORS(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
    }
    
    private static String readRequestBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }
    
    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
    
    private static Map<String, String> parseJSON(String json) {
        Map<String, String> map = new HashMap<>();
        json = json.trim().substring(1, json.length() - 1); // Remove { }
        String[] pairs = json.split(",");
        for (String pair : pairs) {
            String[] kv = pair.split(":", 2);
            String key = kv[0].trim().replace("\"", "");
            String value = kv[1].trim().replace("\"", "");
            map.put(key, value);
        }
        return map;
    }
    
    private static String escapeJSON(String str) {
        return str.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
