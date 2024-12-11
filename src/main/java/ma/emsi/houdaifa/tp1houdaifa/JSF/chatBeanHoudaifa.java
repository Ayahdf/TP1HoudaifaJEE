package ma.emsi.houdaifa.tp1houdaifa.JSF;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import ma.emsi.houdaifa.tp1houdaifa.llm.JsonUtilPourGemini;
import ma.emsi.houdaifa.tp1houdaifa.llm.LlmInteraction;

import java.io.Serializable;
import java.util.*;

@Named
@ViewScoped
public class chatBeanHoudaifa implements Serializable {

    private String systemRole;
    private boolean systemRoleChangeable = true;
    private String question;
    private String reponse;
    private StringBuilder conversation = new StringBuilder();
    private String texteRequeteJson;
    private String texteReponseJson;
    private boolean debug;
    @Inject
    private JsonUtilPourGemini jsonUtil;
    @Inject
    private FacesContext facesContext;

    public chatBeanHoudaifa() {
    }

    public String getSystemRole() {
        return systemRole;
    }

    public void setSystemRole(String systemRole) {
        this.systemRole = systemRole;
    }

    public boolean isSystemRoleChangeable() {
        return systemRoleChangeable;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getReponse() {
        return reponse;
    }

    public void setReponse(String reponse) {
        this.reponse = reponse;
    }

    public String getConversation() {
        return conversation.toString();
    }

    public void setConversation(String conversation) {
        this.conversation = new StringBuilder(conversation);
    }

    public String getTexteRequeteJson() {
        return texteRequeteJson;
    }

    public void setTexteRequeteJson(String texteRequeteJson) {
        this.texteRequeteJson = texteRequeteJson;
    }

    public String getTexteReponseJson() {
        return texteReponseJson;
    }

    public void setTexteReponseJson(String texteReponseJson) {
        this.texteReponseJson = texteReponseJson;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(Boolean debug) {
        this.debug = debug;
    }

    public void toggleDebug() {
        this.setDebug(!isDebug());
    }

    public String envoyer() {
        if (question == null || question.isBlank()) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Texte question vide", "Il manque le texte de la question");
            facesContext.addMessage(null, message);

            return null;
        }

        try {
            // Appel à l'API via JsonUtil
            LlmInteraction interaction = jsonUtil.envoyerRequete(question);
            this.reponse = interaction.reponseExtraite();
            this.texteRequeteJson = interaction.questionJson();
            this.texteReponseJson = interaction.reponseJson();

            // Ajout de la réponse à la conversation
            afficherConversation();

        } catch (Exception e) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Problème de connexion avec l'API du LLM",
                    "Problème de connexion avec l'API du LLM : " + e.getMessage());
            facesContext.addMessage(null, message);
        }
        return null;
    }

    public String nouveauChat() {
        return "index";
    }

    private void afficherConversation() {
        this.conversation.append("== User:\n").append(question).append("\n== Serveur:\n").append(reponse).append("\n");
    }

    private String genererReponseTransformee(String texte) {
        // Transformation 1 : Palindrome
        String palindrome = creerPalindrome(texte);

        // Transformation 2 : Compte des lettres
        int nombreLettres = compterLettres(texte);

        // Transformation 3 : Anagramme
        String anagramme = genererAnagramme(texte);

        // Construction de la réponse ludique
        return String.format("""
            🧩 Transformations ludiques de votre texte :
            
            ✨ Palindrome : %s
            🔤 Nombre de lettres : %d
            🔀 Anagramme possible : %s
            
            Bonus : %s
            """,
                palindrome,
                nombreLettres,
                anagramme,
                genererBonusAleatoire()
        );
    }

    private String creerPalindrome(String texte) {
        // Créer un palindrome simplifié
        String nettoye = texte.replaceAll("[^a-zA-Z]", "").toLowerCase();
        return nettoye + new StringBuilder(nettoye).reverse();
    }

    private int compterLettres(String texte) {
        return texte.replaceAll("[^a-zA-Z]", "").length();
    }

    private String genererAnagramme(String texte) {
        // Simplification : prend les premières lettres en ordre inverse
        String nettoye = texte.replaceAll("[^a-zA-Z]", "").toLowerCase();
        if (nettoye.length() <= 1) return nettoye;

        char[] lettres = nettoye.toCharArray();
        Arrays.sort(lettres);
        return new String(lettres);
    }

    private String genererBonusAleatoire() {
        String[] bonus = {
                "🍀 Chance du jour : Un sourire vous attend !",
                "🚀 Inspiration : Votre créativité explose aujourd'hui !",
                "🌈 Pensée positive : Tout est possible !",
                "🧠 Défi mental : Résolvez un petit puzzle aujourd'hui !",
                "🌟 Motivation : Vous êtes plus fort que vous ne le pensez !"
        };
        return bonus[new Random().nextInt(bonus.length)];
    }

    public List<SelectItem> getSystemRoles() {
        List<SelectItem> listeSystemRoles = new ArrayList<>();

        String role = """
                You are a helpful assistant. You help the user to find the information they need.
                If the user type a question, you answer it.
                """;
        listeSystemRoles.add(new SelectItem(role, "Assistant"));

        role = """
                You are an interpreter. You translate from English to French and from French to English.
                If the user type a French text, you translate it into English.
                If the user type an English text, you translate it into French.
                If the text contains only one to three words, give some examples of usage of these words in English.
                """;
        listeSystemRoles.add(new SelectItem(role, "Traducteur Anglais-Français"));

        role = """
                Your are a travel guide. If the user type the name of a country or of a town,
                you tell them what are the main places to visit in the country or the town
                are you tell them the average price of a meal.
                """;
        listeSystemRoles.add(new SelectItem(role, "Guide touristique"));

        this.systemRole = (String) listeSystemRoles.getFirst().getValue();
        return listeSystemRoles;
    }
}
