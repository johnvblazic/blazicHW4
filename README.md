# blazicHW4
my homework assignment #4 for 538

Run with 3 arguments:
(1) File name of text input, formatted as <Doc ID>:<Text> for QLL, main dataset folder for NB
(2) File name of queries, formatted in Lucene standards for QLL, ignored for NB
(3) String. "QLL" gives Query language likelihood model. "NB" gives naeive bayes model

Please see the included queries.txt and inputText.txt for formatting clarifications

dataset folder should include 3 folders - nonspam-train, spam-train, and test

training for NB takes about 9-10 minutes and classifying a minute or two more.

I've included the memory option on the NB train as I was running short on memory. The current settings were working on my machine but may need to be tweaked.

I've used Jelinek-Mercer smoothing for the QLL model. If smoothing isn't used, documents that don't contain any of the words in a query have scores of 0.

The total accuracy for my NB model is 50% (everything is labeled SPAM). For the spam label, my F1 score is .67, R is 1 and P is .5.

commands are as follows:

sbt -mem 4000 "run spamDataset/ query.txt NB"

sbt "run inputText.txt query.txt QLL"
