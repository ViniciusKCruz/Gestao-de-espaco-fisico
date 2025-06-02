import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.InputMismatchException;
import java.util.Scanner;

public class EspacoFisicoDAO {
    private static final String URL = "jdbc:postgresql://localhost:5432/gestao_de_espaco_fisico";
    private static final String USER = "postgres";
    private static final String PASSWORD = "xxxxxxx"; //COLOQUE A SENHA DO SEU BANCO DE DADOS PROFESSOR

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("\nSistema de Gestão de Espaço Físico");
                System.out.println("1. Fazer Solicitação de Reserva");
                System.out.println("2. Avaliar Solicitações (Gestor)");
                System.out.println("3. Visualizar Histórico de Solicitações");
                System.out.println("4. Sair");
                System.out.print("Escolha uma opção: ");

                try {
                    int opcao = scanner.nextInt();
                    scanner.nextLine();

                    switch (opcao) {
                        case 1:
                            fazerSolicitacao(connection, scanner);
                            break;
                        case 2:
                            avaliarSolicitacoes(connection, scanner);
                            break;
                        case 3:
                            visualizarHistorico(connection);
                            break;
                        case 4:
                            System.out.println("Saindo...");
                            return;
                        default:
                            System.out.println("Opção inválida. Tente novamente.");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Entrada inválida. Por favor, insira um número.");
                    scanner.nextLine();
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao conectar ao banco de dados: " + e.getMessage());
        }
    }

    private static void fazerSolicitacao(Connection connection, Scanner scanner) {
        try {
            //Exibe a data e hora atuais formatadas
            LocalDateTime agora = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            String dataHoraAtual = agora.format(formatter);
            System.out.println("Data e Hora da Solicitação: " + dataHoraAtual);

            //Solicitar dados para a reserva
            System.out.print("Informe o seu ID de solicitante: ");
            int solicitanteId = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Informe o ID do espaço que deseja reservar: ");
            int espacoId = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Informe a data desejada (YYYY-MM-DD): ");
            String dataString = scanner.nextLine();

            //Valida o formato da data
            if (!isValidDate(dataString)) {
                System.out.println("Formato de data inválido. Use o formato YYYY-MM-DD.");
                return;
            }
            Date data = Date.valueOf(dataString);

            System.out.print("Informe a hora desejada (HH:MM): ");
            String horaString = scanner.nextLine();

            //Valida o formato da hora
            if (!isValidTime(horaString)) {
                System.out.println("Formato de hora inválido. Use o formato HH:MM.");
                return;
            }

            //Solicitar a justificativa e validar
            String justificativa = "";
            do {
                System.out.print("Informe a justificativa para a reserva (obrigatório): ");
                justificativa = scanner.nextLine().trim();
                if (justificativa.isEmpty()) {
                    System.out.println("A justificativa não pode ficar vazia. Por favor, informe uma justificativa.");
                }
            } while (justificativa.isEmpty());

            //Converte a hora para java.sql.Time
            Time hora = Time.valueOf(horaString + ":00");

            String sql = "INSERT INTO solicitacoes (solicitante_id, espaco_id, data, hora, status, justificativa) VALUES (?, ?, ?, ?, 'PENDENTE', ?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, solicitanteId);
                stmt.setInt(2, espacoId);
                stmt.setDate(3, data);
                stmt.setTime(4, hora);
                stmt.setString(5, justificativa);
                stmt.executeUpdate();
                System.out.println("Solicitação enviada com sucesso!");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fazer solicitação: " + e.getMessage());
        } catch (InputMismatchException e) {
            System.out.println("Entrada inválida. Por favor, insira um número válido.");
            scanner.nextLine(); // Limpa a entrada incorreta
        }
    }


    private static boolean isValidDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        try {
            sdf.parse(date); // Verifica se a data é válida
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private static boolean isValidTime(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        sdf.setLenient(false);
        try {
            sdf.parse(time); // Verifica se a hora é válida
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private static void avaliarSolicitacoes(Connection connection, Scanner scanner) {
        try {
            //Exibe as solicitações pendentes
            String sql = "SELECT id, solicitante_id, espaco_id, data, hora FROM solicitacoes WHERE status = 'PENDENTE'";
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 var rs = stmt.executeQuery()) {

                if (!rs.next()) {
                    System.out.println("Não há solicitações pendentes.");
                    return;
                }

                do {
                    //Exibe os detalhes da solicitação
                    System.out.println("\nSolicitação ID: " + rs.getInt("id"));
                    System.out.println("Solicitante ID: " + rs.getInt("solicitante_id"));
                    System.out.println("Espaço ID: " + rs.getInt("espaco_id"));
                    System.out.println("Data: " + rs.getDate("data"));
                    System.out.println("Hora: " + rs.getTime("hora"));

                    //Solicitar ao gestor a decisão
                    System.out.print("Deseja aprovar ou rejeitar esta solicitação? (A - Aprovar / R - Rejeitar): ");
                    String decisao = scanner.nextLine().toUpperCase();

                    if (decisao.equals("A")) {
                        //Aprovar a solicitação
                        String updateSql = "UPDATE solicitacoes SET status = 'APROVADA' WHERE id = ?";
                        try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                            updateStmt.setInt(1, rs.getInt("id"));
                            updateStmt.executeUpdate();
                            System.out.println("Solicitação aprovada com sucesso!");
                        }
                    } else if (decisao.equals("R")) {
                        //Rejeitar a solicitação
                        String updateSql = "UPDATE solicitacoes SET status = 'REJEITADA' WHERE id = ?";
                        try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                            updateStmt.setInt(1, rs.getInt("id"));
                            updateStmt.executeUpdate();
                            System.out.println("Solicitação rejeitada.");
                        }
                    } else {
                        System.out.println("Opção inválida. Solicitação não foi alterada.");
                    }
                } while (rs.next());
            }
        } catch (SQLException e) {
            System.err.println("Erro ao avaliar solicitações: " + e.getMessage());
        }
    }

    private static void visualizarHistorico(Connection connection) {
        try {
            //Consulta para exibir todas as solicitações, independentemente do status
            String sql = "SELECT id, solicitante_id, espaco_id, data, hora, status FROM solicitacoes ORDER BY data DESC, hora DESC";
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 var rs = stmt.executeQuery()) {

                if (!rs.next()) {
                    System.out.println("Não há histórico de solicitações.");
                    return;
                }

                //Exibe os dados das solicitações
                System.out.println("\nHistórico de Solicitações:");
                do {
                    System.out.println("\nSolicitação ID: " + rs.getInt("id"));
                    System.out.println("Solicitante ID: " + rs.getInt("solicitante_id"));
                    System.out.println("Espaço ID: " + rs.getInt("espaco_id"));
                    System.out.println("Data: " + rs.getDate("data"));
                    System.out.println("Hora: " + rs.getTime("hora"));
                    System.out.println("Status: " + rs.getString("status"));
                } while (rs.next());

            }
        } catch (SQLException e) {
            System.err.println("Erro ao visualizar histórico de solicitações: " + e.getMessage());
        }
    }

}
