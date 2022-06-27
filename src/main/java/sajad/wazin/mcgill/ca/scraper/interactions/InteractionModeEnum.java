package sajad.wazin.mcgill.ca.scraper.interactions;

/**
 * @author Sajad Wazin @ https://github.com/swzn
 * @project FacebookWebScraper
 * @mail sajad.wazin@mail.mcgill.ca
 */

public enum InteractionModeEnum {
    FOLLOW_ONLY, INTERACTIVE, EXPERIMENTAL, NULL_INTERACTION;

    public static InteractionModeEnum getInteractionMode(String s){
        return switch (s) {
            case "Follow-only" -> FOLLOW_ONLY;
            case "Follow + Post Interactions" -> INTERACTIVE;
            case "Experimental" -> EXPERIMENTAL;
            default -> NULL_INTERACTION;
        };
    }
}
