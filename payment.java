import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

enum TransactionType { PAYMENT, DEDUCTION, BONUS }
enum TransactionStatus { PENDING, SUCCESS, FAILED }








    class PaymentModule{


        static class User {
            String userId;
            String name;
            String role;

            public User(String userId, String name, String role) {
                this.userId = userId;
                this.name = name;
                this.role = role;
            }
        }

        static class MilkCollection {
            float quantity;
            QualityReport qualityReport;

            public MilkCollection(float quantity, QualityReport qualityReport) {
                this.quantity = quantity;
                this.qualityReport = qualityReport;
            }
        }

        static class QualityReport {
            int score;

            public QualityReport(int score) {
                this.score = score;
            }
        }

        static class Transaction {
            String transactionId;
            BigDecimal amount;
            TransactionType type;
            TransactionStatus status;

            public Transaction(String id, BigDecimal amount, TransactionType type) {
                this.transactionId = id;
                this.amount = amount;
                this.type = type;
                this.status = TransactionStatus.PENDING;
            }
        }

        static class PaymentProcessor {
            private static final BigDecimal BASE_RATE = new BigDecimal("50");
            private static final BigDecimal BONUS_MULTIPLIER = new BigDecimal("0.10");

            public Transaction processPayment(User user, MilkCollection collection, List<BigDecimal> deductions) {

                BigDecimal baseAmount = BASE_RATE.multiply(BigDecimal.valueOf(collection.quantity));


                BigDecimal bonus = BigDecimal.ZERO;
                if(collection.qualityReport.score > 80) {
                    int scoreDiff = collection.qualityReport.score - 80;
                    bonus = BigDecimal.valueOf(scoreDiff)
                            .multiply(BONUS_MULTIPLIER)
                            .multiply(BigDecimal.valueOf(collection.quantity));
                }


                BigDecimal totalDeductions = deductions.stream()
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal totalBeforeDeductions = baseAmount.add(bonus);
                BigDecimal maxAllowedDeduction = totalBeforeDeductions.multiply(new BigDecimal("0.30"));
                BigDecimal finalDeductions = totalDeductions.min(maxAllowedDeduction);

                BigDecimal netAmount = totalBeforeDeductions.subtract(finalDeductions);
                Transaction transaction = new Transaction(
                        UUID.randomUUID().toString(),
                        netAmount,
                        TransactionType.PAYMENT
                );


                boolean paymentSuccess = mockMpesaPayment(netAmount);
                transaction.status = paymentSuccess ? TransactionStatus.SUCCESS : TransactionStatus.FAILED;


                sendSmsNotification(user, "Payment of KES " + netAmount + " processed. Status: " + transaction.status);

                return transaction;
            }

            private boolean mockMpesaPayment(BigDecimal amount) {

                return new Random().nextInt(10) > 2; 
            }

            private void sendSmsNotification(User user, String message) {
                System.out.println("[SMS to " + user.name + "]: " + message);
            }
        }

        public static void main(String[] args) {

            User farmer = new User("FARMER_001", "John Doe", "Farmer");
            QualityReport report = new QualityReport(85);
            MilkCollection collection = new MilkCollection(100.0f, report);
            List<BigDecimal> deductions = Arrays.asList(
                    new BigDecimal("500"), 
                    new BigDecimal("200")  
            );


            PaymentProcessor processor = new PaymentProcessor();
            Transaction transaction = processor.processPayment(farmer, collection, deductions);


            System.out.println("\n=== Payment Result ===");
            System.out.println("Transaction ID: " + transaction.transactionId);
            System.out.println("Amount: KES " + transaction.amount);
            System.out.println("Status: " + transaction.status);
        }
    }