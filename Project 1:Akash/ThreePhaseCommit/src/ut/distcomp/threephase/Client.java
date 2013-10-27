package ut.distcomp.threephase;

import java.io.*;
import java.lang.String;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import ut.distcomp.framework.*;

/**
 * Created with IntelliJ IDEA.
 * User: Akash
 * Date: 9/30/13
 * Time: 7:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class Client {
    public static void main(String args[]){
        System.out.println("Receive Args:"+ args[0]);
        if(!args[0].contains("Coordinator") && !args[0].contains("Participant")){
            try {
                //
                // Create a socket to the coordinator and try to send the initiate message
                //
                //logger.log(Level.INFO, "Trying to send INITIATE_PROTOCOL to Coordinator.");
                System.out.println("Trying to send INITIATE_PROTOCOL to Coordinator.");
                Config config = new Config("config1.properties");
                Socket socket = new Socket(config.addresses[0], config.ports[0]);
                socket.shutdownInput();
                OutputStream out = socket.getOutputStream();
                BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
                String instruction = bf.readLine();
                out.write(instruction.getBytes());
                out.write("&".getBytes());
                out.flush();
                out.close();
                System.out.println("Sent INITIATE");

                //logger.log(Level.INFO, "Sent INITIATE_PROTOCOL to Coordinator for TX: " + initiateProtocolMessage.toString());
                //transactionId++;
            }
            catch (IOException e) {
                //logger.log(Level.WARNING, "Looks like Coordinator (Process 0) is not reachable.");
                System.out.println("Coordinator (Process 0) is not reachable. Please retry command after starting Process 0.");
            }

            /*String identity;
            try{
                String filename = args[0];
                System.out.println(filename);
                Config cf =  new Config(filename);
               // List<Process> processList = new ArrayList<Process>();
               *//* for(int i =0; i< cf.numProcesses;i++) {
                    if(i == 0) {                  
                        identity = "Coordinator:" + filename;
                        //Init Coordinator;
                    } else {
                        identity = "Participant:" + String.valueOf(i) + ":"+filename;
                        //Init Participant
                    }
                    String command = "java -jar " + "ThreePhaseCommit.jar " + identity; //String.valueOf(i);// +
                    //((i == 0) ? " true" : " false");
                    processList.add(Runtime.getRuntime().exec(command));
                }*//*

                BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
                String instruction = bf.readLine();
                if(!instruction.equals("GO")){
                    System.out.println("Wrong Command or instruction, Enter GO");
                }else{

                    // TODO:  Create a new outgoing socket.
                    // This will send the received message from the CONSOLE to the Coordinator.

                }
            }
            catch(ArrayIndexOutOfBoundsException e) {
                System.out.println("No filename given!");
                e.printStackTrace();
            } catch(FileNotFoundException e) {
                System.out.println("File not found!");
                e.printStackTrace();
            } catch(IOException e) {
                System.out.println("IOException!");
                e.printStackTrace();
            } catch(Exception e) {
                System.out.println("Some shit happened!");
                e.printStackTrace();
            }*/
        }
        else if(args[0].contains("Coordinator")){

            try {
                System.out.println("New Coordinator Created");
                String filename = args[0].substring(args[0].indexOf(":")+1, args[0].length());
                Config cf =  new Config(filename);
                cf.procNum = 0;

                //Init Coordinator;
                Contributor cd = new Contributor(cf);
            } catch (Exception e) {
                System.out.println("Exception");
                e.printStackTrace();
            }
        } else if(args[0].contains("Participant")) {
            try {
                System.out.println("NEw Participant Created");
                String[] values = args[0].split(":");
                int procId = Integer.parseInt(values[1]);
                String filename = values[2];
                Config cf =  new Config(filename);
                cf.procNum = procId;
                Contributor pt = new Contributor(cf);
                //Init Participant;
            } catch (Exception e) {
                System.out.println("Exception");
                e.printStackTrace();
            }
        }
    }
}
