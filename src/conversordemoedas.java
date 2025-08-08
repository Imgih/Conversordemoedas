import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class conversordemoedas {

    public static void main(String[] args) {

        String apiKey = carregarChaveAPI();
        if (apiKey == null) {
            System.out.println("Erro: chave da API não encontrada.");
            return;
        }

        String baseUrl = "https://v6.exchangerate-api.com/v6/" + apiKey + "/latest/";

        Scanner scanner = new Scanner(System.in);
        boolean continuar = true;

        while (continuar) {
            System.out.println("Escolha uma das opções de conversão de moedas:");
            System.out.println("1. Dólar para Real");
            System.out.println("2. Euro para Real");
            System.out.println("3. Real para Dólar");
            System.out.println("4. Real para Euro");
            System.out.println("5. Dólar para Euro");
            System.out.println("6. Won para Real");
            System.out.println("0. Sair");
            System.out.print("Opção: ");

            int opcao = scanner.nextInt();

            if (opcao == 0) {
                continuar = false;
                System.out.println("Saindo do programa...");
            } else {
                System.out.print("Digite o valor a ser convertido: ");
                double valor = scanner.nextDouble();

                String moedaBase = "", moedaAlvo = "";

                switch (opcao) {
                    case 1:
                        moedaBase = "USD";
                        moedaAlvo = "BRL";
                        break;
                    case 2:
                        moedaBase = "EUR";
                        moedaAlvo = "BRL";
                        break;
                    case 3:
                        moedaBase = "BRL";
                        moedaAlvo = "USD";
                        break;
                    case 4:
                        moedaBase = "BRL";
                        moedaAlvo = "EUR";
                        break;
                    case 5:
                        moedaBase = "USD";
                        moedaAlvo = "EUR";
                        break;
                    case 6:
                        moedaBase = "KRW";
                        moedaAlvo = "BRL";
                        break;
                    default:
                        System.out.println("Opção inválida! Tente novamente.");
                        continue;
                }

                double taxaConversao = obterTaxaDeConversao(baseUrl, moedaBase, moedaAlvo);
                if (taxaConversao != -1) {
                    double resultado = valor * taxaConversao;
                    System.out.printf("Resultado: %.2f %s = %.2f %s%n", valor, moedaBase, resultado, moedaAlvo);
                }
            }
        }
        scanner.close();
    }


    public static String carregarChaveAPI() {
        Properties properties = new Properties();
        try {
            FileInputStream fis = new FileInputStream("config.properties");
            properties.load(fis);
            return properties.getProperty("apiKey");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static double obterTaxaDeConversao(String baseUrl, String moedaBase, String moedaAlvo) {
        try {
            URL url = new URL(baseUrl + moedaBase);  // Construir URL para a API
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            Scanner scanner = new Scanner(connection.getInputStream());
            StringBuilder response = new StringBuilder();
            while (scanner.hasNext()) {
                response.append(scanner.nextLine());
            }
            scanner.close();

            JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
            JsonObject taxas = jsonResponse.getAsJsonObject("conversion_rates");

            if (taxas.has(moedaAlvo)) {
                return taxas.get(moedaAlvo).getAsDouble();
            } else {
                System.out.println("Erro: Moeda de destino não encontrada.");
                return -1;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
