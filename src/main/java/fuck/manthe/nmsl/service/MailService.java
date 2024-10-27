package fuck.manthe.nmsl.service;

public interface MailService {
    void send(String to, String subject, String text);

    void sendResetPasswordEmail(String to, String resetSecret);
}
