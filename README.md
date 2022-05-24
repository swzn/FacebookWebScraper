# FacebookWebScraper
This automated applet will allow the user to scrape relevant data by searching through a curated list of Facebook pages or groups. This applet will be interactive and highly customizable to allow the user to select what type of information needs to be collected. In particular, it can be deployed in command-line (terminal) mode, which allows for a Batch-sequential architecture style of automation. It can also be deployed in GUI mode, which is more convenient for the user.

### Features
- Facebook Page content scraper
- Facebook Group content scraper
- Facebook Page suggestions scraper
- Facebook Group suggestions scraper

# Content Scraper
On Facebook Pages and Groups, users can make posts that are visible to page followers, group members but also the public in some cases. The content scraper will have the options to save a selected amount of posts on the page (starting from the most recent). 
The information that can be gathered from theses posts are: 
- the content of post
- the reactions to the post
- the comments on the post
- the shares

Future versions of the applet may have the ability to verify if the users who have interacted with the post are followers of the page, members of the group or simply Facebook users who have been reached by the post for some extraneous reason. As such, this feature is extremely useful for research purposes as it allows to see how much reach a group or a page has. It also allows to see how likely it is for a group member or a pager follower to interact with the posts. In the field of research, this feature could be paired with [sentiment analysis](https://en.wikipedia.org/wiki/Sentiment_analysis) to gauge how much the members of a group or page followers agree/disagree with the posts.

# Suggestions Scraper
To preface this section, it is important to note that much of the analysis performed in this section is inspired by [Mika Desblancs' analysis of YouTube](https://github.com/mika-jpd/YouTube_Radicalization_Recommendations).

It is first important to understand how the suggestions interface works on Facebook before explaining the scraping strategy.

**Pages Suggestion Interface**: Pages are suggested in at least two different ways.
- Immediately after following a page, a [dropdown](/img/dropdown.png) *can* appear with page suggestions. Testing while using my personal account and some of my friends', these page suggestions are mostly pages similar to the page you just followed, but are also affected by other pages that you and your entourage follow. The suggestions on the dropdown seem to remain mostly unchanged, unless you decide to follow and then unfollow the suggested page, at which point it will be replaced. This hints to some sort of "blacklisting" algorithm which may be influenced by which pages you have decided to unfollow. This will be referred to as the "immediate suggestions".
- There is a global [page discovery tab](/img/pages_suggestion_tab.png) that is available on Facebook. This tab seems to be updated whenever you follow new pages (although not always immediately after), usually following the same type of strategy as the previous option. It is also important to note that a suggestion on this tab will remain on the tab until removed. Removing a suggestion will not replace it, in fact, this is the reason why the attached picture contains no suggestions. Another thing to note about this discovery tab is how inconsistently it is updated. In fact, from personal testing, I have been able to follow 25 pages in a row before having a single suggestion appear on the discovery tab, which had appeared a few hours after the last instance of my account following a new page. Conversely, sometimes following a single page will add more than 1 suggestion to the discovery tab, sometimes even going up to 5. This will be referred to as the "global suggestions"

While scrolling Facebook, you may sometimes have [suggested posts](/img/suggested_post.png) from pages which you aren't following. These suggestions seem to appear on your feed, every 3-5 posts. A future version of this applet may have the feature to analyse how the suggested posts on your feed are affected by your recent activity. 

**Groups Suggestion Interface**: Groups are suggested in at least two different ways.
- Immediately after following/joining or unfollowing/leaving a group, a list of suggestions appears. This list is [often empty](/img/empty_suggestions.png), however in some cases it can contain suggestions. This will be referred to as the "immediate suggestions".
- There is a global group discovery tab that contains 4 different collections of group suggestions. These 4 collections are called ["Suggested For You", "Friend's groups"](/img/groups_suggestion_tab.png), ["Popular near you" and "More Suggestions"](/img/groups_suggestion_tab_2.png). Each of these collection of groups can be expanded into its own discovery tab by clicking the "See All" button. It is important to note that, despite being long, these collections aren't infinite. These collections are also not mutually exclusive; a suggestion may appear in more than one collection at once. Finally, simply refreshing the page isn't enough to update these suggestions. They seem to be updated based on your activity on groups (joining, leaving, interacting with posts, removing group suggestions). This will be referred to as the "global suggestions"

The scraping algorithm will run under two different search modes and three different interaction modes. The algorithm will be based on popular [tree traversal algorithms](https://en.wikipedia.org/wiki/Tree_traversal). In general, the algorithm will visit a node (a page/group), interact with it and then recursively perform a similar visit on either a child of the node (suggestion from this page/group) or a sibling of the node (a page/group that came from the same suggestion list as this node).
### Search modes
Given a depth n and a child-count k, we will be using the Breadth-first search method ([BFS](https://en.wikipedia.org/wiki/Breadth-first_search)) or the Depth-first search method ([DFS](https://en.wikipedia.org/wiki/Depth-first_search)).

**Breadth-first search**: This search mode will first visit all siblings of a node before visiting the children. This search will start at depth 0, where only a single page/group is in the search space. The algorithm will then visit the k first children of the node, before recursively visiting children of its children. In other words, this search completely visits a depth-level before passing onto the next one. This search ends once all the nodes on the n<sup>th</sup> depth-level have been visited.

**Depth-first search**: This search mode will visit all children of its children before visiting its sibling. The search will start at depth 0, only a single page/group is in the search space. The algorithm will then *save* the k first children of the node, but only recursively visit the first child node. The algorithm will keep recursively visit child nodes until it has reached the n<sup>th</sup> depth-level. At this point, the algorithm will travel to its siblings to perform the type of search. In other words, the algorithm only travels to a sibling node if the current node's subtree has been completely visited.

**Figure 1.** A search tree with depth n = 2, child-count k = 2
![](/img/tree.png)

- A BFS search of this tree would result in the following order of visited nodes: A B C D E F G
- A DFS search of this tree would result in the following order of visited nodes: A B D E C F G

**Limitations of this scraping strategy**

There are many limitations to this scraping strategy. The first one is *time*. For the global suggestions, they are very rarely immediately updated upon activity. This means that there must be some type of sleeping strategy put in place to allow time for the global suggestions to be updated. The other limitation is the finiteness of the search space. Both for the immediate and global suggestions, the amount of pages or groups that we can access is very limited. For pages, it seems that there are only 5 suggestions every time you follow a new page, bounding k between 1 and 5. In general, the global suggestions for pages are also very limited, generating nearly no new suggestions whenever a page has been followed. Similarly, when it comes to investigating the group algorithm, the immediate suggestions are very limited as well, usually generating no immediate suggestions when joining or following a group. Although the global suggestions for groups have more options, they suffer from the same finiteness as the pages global suggestion. The only *infinite* source of suggestions is found for pages, by scrolling through the feed and visiting suggested posts.
