package id.co.bankbsi.rizqtracker.service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import id.co.bankbsi.rizqtracker.dto.response.CashflowSummaryResponse;
import id.co.bankbsi.rizqtracker.model.Account;
import id.co.bankbsi.rizqtracker.model.Transaction;
import id.co.bankbsi.rizqtracker.model.User;
import id.co.bankbsi.rizqtracker.repository.AccountRepository;
import id.co.bankbsi.rizqtracker.repository.TransactionRepository;
import id.co.bankbsi.rizqtracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class PdfGeneratorService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionService transactionService;

    @Value("${app.name:RizqTracker}")
    private String appName;
    
    // Indonesian locale for date formatting
    private static final Locale LOCALE_ID = new Locale("id", "ID");
    
    public byte[] generateMonthlyTransactionReport(Integer userId, YearMonth period) throws IOException {
        // Prepare document
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Get user info
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Pengguna tidak ditemukan"));

        Optional<Account> accountOptional = accountRepository.findByUserId(userId);
        if (accountOptional.isEmpty()) {
            throw new RuntimeException("Akun tidak ditemukan untuk pengguna");
        }
        Account account = accountOptional.get();

        // Get period start and end dates
        LocalDateTime startDate = period.atDay(1).atStartOfDay();
        LocalDateTime endDate = period.atEndOfMonth().atTime(23, 59, 59);

        // Get summary data
        CashflowSummaryResponse summary = transactionService.getCashflowSummary(userId, startDate, endDate);

        // Get transactions for the month
        List<Transaction> sentTransactions = transactionRepository
                .findByTransactionType_NameAndSenderAccount_User_IdAndCreatedAtBetween(
                        "transfer", userId, startDate, endDate);
        
        List<Transaction> receivedTransactions = transactionRepository
                .findByTransactionType_NameAndRecipientAccount_User_IdAndCreatedAtBetween(
                        "transfer", userId, startDate, endDate);
                
        List<Transaction> topupTransactions = transactionRepository
                .findByTransactionType_NameAndSenderAccount_User_IdAndCreatedAtBetween(
                        "topup", userId, startDate, endDate);

        // Generate the PDF content
        addHeader(document, user, account, period);
        addSummarySection(document, summary);
        addTransactionSection(document, sentTransactions, receivedTransactions, topupTransactions);
        
        document.close();
        return baos.toByteArray();
    }

    private void addHeader(Document document, User user, Account account, YearMonth period) {
        // Company header with logo
        Paragraph header = new Paragraph(appName)
                .setBold()
                .setFontSize(20)
                .setFontColor(new DeviceRgb(0, 128, 0));
        document.add(header);
        
        // Report title
        document.add(new Paragraph("Laporan Transaksi Bulanan")
                .setBold()
                .setFontSize(16));
        
        // Period info - use Indonesian month names
        DateTimeFormatter idnMonthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", LOCALE_ID);
        document.add(new Paragraph("Periode: " + period.format(idnMonthYearFormatter))
                .setFontSize(12));
        
        // Date generated
        DateTimeFormatter idnDateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", LOCALE_ID);
        document.add(new Paragraph("Dibuat pada: " + LocalDate.now().format(idnDateFormatter))
                .setFontSize(10));
        
        // User info
        document.add(new Paragraph("\nInformasi Pengguna").setBold().setFontSize(14));
        
        Table userTable = new Table(UnitValue.createPercentArray(new float[]{30, 70}));
        userTable.setWidth(UnitValue.createPercentValue(100));
        
        userTable.addCell(createCell("Nama Lengkap:", true));
        userTable.addCell(createCell(user.getFullName(), false));
        
        userTable.addCell(createCell("Email:", true));
        userTable.addCell(createCell(user.getEmail(), false));
        
        userTable.addCell(createCell("Nomor Rekening:", true));
        userTable.addCell(createCell(account.getAccountNumber().toString(), false));
        
        userTable.addCell(createCell("Saldo Saat Ini:", true));
        userTable.addCell(createCell(formatCurrency(account.getBalance()), false));
        
        document.add(userTable);
        document.add(new Paragraph("\n"));
    }

    private void addSummarySection(Document document, CashflowSummaryResponse summary) {
        document.add(new Paragraph("Ringkasan Keuangan").setBold().setFontSize(14));
        
        Table summaryTable = new Table(UnitValue.createPercentArray(new float[]{30, 70}));
        summaryTable.setWidth(UnitValue.createPercentValue(100));
        
        // Income section
        summaryTable.addCell(createHeaderCell("PENDAPATAN", 2));
        
        summaryTable.addCell(createCell("Topup:", true));
        summaryTable.addCell(createCell(formatCurrency(summary.getSummary().getIncome().getTotalTopup()), false));
        
        summaryTable.addCell(createCell("Transfer Masuk:", true));
        summaryTable.addCell(createCell(formatCurrency(summary.getSummary().getIncome().getTotalTransfer()), false));
        
        summaryTable.addCell(createCell("Total Pendapatan:", true));
        long totalIncome = summary.getSummary().getIncome().getTotalTopup() + 
                           summary.getSummary().getIncome().getTotalTransfer();
        summaryTable.addCell(createCell(formatCurrency(totalIncome), false).setBold());
        
        // Expense section
        summaryTable.addCell(createHeaderCell("PENGELUARAN", 2));
        
        summaryTable.addCell(createCell("Kebutuhan:", true));
        summaryTable.addCell(createCell(formatCurrency(summary.getSummary().getExpense().getTotalNeeds()), false));
        
        summaryTable.addCell(createCell("Tagihan:", true));
        summaryTable.addCell(createCell(formatCurrency(summary.getSummary().getExpense().getTotalBills()), false));
        
        summaryTable.addCell(createCell("Belanja:", true));
        summaryTable.addCell(createCell(formatCurrency(summary.getSummary().getExpense().getTotalShopping()), false));
        
        summaryTable.addCell(createCell("Transportasi:", true));
        summaryTable.addCell(createCell(formatCurrency(summary.getSummary().getExpense().getTotalTransport()), false));
        
        summaryTable.addCell(createCell("Penyaluran Harta:", true));
        summaryTable.addCell(createCell(
            formatCurrency(summary.getSummary().getExpense().getTotalTransferOfWealth()), false));
        
        summaryTable.addCell(createCell("Total Pengeluaran:", true));
        long totalExpense = summary.getSummary().getExpense().getTotalNeeds() +
                           summary.getSummary().getExpense().getTotalBills() +
                           summary.getSummary().getExpense().getTotalShopping() +
                           summary.getSummary().getExpense().getTotalTransport() +
                           summary.getSummary().getExpense().getTotalTransferOfWealth();
        summaryTable.addCell(createCell(formatCurrency(totalExpense), false).setBold());
        
        // Net cash flow
        summaryTable.addCell(createCell("ARUS KAS BERSIH:", true).setBold());
        long netCashFlow = totalIncome - totalExpense;
        Cell netCashFlowCell = createCell(formatCurrency(netCashFlow), false).setBold();
        
        // Color code the net cash flow (green for positive, red for negative)
        if (netCashFlow >= 0) {
            netCashFlowCell.setFontColor(ColorConstants.GREEN);
        } else {
            netCashFlowCell.setFontColor(ColorConstants.RED);
        }
        
        summaryTable.addCell(netCashFlowCell);
        
        document.add(summaryTable);
        document.add(new Paragraph("\n"));
    }

    private void addTransactionSection(Document document, 
                                     List<Transaction> sentTransactions, 
                                     List<Transaction> receivedTransactions,
                                     List<Transaction> topupTransactions) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm", LOCALE_ID);
        
        // Sent transactions
        if (!sentTransactions.isEmpty()) {
            document.add(new Paragraph("Transaksi Terkirim").setBold().setFontSize(14));
            
            Table sentTable = new Table(UnitValue.createPercentArray(new float[]{15, 25, 20, 25, 15}));
            sentTable.setWidth(UnitValue.createPercentValue(100));
            
            // Add header row
            sentTable.addHeaderCell(createTableHeaderCell("Tanggal"));
            sentTable.addHeaderCell(createTableHeaderCell("Penerima"));
            sentTable.addHeaderCell(createTableHeaderCell("Kategori"));
            sentTable.addHeaderCell(createTableHeaderCell("Catatan"));
            sentTable.addHeaderCell(createTableHeaderCell("Jumlah"));
            
            // Add data rows
            for (Transaction tx : sentTransactions) {
                sentTable.addCell(createTableCell(tx.getCreatedAt().format(formatter)));
                sentTable.addCell(createTableCell(tx.getRecipientAccount().getUser().getFullName()));
                sentTable.addCell(createTableCell(tx.getTransferCategory().getName()));
                sentTable.addCell(createTableCell(tx.getNotes() != null ? tx.getNotes() : "-"));
                sentTable.addCell(createTableCell(formatCurrency(tx.getAmount())).setTextAlignment(TextAlignment.RIGHT));
            }
            
            document.add(sentTable);
            document.add(new Paragraph("\n"));
        }
        
        // Received transactions
        if (!receivedTransactions.isEmpty()) {
            document.add(new Paragraph("Transaksi Diterima").setBold().setFontSize(14));
            
            Table receivedTable = new Table(UnitValue.createPercentArray(new float[]{15, 25, 20, 25, 15}));
            receivedTable.setWidth(UnitValue.createPercentValue(100));
            
            // Add header row
            receivedTable.addHeaderCell(createTableHeaderCell("Tanggal"));
            receivedTable.addHeaderCell(createTableHeaderCell("Pengirim"));
            receivedTable.addHeaderCell(createTableHeaderCell("Kategori"));
            receivedTable.addHeaderCell(createTableHeaderCell("Catatan"));
            receivedTable.addHeaderCell(createTableHeaderCell("Jumlah"));
            
            // Add data rows
            for (Transaction tx : receivedTransactions) {
                receivedTable.addCell(createTableCell(tx.getCreatedAt().format(formatter)));
                receivedTable.addCell(createTableCell(tx.getSenderAccount().getUser().getFullName()));
                receivedTable.addCell(createTableCell(tx.getTransferCategory().getName()));
                receivedTable.addCell(createTableCell(tx.getNotes() != null ? tx.getNotes() : "-"));
                receivedTable.addCell(createTableCell(formatCurrency(tx.getAmount())).setTextAlignment(TextAlignment.RIGHT));
            }
            
            document.add(receivedTable);
            document.add(new Paragraph("\n"));
        }
        
        // Topup transactions
        if (!topupTransactions.isEmpty()) {
            document.add(new Paragraph("Transaksi Topup").setBold().setFontSize(14));
            
            Table topupTable = new Table(UnitValue.createPercentArray(new float[]{15, 25, 20, 40}));
            topupTable.setWidth(UnitValue.createPercentValue(100));
            
            // Add header row
            topupTable.addHeaderCell(createTableHeaderCell("Tanggal"));
            topupTable.addHeaderCell(createTableHeaderCell("Metode"));
            topupTable.addHeaderCell(createTableHeaderCell("Jumlah"));
            topupTable.addHeaderCell(createTableHeaderCell("Catatan"));
            
            // Add data rows
            for (Transaction tx : topupTransactions) {
                topupTable.addCell(createTableCell(tx.getCreatedAt().format(formatter)));
                topupTable.addCell(createTableCell(tx.getTopupMethod().getName()));
                topupTable.addCell(createTableCell(formatCurrency(tx.getAmount())).setTextAlignment(TextAlignment.RIGHT));
                topupTable.addCell(createTableCell(tx.getNotes() != null ? tx.getNotes() : "-"));
            }
            
            document.add(topupTable);
        }
        
        // Add disclaimer
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("Ini adalah laporan yang dibuat secara otomatis. " +
            "Jika Anda menemukan ketidaksesuaian, silakan hubungi layanan pelanggan.")
            .setFontSize(8)
            .setItalic()
            .setTextAlignment(TextAlignment.CENTER));
    }

    private Cell createCell(String content, boolean isHeader) {
        Cell cell = new Cell();
        cell.add(new Paragraph(content));
        cell.setBorder(Border.NO_BORDER);
        if (isHeader) {
            cell.setBold();
        }
        return cell;
    }
    
    private Cell createHeaderCell(String content, int colspan) {
        Cell cell = new Cell(1, colspan);
        cell.add(new Paragraph(content));
        cell.setBold();
        cell.setBackgroundColor(new DeviceRgb(230, 230, 230));
        cell.setBorder(new SolidBorder(ColorConstants.GRAY, 0.5f));
        return cell;
    }
    
    private Cell createTableHeaderCell(String content) {
        Cell cell = new Cell();
        cell.add(new Paragraph(content));
        cell.setBold();
        cell.setBackgroundColor(new DeviceRgb(230, 230, 230));
        cell.setBorder(new SolidBorder(ColorConstants.GRAY, 0.5f));
        cell.setPadding(5);
        return cell;
    }
    
    private Cell createTableCell(String content) {
        Cell cell = new Cell();
        cell.add(new Paragraph(content));
        cell.setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f));
        cell.setPadding(5);
        return cell;
    }

    private String formatCurrency(Long amount) {
        if (amount == null) {
            return "Rp 0";
        }
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(LOCALE_ID);
        return currencyFormatter.format(amount).replace("Rp", "Rp ");
    }
}