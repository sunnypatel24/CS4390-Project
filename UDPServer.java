import java.io.*;
import java.util.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.*;

class UDPServer {
    public static HashMap<String, String> userLog = new HashMap<>();
    public static void main(String args[]) throws Exception {
        

        DatagramSocket serverSocket = new DatagramSocket(9876);

        byte[] receiveData = new byte[1024];
        byte[] sendData  = new byte[1024];

        int userNum = 1;

        while(true) {
            // create space for received datagram
            DatagramPacket receivePacket =
                    new DatagramPacket(receiveData, receiveData.length);

            //receive datagram
            serverSocket.receive(receivePacket);
            
            // get IP address, port # of sender
            String expression = new String(receivePacket.getData());

            InetAddress IPAddress = receivePacket.getAddress();

            int port = receivePacket.getPort();

            String result = evaluate(expression) + "";


            sendData = result.getBytes();

            // create datagram to send to client
            DatagramPacket sendPacket =
                    new DatagramPacket(sendData, sendData.length, IPAddress,
                            port);

            // DatagramPacket sendPacket2 =
            //         new DatagramPacket(IPAddress.getHostName().getBytes(), IPAddress.getHostName().getBytes().length, IPAddress,
            //                 port);

            //write out datagram to socket
            serverSocket.send(sendPacket);
            //serverSocket.send(sendPacket2);
            log(userNum);
            userNum++;
        } // end of loop, loop back and wait for another datagram
    }

    public static int evaluate(String expression){
        //Stack for operands
        Stack<Integer> operands = new Stack<>();

        //Stack for operators
        Stack<Character> operators = new Stack<>();

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            //check if it is number
            if (Character.isDigit(c)) {

                //Entry is Digit, it could be greater than one digit number
                int num = 0;
                while (Character.isDigit(c)) {
                    num = num * 10 + (c - '0');
                    i++;
                    if(i < expression.length()) {
                        c = expression.charAt(i);
                    } else {
                        break;
                    }
                }

                i--;
                //push it into stack
                operands.push(num);
            } else if (c == '(') {
                //push it to operators stack
                operators.push(c);
            }
            //Closed brace, evaluate the entire brace
            else if ( c== ')') {
                while (operators.peek() != '(') {
                    int result = solveCalculation (operands, operators);
                    //push it back to stack
                    operands.push(result);
                }
                operators.pop();
            }
            // current character is operator
            else if (isOperator(c)) {
                //1. If current operator has higher precedence than operator on top of the stack,
                //the current operator can be placed in stack
                // 2. else keep popping operator from stack and perform the operation in  numbers stack till
                //either stack is not empty or current operator has higher precedence than operator on top of the stack
                while (!operators.isEmpty() && precedence(c) <= precedence(operators.peek())) {
                    int result = solveCalculation(operands, operators);
                    //push it back to stack
                    operands.push(result);
                }
                //now push the current operator to stack
                operators.push(c);
            }
        }
        //If here means entire expression has been processed,
        //Perform the remaining operations in stack to the numbers stack

        while (!operators.isEmpty()) {
            int result = solveCalculation(operands, operators);
            //push it back to stack
            operands.push(result);
        }
        return operands.pop();
    }

    public static boolean isOperator (char c) {
        return (c == '+' || c == '-' || c == '/' || c == '*' || c == '^');
    }

    public static int precedence (char c) {
        switch (c) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            case '^':
                return 3;
        }
        return -1;
    }

    public static int solveCalculation (Stack<Integer> operands, Stack<Character> operators) {
        int a = operands.pop();
        int b = operands.pop();
        char operation = operators.pop();
        switch (operation) {
            case '+':
                return a + b;
            case '-':
                return b - a;
            case '*':
                return a * b;
            case '/':
                if (a == 0) {
                    throw new
                            UnsupportedOperationException("Divide by zero error.");
                }
                    
                return b / a;
        }
        return 0;
    }

    public static void log(int userNumber) { 
        Logger logger = Logger.getLogger(UDPServer.class.getName());
        logger.info("User " + userNumber + "attached to the server");
    }
}

