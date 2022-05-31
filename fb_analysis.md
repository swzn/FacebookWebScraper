
# Facebook Data Mining

The FacebookWebScraper gives access to a lot of data, most of which can be used to come to conclusions about the impact that groups/pages have on the users of the platform. This document serves to first describe the type of data which will be gathered, and then elaborate on how data mining strategies to come to scientific conclusions about how the algorithm affects users.

## Data Collection
Data will be collected from the following sources:
<ol>
<li> Immediate suggestions upon join a group </li>
<li> Immediate suggestions upon following a page </li>
<li> Variations in the global group discovery tab </li>
<li> Variations in the global page discovery tab </li>
<li> The effects of user interaction on the suggestions algorithm </li>
<li> Content of posts </li>
<li> Comments on posts </li>
<li> Reactions to posts </li>
<li> Post shares </li>
</ol>
At the very least, all of the above data can be collected with the applet. The following are other possibilities for data collection, however, may or may not be added depending on the time limitations.

- Follower Interaction Ratio to posts
- Non-Follower Interaction Ratio to posts
- Suggested Posts on the main feed
- Recent activity effect on the advertisements
- Off-site activity effect on suggestions

## Data Analysis

This section will cover how the data with the current plan for the applet can be used for researching purposes.

### Siloing effect
Data collected from sources 1, 2, 3 and 4 allow to study whether or not Facebook is subject to a siloing effect. Data gathered from source 5 will show how intense this siloing effect is, and how the user's interaction tendencies affect it. For this to be effectively analyzed, there must be a clear boundary between the "siloed" pages/groups and the rest. In this case, the study will be conducted around political pages/groups. 

### Unity ratios
Data collected from sources 6, 7, 8 and 9 allow to study how much of a page/group's followers tend to agree with the content that is posted. The data will be gathered on "siloed" pages/groups from sources 1, 2, 3 and 4. The "unity ratio" or the agreement percentage of a page/group will be computed using a point system that will take into account how many positive comments, reactions and shares are found on a certain number of post. This method will use NLP and sentiment to determine if a comment/share is positive/neutral/negative. As for reactions, as a naive approach, positive reactions will be determined based on the reaction type.

## Methodology

### Control Study
To generate a control study, the first experiment will contain no bias towards politics, or even political parties. This control study will follow a certain number of the most followed pages of certain types and will join a certain number of the most popular groups of certain types. This is a naive approach to emulating the average user of Facebook. This data will be used to recreate the siloing effect and unity ratio study on unbiased suggestions of groups and compare any differences.

### Starting Pages/Groups
In the experiment, a curated list of root pages/groups will be used to start finding suggestions. These curated lists will contain mostly political content, with the addition of some other type of content. The experiment will be conducted on both left-leaning and right-leaning curated lists to fish any discrepencies between the two. 

### Siloed Groups/Pages
To determine whether or not a group/page is the result of siloing, the study requires a definition of what the "silo" is and then figuring out whether or not a suggestion belongs to the silo. In this case, the "silo" will be a political party, either left-leaning or right-leaning depending on the experiment. The process of figuring out whether a suggestion belongs to the silo will be done using manual reviewing of the page/group.

### Point System
For the unity ratio experiement, the point system will be very simple. Each type of interaction with a post will be attributed a value that will contribute either to the positive, negative or neutral interactions on the page/group.  In the end, a pie-chart containing the percentage of positive, negative and neutral interactions will be conceived to determine the unity ratio. In this point system, sharing a post is worth 3 points, while leaving a comment or reaction will be worth a single point. The reasoning behind this choice is that a Facebook user/page sharing a post has a much higher chance of reaching people with similar opinion as the entity who initially shared the post.

### Bias and Methodology Flaws
As this research is being conducted as an independent study, it is imperative to denote where flaws in the methodology are found. The first thing to note is how the curated list of pages/groups can be biased depending on the language/location of the researcher. It is also important to state that not all interactions with a post can be classified as positive, negative or neutral. Finally, the point system may seem arbitrary as there is no way to show how much reach a "share" has (on average), in comparison to a reaction or a comment. The assumption made here is that, given a large enough sample size, these flaws will not affect the underlying patterns of the suggestions algorithm, leading to a _qualitative_ conclusion to this research. In fact, with the right technological artillery and funding, these flaws can be solved to provide a more accurate _quantitative_ conclusion to this research.
