import json
import os
from textblob import TextBlob
import matplotlib.pyplot as plot
import numpy as np
import statistics

# Include the path to all the output directories that you want to analyse
directories = []

# Wrapper for textblob
def getPolarity(comment):
    return TextBlob(comment).sentiment.polarity

# The scrapeDirectory method will take a directory path and a boolean as input
# It computes the average percentage of positive, neutral and negative reactions to all pages from the input directory
# If the boolean is set to True, then it will also print the total positive, negative and neutral points from each page
def scrapeDirectory(directory, detailed_print):
    
    pages_amount = 0
    posts_amount = 0
    
    avg_pos = 0
    avg_neg = 0
    avg_neu = 0
    
    # Retrieve all scrapeable files in the directory
    for filepath in os.listdir(directory):
        if filepath.endswith(".json"):
            
            # Open the files and ignore any unknown characters
            file = open(directory + "/" + filepath, encoding="utf-8-sig", errors="ignore")
            json_data = json.load(file)
            
            # Retrieve each page in the file
            for key in json_data:
                page_data = json_data[key]
                
                positive = 0
                neutral = 0
                negative = 0
                
                # Open each post
                for posts in page_data:                    
                    for post in page_data[posts]:
                        
                        # Attribute positive, neutral and negative points based on reactions
                        posts_amount += 1
                        neutral += int(post["reactions"]["wow"])
                        positive += int(post["reactions"]["hearts"])
                        positive += int(post["reactions"]["likes"])
                        positive += int(post["reactions"]["holding_hearts"])
                        positive += int(post["reactions"]["laughs"])
                        negative += int(post["reactions"]["mad"])
                        negative += int(post["reactions"]["sad"])
                        
                        # Attribute positive, neutral and negative poitns based on polarity of comments
                        for comment in post["comments"]:
                            pol = getPolarity(comment)
                            if pol >= 1/3: positive +=10
                            elif pol <= -1/3: negative += 10
                            else: neutral += 10
                    
                    # Print the page URL
                    if detailed_print: print(key)
                    
                    total = positive + negative + neutral
                    
                    # If the total points is 0, then go to the next page
                    if total == 0:
                        if detailed_print: print("\tno stats")
                        continue
                    
                    # Otherwise add the percentages to the average percentage
                    
                    pages_amount += 1
                    avg_pos += positive/total
                    avg_neg += negative/total
                    avg_neu += neutral/total
                    
                    # Print the results if needed
                    if detailed_print:
                        print("\tpositive: " + str(positive) + " (" + str(positive/total) + ")")
                        print("\tnegative: " + str(negative) + " (" + str(negative/total) + ")")
                        print("\tneutral: " + str(neutral) + " (" + str(neutral/total) + ")")
    
    # Print the results for all files in the directory and compute the average percentages
    print("Scrape stats for dir: " + dire)
    print("\t pages= " + str(pages_amount))
    print("\t posts= " + str(posts_amount))
    print("\t average postive=" + str(avg_pos/pages_amount))
    print("\t average negative=" + str(avg_neg/pages_amount))
    print("\t average neutral=" + str(avg_neu/pages_amount))
    
    return (pages_amount, posts_amount, avg_pos/pages_amount, avg_neg/pages_amount, avg_neu/pages_amount)



# If you want to create a pie chart such as the one used in the final report, use this section

pg = 0
avg_pos = 0
avg_neg = 0
avg_neu = 0
post_amount = 0

# Run the analysis method on all directories relevant to your current study and compute the global averages
for dire in [directories]:
    (page_count, posts, positive,negative,neutral) = scrapeDirectory(dire, false)
    pg += page_count
    post_amount += posts
    avg_pos += positive * page_count
    avg_neg += negative * page_count
    avg_neu += neutral * page_count

# Print the results
print(avg_pos/pg)
print(avg_neg/pg)
print(avg_neu/pg)

# Format the results to be in XX.X% format
a = "{:.3f}".format(avg_pos/pg * 100)
b = "{:.3f}".format(avg_neg/pg * 100)
c = "{:.3f}".format(avg_neu/pg * 100)

# Create the pie chart by simply changing the title
title = "Average sentiment analysis for an average Facebook page"
labels = ["Positive " + str(a) + "%", "Negative " + str(b) + "%", "Neutral " + str(c) + "%"]
colors = ["#47ff78", "#ff334e", "#fff475"]
data = np.array([a, b, c])

# The title wll also contain the amount posts that was analyzed in the chart
plot.title(title + " (" + str(post_amount) + ")")
plot.pie(data, labels=labels, colors=colors)
plot.show()