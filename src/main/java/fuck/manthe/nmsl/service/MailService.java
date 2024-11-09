package fuck.manthe.nmsl.service;

public interface MailService {
    void send(String to, String subject, String text);

    void sendTestEmail(String to);

    void sendResetPasswordEmail(String to, String resetSecret);
}
