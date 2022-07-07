package sajad.wazin.mcgill.ca.facebook;

import sajad.wazin.mcgill.ca.utils.SeleniumUtils;

import java.util.Arrays;

/**
 * @author Sajad Wazin @ https://github.com/swzn
 * @project FacebookWebScraper
 * @mail sajad.wazin@mail.mcgill.ca
 */

public class FacebookReactions {

    private final int[] reactions;

    private final String LIKE_EMOJI = "https://scontent.xx.fbcdn.net/m1/v/t6/An-tsvy1nZCAfjJDq_e9hwhgJ_ouDg6GOHdVQtc31Lh3B13GEFJ0N3wRI6j2_Lz8icCyU4RkVsKbJckG5NMDv5TxxWie8OqB_kcvCNizVjn7sw.png";
    private final String LAUGH_EMOJI = "https://scontent.xx.fbcdn.net/m1/v/t6/An9yRlv3tqyIsDTiKV0WfMgtabNG9VPyvNiv5USdzPe0Cbp2FdNMvbGH1mvTvI8TczUcd9kED-M5Q1z9-fVK3zAMCRSiYtsWTpWSid0DJlPasg.png";
    private final String WOW_EMOJI = "https://scontent.xx.fbcdn.net/m1/v/t6/An_f5KryEO3JdbkuRbEs1ixj8HC8itKTXvZ3Hl1c-zaREaiMDPCRTNw6CSwRUjKkq_YXEuxmsqBu06WIeteZ7MBZ2WKuJXvOK6WdOQfGi2Ixg9Sd.png";
    private final String HEARTS_EMOJI = "https://scontent.xx.fbcdn.net/m1/v/t6/An8KxJw0TdKA0hIHqkw35xWvBGYLLbtgD5y14_K8iN_zaDhCWgixktWzvqA45BTxHACGktnPMx_lkq1uE66153QNE58NZp59iYz6MDdtqgcTZw.png";
    private final String HOLDING_HEARTS_EMOJI = "https://scontent.xx.fbcdn.net/m1/v/t6/An-zbifsrGonJXBikRGgj1txFJUkRG3aCN5900mzKo6dVL8tKCuWwF6D9Ov6XB3JJZ7pT1FSxuFsETOjkjZ08b5AyPU0z_GsxZH2nWiW2ScJT4p3rQ.png";
    private final String SAD_EMOJI = "https://scontent.xx.fbcdn.net/m1/v/t6/An9Yzzh8CoEGqeWIfY5w6zR3VdPbG5X1fHXZdMfftnoomx3ObysBj145G99ZhM1T6DcU_ZAH2bEdiOj8sUAQvplVo0cYKS_GprBBJlcwiBHomFx7hQ.png";
    private final String MAD_EMOJI = "https://scontent.xx.fbcdn.net/m1/v/t6/An-mj0uPEZ5b6GVy3OC-_ZMV1AGoboZI3SG9P2r3WElt054OlpAmUSq9QPU0i9RdhF07UwCRHIsC06i-w4_VCrnJnBEent1vmcy8MXOQt0msew.png";

    // 0:likes 1:heart 2:holding_hearts 3:laughing 4:wow 5:sad 6:mad

    public FacebookReactions(){
        reactions = new int[7];
    }

    private void setLikes(int likes) {
        reactions[0] = likes;
    }

    public int getLikes() {
        return reactions[0];
    }

    private void setHeart(int hearts){
        reactions[1] = hearts;
    }

    public int getHearts(){
        return reactions[1];
    }

    private void setHoldingHearts(int holdingHearts){
        reactions[2] = holdingHearts;
    }

    public int getHoldingHearts(){
        return reactions[2];
    }

    private void setLaughing(int laughing) {
        reactions[3] = laughing;
    }

    public int getLaughing() {
        return reactions[3];
    }

    private void setWow(int wow) {
        reactions[4] = wow;
    }

    public int getWow() {
        return reactions[4];
    }

    private void setSad(int sad) {
        reactions[5] = sad;
    }

    public int getSad() {
        return reactions[5];
    }

    private void setMad(int mad) {
        reactions[6] = mad;
    }

    public int getMad() {
        return reactions[6];
    }

    public void setReaction(String IMAGE_URL, String numbers){
        int value = SeleniumUtils.parseReactionNumber(numbers);
        if(IMAGE_URL.startsWith(LIKE_EMOJI)) {
            setLikes(value);
        }
        if(IMAGE_URL.startsWith(LAUGH_EMOJI)) {
            setLaughing(value);
        }
        if(IMAGE_URL.startsWith(HEARTS_EMOJI)) {
            setHeart(value);
        }
        if(IMAGE_URL.startsWith(HOLDING_HEARTS_EMOJI)) {
            setHoldingHearts(value);
        }
        if(IMAGE_URL.startsWith(SAD_EMOJI)) {
            setSad(value);
        }
        if(IMAGE_URL.startsWith(MAD_EMOJI)) {
            setMad(value);
        }
        if(IMAGE_URL.startsWith(WOW_EMOJI)) {
            setWow(value);
        }
    }

    public String toString(){
        StringBuilder stringValue = new StringBuilder();
        stringValue.append("Reactions on this post: \n");

        stringValue.append("Likes: ");
        stringValue.append(getLikes());
        stringValue.append("\n");

        stringValue.append("Hearts: ");
        stringValue.append(getHearts());
        stringValue.append("\n");

        stringValue.append("Laughs: ");
        stringValue.append(getLaughing());
        stringValue.append("\n");

        stringValue.append("Holding Hearts: ");
        stringValue.append(getHoldingHearts());
        stringValue.append("\n");

        stringValue.append("Wow: ");
        stringValue.append(getWow());
        stringValue.append("\n");

        stringValue.append("Sad: ");
        stringValue.append(getSad());
        stringValue.append("\n");

        stringValue.append("Mad: ");
        stringValue.append(getMad());
        stringValue.append("\n");

        stringValue.append(Arrays.toString(reactions));

        return stringValue.toString();
    }
}
