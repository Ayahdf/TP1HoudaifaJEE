package ma.emsi.houdaifa.tp1houdaifa.llm;


public class RequeteException extends Exception {

    // Constructeur par défaut
    public RequeteException(String message, String s) {
        super("Erreur lors de la requête");
    }

    // Constructeur avec message personnalisé
    public RequeteException(String message) {
        super(message);
    }

    // Constructeur avec message et exception originale
    public RequeteException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructeur avec exception originale uniquement
    public RequeteException(Throwable cause) {
        super(cause);
    }
}
