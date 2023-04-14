# TD Question 2: Implement a File server & Download Manager (CLI)

1. Start server - ServerDispatcher
2. Start client app - ClientApp

3. On console client app will show:
   Type the command
   - index: To list out all the available files
   - get <file-name>: To download the file-name file
   - quit/q: to exit the server

4. To list out all the available files:
   - Type: index
   - Enter.
   - Output:  
        ```
        +---------+----------------------+
        | Sr No   | Filename             |
        +---------+----------------------+
        | 1       | a.zip                |
        | 2       | .DS_Store            |
        | 3       | 1.pdf                |
        | 4       | 3.pdf                |
        | 5       | 2.pdf                |
        +---------+----------------------+
        
        ```


5. To download the files:
    - Type: get file1 file2
    - Enter.
    - Output: 
    
    ```
    Starting download file 
    Downloading Percent : 100.0%
    Files Successfully Downloaded
    ```
    
    ```
    File abc Not Found!
    ```

6. To exit the app:
    - Type: quit or q
    - Enter
    - Output: 
    ```
    Closing this connection : Socket[addr=localhost/127.0.0.1,port=5056,localport=59786]`
    Connection closed
    ```
