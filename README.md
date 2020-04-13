# Language Correction - Basic Version

# How to run
* Import this project into Intellj IDEA.
* Add dependencies:
    ```
    redis.clients:jedis:2.8.1
    jsoup-1.13.1
    StanfordCoreNLP
    com.google.code.gson:gson:2.8.6
    com.googlecode.json-simple:json-simple:1.1.1
    commons-io:commons-io:2.6
    ```
* Run redis-server on Terminal
    ```bash
    cd redis-stable
    make
    cd src
    ./redis-server
    ```
* Run crawler

  Program Argument : src/Crawler/test.txt

  VM options: -Xmx8g
* Run nlpchecker

  Program Argument : src/Checker/input.txt

  VM options: -Xmx8g

# Team 8
* Luxuan Qi
* Ningrong Chen
* Junyou Chi
