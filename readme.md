# This Project is an implementation of Lamport algorithm using Corba
## Lamport Algorithm:
LA1: To request entry into its CS, a process sends a time-stamped request to every other
process in the system and also enters the request in its local Q.  

LA2: When a process receives a request, it places it in its Q. If the process is not in its CS, then it sends a time-stamped ack to the sender. Otherwise, it defers the sending of the ack until its exit from the CS.

LA3: A process enters its CS, when (1) its request is ordered ahead of all other requests (i.e., the time stamp of its own request is less than the time stamps of all other requests) in its local Q and (2) it has received the acks from every other process in response to its current request.

LA4: To exit from the CS, a process (1) deletes the request from its local queue and (2) sends a time-stamped release message to all the other processes.

LA5: When a process receives a release message, it removes the corresponding request from its local queue.
##How to run the simulation
This project support JAVA 8  
First run this command in your terminal
```
orbd -ORBInitialPort 1050 //for UNIX 
start orbd -ORBInitialPort 1050 //for MSDOS 
```
To run 3 different instances we use the main inside Server class with these arguments for each one,
the first value represent the instance id.
```
0 -ORBInitialPort 1050 -ORBInitialHost localhost
1 -ORBInitialPort 1050 -ORBInitialHost localhost
2 -ORBInitialPort 1050 -ORBInitialHost localhost
```
To request a critical session we use the main inside Client class with one of these arguments
the first value represent instance id of the requested instance to use the critical session.
```
0 -ORBInitialPort 1050 -ORBInitialHost localhost
1 -ORBInitialPort 1050 -ORBInitialHost localhost
2 -ORBInitialPort 1050 -ORBInitialHost localhost
```
![Server P0 runing](./img/1.png)
  
![Server P0 runing](./img/2.png)
  
![Server P0 runing](./img/3.png)
  

### P0 ask for critical session
##### P0 output

![Server P0 output](./img/4.png)
##### P1 output

![Server P1 output](./img/5.png)
##### P2 output
![Server P2 output](./img/6.png)

### P0 and P1 ask for critical session

##### P0 output

![Server P0 output](./img/7.png)
##### P1 output

![Server P1 output](./img/8.png)
##### P2 output
![Server P2 output](./img/9.png)