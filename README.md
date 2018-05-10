# Hooli-Search
A little search engine using a suffix trie.

![screen shot 2018-05-10 at 21 54 05](https://user-images.githubusercontent.com/28049022/39891260-5d90014e-549d-11e8-8c19-db14c19eda76.png)

# Background:
Around 2 years ago I made a web crawler and set it loose on my school's intranet, hoping to create an easy way to find things. The crawler stored the URL of each page, split the contents of the page up into a list of individual words and stored those words and the position of each word on each page (e.g. "banana"; word 637; http://www.website.com) in a SQLite database. I then made a little application that allowed me to search for things on the intranet. All it did was split the search string up into words and run a SQL query for each word to get the URLs of all the web pages containing those terms and then find the URLs that appeared in all of these lists. It worked well enough. There was a noticeable delay between hitting enter and getting search results but nothing too bad (perhaps about 0.5 seconds). One frustration was that it didn't take into account where on the page each of the search terms appeared so searching for "bad apple" would bring up every page that contained both the words "bad" and "apple" regardless of whether or not they had anything to do with apples that were bad.

# Hooli-Search
Fast forward to today, I thought I would use some of my new-found knowledge of string processing to make my search tool faster and bring up better results (even though I'm no longer at school). When it starts up, the application reads all of the words in the index into a suffix-trie in memory. When the user enters a query string, it is split up into words and each word is looked up in the suffix trie. These lookups are effectively instant since an n letter word can be looked up in n comparisons in the suffix trie so a word would have to be millions of letters long to cause a noticeable delay. The URLs and word positions for each word are found in the suffix trie and a "term proximity score" is given to each URL. The proximity score is intended to be a measure of how close together the words in the search string were on a page. The closer the words are to each other, the higher the proximity score. If a user searches "bad apple" and a page contains "...the apple is bad because..." then I would want this page to appear near the top of the search results because I think it is likely to be relevant. Based on this heuristic, I decided to rank the pages by their proximity score in the hopes of bringing the most relevant pages to the top of the search results. In the screenshot above, the number to the right of each URL in the results is the term proximity score of that page.

# Success!
The search time now seems to be in the single digit millisecond (and often fraction of a millisecond) range and appears to be putting the pages I want at the top of the search results most of the time.