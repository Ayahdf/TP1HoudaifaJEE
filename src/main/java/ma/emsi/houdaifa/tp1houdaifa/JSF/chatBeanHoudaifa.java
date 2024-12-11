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
        this.conversation.append("== User:\n").append(question).append("\n== Poète génératif:\n").append(reponse).append("\n\n");
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
                and you tell them the average price of a meal.
                """;
        listeSystemRoles.add(new SelectItem(role, "Guide touristique"));

        role = """
                You are a generative poet. When the user types a question or phrase,
                you respond with a poetic interpretation of their input.
                Use rhyme, meter, and creative language to engage the user.
                """;
        listeSystemRoles.add(new SelectItem(role, "Poète génératif"));

        this.systemRole = (String) listeSystemRoles.getFirst().getValue();
        return listeSystemRoles;
    }
}
