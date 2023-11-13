# WordCount-API
A simple API implemented with Spring Boot for counting word frequencies in text files.
Returns the most common words found in the text file in an descending order array of JSON objects with the format of { word: `word`, amount: `amount` }, e.g. `[{ word: "foo", amount: 2 }, { word: "bar", amount: 1}]`.

Must have Java runtime environment (JRE) installed in your computer (Java version 21 or above) to run the .jar file. If you want to pull and compile the code yourself then you will also need a Java Development Kit (JDK, Java version 21 or above).

Instructions:
1. Download the .jar file from the release page https://github.com/eerojala/WordCount-API/releases/tag/v1.0
2. Place the .jar on location of your choosing and open a command line terminal.
3. Open/navigate the command line whereever you placed the .jar
4. Before running the application make sure that nothing is running on port 8080.
5. Run the .jar file with `java -jar wordcount.api-1.0.0.jar`
6. The application should start and run in localhost:8080
7. To count word frequencies you need to make HTTP POST requests to localhost:8080/wordcount with following information:
   1. Headers: `content-type multipart/form-data`
   2. Body: two key/value pairs:
      1. key: `file`, value: the text file you wish to count the word frequencies for. Maximum `file` size is 50 MB.
      2. key: `amount`, value: the amount of most common words from `file` you wish for the API to return, i.e if `amount` = 10 then the API will return the 10 most common words from `file`. `amount` must be an integer between 1 and 2147483647.
     
 [Here]{https://apidog.com/blog/postman-upload-file-detailed-guide/} is a guide on how you can send a file with Postman.

 Below are screenshots of my Postman test request for reference:
![Headers](https://github.com/eerojala/WordCount-API/assets/11409371/dd7a27b8-be51-4757-9de0-f4f0aed086dc)
![Body](https://github.com/eerojala/WordCount-API/assets/11409371/24a32aaa-6a71-42c5-8b01-2f0cb7fbab53)


    
    
   
